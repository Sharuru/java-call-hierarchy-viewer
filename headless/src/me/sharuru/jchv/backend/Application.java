package me.sharuru.jchv.backend;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;
import org.eclipse.jdt.internal.corext.javadoc.JavaDocCommentReader;

/**
 * This class controls all aspects of the application's execution
 */
@SuppressWarnings("restriction")
public class Application implements IApplication {

    private List<String> tgtExtNames = Arrays.asList(".java");
    private List<String> tgtProjPrefixs = Arrays.asList("xx");
    private List<String> tgtIgnExtNames = Arrays.asList("model.java", "criteria.java", "dto.java", "result.java", "post.java");
    
	@Override
	public Object start(IApplicationContext context) throws Exception {
        System.out.println("JCHV headless backend is initializing...");

        String[] appArgs = Platform.getApplicationArgs();
        for (int i = 0; i < appArgs.length; i++) {
            String currArg = appArgs[i];
            if (currArg.startsWith("-jchv.tgtExtNames")) {
                tgtExtNames = Arrays.asList(currArg.split("=")[1].split(","));
            } else if (currArg.startsWith("-jchv.tgtProjPrefixs")) {
                tgtProjPrefixs = Arrays.asList(currArg.split("=")[1].split(","));
            } else if (currArg.startsWith("-jchv.tgtIgnNames")) {
                tgtIgnExtNames = Arrays.asList(currArg.split("=")[1].split(","));
            }
        }

        System.out.println("JCHV headless backend will using following settings:");
        System.out.println(
                "Target extend names: " + tgtExtNames.stream().collect((Collectors.joining(", "))));
        System.out.println("Target project prefixs: "
                + tgtProjPrefixs.stream().collect((Collectors.joining(", "))));
        System.out.println("Target ignored extend names: "
                + tgtIgnExtNames.stream().collect((Collectors.joining(", "))));

        System.out.println("JCHV headless backend is starting...");
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject[] projects = root.getProjects();
        for (IProject project : projects) {
            System.out.println("Visiting " + project.getName() + "...");
            boolean isTarget = false;
            for(String tgtProjPrefix : tgtProjPrefixs) {
                if(project.getName().startsWith(tgtProjPrefix)){
                    isTarget = true;
                    break;
                }
            }
            if(isTarget) {
                getProjectInfo(project);
            }
        }
		return IApplication.EXIT_OK;
	}
	
    private void getProjectInfo(IProject project) throws JavaModelException {
        System.out.println("Entering target project " + project.getName() + "...");
        IJavaProject javaProject = JavaCore.create(project);
        getPackageInfo(javaProject);
    }
    
    private void getPackageInfo(IJavaProject javaProject) throws JavaModelException {
        IPackageFragment[] packages = javaProject.getPackageFragments();
        for (IPackageFragment currentPackages : packages) {
            if (currentPackages.getKind() == IPackageFragmentRoot.K_SOURCE) {
                for (ICompilationUnit unit : currentPackages.getCompilationUnits()) {
                    getCompilationUnitDetails(unit);
                }
            }
        }
    }

    private void getCompilationUnitDetails(ICompilationUnit unit) throws JavaModelException {
        String lowcasedElementName = unit.getElementName().toLowerCase();
        String extName = lowcasedElementName.substring(lowcasedElementName.lastIndexOf('.'));
        if (tgtExtNames.contains(extName)) {
            boolean isTarget = true;
            String fileName = unit.getElementName().toLowerCase();
            for(String tgtIgnExtName : tgtIgnExtNames) {
                if(fileName.endsWith(tgtIgnExtName)) {
                    isTarget = false;
                }
            }
            if(isTarget) {
                getIMethods(unit);
            }
        }
    }

    private void getIMethods(ICompilationUnit unit) throws JavaModelException {
        IType[] allTypes = unit.getAllTypes();
        for (IType type : allTypes) {
            getIMethodDetails(type);
        }
    }
    
    private void getIMethodDetails(IType type) throws JavaModelException {
        IMethod[] methods = type.getMethods();
        for (IMethod method : methods) {
            CallHierarchy callHierarchy = CallHierarchy.getDefault();
            IMember[] members = {method};
            MethodWrapper[] methodWrappers = callHierarchy.getCalleeRoots(members);
            HashSet<IMethod> callees = new LinkedHashSet<>();
            for (MethodWrapper currentMw : methodWrappers) {
                MethodWrapper[] mw2 = currentMw.getCalls(new NullProgressMonitor());
                HashSet<IMethod> calleeMethods = getIMethods(mw2);
                callees.addAll(calleeMethods);
            }

            //
            String newParamStr = "";
            String comma = "";
            String[] parameterTypes = method.getParameterTypes();
            try {
                String[] parameterNames = method.getParameterNames();
                for (int i = 0; i < method.getParameterTypes().length; ++i) {
                    newParamStr = newParamStr.concat(comma);
                    newParamStr = newParamStr.concat(Signature.toString(parameterTypes[i]));
                    newParamStr = newParamStr.concat(" ");
                    newParamStr = newParamStr.concat(parameterNames[i]);
                    comma = ", ";
                }
            } catch (JavaModelException e) {
                System.out.println(e.getMessage());
            }
            //

            System.out.println("Method: " + method.getPath() + "#" + method.getElementName() + "("
                    + newParamStr + ")" + " is calling following methods:");
            String methodPath =
                    method.getPath() + "#" + method.getElementName() + "(" + newParamStr + ")";
            String methodType = "";
            String methodCallee = "";
            String methodComment = "";
            ISourceRange javadocRange = method.getJavadocRange();
            IBuffer buf = method.getOpenable().getBuffer();
            if (javadocRange != null) {
                JavaDocCommentReader reader =
                        new JavaDocCommentReader(buf, javadocRange.getOffset(),
                                javadocRange.getOffset() + javadocRange.getLength() - 1);
                if (!containsOnlyInheritDoc(reader, javadocRange.getLength())) {
                    reader.reset();
                }
                try {
                    methodComment = reader.getString().replaceAll("'", "''");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String url = "";
            String user = "";
            String password = "";
            String tableName = "";
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String methodqualifiedName = getMethodFullName(method);
            methodPath = methodPath.substring(1);
            String BASESQL = "INSERT INTO " + tableName
                    + "(method_qualified_name, method_path, method_comment, method_type, method_callee_qualified_name, method_callee_seq) VALUES (";
            BASESQL += "'" + methodqualifiedName + "','" + methodPath + "','" + methodComment
                    + "','" + "BASE" + "','" + methodqualifiedName + "','" + "0" + "')";
//            try {
//                conn.prepareStatement(BASESQL).executeUpdate();
//            } catch (SQLException e) {
//                e.printStackTrace();
//                System.out.println(e.getMessage());
//            }
            System.out.println(BASESQL);
            int methodCalleeSeq = 1;
            for (IMethod callee : callees) {

                //
                String newCalleeParamStr = "";
                String calleeComma = "";
                String[] calleeParameterTypes = callee.getParameterTypes();
                try {
                    String[] calleeParameterNames = callee.getParameterNames();
                    for (int i = 0; i < callee.getParameterTypes().length; ++i) {
                        newCalleeParamStr = newCalleeParamStr.concat(calleeComma);
                        newCalleeParamStr = newCalleeParamStr
                                .concat(Signature.toString(calleeParameterTypes[i]));
                        newCalleeParamStr = newCalleeParamStr.concat(" ");
                        newCalleeParamStr = newCalleeParamStr.concat(calleeParameterNames[i]);
                        calleeComma = ", ";
                    }
                } catch (JavaModelException e) {
                    System.out.println(e.getMessage());
                }
                //
                String methodCalleeComment = "";

                if (callee.isBinary()) {
                    List<String> binElemNameLst = new ArrayList<>();
                    IJavaElement currentJavaElem = callee.getParent();
                    while (true) {
                        if (currentJavaElem != null) {
                            binElemNameLst.add(currentJavaElem.getElementName());
                            currentJavaElem = currentJavaElem.getParent();
                        } else {
                            break;
                        }
                    }
                    String binPath = "";
                    for (int i = 1; i < binElemNameLst.size(); i++) {
                        String str = binElemNameLst.get(i);
                        if (!str.endsWith(".jar") && !str.endsWith(".class")) {
                            str = str.replaceAll("\\.", "/");
                        }
                        binPath = str + "/" + binPath;
                        if (str.endsWith(".jar")) {
                            break;
                        }
                    }
                    binPath = binPath.substring(0, binPath.length() - 1);
                    binPath = binPath + "#" + callee.getElementName();
                    binPath = binPath + "(" + newCalleeParamStr + ")";
                    methodType = "BIN";
                    methodCallee = binPath;

                } else {
                    String path = callee.getPath() + "#" + callee.getElementName();
                    if (!(method.getPath() + "#" + method.getElementName()).equals(path)) {
                        methodType = "SRC";
                        methodCallee = callee.getPath() + "#" + callee.getElementName() + "("
                                + newCalleeParamStr + ")";
                    } else {
                        methodType = "LOOP-SRC";
                        methodCallee = callee.getPath() + "#" + callee.getElementName() + "("
                                + newCalleeParamStr + ")";
                    }
                    methodCallee = methodCallee.substring(1);
                    ISourceRange calleeJavadocRange = callee.getJavadocRange();
                    IBuffer calleeBuf = callee.getOpenable().getBuffer();
                    if (calleeJavadocRange != null) {
                        JavaDocCommentReader calleeReader = new JavaDocCommentReader(calleeBuf,
                                calleeJavadocRange.getOffset(), calleeJavadocRange.getOffset()
                                        + calleeJavadocRange.getLength() - 1);
                        if (!containsOnlyInheritDoc(calleeReader, calleeJavadocRange.getLength())) {
                            calleeReader.reset();
                        }
                        try {
                            methodCalleeComment = calleeReader.getString().replaceAll("'", "''");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                String methodCalleequalifiedName = getMethodFullName(callee);
                String SQL = "";
                SQL = "INSERT INTO" + tableName
                        + "(method_qualified_name, method_path, method_comment, method_type, method_callee_qualified_name, method_callee_seq) VALUES (";
                SQL += "'" + methodqualifiedName + "','" + methodCallee + "','"
                        + methodCalleeComment + "','" + methodType + "','"
                        + methodCalleequalifiedName + "','" + methodCalleeSeq + "')";
//                try {
//                    conn.prepareStatement(SQL).executeUpdate();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    System.out.println(e.getMessage());
//                }
                System.out.println(SQL);
                methodCalleeSeq++;
            }

//            try {
//                conn.close();
//            } catch (SQLException e) {
//                System.out.println(e.getMessage());
//                e.printStackTrace();
//            }
        }
    }

    private static boolean containsOnlyInheritDoc(Reader reader, int length) {
        char[] content = new char[length];
        try {
            reader.read(content, 0, length);
        } catch (IOException e) {
            return false;
        }
        return new String(content).trim().equals("{@inheritDoc}");
    }


    HashSet<IMethod> getIMethods(MethodWrapper[] methodWrappers) {
        HashSet<IMethod> c = new LinkedHashSet<IMethod>();
        for (MethodWrapper m : methodWrappers) {
            IMethod im = getIMethodFromMethodWrapper(m);
            if (im != null) {
                c.add(im);
            }
        }
        return c;
    }

    IMethod getIMethodFromMethodWrapper(MethodWrapper m) {
        try {
            IMember im = m.getMember();
            if (im.getElementType() == IJavaElement.METHOD) {
                return (IMethod) m.getMember();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getMethodFullName(IMethod iMethod) {
        StringBuilder name = new StringBuilder();
        name.append(iMethod.getDeclaringType().getFullyQualifiedName());
        name.append(".");
        name.append(iMethod.getElementName());
        name.append("(");

        String comma = "";
        String[] parameterTypes = iMethod.getParameterTypes();
        try {
            String[] parameterNames = iMethod.getParameterNames();
            for (int i = 0; i < iMethod.getParameterTypes().length; ++i) {
                name.append(comma);
                name.append(Signature.toString(parameterTypes[i]));
                comma = ", ";
            }
        } catch (JavaModelException e) {
            System.out.println(e.getMessage());
        }

        name.append(")");

        return name.toString();
    }

	

	@Override
	public void stop() {
		// nothing to do
	}
}

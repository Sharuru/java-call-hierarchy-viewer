package projectast.views;

import javax.inject.Inject;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import projectast.parts.SettingShell;
import projectast.parts.WorkJob;

public class PSTView extends ViewPart {

    public static final String ID = "projectast.views.PSTView";

    @Inject
    private IWorkbench workbench;
    @Inject
    private UISynchronize uiSyncThread;
    private Shell viewShell;
    private Action runJobAction;
    private Action settingAction;
    private Action clearLogAction;
    private Text logText;
    WorkJob work;
    public SharedData sharedData;

    public SharedData getSharedData() {
        return sharedData;
    }

    @Override
    public void createPartControl(Composite parent) {

        viewShell = parent.getShell();

        logText = new Text(parent, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
        logText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        this.sharedData = new SharedData(this);
        initComponentActions();
        initViewActionBar();
    }

    private void initComponentActions() {
        runJobAction = new Action() {
            @Override
            public void run() {
                logText.append("Starting work job with parameters: XX,YY,ZZ... \n");

                Job job = Job.create("PST job", (ICoreRunnable) monitor -> {
                    work = new WorkJob();
                    try {
                        work.execute(sharedData);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });
                job.schedule();

            }
        };

        runJobAction.setText("Run PST job");
        runJobAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));


        settingAction = new Action() {
            @Override
            public void run() {
                SettingShell.main(sharedData, Display.getCurrent());
            }
        };
        settingAction.setText("PST Settings...");
        settingAction.setImageDescriptor(
                workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
        
        clearLogAction = new Action() {
            @Override
            public void run() {
                logText.setText("LOG CLEARED \n");
            }
        };
        clearLogAction.setText("Clear log");
        clearLogAction.setImageDescriptor(
                workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
    }

    private void initViewActionBar() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(settingAction);
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(runJobAction);
        manager.add(clearLogAction);
    }

    @Override
    public void setFocus() {
        viewShell.setFocus();
    }

    
    public class SharedData {
        private String jdbcUrl = "";
        private String jdbcUsername = "";
        private String jdbcPassword = "";
        private String tableName = "";
        private PSTView pstView;
        private Text logText;
        private UISynchronize uiSyncThread;
        
        public SharedData(PSTView pstView) {
            this.pstView = pstView;
            this.logText = pstView.logText;
            this.uiSyncThread = pstView.uiSyncThread;
            
        }
        public String getJdbcUrl() {
            return jdbcUrl;
        }
        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }
        public String getJdbcUsername() {
            return jdbcUsername;
        }
        public void setJdbcUsername(String jdbcUsername) {
            this.jdbcUsername = jdbcUsername;
        }
        public String getJdbcPassword() {
            return jdbcPassword;
        }
        public void setJdbcPassword(String jdbcPassword) {
            this.jdbcPassword = jdbcPassword;
        }
        public Text getLogText() {
            return logText;
        }
        public void setLogText(Text logText) {
            this.logText = logText;
        }
        public PSTView getPstView() {
            return pstView;
        }
        public void setPstView(PSTView pstView) {
            this.pstView = pstView;
        }
        public UISynchronize getUiSyncThread() {
            return uiSyncThread;
        }
        public void setUiSyncThread(UISynchronize uiSyncThread) {
            this.uiSyncThread = uiSyncThread;
        }
        public String getTableName() {
            return tableName;
        }
        public void setTableName(String tableName) {
            this.tableName = tableName;
        }
    }
    
    
}

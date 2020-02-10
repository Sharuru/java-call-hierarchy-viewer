package me.sharuru.jchv.frontend.service;

import lombok.extern.slf4j.Slf4j;
import me.sharuru.jchv.frontend.entity.TblMetaContent;
import me.sharuru.jchv.frontend.model.BusinessApiResponse;
import me.sharuru.jchv.frontend.model.TreeGraphNode;
import me.sharuru.jchv.frontend.repository.MetaContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Business service
 */
@Service
@Slf4j
// TODO combine callee and caller logic with facade or else
public class MetaContentService {

    @Autowired
    MetaContentRepository metaContentRepository;

    /**
     * Get callee method information
     *
     * @param methodQualifiedName base layer method name
     * @return information result
     */
    public BusinessApiResponse getCalleeMethodInfo(String methodQualifiedName) {

        BusinessApiResponse response = new BusinessApiResponse();

        List<TblMetaContent> metaLayer = metaContentRepository.findCalleeByMethodQualifiedName(methodQualifiedName);
        if (metaLayer.isEmpty() || (!"BASE".equals(metaLayer.get(0).getMethodType()) && !"ITFS".equals(metaLayer.get(0).getMethodType()))) {
            throw new RuntimeException("Meta node is not found: " + methodQualifiedName);
        } else {
            TblMetaContent metaBase = metaLayer.get(0);
            response.getTreeGraphData().setId(metaBase.getId());
            response.getTreeGraphData().setMethodQualifiedName(metaBase.getMethodCalleeQualifiedName());
            response.getTreeGraphData().setMethodPath(metaBase.getMethodPath());
            response.getTreeGraphData().setMethodType(metaBase.getMethodType());
            response.getTreeGraphData().setMethodComment(metaBase.getMethodComment());

            Map<String, Integer> methodIndexMap = new HashMap<>();
            methodIndexMap.put(metaBase.getMethodCalleeQualifiedName(), 1);

            response.getTreeGraphData().setChildren(visitCalleeMethod(metaLayer, methodIndexMap));
            response.setMethodIndexMap(methodIndexMap);
            return response;
        }
    }

    /**
     * Callee traversal method
     *
     * @param parentLayer travel criteria layer, usually the base
     * @return graph model
     */
    private LinkedList<TreeGraphNode> visitCalleeMethod(List<TblMetaContent> parentLayer, Map<String, Integer> idxMap) {

        LinkedList<TreeGraphNode> nodeGraphList = new LinkedList<>();

        for (TblMetaContent node : parentLayer) {
            if (("SRC".equals(node.getMethodType()) || "LOOP-SRC".equals(node.getMethodType()))
                    && !node.getMethodPath().contains("Model.java#")
                    && !node.getMethodPath().contains("Base.java#")
                    && !node.getMethodPath().contains("Criteria.java#")) {

                TreeGraphNode currentNode = new TreeGraphNode();
                currentNode.setId(node.getId());
                currentNode.setMethodQualifiedName(node.getMethodCalleeQualifiedName());
                currentNode.setMethodPath(node.getMethodPath());
                currentNode.setMethodType(node.getMethodType());

                // TODO import callee comment to DB in next version of data importer
                List<TblMetaContent> currentNodeInfo = metaContentRepository.findCalleeByMethodQualifiedName(node.getMethodCalleeQualifiedName());
                if (!currentNodeInfo.isEmpty() && "BASE".equals(currentNodeInfo.get(0).getMethodType())) {
                    currentNode.setMethodComment(currentNodeInfo.get(0).getMethodComment());
                } else {
                    currentNode.setMethodComment("Details not found.");
                }

                if (idxMap.containsKey(node.getMethodCalleeQualifiedName())) {
                    idxMap.compute(node.getMethodCalleeQualifiedName(), (k, v) -> v += 1);
                    log.info("{} is already existed in the callee chain, skipped.", node.getMethodCalleeQualifiedName());
                } else {
                    idxMap.put(node.getMethodCalleeQualifiedName(), 1);
                    if (!"LOOP-SRC".equals(node.getMethodType())) {
                        currentNode.setChildren(visitCalleeMethod(metaContentRepository.findCalleeByMethodQualifiedName(node.getMethodCalleeQualifiedName()), idxMap));
                    }
                    nodeGraphList.add(currentNode);
                }
            }
        }

        return nodeGraphList;
    }

    /**
     * Get caller method information
     *
     * @param methodCalleeQualifiedName base layer method name
     * @return information result
     */
    public BusinessApiResponse getCallerMethodInfo(String methodCalleeQualifiedName) {
        BusinessApiResponse response = new BusinessApiResponse();

        List<TblMetaContent> metaLayer = metaContentRepository.findCalleeByMethodQualifiedName(methodCalleeQualifiedName);

        if (metaLayer.isEmpty() || (!"BASE".equals(metaLayer.get(0).getMethodType()) && !"ITFS".equals(metaLayer.get(0).getMethodType()))) {
            throw new RuntimeException("Meta node is not found: " + methodCalleeQualifiedName);
        } else {
            TblMetaContent metaBase = metaLayer.get(0);
            response.getTreeGraphData().setId(metaBase.getId());
            response.getTreeGraphData().setMethodQualifiedName(metaBase.getMethodCalleeQualifiedName());
            response.getTreeGraphData().setMethodPath(metaBase.getMethodPath());
            response.getTreeGraphData().setMethodType(metaBase.getMethodType());
            response.getTreeGraphData().setMethodComment(metaBase.getMethodComment());

            Map<String, Integer> callerMethodIndexMap = new HashMap<>();
            callerMethodIndexMap.put(metaBase.getMethodCalleeQualifiedName(), 1);

            response.getTreeGraphData().setChildren(visitCallerMethod(metaContentRepository.findCallerByMethodCalleeQualifiedName(methodCalleeQualifiedName), callerMethodIndexMap));
            response.setMethodIndexMap(callerMethodIndexMap);

            return response;
        }
    }

    /**
     * Get qualified name by method simple name
     *
     * @param methodSimpleClass method simple name
     * @param methodSimpleMethod method simple name
     * @return information result
     */
    public BusinessApiResponse getQualifiedNames(String methodSimpleClass, String methodSimpleMethod) {
        BusinessApiResponse response = new BusinessApiResponse();


        if (StringUtils.isEmpty(methodSimpleClass) && StringUtils.isEmpty(methodSimpleMethod)) {
            throw new RuntimeException("Transferred value is not acceptable.");
        }

        List<TblMetaContent> queryResult = metaContentRepository.findQualifiedBySimpleName(methodSimpleClass, methodSimpleMethod);

        if (queryResult.isEmpty()) {
            throw new RuntimeException("Matched qualified name is not found: " + methodSimpleClass + ", " + methodSimpleMethod);
        }

        List<TreeGraphNode> results = new ArrayList<>();

        queryResult.forEach(result -> {
            TreeGraphNode node = new TreeGraphNode();
            node.setId(result.getId());
            node.setMethodQualifiedName(result.getMethodCalleeQualifiedName());
            node.setMethodPath(result.getMethodPath());
            node.setMethodType(result.getMethodType());
            node.setMethodComment(result.getMethodComment());
            results.add(node);
        });

        response.getTreeGraphData().getChildren().addAll(results);
        response.setBizCode(200L);

        return response;
    }

    /**
     * Caller traversal method
     *
     * @param parentLayer travel criteria layer, usually the base
     * @return graph model
     */
    private LinkedList<TreeGraphNode> visitCallerMethod(List<TblMetaContent> parentLayer, Map<String, Integer> idxMap) {
        LinkedList<TreeGraphNode> nodeGraphList = new LinkedList<>();
        for (TblMetaContent node : parentLayer) {
            if ("SRC".equals(node.getMethodType())) {
                TreeGraphNode currentNode = new TreeGraphNode();
                currentNode.setId(node.getId());
                currentNode.setMethodQualifiedName(node.getMethodQualifiedName());
                currentNode.setMethodType(node.getMethodType());

                // TODO import callee comment to DB in next version of data importer
                List<TblMetaContent> currentNodeInfo = metaContentRepository.findCalleeByMethodQualifiedName(node.getMethodCalleeQualifiedName());
                if (!currentNodeInfo.isEmpty() && ("BASE".equals(currentNodeInfo.get(0).getMethodType()) || "ITFS".equals(currentNodeInfo.get(0).getMethodType()))) {
                    currentNode.setMethodPath(currentNodeInfo.get(0).getMethodPath().substring(currentNodeInfo.get(0).getMethodPath().lastIndexOf('/') + 1));
                    currentNode.setMethodComment(currentNodeInfo.get(0).getMethodComment());
                } else {
                    currentNode.setMethodPath("Details not found.");
                    currentNode.setMethodComment("Details not found.");
                }

                if (idxMap.containsKey(node.getMethodQualifiedName())) {
                    idxMap.compute(node.getMethodQualifiedName(), (k, v) -> v += 1);
                    log.info("{} is already existed in caller chain, skipped.", node.getMethodQualifiedName());
                } else {
                    idxMap.put(node.getMethodQualifiedName(), 1);
                    currentNode.setChildren(visitCallerMethod(metaContentRepository.findCallerByMethodCalleeQualifiedName(node.getMethodQualifiedName()), idxMap));
                    nodeGraphList.add(currentNode);
                }
            }
        }
        return nodeGraphList;
    }

}

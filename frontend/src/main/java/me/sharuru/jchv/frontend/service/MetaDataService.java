package me.sharuru.jchv.frontend.service;

import lombok.extern.slf4j.Slf4j;
import me.sharuru.jchv.frontend.entity.TblMetaData;
import me.sharuru.jchv.frontend.model.Node;
import me.sharuru.jchv.frontend.model.SearchResponse;
import me.sharuru.jchv.frontend.model.TreantNode;
import me.sharuru.jchv.frontend.model.TreantRoot;
import me.sharuru.jchv.frontend.repository.MetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MetaDataService {

    @Autowired
    MetaDataRepository metaDataRepository;

    public SearchResponse search(String searchPath) {
        SearchResponse response = new SearchResponse();

        List<TblMetaData> selfMetaLst = metaDataRepository.findSelfByPath(searchPath);
        if (selfMetaLst.size() > 1) {
            throw new RuntimeException("Found more than one root node.");
        } else if (selfMetaLst.isEmpty()) {
            throw new RuntimeException("Root node not found.");
        }
        TblMetaData rootNode = selfMetaLst.get(0);

        Node<TblMetaData> callerTreeGraph = new Node<>(rootNode);
        for (TblMetaData childNode : metaDataRepository.findCallerByPath(rootNode.getMethod())) {
            String line = childNode.getMethod();
            if(!line.contains("Model.java#") && !line.contains("Base.java#") && !line.contains("Criteria.java#")){
                callerTreeGraph.addChild(new Node<>(childNode));
            }

        }
        visitChildNode(callerTreeGraph.getChildren());

        List<String> allCalleeMethods = new ArrayList<>();

        Node<TblMetaData> calleeTreeGraph = new Node<>(rootNode);
        List<String> calleeMethodLst = new ArrayList<>();
        Arrays.asList(rootNode.getContext().split("\\R")).forEach(line ->{
            if(line.startsWith("SRC") && !line.contains("Model.java#") && !line.contains("Base.java#") && !line.contains("Criteria.java#")
            && !rootNode.getMethod().equals(line)){
                calleeMethodLst.add(line.substring(line.indexOf("SRC: ") + 5));
            }
        });
        for(String method : calleeMethodLst){
            TblMetaData meta = metaDataRepository.findSelfByPath(method).get(0);
            calleeTreeGraph.addChild(new Node<>(meta));
            allCalleeMethods.add(meta.getMethod());
        }
        visitCalleeNode(calleeTreeGraph.getChildren(), allCalleeMethods);

        // TODO
        TreantRoot rootJson = new TreantRoot();
        TreantNode rootNodeJson = new TreantNode();
        rootNodeJson.setName(getSearchPath(rootNode.getMethod()));
        rootNodeJson.setDesc(rootNode.getComment().split("\\R", 2)[0]);
        rootNodeJson.setDataFullPath(rootNode.getMethod());
        rootJson.setText(rootNodeJson);
        setJson(callerTreeGraph.getChildren(), rootJson);

        TreantRoot calleeRootJson = new TreantRoot();
        TreantNode calleeRootNodeJson = new TreantNode();
        calleeRootNodeJson.setName(getSearchPath(rootNode.getMethod()));
        calleeRootNodeJson.setDesc(rootNode.getComment().split("\\R", 2)[0]);
        calleeRootNodeJson.setDataFullPath(rootNode.getMethod());
        calleeRootJson.setText(calleeRootNodeJson);
        setJson(calleeTreeGraph.getChildren(), calleeRootJson);


        List<String> distinctCalleeLest = allCalleeMethods.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        response.setCalleeLst(distinctCalleeLest);
        response.setCalleeNodeStructure(calleeRootJson);
        response.setNodeStructure(rootJson);
        return response;

    }


    private void setJson(List<Node<TblMetaData>> subNode, TreantRoot rootJson){
        for(Node<TblMetaData> ssNode : subNode){
            TreantRoot cRoot = new TreantRoot();
            TreantNode cNode = new TreantNode();
            cNode.setName(getSearchPath(ssNode.getData().getMethod()));
            cNode.setDataFullPath(ssNode.getData().getMethod());
            // get first line
            cNode.setDesc(ssNode.getData().getComment().split("\\R", 2)[0]);
            cRoot.setText(cNode);
            rootJson.getChildren().add(cRoot);
            setJson(ssNode.getChildren(), cRoot);
        }
    }

    private String getSearchPath(String rawInput) {
        return rawInput.substring(rawInput.lastIndexOf("/") + 1);
    }

    private void visitChildNode(List<Node<TblMetaData>> parentNodeLst) {
        for (Node<TblMetaData> childNode : parentNodeLst) {
            for (TblMetaData tblMetaData : metaDataRepository.findCallerByPath(childNode.getData().getMethod())) {
                if (!tblMetaData.getId().equals(childNode.getData().getId())) {
                    if(!isLooping(childNode, tblMetaData)){
                        childNode.addChild(new Node<>(tblMetaData));
                    }else{
                        log.error("This node is skipped.");
                    }
                }
            }
            visitChildNode(childNode.getChildren());
        }
    }

    private void visitCalleeNode(List<Node<TblMetaData>> parentNodeLst, List<String> allCalleeMethods){
        for (Node<TblMetaData> childNode : parentNodeLst) {
            List<String> calleeMethodLst = new ArrayList<>();
            Arrays.asList(childNode.getData().getContext().split("\\R")).forEach(line ->{
                if(line.startsWith("SRC") && !line.contains("Model.java#") && !line.contains("Base.java#") && !line.contains("Criteria.java#")){
                    calleeMethodLst.add(line.substring(line.indexOf("SRC: ") + 5));
                    log.info("Line {} is ADDED to {}", line, childNode.getData().getMethod());
                }
            });
            for(String method : calleeMethodLst){
                TblMetaData meta = metaDataRepository.findSelfByPath(method).get(0);
                if(!method.equals(childNode.getData().getMethod())){
                    if(!isLooping(childNode, meta)){
                        childNode.addChild(new Node<>(meta));
                        allCalleeMethods.add(meta.getMethod());
                    }else{
                        log.error("This node is skipped.");
                    }
                }
            }
            visitCalleeNode(childNode.getChildren(), allCalleeMethods);
        }
    }


    private boolean isLooping(Node<TblMetaData> childNode, TblMetaData tbl) {
        List<Long> pIds = new ArrayList<>();
        Node<TblMetaData> currNode = childNode;
        while (currNode != null) {
            pIds.add(currNode.getData().getId());
            currNode = currNode.getParent();
        }
        log.info("Current childNodeId: {}", childNode.getData().getId());
        log.info("Current addedNode: {}, Pid: {}, isContain? {}", tbl.getId(), pIds.toString(), pIds.contains(tbl.getId()));
        if (pIds.contains(tbl.getId())) {
            return true;
        }
        return false;
    }
}

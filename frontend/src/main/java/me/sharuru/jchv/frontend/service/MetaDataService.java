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

@Service
@Slf4j
public class MetaDataService {

    @Autowired
    MetaDataRepository metaDataRepository;

    public SearchResponse search(String searchPath) {
        SearchResponse response = new SearchResponse();

        List<TblMetaData> selfMetaLst = metaDataRepository.findSelfByPath(searchPath);
        if (selfMetaLst.size() > 1) {
            throw new RuntimeException("Find more than one root node.");
        } else if (selfMetaLst.isEmpty()) {
            throw new RuntimeException("Root node not found.");
        }
        TblMetaData rootNode = selfMetaLst.get(0);

        Node<TblMetaData> callerTreeGraph = new Node<>(rootNode);
        for (TblMetaData childNode : metaDataRepository.findCallerByPath(rootNode.getMethod())) {
            callerTreeGraph.addChild(new Node<>(childNode));
        }

        visitChildNode(callerTreeGraph.getChildren());

        // TODO
        TreantRoot rootJson = new TreantRoot();
        TreantNode rootNodeJson = new TreantNode();
        rootNodeJson.setName(getSearchPath(rootNode.getMethod()));
        rootNodeJson.setDesc(rootNode.getComment().split("\\R", 2)[0]);
        rootJson.setText(rootNodeJson);
        setJson(callerTreeGraph.getChildren(), rootJson);

        response.setCalleeLst(Arrays.asList(rootNode.getContext(), rootNode.getComment().split("\\R", 2)[0]));
        //response.setCallerLst(Arrays.asList(callerTreeGraph.toString()));
        response.setNodeStructure(rootJson);
        return response;

    }


    private void setJson(List<Node<TblMetaData>> subNode, TreantRoot rootJson){
        for(Node<TblMetaData> ssNode : subNode){
            TreantRoot cRoot = new TreantRoot();
            TreantNode cNode = new TreantNode();
            cNode.setName(getSearchPath(ssNode.getData().getMethod()));
            // get first line
            cNode.setDesc(ssNode.getData().getComment().split("\\R", 2)[0]);
//            try {
//                cNode.setDesc(URLEncoder.encode(ssNode.getData().getComment().split("\\R", 2)[0], "utf-8"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
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


    private boolean isLooping(Node<TblMetaData> childNode, TblMetaData tbl) {
        List<Long> pIds = new ArrayList<>();
        Node<TblMetaData> currNode = childNode;
        while (currNode != null) {
            pIds.add(currNode.getData().getId());
            currNode = currNode.getParent();
        }
        log.info("Current childNodeId: {}", childNode.getData().getId());
        log.info("Current addedNode: {}, Pid: {}, isContain? {}", tbl.getId(), pIds.toString(), pIds.contains(tbl.getId()));
        //tbl.setComment(tbl.getComment() + "#" + tbl.getId() + "#" + pIds.toString() + "#" + pIds.contains(tbl.getId()));
        if (pIds.contains(tbl.getId())) {
            return true;
        }
        return false;
    }
}

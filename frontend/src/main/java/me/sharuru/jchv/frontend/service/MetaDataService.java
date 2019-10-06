package me.sharuru.jchv.frontend.service;

import lombok.extern.slf4j.Slf4j;
import me.sharuru.jchv.frontend.entity.TblMetaData;
import me.sharuru.jchv.frontend.model.Node;
import me.sharuru.jchv.frontend.model.SearchResponse;
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

        List<TblMetaData> selfMetaLst = metaDataRepository.findSelfByPath(getSearchPath(searchPath));
        if (selfMetaLst.size() > 1) {
            throw new RuntimeException("Find more than one root node.");
        } else if (selfMetaLst.isEmpty()) {
            throw new RuntimeException("Root node not found.");
        }
        TblMetaData rootNode = selfMetaLst.get(0);

        Node<TblMetaData> callerTreeGraph = new Node<>(rootNode);
        metaDataRepository.findCallerByPath(getSearchPath(rootNode.getMethod())).forEach(childNode -> callerTreeGraph.addChild(new Node<>(childNode)));

        visitChildNode(callerTreeGraph.getChildren());




        // TODO
        response.setCalleeLst(Arrays.asList(rootNode.getContext(), rootNode.getComment()));
        response.setCallerLst(Arrays.asList(callerTreeGraph.toString()));

        return response;

    }

    private String getSearchPath(String rawInput) {
        return rawInput.substring(rawInput.lastIndexOf("/") + 1);
    }

    private void visitChildNode(List<Node<TblMetaData>> parentNodeLst) {
        for (Node<TblMetaData> childNode : parentNodeLst) {
            for (TblMetaData tblMetaData : metaDataRepository.findCallerByPath(getSearchPath(childNode.getData().getMethod()))) {
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
        tbl.setComment(tbl.getComment() + "#" + tbl.getId() + "#" + pIds.toString() + "#" + pIds.contains(tbl.getId()));
        if (pIds.contains(tbl.getId())) {
            return true;
        }
        return false;
    }
}

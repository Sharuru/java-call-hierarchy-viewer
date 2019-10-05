package me.sharuru.jchv.frontend.service;

import lombok.extern.slf4j.Slf4j;
import me.sharuru.jchv.frontend.entity.TblMetaData;
import me.sharuru.jchv.frontend.model.Node;
import me.sharuru.jchv.frontend.model.SearchResponse;
import me.sharuru.jchv.frontend.repository.MetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if(selfMetaLst.size() > 1){
            throw new RuntimeException("Find more than one root node.");
        }else if(selfMetaLst.isEmpty()){
            throw new RuntimeException("Root node not found.");
        }
        TblMetaData rootNode = selfMetaLst.get(0);

        Node<TblMetaData> callerTreeGraph = new Node<>(rootNode);
        metaDataRepository.findCallerByPath(getSearchPath(rootNode.getMethod())).forEach(childNode -> callerTreeGraph.addChild(new Node<>(childNode)));

        Node<TblMetaData> currentVisitingNode = callerTreeGraph;

        for (int i = 1; i <= 25; i++) {
            for (Node<TblMetaData> node : currentVisitingNode.getChildren()) {
                metaDataRepository.findCallerByPath(getSearchPath(node.getData().getMethod())).forEach(childNode -> node.addChild(new Node<>(childNode)));
                currentVisitingNode = node;
            }
            if (currentVisitingNode.getChildren().isEmpty()) {
                break;
            }else if(i == 25){
                TblMetaData overLimitData = new TblMetaData();
                overLimitData.setMethod("Warn: Over 25 layers found, terminated.");
                overLimitData.setContext("Warn: Over 25 layers found, terminated.");
                overLimitData.setComment("Warn: Over 25 layers found, terminated.");
                currentVisitingNode.addChild(new Node<>(overLimitData));
            }
        }

        // TODO
        response.setCalleeLst(Arrays.asList(rootNode.getContext(), rootNode.getComment()));
        response.setCallerLst(Arrays.asList(callerTreeGraph.toString()));

        return response;

    }

    private String getSearchPath(String rawInput) {
        return rawInput.substring(rawInput.lastIndexOf("/") + 1);
    }
}

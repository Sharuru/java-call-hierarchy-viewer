package me.sharuru.jchv.frontend.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResponse {

    List<String> callerLst = new ArrayList<>(0);
    List<String> calleeLst = new ArrayList<>(0);

    TreantRoot nodeStructure = new TreantRoot();



}

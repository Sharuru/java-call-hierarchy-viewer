package me.sharuru.jchv.frontend.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import lombok.Data;
import lombok.ToString;

/**
 * API response model from backend
 */
@Data
@ToString
public class BusinessApiResponse implements Serializable {

    /**
     * Business status
     */
    private String bizStatus = "INOP";

    /**
     * Business status code
     */
    private Long bizCode = 65535L;

    /**
     * Method complexity index map
     */
    private Map<String, Integer> methodIndexMap = new HashMap<>();

    /**
     * Method Path map
     */
    private LinkedList<String> methodPathList = new LinkedList<>();

    /**
     * Tree graph data
     */
    private TreeGraphNode treeGraphData = new TreeGraphNode();

    /**
     * Edge nodes' qualified name list
     */
    private LinkedList<String> edgeNodeQualifiedName = new LinkedList<>();

    /**
     * Not found callers' qualified name list
     */
    private LinkedList<String> notFoundCallerQualifiedName = new LinkedList<>();

    /**
     * Fuzzy qualified name
     */
    private String fuzzyQualifiedName = "<-";
}

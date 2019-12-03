package me.sharuru.jchv.frontend.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
     * Tree graph data
     */
    private TreeGraphNode treeGraphData = new TreeGraphNode();
}

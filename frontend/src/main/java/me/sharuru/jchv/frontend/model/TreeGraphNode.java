package me.sharuru.jchv.frontend.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Tree graph node
 */
@Data
@ToString
public class TreeGraphNode implements Serializable {

    private Long id;
    private String methodQualifiedName;
    private String methodPath;
    private String methodType;
    private String methodComment;

    private String uiMethodContext = "";
    private String uiMethodPath;
    private String uiMethodComment;
    private String uiMethodCommentSummary;

    private LinkedList<TreeGraphNode> children = new LinkedList<>();

    public String getUiMethodContext() {
        if (this.methodQualifiedName == null || this.methodQualifiedName.isEmpty()) {
            return "";
        }
        String rawQualifiedName = this.methodQualifiedName.substring(0, this.methodQualifiedName.lastIndexOf('('));
        String classWithMethod = rawQualifiedName.substring(rawQualifiedName.lastIndexOf('.', rawQualifiedName.lastIndexOf('.') - 1) + 1);
        String className = classWithMethod.split("\\.")[0];
        String methodName = classWithMethod.split("\\.")[1];
        int paramCount = this.methodQualifiedName.contains("()") ? 0 : this.methodQualifiedName.split(",").length;

        className = className.length() >= 32 ? className.substring(0, 30).concat("...") : className;
        methodName = methodName.length() >= 32 ? methodName.substring(0, 30).concat("...") : methodName;

        this.uiMethodPath = classWithMethod.replace('.', '#');

        return className + "\n" + methodName + "\n" + paramCount + " Params";
    }

    public String getUiMethodCommentSummary() {
        if (this.methodComment == null || this.methodComment.isEmpty()) {
            return "No comment";
        } else {
            String noLbComment = this.methodComment.replaceAll("(\r\n|\n)", " ");
            return noLbComment.length() >= 20 ? noLbComment.substring(0, 16).concat("...") : noLbComment;
        }
    }

    public String getUiMethodComment() {
        if (this.methodComment == null || this.methodComment.isEmpty()) {
            return "No comment";
        } else {
            if (this.methodComment.length() >= 300) {
                return this.methodComment.substring(0, 260).concat("\n\n Following comment is truncated by server.");
            } else {
                return this.methodComment;
            }
        }
    }
}

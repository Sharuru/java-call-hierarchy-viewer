package me.sharuru.jchv.frontend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Business working table
 * <p>
 * Index on name columns are recommended for performance tuning.
 */
@Data
@Entity
@Table(name = "meta_data")
@NoArgsConstructor
public class TblMetaData {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @SequenceGenerator(name = "seq")
    private Long id;

    /**
     * Method parent's qualified name
     */
    @Column(name = "method_qualified_name")
    private String methodQualifiedName;

    /**
     * Method self method path(for compatible with Excel tools)
     */
    @Column(name = "method_path")
    private String methodPath;

    /**
     * Method self comment
     */
    @Column(name = "method_comment")
    private String methodComment;

    /**
     * Method self type: BASE, BIN, SRC, LOOP-SRC
     */
    @Column(name = "method_type")
    private String methodType;

    /**
     * Method self qualified name(callees' for BASE type)
     */
    @Column(name = "method_callee_qualified_name")
    private String methodCalleeQualifiedName;

    /**
     * Method calling sequence
     */
    @Column(name = "method_callee_seq")
    private Long methodCalleeSeq;

    /**
     * Method self class name(used for autocomplete)
     */
    @Column(name = "method_callee_class")
    private String methodCalleeClass;

    /**
     * Method self method name(used for autocomplete)
     */
    @Column(name = "method_callee_method")
    private String methodCalleeMethod;
}

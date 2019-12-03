package me.sharuru.jchv.frontend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Business working table
 * <p>
 * Index on name columns are recommended for performance tuning.
 * <p>
 * This is v3 version and v4 is under reviewing.
 * v5 is also on the way :)
 * 2019/12/01
 */
@Data
@Entity
@Table(name = "meta_proto_gamma")
@NoArgsConstructor
public class TblMetaContent {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = "meta_proto_gamma_id_seq")
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
}

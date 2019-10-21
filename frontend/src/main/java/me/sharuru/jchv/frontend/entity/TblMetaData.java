package me.sharuru.jchv.frontend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "meta_clean")
@NoArgsConstructor
public class TblMetaData {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = "meta_data_id_seq")
    private Long id;

    private String method;

    private String context;

    private String comment;

}

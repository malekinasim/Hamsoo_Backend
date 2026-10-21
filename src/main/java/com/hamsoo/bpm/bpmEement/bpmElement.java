package com.hamsoo.bpm.bpmEement;

import com.hamsoo.bpm.base.BaseEntity;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="ENTERPRISEMAPLANES")
@Data
public class bpmElement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID" , nullable = false,precision = 0)
    private long id;

}

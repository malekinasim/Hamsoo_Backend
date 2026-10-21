package com.hamsoo.bpm.proceessareaprocess;

import com.hamsoo.bpm.process.Process;
import com.hamsoo.bpm.processarea.ProcessArea;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="PROCESSES")
@Data
public class ProcessAreaProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID" , nullable = false,precision = 0)
    private long id;

    @ManyToOne
    @JoinColumn( name="PROCESS_AREA_ID",referencedColumnName = "ID",nullable = false)
    private ProcessArea processArea;

    @ManyToOne
    @JoinColumn( name="Process_ID",referencedColumnName = "ID",nullable = false)
    private Process process;
}

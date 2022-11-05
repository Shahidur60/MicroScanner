package com.msscanner.msscanner.model;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "risk_score")
    private Double risk_score;

    @Column(name = "timeTaken")
    private Long timeTaken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

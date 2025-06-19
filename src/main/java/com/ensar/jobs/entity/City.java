package com.ensar.jobs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class City extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "rankn")
    private Integer rankn;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 100)
    private String state;
    
    @Column(length = 100)
    private String country;
    
    private Integer population;
    
    private Double growth;
} 
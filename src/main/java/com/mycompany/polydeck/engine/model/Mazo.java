/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.polydeck.engine.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Mazo {

    @Id 
    @GeneratedValue
    private Long id;
    
    private String nom;
    private LocalDate dataCreacio;
    
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Carta> cartes = new ArrayList<>();
    
    public Mazo() {}

    public Mazo(String nom, LocalDate dataCreacio) {
        this.nom = nom;
        this.dataCreacio = dataCreacio;
    }

}

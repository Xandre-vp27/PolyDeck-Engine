/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.polydeck.engine.model;

import com.polydeck.model.Raresa;
import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 *
 * @author alumnet
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Carta implements Serializable {
    @Id
    @GeneratedValue
    Long id;
    
    String nom;
    
    String descripcio;
    
    Raresa raresa;
    
    String edicio;
    
    @Embedded
    CostMana cost;
    
    public Carta() {}

    public Carta(String nom, String descripcio, String edicio, Raresa raresa, CostMana cost) {
        this.nom = nom;
        this.descripcio = descripcio;
        this.edicio = edicio;
        this.raresa = raresa;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public Raresa getRaresa() {
        return raresa;
    }

    public String getEdicio() {
        return edicio;
    }

    public CostMana getCost() {
        return cost;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public void setRaresa(Raresa raresa) {
        this.raresa = raresa;
    }

    public void setEdicio(String edicio) {
        this.edicio = edicio;
    }

    public void setCost(CostMana cost) {
        this.cost = cost;
    }
    
    
}

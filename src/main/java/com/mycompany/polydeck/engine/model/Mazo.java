package com.mycompany.polydeck.engine.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Mazo {

    @Id 
    @GeneratedValue
    private Long id;
    
    private String nom;
    
    @Temporal(TemporalType.DATE) 
    private Date dataCreacio; 
    
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Carta> cartes = new ArrayList<>();
    
    public Mazo() {}

    // Canviar també el constructor
    public Mazo(String nom, Date dataCreacio) {
        this.nom = nom;
        this.dataCreacio = dataCreacio;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDataCreacio() {
        return dataCreacio;
    }

    public void setDataCreacio(Date dataCreacio) {
        this.dataCreacio = dataCreacio;
    }

    public List<Carta> getCartes() {
        return cartes;
    }

    public void setCartes(List<Carta> cartes) {
        this.cartes = cartes;
    }
    
    public void afegirCarta(Carta carta) {
        this.cartes.add(carta);
    }

}

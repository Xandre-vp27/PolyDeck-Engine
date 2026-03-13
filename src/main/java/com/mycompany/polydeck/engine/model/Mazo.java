package com.mycompany.polydeck.engine.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Mazo {

    @Id 
    @GeneratedValue
    private Long id;
    
    private String nom;
    
    @Temporal(TemporalType.DATE) // Afegir aquesta anotació
    private Date dataCreacio; // Canviar a Date
    
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Carta> cartes = new ArrayList<>();
    
    public Mazo() {}

    // Canviar també el constructor
    public Mazo(String nom, Date dataCreacio) {
        this.nom = nom;
        this.dataCreacio = dataCreacio;
    }
    
    public void afegirCarta(Carta carta) {
        this.cartes.add(carta);
    }

    public List<Carta> getCartes() {
        return cartes;
    }

    public String getNom() { return nom; }
}
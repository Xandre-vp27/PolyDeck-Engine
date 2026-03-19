package com.mycompany.polydeck.engine.model;

import com.polydeck.model.Raresa;
import javax.persistence.Entity;

@Entity
public class Criatura extends Carta {
    int forca;
    
    int resistencia;
    
    String tipusCarta;
    
    boolean volar;
    
    public Criatura() {}

    // Constructor completo
    public Criatura(String nom, String descripcio, String edicio, Raresa raresa, CostMana cost, 
                    int forca, int resistencia, String tipusCarta, boolean volar) {
        super(nom, descripcio, edicio, raresa, cost); 
        this.forca = forca;
        this.resistencia = resistencia;
        this.tipusCarta = tipusCarta;
        this.volar = volar;
    }
}
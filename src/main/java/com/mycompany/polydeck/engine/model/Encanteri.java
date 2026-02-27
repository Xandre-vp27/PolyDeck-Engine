/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.polydeck.engine.model;

import com.polydeck.model.Raresa;
import javax.persistence.Entity;

@Entity
public class Encanteri extends Carta {
    String tipus;
    boolean esInstantani;

    public Encanteri() {
    }

    public Encanteri(String nom, String descripcio, String edicio, Raresa raresa, CostMana cost, String tipus, boolean esInstantani) {
        super(nom, descripcio, edicio, raresa, cost);
        this.tipus = tipus;
        this.esInstantani = esInstantani;
    }
    
    
}

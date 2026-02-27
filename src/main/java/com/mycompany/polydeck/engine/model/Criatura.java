/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.polydeck.engine.model;

import com.polydeck.model.Raresa;
import javax.persistence.Entity;

@Entity
public class Criatura extends Carta {
    int força;
    
    int resistencia;
    
    String tipusCarta;
    
    boolean volar;
    
    public Criatura() {}

    // Constructor completo
    public Criatura(String nom, String descripcio, String edicio, Raresa raresa, CostMana cost, 
                    int forca, int resistencia, String tipusCarta, boolean volar) {
        super(nom, descripcio, edicio, raresa, cost); 
        this.força = forca;
        this.resistencia = resistencia;
        this.tipusCarta = tipusCarta;
        this.volar = volar;
    }
}

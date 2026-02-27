/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.polydeck.engine.model;

import com.polydeck.model.Raresa;
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
public abstract class Carta {
    @Id
    @GeneratedValue
    Long id;
    
    String nom;
    
    String descripcio;
    
    Raresa raresa;
    
    String edicio;
    
    @Embedded
    CostMana cost;
}

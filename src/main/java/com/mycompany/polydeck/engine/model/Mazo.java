/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.polydeck.engine.model;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.ManyToMany;

/**
 *
 * @author alumnet
 */

public class Mazo {
    Long id;
    String nom;
    LocalDate dataCreacio;
    
    @ManyToMany
    List<Carta> cartes;
    
    // Falta poner configuracion de CascadeType... preguntar a la IA
    
}

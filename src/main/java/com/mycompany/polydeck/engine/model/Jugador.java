/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.polydeck.engine.model;

import java.util.List;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 *
 * @author alumnet
 */
public class Jugador {
    Long id;
    String nick;
    int nivell;
    
    @OneToMany
    List<Mazo> mazos;
    
    // Cascade Type.ALL falta por añadir
    
    @ManyToMany
    List<Carta> coleccion;
}

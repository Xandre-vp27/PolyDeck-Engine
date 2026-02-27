/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.polydeck.engine.model;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
public class Jugador {

    @Id 
    @GeneratedValue
    private Long id;
    
    private String nick;
    private int nivell;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mazo> mazos = new ArrayList<>();
    
    @ManyToMany
    private List<Carta> coleccion = new ArrayList<>();

    public Jugador() {}

    public Jugador(String nick, int nivell) {
        this.nick = nick;
        this.nivell = nivell;
    }

}

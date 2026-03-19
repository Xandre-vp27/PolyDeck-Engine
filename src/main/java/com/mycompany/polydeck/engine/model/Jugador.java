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
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public int getNivell() {
        return nivell;
    }

    public void setNivell(int nivell) {
        this.nivell = nivell;
    }

    public List<Mazo> getMazos() {
        return mazos;
    }

    public void setMazos(List<Mazo> mazos) {
        this.mazos = mazos;
    }

    public List<Carta> getColeccion() {
        return coleccion;
    }

    public void setColeccion(List<Carta> coleccion) {
        this.coleccion = coleccion;
    }

    public void afegirMazo(Mazo mazo) {
        this.mazos.add(mazo);
    }


}

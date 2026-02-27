/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.polydeck.engine.model;

import javax.persistence.Embeddable;

/**
 *
 * @author alumnet
 */

@Embeddable
public class CostMana {
    int blanc;
    int blau;
    int negre;
    int vermell;
    int verd;
    int incolor;
    
    public CostMana() {}

    public CostMana(int blanc, int blau, int negre, int vermell, int verd, int incolor) {
        this.blanc = blanc;
        this.blau = blau;
        this.negre = negre;
        this.vermell = vermell;
        this.verd = verd;
        this.incolor = incolor;
    }

    public int getBlanc() {
        return blanc;
    }

    public int getBlau() {
        return blau;
    }

    public int getNegre() {
        return negre;
    }

    public int getVermell() {
        return vermell;
    }

    public int getVerd() {
        return verd;
    }

    public int getIncolor() {
        return incolor;
    }

    public void setBlanc(int blanc) {
        this.blanc = blanc;
    }

    public void setBlau(int blau) {
        this.blau = blau;
    }

    public void setNegre(int negre) {
        this.negre = negre;
    }

    public void setVermell(int vermell) {
        this.vermell = vermell;
    }

    public void setVerd(int verd) {
        this.verd = verd;
    }

    public void setIncolor(int incolor) {
        this.incolor = incolor;
    }
    
    
}

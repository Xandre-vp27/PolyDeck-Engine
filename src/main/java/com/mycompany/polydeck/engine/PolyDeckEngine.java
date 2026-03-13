/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.polydeck.engine;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.mycompany.polydeck.engine.model.Carta;
import com.mycompany.polydeck.engine.service.ImportadorCartes;

/**
 *
 * @author alumnet
 */
public class PolyDeckEngine {
    
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("db/polydeck.odb");
    static EntityManager em = emf.createEntityManager();
    
    public static void main(String[] args) {

        System.out.println("Iniciant Poly-Deck Engine...");

        

        try {
            long totalCartes = 0;

            // Intentamos contar las cartas. Si la BD es nueva, lanzará una excepción porque no conoce "Carta"
            try {
                totalCartes = em.createQuery("SELECT COUNT(c) FROM Carta c", Long.class).getSingleResult();
            } catch (Exception ex) {
                System.out.println("-> Esquema verge detectat. La base de dades no conté dades.");
            }

            // Importación de cartas 
            if (totalCartes == 0) {
                System.out.println("-> Important cartes des de 'cartes.txt'...");
                ImportadorCartes.importar("cartes.txt", em);
            } else {
                System.out.println("-> Base de dades carregada. Total cartes: " + totalCartes);
            }

            // Listado polimórfico 
            System.out.println("\n--- LLISTAT DE CARTES (Polimorfisme) ---");
            TypedQuery<Carta> query = em.createQuery("SELECT c FROM Carta c", Carta.class);
            List<Carta> llistaCartes = query.getResultList();

            for (Carta c : llistaCartes) {
                System.out.println("- "+ c.getId() +" [" + c.getClass().getSimpleName() + "] " + c.getNom());
            }
            
            comprovarGarantiaIdentitat();

        } catch (Exception e) {
            System.err.println("Error crític: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
            System.out.println("\nConnexió tancada correctament.");
        }
    }

    public static void comprovarGarantiaIdentitat() {
        System.out.println("\n--- PROVA DE GARANTIA D'IDENTITAT (Cache L1) ---");

        // Buscamos la misma carta dos veces
        Carta c1 = CartaDAO.buscarPerId(em, 1L);
        Carta c2 = CartaDAO.buscarPerId(em, 1L);

        if (c1 != null && c2 != null) {
            System.out.println("Primera cerca: " + c1.getNom());
            System.out.println("Segona cerca: " + c2.getNom());

            // Comprovamos la garantía de identidad 
            if (c1 == c2) {
                System.out.println("RESULTAT: c1 == c2 és VERTADER.");
                System.out.println("Explicació: L'EntityManager retorna exactament la mateixa instància en memòria.");
            } else {
                System.out.println("RESULTAT: c1 == c2 és FALS (Error en la configuració de la Cache L1).");
            }
        }
    }
}

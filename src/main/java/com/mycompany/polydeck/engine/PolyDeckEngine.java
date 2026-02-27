/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.polydeck.engine;

import com.mycompany.polydeck.engine.model.Carta;
import com.mycompany.polydeck.engine.service.ImportadorCartes;
import java.io.File;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author alumnet
 */
public class PolyDeckEngine {

    public static void main(String[] args) {
        
        System.out.println("Iniciant Poly-Deck Engine...");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("db/polydeck.odb");
        EntityManager em = emf.createEntityManager();

        try {
            long totalCartes = 0;
            
            // Intentamos contar las cartas. Si la BD es nueva, lanzará una excepción porque no conoce "Carta"
            try {
                totalCartes = em.createQuery("SELECT COUNT(c) FROM Carta c", Long.class).getSingleResult();
            } catch (Exception ex) {
                System.out.println("-> Esquema verge detectat. La base de dades no conté dades.");
            }

            // Lógica de importación segura
            if (totalCartes == 0) {
                System.out.println("-> Important cartes des de 'cartes.txt'...");
                ImportadorCartes.importar("cartes.txt", em);
            } else {
                System.out.println("-> Base de dades carregada. Total cartes: " + totalCartes);
            }

            // Listado polimórfico [cite: 147, 150-151]
            System.out.println("\n--- LLISTAT DE CARTES (Polimorfisme) ---");
            TypedQuery<Carta> query = em.createQuery("SELECT c FROM Carta c", Carta.class);
            List<Carta> llistaCartes = query.getResultList();

            for (Carta c : llistaCartes) {
                System.out.println("- [" + c.getClass().getSimpleName() + "] " + c.getNom());
            }

        } catch (Exception e) {
            System.err.println("Error crític: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
            if (emf != null) emf.close();
            System.out.println("\nConnexió tancada correctament.");
        }
    }
}

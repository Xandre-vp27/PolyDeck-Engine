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
import com.mycompany.polydeck.engine.model.Jugador;
import com.mycompany.polydeck.engine.model.Mazo;
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

            // Llamamos al método para crear los datos de pruebaw
            JugadorDAO.crearJugadorsIMazosDeProva(em);

            // Listado polimórfico 
            System.out.println("\n--- LLISTAT DE CARTES (Polimorfisme) ---");
            TypedQuery<Carta> query = em.createQuery("SELECT c FROM Carta c", Carta.class);
            List<Carta> llistaCartes = query.getResultList();

            for (Carta c : llistaCartes) {
                System.out.println("- "+ c.getId() +" [" + c.getClass().getSimpleName() + "] " + c.getNom());
            }
            
            // --- AFEGEIX AIXÒ ABANS DE TANCAR LA CONNEXIÓ ---
            TypedQuery<Jugador> queryJugadors = em.createQuery("SELECT j FROM Jugador j", Jugador.class);
            List<Jugador> jugadors = queryJugadors.getResultList();
            
            for (Jugador j : jugadors) {
                System.out.println("Trobat Jugador: " + j.getNick());
                for (Mazo m : j.getMazos()) {
                    System.out.println("  -> Té el mazo: " + m.getNom() + " amb " + m.getCartes().size() + " cartes a dins.");
                }
            }
            comprovarGarantiaIdentitat();
            ejecutarTascaA_DirtyChecking();
            ejecutarTascaB_Merge();



        } catch (Exception e) {
            System.err.println("Error crític: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) em.close();
            if (emf != null && emf.isOpen()) emf.close();
            System.out.println("\nConnexió tancada.");
        }
    }



    public static void ejecutarTascaA_DirtyChecking() {
        System.out.println("\n--- TASCA A: Iniciant Dirty Checking (Managed) ---");
        em.getTransaction().begin();
        
        // L'objecte passa a estat MANAGED al ser trobat per l'em actual
        Carta cartaManaged = em.find(Carta.class, 1L); 
        
        if (cartaManaged != null) {
            cartaManaged.setDescripcio("Modificat per Dirty Checking " + java.time.LocalTime.now());
            // El commit sincronitza automàticament els canvis
            em.getTransaction().commit(); 
            System.out.println("-> OK: Canvi guardat automàticament pel Dirty Checking.");
        } else {
            em.getTransaction().rollback();
            System.out.println("-> Error: No s'ha trobat la carta 1L.");
        }
    }


    public static void ejecutarTascaB_Merge() {
        System.out.println("\n--- TASCA B: Iniciant Merge (Detached) ---");
        
        // 1. Recuperar i tancar l'EntityManager actual per forçar estat DETACHED
        Carta cartaDetached = em.find(Carta.class, 2L); 
        if (em.isOpen()) em.close(); 
        
        if (cartaDetached != null) {
            // 2. Modificar l'objecte mentre està "desconectat"
            cartaDetached.setNom("Nom Modificat en Detached");

            // 3. Obrir un nou EntityManager per fer el merge
            EntityManager em2 = emf.createEntityManager();
            em2.getTransaction().begin();
            
            // Sincronitzem l'objecte detached amb la base de dades
            em2.merge(cartaDetached); 
            
            em2.getTransaction().commit();
            em2.close();
            System.out.println("-> OK: Canvis sincronitzats correctament amb merge().");
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

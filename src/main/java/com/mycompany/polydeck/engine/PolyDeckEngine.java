package com.mycompany.polydeck.engine;

import com.mycompany.polydeck.engine.model.*;
import com.mycompany.polydeck.engine.service.ImportadorCartes;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;

public class PolyDeckEngine {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("db/polydeck.odb");
        EntityManager em = emf.createEntityManager();

        try {
            // --- 1. PREPARACIÓ I IMPORTACIÓ --- 
            // Si el método falla, el catch llamará al importador
            inicialitzarDades(em);

            // --- 2. CERCA PER ID I GARANTIA D'IDENTITAT --- 
            executarProvaIdentitat(em);

            // --- 3. CREACIÓ DE JUGADOR I MAZO (Deep Path Setup) --- 
            crearJugadorExemple(em);

            // Llamamos al método para crear los datos de prueba
            JugadorDAO.crearJugadorsIMazosDeProva(em);

            // --- 4. CONSULTES JPQL AVANÇADES --- 
            ejecutarConsultes(em);

            // --- 5. CICLE DE VIDA: DIRTY CHECKING --- 
            provarDirtyChecking(em);

            // --- 6. CICLE DE VIDA: MERGE (DETACHED) --- 
            provarMerge(emf);

            // --- 7. ELIMINACIÓ: ORPHAN REMOVAL --- 
            provarOrphanRemoval(em);

        } catch (Exception e) {
            System.err.println("Error detectat, intentant importar dades: " + e.getMessage());
            ImportadorCartes.importar("cartes.txt", em);
        } finally {
            if (em.isOpen()) em.close();
            if (emf.isOpen()) emf.close();
            System.out.println("\n>>> Motor aturat i recursos alliberats.");
        }
    }

    // --- Mètodes auxiliars (es mantenen igual) ---

    private static void inicialitzarDades(EntityManager em) {
        // Implementación según tu lógica (podría estar vacía o llamar a otro service)
    }

    private static void executarProvaIdentitat(EntityManager em) {
        System.out.println("\n[TASCA A] Garantia d'Identitat:");
        Carta c1 = em.find(Carta.class, 1L);
        Carta c2 = em.find(Carta.class, 1L);
        System.out.println("Identitat (c1 == c2): " + (c1 == c2)); // Ha de ser true per la Cache L1 
    }

    private static void crearJugadorExemple(EntityManager em) {
        System.out.println("\n-> Creant jugador i mazo de prova...");
        try {
            em.getTransaction().begin();
            Jugador j = new Jugador("AlexDAM", 5);
            Mazo m = new Mazo("Mazo Foc", LocalDate.now());
            
            List<Carta> criatures = em.createQuery("SELECT c FROM Criatura c", Carta.class).setMaxResults(2).getResultList();
            m.getCartes().addAll(criatures);
            
            j.getMazos().add(m);
            em.persist(j); 
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        }
    }

    private static void ejecutarConsultes(EntityManager em) {
        System.out.println("\n[TASCA B] Consultes JPQL:");
        
        System.out.println("- Criatures voladores (Cost Negre > 1):");
        ConsultesDAO.buscarCriaturesVoladoresNegres(em, 1).forEach(c -> System.out.println("  * " + c.getNom()));

        Double mitjana = ConsultesDAO.mitjanaForçaJugador(em, "AlexDAM");
        System.out.printf("- Mitjana força AlexDAM (Deep Path): %.2f\n", mitjana);

        System.out.println("- Encanteris (Incolor > 1, sense Blau/Blanc):");
        ConsultesDAO.buscarEncanterisSenseBlauBlanc(em, 1).forEach(e -> System.out.println("  * " + e.getNom()));
    }

    private static void provarDirtyChecking(EntityManager em) {
        System.out.println("\n[CICLE VIDA] Prova Dirty Checking:");
        em.getTransaction().begin();
        Carta c = em.find(Carta.class, 1L);
        if (c != null) {
            c.setDescripcio("DESCRIPCIÓ MODIFICADA PER DIRTY CHECKING"); 
        }
        em.getTransaction().commit(); 
        System.out.println("Fet: Canvi guardat automàticament per estar en estat Managed.");
    }

    private static void provarMerge(EntityManagerFactory emf) {
        System.out.println("\n[CICLE VIDA] Prova Merge (Detached):");
        EntityManager em1 = emf.createEntityManager();
        Carta c = em1.find(Carta.class, 2L);
        em1.close(); 

        if (c != null) {
            c.setNom("Nom Modificat en Detached");
            EntityManager em2 = emf.createEntityManager();
            em2.getTransaction().begin();
            em2.merge(c); 
            em2.getTransaction().commit();
            em2.close();
            System.out.println("Fet: Objecte Detached sincronitzat amb merge().");
        }
    }

    private static void provarOrphanRemoval(EntityManager em) {
        System.out.println("\n[DELETE] Prova Orphan Removal:");
        try {
            em.getTransaction().begin();
            Jugador j = em.createQuery("SELECT j FROM Jugador j WHERE j.nick = 'AlexDAM'", Jugador.class).getSingleResult();
            
            if (j != null && !j.getMazos().isEmpty()) {
                j.getMazos().remove(0); 
                System.out.println("Fet: Mazo eliminat de la BD en treure'l de la llista del Jugador.");
            }
            em.getTransaction().commit();
        } catch (NoResultException e) {
            System.out.println("No s'ha trobat el jugador AlexDAM.");
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        }
    }
}
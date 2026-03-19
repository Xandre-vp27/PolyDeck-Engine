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
            if (em.isOpen()) {
                em.close();
            }
            if (emf.isOpen()) {
                emf.close();
            }
            System.out.println("\n>>> Motor aturat i recursos alliberats.");
        }
    }

    // --- Mètodes auxiliars (es mantenen igual) ---
    private static void inicialitzarDades(EntityManager em) {
        try {
            // Ús de la ruta completa per evitar que ObjectDB es perdi
            String jpql = "SELECT COUNT(c) FROM com.mycompany.polydeck.engine.model.Carta c";
            long total = em.createQuery(jpql, Long.class).getSingleResult();

            if (total == 0) {
                System.out.println("-> Base de dades buida. Important cartes...");
                ImportadorCartes.importar("cartes.txt", em);
            }
        } catch (Exception e) {
            System.out.println("-> Esquema verge detectat. Important cartes inicials...");
            ImportadorCartes.importar("cartes.txt", em);
        }
    }

    private static void executarProvaIdentitat(EntityManager em) {
        System.out.println("\n[TASCA A] Garantia d'Identitat:");
        // Obtenim el primer ID vàlid que existeixi a la BD
        String jpql = "SELECT c.id FROM com.mycompany.polydeck.engine.model.Carta c";
        Long idValid = em.createQuery(jpql, Long.class).setMaxResults(1).getSingleResult();

        Carta c1 = em.find(Carta.class, idValid);
        Carta c2 = em.find(Carta.class, idValid);

        System.out.println("Utilitzant ID: " + idValid);
        System.out.println("Identitat (c1 == c2): " + (c1 == c2)); // Ara sí compararà dos objectes reals
    }

    private static void crearJugadorExemple(EntityManager em) {
        System.out.println("\n-> Creant jugador i mazo de prova...");
        try {
            em.getTransaction().begin();
            Jugador j = new Jugador("AlexDAM", 5);
            Mazo m = new Mazo("Mazo Foc", new java.util.Date()); // Recorda mantenir el java.util.Date aquí

            // CANVI: Ús del nom complet del paquet per a Criatura
            String jpql = "SELECT c FROM com.mycompany.polydeck.engine.model.Criatura c";
            List<Carta> criatures = em.createQuery(jpql, Carta.class).setMaxResults(2).getResultList();

            m.getCartes().addAll(criatures);
            j.getMazos().add(m);

            em.persist(j);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error creant el jugador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ejecutarConsultes(EntityManager em) {
        System.out.println("\n[TASCA B] Consultes JPQL:");

        // B1: Polimòrfica
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

        String jpql = "SELECT c.id FROM com.mycompany.polydeck.engine.model.Carta c";
        Long idValid = em.createQuery(jpql, Long.class).setMaxResults(1).getSingleResult();
        Carta c = em.find(Carta.class, idValid);

        c.setDescripcio("DESCRIPCIÓ MODIFICADA PER DIRTY CHECKING");

        em.getTransaction().commit();
        System.out.println("Fet: Canvi guardat automàticament a la carta amb ID " + idValid);
    }

    private static void provarMerge(EntityManagerFactory emf) {
        System.out.println("\n[CICLE VIDA] Prova Merge (Detached):");
        EntityManager em1 = emf.createEntityManager();
        
        String jpql = "SELECT c.id FROM com.mycompany.polydeck.engine.model.Carta c";
        Long idValid = em1.createQuery(jpql, Long.class).setMaxResults(1).getSingleResult();
        Carta c = em1.find(Carta.class, idValid);
        em1.close(); // L'objecte c ara és DETACHED 

        c.setNom(c.getNom() + " (Modificat en Detached)");

        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        em2.merge(c); 
        em2.getTransaction().commit();
        em2.close();
        System.out.println("Fet: Objecte Detached (ID " + idValid + ") sincronitzat amb merge().");
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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // En lloc de getSingleResult (que peta si no hi ha res), usem una llista
            TypedQuery<Jugador> q = em.createQuery("SELECT j FROM Jugador j WHERE j.nick = 'AlexDAM'", Jugador.class);
            List<Jugador> resultats = q.getResultList();

            if (resultats.isEmpty()) {
                System.out.println("Error: No s'ha trobat el jugador 'AlexDAM' a la base de dades.");
                return;
            }

            em.getTransaction().begin();
            Jugador j = resultats.get(0);

            if (!j.getMazos().isEmpty()) {
                // L'Orphan Removal s'activa en treure l'objecte de la llista gestionada
                j.getMazos().remove(0);
                System.out.println("Fet: Mazo eliminat de la BD automàticament (Orphan Removal).");
            } else {
                System.out.println("El jugador no té mazos per eliminar.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error en Orphan Removal: " + e.getMessage());
        }
    }
}

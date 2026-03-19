package com.mycompany.polydeck.engine;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.mycompany.polydeck.engine.model.Carta;
import com.mycompany.polydeck.engine.model.Jugador;
import com.mycompany.polydeck.engine.model.Mazo;
import com.mycompany.polydeck.engine.service.ImportadorCartes;

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
            Mazo m = new Mazo("Mazo Foc", new Date());

            List<Carta> criatures = em.createQuery("SELECT c FROM Criatura c", Carta.class).setMaxResults(2).getResultList();

            if (criatures.isEmpty()) {
                System.out.println("Alerta: No s'han trobat criatures per afegir al mazo.");
            }

            m.getCartes().addAll(criatures);
            j.getMazos().add(m);
            em.persist(j); 

            em.persist(j);
            em.getTransaction().commit();
            System.out.println("Fet: Jugador creat correctament.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error creant el jugador: " + e.getMessage());
            e.printStackTrace(); // Això t'ensenyarà la línia exacta de l'error
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
    
    try {
        // 1. Iniciamos la transacción
        em.getTransaction().begin();
        
        // 2. Buscamos la entidad (pasa a estado MANAGED)
        Carta c = em.find(Carta.class, 1L);
        
        if (c != null) {
            // 3. Modificamos el objeto en memoria
            c.setDescripcio("DESCRIPCIÓ MODIFICADA PER DIRTY CHECKING");
            System.out.println("   - Modificant l'entitat '" + c.getNom() + "' en memòria...");
            
            // IMPORTANTE: No hace falta hacer em.persist() ni em.merge() 
            // El motor detecta el cambio automáticamente al hacer commit.
        } else {
            System.out.println("   - Alerta: No s'ha trobat la carta amb ID 1.");
        }

        // 4. Finalizamos la transacción (aquí se sincroniza con la BD)
        em.getTransaction().commit();
        System.out.println("Fet: Canvi guardat automàticament per estar en estat Managed.");
        
    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        System.err.println("Error en Dirty Checking: " + e.getMessage());
        // Re-lanzamos la excepción si queremos que el main la capture, 
        // pero en este caso es mejor controlarla aquí para no romper el flujo.
    }
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
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            // 1. En lugar de getSingleResult, usamos una lista para evitar la excepción
            TypedQuery<Jugador> q = em.createQuery(
                "SELECT j FROM Jugador j WHERE j.nick = 'AlexDAM'", Jugador.class);
            q.setMaxResults(1); // Nos aseguramos de traer solo uno si hay duplicados
            List<Jugador> resultats = q.getResultList();

            if (!resultats.isEmpty()) {
                Jugador j = resultats.get(0);
                
                if (!j.getMazos().isEmpty()) {
                    // 2. Al eliminar de la lista, JPA detecta 'orphanRemoval=true' 
                    // y lanza el DELETE en la base de datos automáticamente.
                    Mazo mazoAEliminar = j.getMazos().get(0);
                    j.getMazos().remove(0); 
                    
                    System.out.println("   - Eliminant el mazo: " + mazoAEliminar.getNom());
                    System.out.println("Fet: Mazo eliminat de la BD per Orphan Removal.");
                } else {
                    System.out.println("   - El jugador 'AlexDAM' no té mazos per eliminar.");
                }
            } else {
                System.out.println("   - No s'ha trobat cap jugador amb el nick 'AlexDAM'.");
            }
            
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Error en Orphan Removal: " + e.getMessage());
        }
    }
}

package org.example;

import org.example.model.Categorie;
import org.example.model.Produit;
import org.h2.tools.Server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

public class App {
    public static void main(String[] args) {
        try {
            Server.createWebServer("-web", "-webPort", "8082").start();
            System.out.println("Console H2 disponible sur : http://localhost:8082");
        } catch (Exception e) {
            e.printStackTrace();
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("TP1");

        insererDonnees(emf);
        lireDonnees(emf);
        mettreAJourEtSupprimer(emf);

        emf.close();
    }

    private static void insererDonnees(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Création des catégories
            Categorie catInformatique = new Categorie("Informatique");
            Categorie catAccessoires = new Categorie("Accessoires");

            // Création des produits
            Produit p1 = new Produit("PC Gamer", new BigDecimal("1200.00"));
            Produit p2 = new Produit("Tablette", new BigDecimal("450.00"));
            Produit p3 = new Produit("Clavier Mécanique", new BigDecimal("80.00"));

            // Association via la méthode de commodité
            catInformatique.addProduit(p1);
            catInformatique.addProduit(p2);
            catAccessoires.addProduit(p3);

            // Grâce au CascadeType.ALL, persister la catégorie persistera aussi ses produits
            em.persist(catInformatique);
            em.persist(catAccessoires);

            em.getTransaction().commit();
            System.out.println("Données insérées avec succès !");
        } finally {
            em.close();
        }
    }

    private static void lireDonnees(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            System.out.println("\n--- Liste des Produits ---");
            List<Produit> produits = em.createQuery("SELECT p FROM Produit p JOIN FETCH p.categorie", Produit.class).getResultList();
            for (Produit p : produits) {
                System.out.println(p);
            }

            System.out.println("\n--- Recherche par Catégorie (JPQL) ---");
            List<Produit> produitsInfo = em.createQuery("SELECT p FROM Produit p WHERE p.categorie.nom = :nomCat", Produit.class)
                    .setParameter("nomCat", "Informatique")
                    .getResultList();

            for (Produit p : produitsInfo) {
                System.out.println("Trouvé en Informatique: " + p.getNom());
            }
        } finally {
            em.close();
        }
    }

    private static void mettreAJourEtSupprimer(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // UPDATE
            Produit p = em.find(Produit.class, 1L);
            if (p != null) {
                p.setPrix(new BigDecimal("1100.00")); // Promotion
                System.out.println("\nProduit mis à jour : " + p);
            }

            // DELETE
            Produit pToDelete = em.find(Produit.class, 3L);
            if (pToDelete != null) {
                em.remove(pToDelete);
                System.out.println("Produit ID 3 supprimé.");
            }

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
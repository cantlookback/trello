package ru.zinakov.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.ServletContext;

public class JpaUtil {

    public static EntityManager getEntityManager(ServletContext context) {
        EntityManagerFactory emf =
                (EntityManagerFactory) context.getAttribute("EMF");
        return emf.createEntityManager();
    }
}
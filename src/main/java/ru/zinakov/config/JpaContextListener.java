package ru.zinakov.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class JpaContextListener implements ServletContextListener {

    private static final String EMF_KEY = "EMF";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("trelloPU");

        ServletContext context = sce.getServletContext();
        context.setAttribute(EMF_KEY, emf);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        EntityManagerFactory emf =
                (EntityManagerFactory) sce.getServletContext()
                        .getAttribute(EMF_KEY);

        if (emf != null) {
            emf.close();
        }
    }
}
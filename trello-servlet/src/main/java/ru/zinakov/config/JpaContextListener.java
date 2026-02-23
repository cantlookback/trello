package ru.zinakov.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import static ru.zinakov.keys.ContextKeys.EMF;

@WebListener
public class JpaContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("trelloPU");

        ServletContext context = sce.getServletContext();
        context.setAttribute(EMF, emf);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        EntityManagerFactory emf =
                (EntityManagerFactory) sce.getServletContext()
                        .getAttribute(EMF);

        if (emf != null) {
            emf.close();
        }
    }
}
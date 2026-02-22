package ru.zinakov;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import ru.zinakov.domain.Board;
import ru.zinakov.domain.BoardColumn;
import ru.zinakov.domain.Card;

public class BoardPersistenceTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    @SuppressWarnings("unused")
    static void init() {
        emf = Persistence.createEntityManagerFactory("trelloPU");
    }

    @AfterAll
    @SuppressWarnings("unused")
    static void close() {
        emf.close();
    }

    @Test
    void shouldCreateBoard() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Board board = new Board("Board A");
            em.persist(board);

            em.getTransaction().commit();
            Long id = board.getId();

            em.clear();

            Board fromDb = em.find(Board.class, id);

            assertNotNull(fromDb);
            assertEquals("Board A", fromDb.getName());
        }
    }

    @Test
    void shouldAddColumnToBoard() {

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Board board = new Board("Board A");
            BoardColumn column = new BoardColumn("To Do");

            board.addColumn(column);

            em.persist(board);
            em.getTransaction().commit();

            Long id = board.getId();

            em.clear();

            em.getTransaction().begin();
            Board fromDb = em.find(Board.class, id);

            assertEquals(1, fromDb.getColumns().size());
            assertEquals("To Do", fromDb.getColumns().get(0).getTitle());

            em.getTransaction().commit();
        }
    }

    @Test
    void shouldAddCardToColumn() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Board board = new Board("Board A");
            BoardColumn column = new BoardColumn("To Do");
            Card card = new Card("Task 1");

            column.addCard(card);
            board.addColumn(column);

            em.persist(board);
            em.getTransaction().commit();

            Long id = board.getId();

            em.clear();

            em.getTransaction().begin();
            Board fromDb = em.find(Board.class, id);

            BoardColumn dbColumn = fromDb.getColumns().get(0);
            assertEquals(1, dbColumn.getCards().size());
            assertEquals("Task 1", dbColumn.getCards().get(0).getTitle());

            em.getTransaction().commit();
        }
    }
}

package ru.zinakov.service;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContext;
import ru.zinakov.domain.Board;
import ru.zinakov.domain.BoardColumn;
import ru.zinakov.domain.Card;
import ru.zinakov.repository.BoardRepository;
import ru.zinakov.util.JpaUtil;

public class BoardService {
    private final BoardRepository repository = new BoardRepository();


    public Long createBoard(ServletContext context, String name) {
        EntityManager em = JpaUtil.getEntityManager(context);

        try {
            em.getTransaction().begin();

            Board board = new Board(name);

            repository.save(em, board);

            em.getTransaction().commit();
            return board.getId();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void addColumn(ServletContext context, Long boardId, String title) {
        EntityManager em = JpaUtil.getEntityManager(context);

        try {
            em.getTransaction().begin();

            Board board = repository.findById(em, boardId);

            BoardColumn column = new BoardColumn(title);
            board.addColumn(column);

            repository.save(em, board);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void addCard(ServletContext context, Long boardId, Long columnId, String title) {
        EntityManager em = JpaUtil.getEntityManager(context);

        try {
            em.getTransaction().begin();

            Board board = repository.findById(em, boardId);

            BoardColumn targetColumn = board
                .getColumns()
                .stream()
                .filter(c -> c.getId().equals(columnId))
                .findFirst()
                .orElseThrow();

            Card card = new Card(title);
            targetColumn.addCard(card);

            repository.save(em, board);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

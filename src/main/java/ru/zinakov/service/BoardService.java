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
        try (EntityManager em = JpaUtil.getEntityManager(context)) {
            em.getTransaction().begin();
            try {
                Board board = new Board(name);

                repository.save(em, board);

                em.getTransaction().commit();
                return board.getId();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    public void addColumn(ServletContext context, Long boardId, String title) {
        try (EntityManager em = JpaUtil.getEntityManager(context)) {
            em.getTransaction().begin();
            try {
                Board board = repository.findById(em, boardId);

                BoardColumn column = new BoardColumn(title);
                board.addColumn(column);

                repository.save(em, board);

                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    public void addCard(ServletContext context, Long boardId, Long columnId, String title) {
        try (EntityManager em = JpaUtil.getEntityManager(context)) {
            em.getTransaction().begin();
            try {
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
            }
        }
    }
}

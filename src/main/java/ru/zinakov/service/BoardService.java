package ru.zinakov.service;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContext;
import ru.zinakov.domain.Board;
import ru.zinakov.domain.BoardColumn;
import ru.zinakov.domain.Card;
import ru.zinakov.exception.BadRequestException;
import ru.zinakov.exception.NotFoundException;
import ru.zinakov.repository.BoardRepository;
import ru.zinakov.util.JpaUtil;
import ru.zinakov.web.dto.BoardResponse;
import ru.zinakov.web.dto.CardResponse;
import ru.zinakov.web.dto.ColumnResponse;

public class BoardService {
    private final BoardRepository repository = new BoardRepository();

    public Long createBoard(ServletContext context, String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Board name cannot be empty");
        }
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
        if (title == null || title.isBlank()) {
            throw new BadRequestException("Board name cannot be empty");
        }
        try (EntityManager em = JpaUtil.getEntityManager(context)) {
            em.getTransaction().begin();
            try {
                Board board = repository.findById(em, boardId);
                if (board == null) {
                    throw new NotFoundException("Board not found");
                }

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

    /*
     * public Board getBoard(ServletContext context, Long id) {
     * try (EntityManager em = JpaUtil.getEntityManager(context)) {
     * 
     * em.getTransaction().begin();
     * 
     * Board board = em.find(Board.class, id);
     * if (board == null) {
     * throw new NotFoundException("Board not found");
     * }
     * 
     * board.getColumns().size();
     * board.getColumns().forEach(c -> c.getCards().size());
     * 
     * em.getTransaction().commit();
     * 
     * return board;
     * }
     * }
     */

    public BoardResponse getBoard(ServletContext context, Long id) {
        try (EntityManager em = JpaUtil.getEntityManager(context)) {

            Board board = repository.findWithDetails(em, id);

            if (board == null) {
                throw new NotFoundException("Board not found");
            }

            return mapToResponse(board);
        }
    }

    private BoardResponse mapToResponse(Board board) {
        BoardResponse br = new BoardResponse();
        br.id = board.getId();
        br.name = board.getName();

        br.columns = board.getColumns().stream()
                .map(c -> {
                    ColumnResponse cr = new ColumnResponse();
                    cr.id = c.getId();
                    cr.title = c.getTitle();

                    cr.cards = c.getCards().stream()
                            .map(card -> {
                                CardResponse cardResp = new CardResponse();
                                cardResp.id = card.getId();
                                cardResp.title = card.getTitle();
                                return cardResp;
                            }).toList();

                    return cr;
                }).toList();

        return br;
    }

    public void deleteBoard(ServletContext context, Long id) {
        try (EntityManager em = JpaUtil.getEntityManager(context)) {

            em.getTransaction().begin();
            try {
                Board board = em.find(Board.class, id);
                if (board == null) {
                    throw new NotFoundException("Board not found");
                }

                em.remove(board);

                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    public void addCard(ServletContext context, Long boardId, Long columnId, String title) {
        if (title == null || title.isBlank()) {
            throw new BadRequestException("Card title cannot be empty");
        }
        try (EntityManager em = JpaUtil.getEntityManager(context)) {
            em.getTransaction().begin();
            try {
                Board board = repository.findById(em, boardId);

                if (board == null) {
                    throw new NotFoundException("Board not found");
                }

                BoardColumn targetColumn = board
                        .getColumns()
                        .stream()
                        .filter(c -> c.getId().equals(columnId))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Column not found"));

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

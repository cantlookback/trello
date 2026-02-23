package ru.zinakov.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import ru.zinakov.domain.Board;
import ru.zinakov.domain.BoardColumn;
import ru.zinakov.domain.Card;
import ru.zinakov.exception.BadRequestException;
import ru.zinakov.exception.NotFoundException;
import ru.zinakov.repository.BoardRepository;

public class BoardService {
    private final EntityManagerFactory emf;
    private final BoardRepository repository;

    public BoardService(EntityManagerFactory emf) {
        this.emf = emf;
        this.repository = new BoardRepository();
    }

    public Long createBoard(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Board name cannot be empty");
        }
        try (EntityManager em = emf.createEntityManager()) {
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

    public void addColumn(Long boardId, String title) {
        if (title == null || title.isBlank()) {
            throw new BadRequestException("Column title cannot be empty");
        }
        try (EntityManager em = emf.createEntityManager()) {
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
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    public Board getBoardWithDetails(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Board board = repository.findWithDetails(em, id);

            if (board == null) {
                throw new NotFoundException("Board not found");
            }

            return board;
        }
    }

    public BoardColumn getColumn(Long boardId, Long columnId) {

        try (EntityManager em = emf.createEntityManager()) {

            BoardColumn column = repository.findColumn(em, boardId, columnId);

            if (column == null) {
                throw new NotFoundException("Column not found");
            }

            return column;
        }
    }

    public Card getCard(Long boardId,
            Long columnId,
            Long cardId) {

        try (EntityManager em = emf.createEntityManager()) {

            Card card = repository.findCard(em, boardId, columnId, cardId);

            if (card == null) {
                throw new NotFoundException("Card not found");
            }

            return card;
        }
    }

    public void deleteBoard(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Board board = em.find(Board.class, id);
                if (board == null) {
                    throw new NotFoundException("Board not found");
                }

                em.remove(board);

                em.getTransaction().commit();
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }

    public void addCard(Long boardId, Long columnId, String title) {
        if (title == null || title.isBlank()) {
            throw new BadRequestException("Card title cannot be empty");
        }
        try (EntityManager em = emf.createEntityManager()) {
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
            } catch (RuntimeException e) {
                em.getTransaction().rollback();
                throw e;
            }
        }
    }
}

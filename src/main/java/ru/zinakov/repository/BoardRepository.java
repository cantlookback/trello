package ru.zinakov.repository;

import jakarta.persistence.EntityManager;
import ru.zinakov.domain.Board;

public class BoardRepository {

    public Board findById(EntityManager em, Long id) {
        return em.find(Board.class, id);
    }

    public void save(EntityManager em, Board board) {
        if (board.getId() == null) {
            em.persist(board);
        } else {
            em.merge(board);
        }
    }
}
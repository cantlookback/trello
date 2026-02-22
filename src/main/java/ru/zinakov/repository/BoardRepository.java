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

    public Board findWithDetails(EntityManager em, Long id) {
        return em.createQuery("""
                select distinct b
                from Board b
                left join fetch b.columns c
                left join fetch c.cards
                where b.id = :id
                """, Board.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
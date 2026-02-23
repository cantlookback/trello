package ru.zinakov.repository;

import jakarta.persistence.EntityManager;
import ru.zinakov.domain.Board;
import ru.zinakov.domain.BoardColumn;
import ru.zinakov.domain.Card;

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

    public BoardColumn findColumn(EntityManager em, Long boardId, Long columnId) {
        return em.createQuery("""
                select c
                from BoardColumn c
                where c.id = :columnId
                and c.board.id = :boardId
                """, BoardColumn.class)
                .setParameter("columnId", columnId)
                .setParameter("boardId", boardId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public Card findCard(EntityManager em,
            Long boardId,
            Long columnId,
            Long cardId) {

        return em.createQuery("""
                select cd
                from Card cd
                where cd.id = :cardId
                and cd.column.id = :columnId
                and cd.column.board.id = :boardId
                """, Card.class)
                .setParameter("cardId", cardId)
                .setParameter("columnId", columnId)
                .setParameter("boardId", boardId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

}
package ru.zinakov.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    @OneToMany(
        mappedBy = "board",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<BoardColumn> columns = new ArrayList<>();

    protected Board() {}

    public Board(String name) {
        this.name = name;
    }

    public void addColumn(BoardColumn column) {
        column.setBoard(this);
        columns.add(column);
    }

    public Long getId() {
        return id;
    }

    public List<BoardColumn> getColumns(){
        return this.columns;
    }
}
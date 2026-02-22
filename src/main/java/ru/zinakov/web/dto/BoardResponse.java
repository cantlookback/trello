package ru.zinakov.web.dto;

import java.util.List;

public class BoardResponse {
    public long id;
    public String name;
    public List<ColumnResponse> columns;
}

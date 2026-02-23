package ru.zinakov.web;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zinakov.domain.Board;
import ru.zinakov.domain.BoardColumn;
import ru.zinakov.domain.Card;
import ru.zinakov.exception.BadRequestException;
import ru.zinakov.exception.NotFoundException;
import static ru.zinakov.keys.ContextKeys.EMF;
import ru.zinakov.service.BoardService;
import ru.zinakov.web.dto.BoardResponse;
import ru.zinakov.web.dto.CardResponse;
import ru.zinakov.web.dto.ColumnResponse;
import ru.zinakov.web.dto.CreateBoardRequest;
import ru.zinakov.web.dto.CreateCardRequest;
import ru.zinakov.web.dto.CreateColumnRequest;

@WebServlet("/boards/*")
public class BoardServlet extends HttpServlet {
    private BoardService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() {
        EntityManagerFactory emf = (EntityManagerFactory) getServletContext().getAttribute(EMF);

        this.service = new BoardService(emf);
    }

    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            throw new BadRequestException("Board id required");
        }

        String[] parts = path.split("/");

        switch (parts.length) {
            case 2 -> {
                Long boardId = Long.valueOf(parts[1]);
                Board board = service.getBoardWithDetails(boardId);
                mapper.writeValue(resp.getOutputStream(), mapToResponse(board));
            }
            case 4 -> {
                if ("columns".equals(parts[2])) {
                    Long boardId = Long.valueOf(parts[1]);
                    Long columnId = Long.valueOf(parts[3]);

                    var column = service.getColumn(boardId, columnId);

                    mapper.writeValue(resp.getOutputStream(), mapToResponse(column));
                } else {
                    throw new NotFoundException("Invalid path");
                }
            }
            case 6 -> {
                if ("columns".equals(parts[2]) &&
                        "cards".equals(parts[4])) {
                    Long boardId = Long.valueOf(parts[1]);
                    Long columnId = Long.valueOf(parts[3]);
                    Long cardId = Long.valueOf(parts[5]);

                    var card = service.getCard(boardId, columnId, cardId);

                    mapper.writeValue(resp.getOutputStream(), mapToResponse(card));
                } else {
                    throw new NotFoundException("Invalid path");
                }
            }
            default -> throw new NotFoundException("Invalid path");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,
            HttpServletResponse resp) throws IOException {

        String path = req.getPathInfo();
        if (path == null) {
            createBoard(req, resp);
            return;
        }

        String[] parts = path.split("/");

        if (parts.length == 3 && parts[2].equals("columns")) {
            Long boardId = Long.valueOf(parts[1]);
            createColumn(req, resp, boardId);
            return;
        }

        if (parts.length == 5 &&
                parts[2].equals("columns") &&
                parts[4].equals("cards")) {

            Long boardId = Long.valueOf(parts[1]);
            Long columnId = Long.valueOf(parts[3]);

            createCard(req, resp, boardId, columnId);
            return;
        }

        throw new NotFoundException("Invalid path");
    }

    private void createBoard(HttpServletRequest req,
            HttpServletResponse resp)
            throws IOException {

        CreateBoardRequest request = mapper.readValue(req.getInputStream(),
                CreateBoardRequest.class);

        Long id = service.createBoard(request.name);

        BoardResponse response = new BoardResponse();
        response.id = id;
        response.title = request.name;

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);

        mapper.writeValue(resp.getOutputStream(), response);
    }

    private void createColumn(HttpServletRequest req,
            HttpServletResponse resp,
            Long boardId)
            throws IOException {

        CreateColumnRequest request = mapper.readValue(req.getInputStream(), CreateColumnRequest.class);

        service.addColumn(boardId, request.title);

        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    private void createCard(HttpServletRequest req,
            HttpServletResponse resp,
            Long boardId,
            Long columnId) throws IOException {

        CreateCardRequest request = mapper.readValue(req.getInputStream(), CreateCardRequest.class);

        service.addCard(boardId, columnId, request.title);

        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    private BoardResponse mapToResponse(Board board) {
        BoardResponse br = new BoardResponse();
        br.id = board.getId();
        br.title = board.getTitle();

        br.columns = board.getColumns().stream()
                .map(column -> {
                    ColumnResponse cr = new ColumnResponse();
                    cr.id = column.getId();
                    cr.title = column.getTitle();

                    cr.cards = column.getCards().stream()
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

    private ColumnResponse mapToResponse(BoardColumn column) {
        ColumnResponse bcr = new ColumnResponse();
        bcr.id = column.getId();
        bcr.title = column.getTitle();

        bcr.cards = column.getCards().stream()
                .map(card -> {
                    CardResponse cr = new CardResponse();
                    cr.id = card.getId();
                    cr.title = card.getTitle();
                    return cr;
                }).toList();

        return bcr;
    }

    private CardResponse mapToResponse(Card card) {
        CardResponse cr = new CardResponse();
        cr.id = card.getId();
        cr.title = card.getTitle();

        return cr;
    }

}

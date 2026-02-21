package ru.zinakov.web;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.zinakov.service.BoardService;

@WebServlet("/boards")
public class BoardServlet extends HttpServlet {
     private final BoardService service = new BoardService();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws IOException {

        resp.setContentType("text/plain");
        resp.getWriter().write("Boards endpoint works");
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws IOException {

        String name = req.getParameter("name");

        Long id = service.createBoard(
                getServletContext(),
                name
        );

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("Created board id=" + id);
    }
}

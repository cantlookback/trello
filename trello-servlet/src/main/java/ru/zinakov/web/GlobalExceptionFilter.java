package ru.zinakov.web;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import ru.zinakov.exception.BadRequestException;
import ru.zinakov.exception.NotFoundException;
import ru.zinakov.web.dto.ErrorResponse;

@WebFilter("/*")
public class GlobalExceptionFilter implements Filter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        try {
            chain.doFilter(request, response);

        } catch (BadRequestException e) {
            sendError((HttpServletResponse) response, 400, e.getMessage());

        } catch (NotFoundException e) {
            sendError((HttpServletResponse) response, 404, e.getMessage());

        } catch (ServletException | IOException e) {
            sendError((HttpServletResponse) response, 500,
                    "Internal server error");
        }
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("applicatin/json");

        ErrorResponse error = new ErrorResponse(message);

        mapper.writeValue(response.getOutputStream(), error);
    }

}

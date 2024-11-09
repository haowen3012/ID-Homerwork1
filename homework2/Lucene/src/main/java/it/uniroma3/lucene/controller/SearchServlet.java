package it.uniroma3.lucene.controller;

import it.uniroma3.lucene.dto.DocumentDTO;
import it.uniroma3.lucene.service.Searcher;
import org.apache.lucene.search.IndexSearcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/submitForm")
public class SearchServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String field = request.getParameter("fields");
        String query = request.getParameter("query");


        try {
            IndexSearcher indexSearcher = (IndexSearcher) getServletContext().getAttribute("indexSearcher");
            if (indexSearcher == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "indexSearcher not initialized");
                return;
            } else {
                List<DocumentDTO> documentDTOS = Searcher.search(query,field, (IndexSearcher) getServletContext().getAttribute("indexSearcher"));
                request.setAttribute("documents", documentDTOS);
                request.getRequestDispatcher("/result.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("Error in searching");
        }

    }
}

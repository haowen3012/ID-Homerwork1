package it.uniroma3.lucene.searchEngineLucene.controller;

import it.uniroma3.lucene.searchEngineLucene.dto.DocumentDTO;
import it.uniroma3.lucene.searchEngineLucene.service.Searcher;
import org.apache.lucene.search.IndexSearcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@WebServlet("/submitForm")
public class SearchServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");


        String field = request.getParameter("fields");
        String query = request.getParameter("query");
        String query1 = request.getParameter("query1");
        String query2 = request.getParameter("query2");


        try {
            IndexSearcher indexSearcher = (IndexSearcher) getServletContext().getAttribute("indexSearcher");
            if (indexSearcher == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "indexSearcher not initialized");
                return;
            } else {
                List<DocumentDTO> documentDTOS;
                if(!query1.isEmpty() && !query2.isEmpty()) {
                    documentDTOS = Searcher.search(null, query1, query2, field, (IndexSearcher) getServletContext().getAttribute("indexSearcher"));
                } else {
                    documentDTOS = Searcher.search(query, null, null, field, (IndexSearcher) getServletContext().getAttribute("indexSearcher"));
                }
                request.setAttribute("documents", documentDTOS);
                request.getRequestDispatcher("/result.jsp").forward(request, response);
            }
        } catch (Exception e) {

            System.out.println("Error in searching:" + e);
        }

    }
}

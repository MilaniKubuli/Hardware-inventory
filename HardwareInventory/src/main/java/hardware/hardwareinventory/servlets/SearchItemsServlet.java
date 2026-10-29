package hardware.hardwareinventory.servlets;

import hardware.hardwareinventory.database.ItemDAO;
import hardware.hardwareinventory.models.Items;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class SearchItemsServlet extends HttpServlet {
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("q");
        String role = (String) request.getSession().getAttribute("role");

        if (query == null || query.trim().isEmpty()) {
            response.sendRedirect("search-items.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            MainServlet.printPageStart(out, "Search Results");
            MainServlet.printNav(out, role);
            MainServlet.printBreadcrumbs(out, "Search Results");

            out.println("<main><section class='page-card'>");
            out.println("<h1>Search Results</h1>");
            out.println("<p>Results for: <strong>" + MainServlet.escape(query) + "</strong></p>");

            try {
                List<Items> items = itemDAO.search(query);

                if (items.isEmpty()) {
                    out.println("<p class='message'>No matching items found.</p>");
                } else {
                    ViewAllServlet.printItemsTable(out, items, false);
                }

            } catch (SQLException ex) {
                out.println("<p class='message error'>Database error: "
                        + MainServlet.escape(ex.getMessage()) + "</p>");
            }

            MainServlet.printBackButton(out, "search-items.html", "Back to Search");
            MainServlet.printBackButton(out, "main", "Back to Home");

            out.println("</section></main>");
            MainServlet.printPageEnd(out);
        }
    }
}
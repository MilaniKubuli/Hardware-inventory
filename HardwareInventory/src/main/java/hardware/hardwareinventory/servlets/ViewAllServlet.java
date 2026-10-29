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

public class ViewAllServlet extends HttpServlet {
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String role = (String) request.getSession().getAttribute("role");

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            MainServlet.printPageStart(out, "View All Items");
            MainServlet.printNav(out, role);
            MainServlet.printBreadcrumbs(out, "View All Items");

            out.println("<main>");
            out.println("<section class='page-card'>");
            out.println("<h1>View All Items</h1>");
            out.println("<p>Below is the full list of hardware inventory items.</p>");

            try {
                List<Items> items = itemDAO.findAll();

                if (items.isEmpty()) {
                    out.println("<p class='message'>No items found.</p>");
                } else {
                    printItemsTable(out, items, false);
                }

            } catch (SQLException ex) {
                out.println("<p class='message error'>Database error: "
                        + MainServlet.escape(ex.getMessage()) + "</p>");
            }

            MainServlet.printBackButton(out, "main", "Back to Home");

            out.println("</section>");
            out.println("</main>");

            MainServlet.printPageEnd(out);
        }
    }

    static void printItemsTable(PrintWriter out, List<Items> items, boolean showBuyButton) {
        out.println("<table>");
        out.println("<tr>");
        out.println("<th>Item#</th>");
        out.println("<th>Name</th>");
        out.println("<th>Color</th>");
        out.println("<th>Description</th>");
        out.println("<th>Price</th>");
        out.println("<th>Stock</th>");
        out.println("<th>Available</th>");

        if (showBuyButton) {
            out.println("<th>Action</th>");
        }

        out.println("</tr>");

        for (Items item : items) {
            out.println("<tr>");
            out.println("<td>" + item.getItemId() + "</td>");
            out.println("<td>" + MainServlet.escape(item.getName()) + "</td>");
            out.println("<td>" + MainServlet.escape(item.getColor()) + "</td>");
            out.println("<td>" + MainServlet.escape(item.getDescription()) + "</td>");
            out.println("<td>R" + item.getPrice() + "</td>");
            out.println("<td>" + item.getQuantity() + "</td>");
            out.println("<td>" + item.getAvailabilityText() + "</td>");

            if (showBuyButton) {
                out.println("<td>");
                out.println("<form class='action-form' action='buyItems' method='post'>");
                out.println("<input type='hidden' name='itemId' value='" + item.getItemId() + "'>");
                out.println("<input type='hidden' name='quantity' value='1'>");

                if (item.getQuantity() > 0 && item.isAvailable()) {
                    out.println("<button type='submit'>Add to Cart</button>");
                } else {
                    out.println("<button type='button' disabled>Out of Stock</button>");
                }

                out.println("</form>");
                out.println("</td>");
            }

            out.println("</tr>");
        }

        out.println("</table>");
    }
}
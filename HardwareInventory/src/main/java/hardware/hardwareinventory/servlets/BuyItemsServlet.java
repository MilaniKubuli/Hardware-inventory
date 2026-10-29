package hardware.hardwareinventory.servlets;

import hardware.hardwareinventory.database.CartDAO;
import hardware.hardwareinventory.database.ItemDAO;
import hardware.hardwareinventory.models.Items;
import hardware.hardwareinventory.websocket.InventorySocket;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class BuyItemsServlet extends HttpServlet {
    private final ItemDAO itemDAO = new ItemDAO();
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Object userIdValue = request.getSession().getAttribute("userId");

        if (userIdValue == null) {
            response.sendRedirect("login.html");
            return;
        }

        String role = (String) request.getSession().getAttribute("role");

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            MainServlet.printPageStart(out, "Buy Items");
            MainServlet.printNav(out, role);
            MainServlet.printBreadcrumbs(out, "Buy Items");

            out.println("<main>");
            out.println("<section class='page-card'>");
            out.println("<h1>Buy Items</h1>");
            out.println("<p>Select an item below to add it to your cart.</p>");

            try {
                List<Items> items = itemDAO.findAll();

                if (items.isEmpty()) {
                    out.println("<p class='message'>No items available.</p>");
                } else {
                    ViewAllServlet.printItemsTable(out, items, true);
                }

            } catch (SQLException ex) {
                out.println("<p class='message error'>Database error: "
                        + MainServlet.escape(ex.getMessage()) + "</p>");
            }

            out.println("<div class='page-actions'>");
            out.println("<a class='secondary-button' href='viewCart'>View Cart</a>");
            out.println("<a class='secondary-button' href='main'>Back to Home</a>");
            out.println("</div>");

            out.println("</section>");
            out.println("</main>");

            MainServlet.printPageEnd(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Object userIdValue = request.getSession().getAttribute("userId");

        if (userIdValue == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (Integer) userIdValue;
        int itemId = Integer.parseInt(request.getParameter("itemId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        try {
            boolean updated = itemDAO.reduceQuantity(itemId, quantity);

            if (updated) {
                cartDAO.addItem(userId, itemId, quantity);
                InventorySocket.broadcast("Item " + itemId + " was added to a cart.");
                response.sendRedirect("viewCart");
            } else {
                response.sendRedirect("buyItems");
            }

        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }
}
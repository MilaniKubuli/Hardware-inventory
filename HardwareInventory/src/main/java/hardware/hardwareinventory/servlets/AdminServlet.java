package hardware.hardwareinventory.servlets;

import hardware.hardwareinventory.database.ItemDAO;
import hardware.hardwareinventory.models.Items;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class AdminServlet extends HttpServlet {
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String role = (String) request.getSession().getAttribute("role");

        if (!"admin".equals(role)) {
            response.sendRedirect("main");
            return;
        }

        String action = request.getParameter("action");

        if ("report".equals(action)) {
            showReport(response, role);
        } else {
            showDashboard(response, role);
        }
    }

    private void showDashboard(HttpServletResponse response, String role) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            MainServlet.printPageStart(out, "Admin");
            MainServlet.printNav(out, role);
            MainServlet.printBreadcrumbs(out, "Admin");

            out.println("<main><section class='page-card'>");
            out.println("<h1>Admin Dashboard</h1>");
            out.println("<p class='message'>Manage inventory items and generate reports.</p>");

            /*try {
                out.println("<div class='report-box'>");
                out.println("<div class='report-card'><h2>" + itemDAO.countItems() + "</h2><p>Total items</p></div>");
                out.println("<div class='report-card'><h2>" + itemDAO.countLowStockItems() + "</h2><p>Low stock items</p></div>");
                out.println("<div class='report-card'><h2>Live</h2><p>Inventory status</p></div>");
                out.println("</div>");
            } catch (SQLException ex) {
                out.println("<p class='message error'>Report error: "
                        + MainServlet.escape(ex.getMessage()) + "</p>");
            }*/

            out.println("<div class='page-actions'>");
            out.println("<a class='secondary-button' href='admin?action=report'>Generate Inventory Report</a>");
            out.println("<a class='secondary-button' href='main'>Back to Home</a>");
            out.println("</div>");

            out.println("<h2>Add New Item</h2>");
            printItemForm(out, "add", null);

            out.println("<h2>Update Existing Items</h2>");

            try {
                List<Items> items = itemDAO.findAll();

                for (Items item : items) {
                    out.println("<div class='report-card'>");
                    out.println("<h3>" + item.getItemId() + " - "
                            + MainServlet.escape(item.getName()) + "</h3>");
                    printItemForm(out, "update", item);
                    out.println("</div>");
                }

            } catch (SQLException ex) {
                out.println("<p class='message error'>Could not load items: "
                        + MainServlet.escape(ex.getMessage()) + "</p>");
            }

            out.println("</section></main>");
            MainServlet.printPageEnd(out);
        }
    }

    private void showReport(HttpServletResponse response, String role) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            MainServlet.printPageStart(out, "Inventory Report");
            MainServlet.printNav(out, role);
            MainServlet.printBreadcrumbs(out, "Inventory Report");

            out.println("<main><section class='page-card'>");
            out.println("<h1>Inventory Report</h1>");

            try {
                int totalItems = itemDAO.countItems();
                int lowStock = itemDAO.countLowStockItems();
                int outOfStock = itemDAO.countOutOfStockItems();
                BigDecimal totalValue = itemDAO.inventoryValue();
                List<Items> items = itemDAO.findAll();

                out.println("<div class='report-box'>");
                out.println("<div class='report-card'><h2>" + totalItems + "</h2><p>Total Inventory Items</p></div>");
                out.println("<div class='report-card'><h2>" + lowStock + "</h2><p>Low Stock Items</p></div>");
                out.println("<div class='report-card'><h2>" + outOfStock + "</h2><p>Out Of Stock Items</p></div>");
                out.println("<div class='report-card'><h2>R" + totalValue + "</h2><p>Total Inventory Value</p></div>");
                out.println("</div>");

                out.println("<h2>Full Inventory Listing</h2>");
                ViewAllServlet.printItemsTable(out, items, false);

            } catch (SQLException ex) {
                out.println("<p class='message error'>Report error: "
                        + MainServlet.escape(ex.getMessage()) + "</p>");
            }

            out.println("<div class='page-actions'>");
            out.println("<a class='secondary-button' href='admin'>Back to Admin Dashboard</a>");
            out.println("<a class='secondary-button' href='main'>Back to Home</a>");
            out.println("</div>");

            out.println("</section></main>");
            MainServlet.printPageEnd(out);
        }
    }

    private void printItemForm(PrintWriter out, String action, Items item) {
        String itemId = item == null ? "" : String.valueOf(item.getItemId());
        String name = item == null ? "" : item.getName();
        String color = item == null ? "" : item.getColor();
        String description = item == null ? "" : item.getDescription();
        String price = item == null ? "" : String.valueOf(item.getPrice());
        String quantity = item == null ? "" : String.valueOf(item.getQuantity());

        out.println("<form class='admin-form' action='admin' method='post'>");
        out.println("<input type='hidden' name='action' value='" + action + "'>");

        out.println("<div><label>Item ID</label>");
        out.println("<input name='itemId' type='number' value='" + MainServlet.escape(itemId) + "' required></div>");

        out.println("<div><label>Name</label>");
        out.println("<input name='name' value='" + MainServlet.escape(name) + "' required></div>");

        out.println("<div><label>Color</label>");
        out.println("<input name='color' value='" + MainServlet.escape(color) + "' required></div>");

        out.println("<div><label>Price</label>");
        out.println("<input name='price' type='number' step='0.01' value='" + MainServlet.escape(price) + "' required></div>");

        out.println("<div><label>Quantity</label>");
        out.println("<input name='quantity' type='number' value='" + MainServlet.escape(quantity) + "' required></div>");

        out.println("<div class='full'><label>Description</label>");
        out.println("<textarea name='description' required>" + MainServlet.escape(description) + "</textarea></div>");

        out.println("<div class='full'><button type='submit'>Save Item</button></div>");
        out.println("</form>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String role = (String) request.getSession().getAttribute("role");

        if (!"admin".equals(role)) {
            response.sendRedirect("main");
            return;
        }

        String action = request.getParameter("action");

        Items item = new Items(
                Integer.parseInt(request.getParameter("itemId")),
                request.getParameter("name"),
                request.getParameter("color"),
                request.getParameter("description"),
                new BigDecimal(request.getParameter("price")),
                Integer.parseInt(request.getParameter("quantity")),
                true
        );

        try {
            if ("update".equals(action)) {
                itemDAO.updateItem(item);
            } else {
                itemDAO.addItem(item);
            }

            response.sendRedirect("admin");

        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }
}
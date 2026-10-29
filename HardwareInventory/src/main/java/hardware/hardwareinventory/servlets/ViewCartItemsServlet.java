package hardware.hardwareinventory.servlets;

import hardware.hardwareinventory.database.CartDAO;
import hardware.hardwareinventory.models.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ViewCartItemsServlet extends HttpServlet {
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Object userIdValue = request.getSession().getAttribute("userId");
        String role = (String) request.getSession().getAttribute("role");

        if (userIdValue == null) {
            response.sendRedirect("login.html");
            return;
        }

        int userId = (Integer) userIdValue;
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            MainServlet.printPageStart(out, "Cart");
            MainServlet.printNav(out, role);
            MainServlet.printBreadcrumbs(out, "Cart");

            out.println("<main><section class='page-card'>");
            out.println("<h1>Your Cart</h1>");

            try {
                List<CartItem> cartItems = cartDAO.findByUser(userId);

                if (cartItems.isEmpty()) {
                    out.println("<p class='message'>Your cart is empty.</p>");
                } else {
                    BigDecimal total = BigDecimal.ZERO;

                    out.println("<table>");
                    out.println("<tr><th>Item#</th><th>Name</th><th>Color</th><th>Description</th><th>Qty</th>"
                            + "<th>Price</th><th>Total</th><th>Action</th></tr>");

                    for (CartItem cartItem : cartItems) {
                        total = total.add(cartItem.getLineTotal());

                        out.println("<tr>");
                        out.println("<td>" + cartItem.getItem().getItemId() + "</td>");
                        out.println("<td>" + MainServlet.escape(cartItem.getItem().getName()) + "</td>");
                        out.println("<td>" + MainServlet.escape(cartItem.getItem().getColor()) + "</td>");
                        out.println("<td>" + MainServlet.escape(cartItem.getItem().getDescription()) + "</td>");
                        out.println("<td>" + cartItem.getQuantity() + "</td>");
                        out.println("<td>R" + cartItem.getItem().getPrice() + "</td>");
                        out.println("<td>R" + cartItem.getLineTotal() + "</td>");
                        out.println("<td>");
                        out.println("<form class='inline-form' action='viewCart' method='post'>");
                        out.println("<input type='hidden' name='action' value='remove'>");
                        out.println("<input type='hidden' name='itemId' value='" + cartItem.getItem().getItemId() + "'>");
                        out.println("<button class='danger-button' type='submit'>Remove</button>");
                        out.println("</form>");
                        out.println("</td>");
                        out.println("</tr>");
                    }

                    out.println("<tr><th colspan='6'>Final Total</th><th colspan='2'>R" + total + "</th></tr>");
                    out.println("</table>");
                }

            } catch (SQLException ex) {
                out.println("<p class='message error'>Database error: "
                        + MainServlet.escape(ex.getMessage()) + "</p>");
            }

            MainServlet.printBackButton(out, "buyItems", "Back to Buy Items");
            MainServlet.printBackButton(out, "main", "Back to Home");

            out.println("</section></main>");
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

        String action = request.getParameter("action");

        if ("remove".equals(action)) {
            int userId = (Integer) userIdValue;
            int itemId = Integer.parseInt(request.getParameter("itemId"));

            try {
                cartDAO.removeItem(userId, itemId);
            } catch (SQLException ex) {
                throw new ServletException(ex);
            }
        }

        response.sendRedirect("viewCart");
    }
}
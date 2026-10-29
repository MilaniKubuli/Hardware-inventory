package hardware.hardwareinventory.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MainServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = (String) request.getSession().getAttribute("username");
        String role = (String) request.getSession().getAttribute("role");

        if (username == null) {
            response.sendRedirect("login.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            printPageStart(out, "Home");
            printNav(out, role);
            printBreadcrumbs(out, "Home");

            out.println("<main><section class='page-card'>");
            out.println("<h1>Hardware Inventory</h1>");
            out.println("<p class='message'>Welcome, " + escape(username) + 
                    ". Select an option below.</p>");

            out.println("<div class='menu-grid'>");
            out.println("<a class='menu-link' href='viewAll'>View All Items</a>");
            out.println("<a class='menu-link' href='buyItems'>Buy Items</a>");
            out.println("<a class='menu-link' href='viewCart'>View Cart</a>");
            out.println("<a class='menu-link' href='search-items.html'>Search Items</a>");
            if ("admin".equals(role)) {
                out.println("<a class='menu-link' href='admin'>Admin Dashboard</a>");
            }
            out.println("</div>");

            out.println("</section></main>");
            printPageEnd(out);
        }
    }

    static void printPageStart(PrintWriter out, String title) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + escape(title) + "</title>");
        out.println("<link rel='stylesheet' href='css/styles.css'>");
        out.println("</head>");
        out.println("<body>");
    }

    static void printPageEnd(PrintWriter out) {
        out.println("</body>");
        out.println("</html>");
    }

    static void printNav(PrintWriter out, String role) {
        out.println("<nav>");
        out.println("<a href='main'>Home</a>");
        out.println("<a href='viewAll'>View All Items</a>");
        out.println("<a href='buyItems'>Buy Items</a>");
        out.println("<a href='viewCart'>View Cart</a>");
        out.println("<a href='search-items.html'>Search</a>");
        if ("admin".equals(role)) {
            out.println("<a href='admin'>Admin</a>");
        }
        out.println("<a href='logout'>Logout</a>");
        out.println("</nav>");
    }

    static void printBreadcrumbs(PrintWriter out, String currentPage) {
        out.println("<main>");
        out.println("<div class='breadcrumbs'>");
        out.println("<a href='main'>Home</a>");
        out.println("<span>/</span>");
        out.println("<span>" + escape(currentPage) + "</span>");
        out.println("</div>");
        out.println("</main>");
    }

    static void printBackButton(PrintWriter out, String url, String text) {
        out.println("<div class='page-actions'>");
        out.println("<a class='secondary-button' href='" + url + "'>" + escape(text) + "</a>");
        out.println("</div>");
    }

    static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
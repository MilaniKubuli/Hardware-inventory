package hardware.hardwareinventory.servlets;

import hardware.hardwareinventory.database.UserDAO;
import hardware.hardwareinventory.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class RegistrationServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("register.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            try {
                User user = userDAO.register(username, password);

                out.println("<html>");
                out.println("<head><title>Registration</title></head>");
                out.println("<body>");
                out.println("<h2>Registration successful.</h2>");
                out.println("<p>Your user ID is: " + user.getUserId() + "</p>");
                out.println("<a href='login.html'>Go to Login</a>");
                out.println("</body>");
                out.println("</html>");

            } catch (SQLException ex) {
                out.println("<html>");
                out.println("<head><title>Registration Error</title></head>");
                out.println("<body>");
                out.println("<h2>Registration failed.</h2>");
                out.println("<p>" + ex.getMessage() + "</p>");
                out.println("<a href='register.html'>Try Again</a>");
                out.println("</body>");
                out.println("</html>");
            }
        }
    }
}
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

public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = userDAO.login(username, password);

            if (user != null) {
                request.getSession().setAttribute("userId", user.getUserId());
                request.getSession().setAttribute("username", user.getUsername());
                request.getSession().setAttribute("role", user.getRole());
                response.sendRedirect("main");
            } else {
                showMessage(response, "Invalid username or password.");
            }

        } catch (SQLException ex) {
            showMessage(response, "Database error: " + ex.getMessage());
        }
    }

    private void showMessage(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head><title>Login Result</title></head>");
            out.println("<body>");
            out.println("<h2>" + message + "</h2>");
            out.println("<a href='login.html'>Back to Login</a>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
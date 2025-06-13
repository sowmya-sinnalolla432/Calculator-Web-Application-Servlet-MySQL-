package cal;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.sql.*;

public class CalculatorServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        int num1 = 0, num2 = 0;
        double result = 0;
        String operation = "";
        String expression = "";

        try {
            num1 = Integer.parseInt(request.getParameter("num1"));
            num2 = Integer.parseInt(request.getParameter("num2"));
            operation = request.getParameter("operation");

            switch (operation) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 == 0) throw new ArithmeticException("Division by zero");
                    result = (double) num1 / num2;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operation");
            }

            expression = num1 + " " + operation + " " + num2;

        } catch (Exception e) {
            out.println("<html><body>");
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
            out.println("<a href='index.html'>Go Back</a>");
            out.println("</body></html>");
            return;
        }

        // Database connection
        String url = "jdbc:mysql://localhost:3306/calculator_db";
        String user = "root";
        String password = "root";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(url, user, password);
                 PreparedStatement ps = con.prepareStatement(
                         "INSERT INTO calculations (expression, result) VALUES (?, ?)")) {

                ps.setString(1, expression);
                ps.setDouble(2, result);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<html><body>");
            out.println("<h3>Error while storing data in DB.</h3>");
            out.println("</body></html>");
        }

        out.println("<html><body>");
        out.println("<h2>Result: " + expression + " = " + result + "</h2>");
        out.println("<a href='index.html'>Go Back</a>");
        out.println("</body></html>");
    }

    // Add this to handle GET requests
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h3>Please use the <a href='index.html'>calculator form</a> to submit values.</h3>");
        out.println("</body></html>");
    }
}

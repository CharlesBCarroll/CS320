package edu.ycp.cs320.lab02.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs320.lab02.controller.NumbersController;
import edu.ycp.cs320.lab02.model.Numbers;

public class MultiplyNumbersServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("MultiplyNumbers Servlet: doGet");

        // Call JSP to generate empty form
        req.getRequestDispatcher("/_view/MultiplyNumbers.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("MultiplyNumbers Servlet: doPost");

        // Holds the error message text, if there is any
        String errorMessage = null;

        // Create a new instance of the Numbers model
        Numbers numbers = new Numbers(null,null);

        // Decode POSTed form parameters and dispatch to controller
        try {
            // Set values from request parameters to the model
            numbers.setFirst(getDoubleFromParameter(req.getParameter("first")));
            numbers.setSecond(getDoubleFromParameter(req.getParameter("second")));

            // Check if any of the parameters are null
            if (numbers.getFirst() == null || numbers.getSecond() == null) {
                errorMessage = "Please specify two numbers";
            } else {
                // Create a new instance of the NumbersController
                NumbersController controller = new NumbersController();

                // Call the controller method to perform the operation and set the result in the model
                controller.multiply(numbers);
            }
        } catch (NumberFormatException e) {
            errorMessage = "Invalid double";
        }

        // Set the Numbers model as an attribute in the request
        req.setAttribute("numbers", numbers);

        // Set error message as attribute in the request
        req.setAttribute("errorMessage", errorMessage);

        // Forward to view to render the result HTML document
        req.getRequestDispatcher("/_view/MultiplyNumbers.jsp").forward(req, resp);
    }

    // Gets double from the request with attribute named s
    private Double getDoubleFromParameter(String s) {
        if (s == null || s.equals("")) {
            return null;
        } else {
            return Double.parseDouble(s);
        }
    }
}

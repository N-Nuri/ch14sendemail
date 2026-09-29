package murach.email;

import java.io.IOException;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import murach.business.User;
import murach.util.MailUtilResend;

public class EmailListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletContext()
                .getRequestDispatcher("/index.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // get current action
        String action = request.getParameter("action");
        if (action == null) {
            action = "join"; // default action
        }

        // perform action and set URL to appropriate page
        String url = "/index.jsp";
        if (action.equals("join")) {
            url = "/index.jsp";
        } else if (action.equals("add")) {
            // get parameters from the request
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");

            // store data in User object (just to pass it to the view, no persistence)
            User user = new User(firstName, lastName, email);
            request.setAttribute("user", user);

            // send email to user
            String to = email;
            String from = System.getenv().getOrDefault("MAIL_FROM", "Email List <onboarding@resend.dev>");
            String subject = "Welcome to our email list";
            String body = "Dear " + firstName + ",\n\n"
                    + "Thanks for joining our email list. "
                    + "We'll make sure to send you announcements about new products "
                    + "and promotions.\n\n"
                    + "Have a great day and thanks again!\n\n"
                    + "Kelly Slivkoff\n"
                    + "Mike Murach & Associates";
            boolean isBodyHTML = false;
            try {
                MailUtilResend.sendMail(to, from, subject, body, isBodyHTML);
            } catch (MessagingException e) {
                String seenKey = System.getenv().getOrDefault("RESEND_API_KEY", "");
                String errorMessage
                        = "ERROR: Unable to send email. "
                        + "Check Tomcat logs for details.<br>"
                        + "NOTE: You may need to configure the RESEND_API_KEY environment variable.<br>"
                        + "DEBUG - RESEND_API_KEY length seen by app: " + seenKey.length() + "<br>"
                        + "ERROR MESSAGE: " + e.getMessage();
                request.setAttribute("errorMessage", errorMessage);
                this.log(
                        "Unable to send email. \n"
                        + "Here is the email you tried to send: \n"
                        + "========================================\n"
                        + "TO: " + email + "\n"
                        + "FROM: " + from + "\n"
                        + "SUBJECT: " + subject + "\n\n"
                        + body + "\n\n");
            }
            url = "/thanks.jsp";
        }

        // forward to the view
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }
}

package murach.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.mail.MessagingException;

// Sends mail through the SendGrid HTTP API (https://sendgrid.com) instead of
// raw SMTP. Cloud hosts like Render commonly block outbound SMTP ports on
// free tiers, but HTTPS (443) is never blocked. Unlike Resend's free tier,
// SendGrid's "Single Sender Verification" lets one verified email address
// send to ANY recipient, with no custom domain required.
public class MailUtilSendGrid {
    private static final String API_KEY = System.getenv().getOrDefault("SENDGRID_API_KEY", "");
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void sendMail(String to, String from, String fromName, String subject, String body,
            boolean bodyIsHTML) throws MessagingException {

        String contentType = bodyIsHTML ? "text/html" : "text/plain";
        String json = "{"
                + "\"personalizations\":[{\"to\":[{\"email\":\"" + escape(to) + "\"}]}],"
                + "\"from\":{\"email\":\"" + escape(from) + "\",\"name\":\"" + escape(fromName) + "\"},"
                + "\"subject\":\"" + escape(subject) + "\","
                + "\"content\":[{\"type\":\"" + contentType + "\",\"value\":\"" + escape(body) + "\"}]"
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.sendgrid.com/v3/mail/send"))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new MessagingException(
                        "SendGrid API error (HTTP " + response.statusCode() + "): " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new MessagingException("Unable to reach the SendGrid API: " + e.getMessage(), e);
        }
    }

    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");
    }
}

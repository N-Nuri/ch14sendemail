package murach.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.mail.MessagingException;

// Sends mail through the Resend HTTP API (https://resend.com) instead of raw
// SMTP. Cloud hosts like Render commonly block outbound SMTP ports (25/465/587)
// on free tiers, but HTTPS (443) is never blocked, so this works everywhere
// SMTP does not.
public class MailUtilResend {
    private static final String API_KEY = System.getenv().getOrDefault("RESEND_API_KEY", "");
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void sendMail(String to, String from, String subject, String body, boolean bodyIsHTML)
            throws MessagingException {

        String bodyField = bodyIsHTML ? "html" : "text";
        String json = "{"
                + "\"from\":\"" + escape(from) + "\","
                + "\"to\":[\"" + escape(to) + "\"],"
                + "\"subject\":\"" + escape(subject) + "\","
                + "\"" + bodyField + "\":\"" + escape(body) + "\""
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new MessagingException(
                        "Resend API error (HTTP " + response.statusCode() + "): " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new MessagingException("Unable to reach the Resend API: " + e.getMessage(), e);
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

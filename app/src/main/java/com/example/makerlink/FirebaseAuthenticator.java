package com.example.makerlink;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.Credentials;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FirebaseAuthenticator {

    public String getAccessToken() throws IOException {
        // Load the service account key JSON file
        String jsonString = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"makerlink-39d19\",\n" +
                "  \"private_key_id\": \"ef67129c1e1019cf2021b934ba128ce1093d4cdc\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDHiwR6t/VUhjJW\\n/Vthil90DoSuoGIcov70RakVv9+zb6GKkZ49Zy9uFfL5/q8YGTPnoc/1xQ/MenQF\\nqsvQ4F6y+NzF8z6hgasjPmTnzuHLzXZQG1HMUxHjjrD34Pz77838Y5w99UyDC/oH\\n4mtsMam8GBemZQwblhCT6gk3v6emcVmYOUFtaTBFLL0WY3n5QcDCs/RveCiFpPo0\\nDXjaoZo+QN0Vsssl6KxXQYIsiXBuL+Y+HZ3pvtiMPQxLn/JQZzWRoWIJCNybHDSy\\ndrjw0HfYq9PUfbk1YluGZj5CrIprp9YDYxoeYH20hrhySQOa21Z11fLlswfwm9lt\\nqAc8KxfbAgMBAAECggEABHRXAkJPCLcCMqzvOEs1fErCrX9JEWiWFm/jqWeiH8hT\\nK9u6U5aVGXkF9oy37erKHNag5NSkWKU3J9U7PYt0sbwaeZTHhiV145CGAiK9KaaJ\\niJpDhjMoiBLsYmw0WY9t4r96gmuSbK58w2ZLjZvCNT+4b298CLWd4x9rW0I9BNsi\\n1Al04D6n8LhChcK88zbpz8T4rUdjm1F53zefRSoEHtDZmOX5u8dfjkmSVbgP8vVY\\nsMrAkimxil8BoWcDNiQAgB1a2WXNheZSRS7YZPwd7FxxoE787M7QaHBvYUGBAWt6\\nqSMlNRuiiypxMxqlKMXLAelHZKmd81yjlF7j4C15AQKBgQDmxMI8gPMmpqMIxYig\\niMxg2IhgON406+RI02tts9iaBVctImj/5Fg2RzaGDn2JtRT8AggZ7s3l7RF9YAhR\\nkbHbvZXS28fheh220PbqbNAIpMI02c5cJAMKrx120HQ/flM+Z/+p1VuiLHHwrLnV\\ngWT+pnfpWJuV20kXS58JbvU1gQKBgQDdXEBwlya86Th4SwjrpVPF/Ds/0+ZDnnZ3\\ngwSmDHzVYYOZmLjMLIwz+n9qIXoGCphwbCB52JkogVPN17Tq6TEgadsYaBvmAgyi\\ntsh/oMjOT1IWiTBI+wUpbOwR3jRltnnZd7nR4aG0BTJJ3Qi/Qx3XEwkeCM1+3FnE\\nlL3hcUiTWwKBgBxXlAd8SuC3XJSqNhKJWpiWX4F52oijZCExniMWEIcyGrfXXw/2\\nioL4EhE63F46WD5vPXF5693OVPSSA9OI0lBLo4gHKDBgICAg4AKApLg7DAMTsQ2N\\n+rWoNjUueuiFalHi0fqY0q1DSicl/5jiUHYaGeYV1N391ac/yWOONh2BAoGACz9e\\nvtnPTBYFmoclguO5p/uy/wFDV/g7SMNNfypvRWBZEXrjGOzCivmVc80TqC7to/i6\\nhbZtwdMgcPse5DjJGD6ItWrE0CEWh0YGVgXTbjg1kZOS8oJetsYkEJxbyTdGZbhh\\nprpyPvVsorgqc51zbLQ46F/GIUYNAaIVK9lXuLUCgYEA2gnOt5prpGqxCcFb1irx\\npZgz6RXADnhhuEVQUZCncBVB/dSoI63CZJ0OFe0AOnwHO+7fT4jDxNJvvC0ZU7dE\\n1pBpeewcDmUjfMIQonYr++gOsIWrQNWagOzgbLUVDj/Jl6yi8qAr4SUnGceR5FzS\\nPLLL3xlXOhFPvyja/EVZ7UI=\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"firebase-adminsdk-fbsvc@makerlink-39d19.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"105012101430546944232\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40makerlink-39d19.iam.gserviceaccount.com\",\n" +
                "  \"universe_domain\": \"googleapis.com\"\n" +
                "}";
        InputStream serviceAccountStream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
        // Use the service account credentials to generate an access token
        Credentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream)
                .createScoped("https://www.googleapis.com/auth/firebase.messaging");

        // Use the credentials to get an access token
        GoogleCredentials googleCredentials = (GoogleCredentials) credentials;
        googleCredentials.refreshIfExpired();  // Ensure the token is fresh
        String accessToken = googleCredentials.getAccessToken().getTokenValue();

        return accessToken;  // This is the Bearer token
    }
}

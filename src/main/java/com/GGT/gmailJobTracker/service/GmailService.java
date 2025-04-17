package com.GGT.gmailJobTracker.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class GmailService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public GmailService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    public Gmail getGmailService(OAuth2AuthenticationToken authentication) throws GeneralSecurityException, IOException {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

        GoogleCredential credential = new GoogleCredential().setAccessToken(client.getAccessToken().getTokenValue());

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Job Tracker Application")
                .build();
    }

    public List<Message> fetchJobApplicationEmails(OAuth2AuthenticationToken authentication) throws IOException, GeneralSecurityException {
        Gmail service = getGmailService(authentication);
        String userId = "me";
        String query = "subject:(\"application\" OR \"applied\" OR \"thank you for applying\" OR \"interview\" OR \"job\")";

        List<Message> result = new ArrayList<>();
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

        if (response.getMessages() != null) {
            result.addAll(response.getMessages());

            String pageToken = response.getNextPageToken();
            while (pageToken != null) {
                response = service.users().messages().list(userId).setQ(query)
                        .setPageToken(pageToken).execute();
                result.addAll(response.getMessages());
                pageToken = response.getNextPageToken();
            }
        }

        // Fetch full message details
        List<Message> detailedMessages = new ArrayList<>();
        for (Message message : result) {
            Message detailedMessage = service.users().messages().get(userId, message.getId()).execute();
            detailedMessages.add(detailedMessage);
        }

        return detailedMessages;
    }

    public String extractEmailContent(Message message) {
        try {
            if (message.getPayload().getParts() == null) {
                // Handle cases where there are no parts (plain text email)
                if (message.getPayload().getBody() != null && message.getPayload().getBody().getData() != null) {
                    return new String(Base64.getUrlDecoder().decode(message.getPayload().getBody().getData()));
                }
                return "";
            }

            StringBuilder result = new StringBuilder();
            extractParts(message.getPayload().getParts(), result);
            return result.toString();
        } catch (Exception e) {
            log.error("Error extracting email content", e);
            return "";
        }
    }

    private void extractParts(List<MessagePart> parts, StringBuilder result) {
        if (parts != null) {
            for (MessagePart part : parts) {
                if (part.getMimeType().equals("text/plain") && part.getBody() != null && part.getBody().getData() != null) {
                    result.append(new String(Base64.getUrlDecoder().decode(part.getBody().getData())));
                }

                if (part.getParts() != null) {
                    extractParts(part.getParts(), result);
                }
            }
        }
    }

    public String getEmailSubject(Message message) {
        if (message.getPayload() != null && message.getPayload().getHeaders() != null) {
            for (MessagePartHeader header : message.getPayload().getHeaders()) {
                if (header.getName().equals("Subject")) {
                    return header.getValue();
                }
            }
        }
        return "";
    }
}

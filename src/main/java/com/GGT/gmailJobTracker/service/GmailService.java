package com.GGT.gmailJobTracker.service;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GmailService {

    private final NetHttpTransport httpTransport;
    private final GsonFactory jsonFactory;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public GmailService(NetHttpTransport httpTransport, GsonFactory jsonFactory,
                        OAuth2AuthorizedClientService authorizedClientService) {
        this.httpTransport = httpTransport;
        this.jsonFactory = jsonFactory;
        this.authorizedClientService = authorizedClientService;
    }

    private Gmail getGmailService() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        return new Gmail.Builder(httpTransport, jsonFactory,
                request -> request.getHeaders().setAuthorization("Bearer " + client.getAccessToken().getTokenValue()))
                .setApplicationName("Gmail Job Tracker")
                .build();
    }

    public List<Message> getMessages(String userId, int maxResults) throws IOException {
        Gmail gmail = getGmailService();
        ListMessagesResponse response = gmail.users().messages().list(userId)
                .setMaxResults((long) maxResults)
                .execute();

        List<Message> messages = new ArrayList<>();
        if (response.getMessages() != null) {
            for (Message message : response.getMessages()) {
                Message fullMessage = gmail.users().messages()
                        .get(userId, message.getId())
                        .setFormat("full")
                        .execute();
                messages.add(fullMessage);
            }
        }
        return messages;
    }

    public Message getMessageById(String userId, String messageId) throws IOException {
        Gmail gmail = getGmailService();
        return gmail.users().messages()
                .get(userId, messageId)
                .setFormat("full")
                .execute();
    }
}
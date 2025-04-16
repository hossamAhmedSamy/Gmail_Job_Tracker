package com.GGT.gmailJobTracker.controller;

import com.GGT.gmailJobTracker.service.GmailService;
import com.google.api.services.gmail.model.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class GmailController {

    private final GmailService gmailService;

    public GmailController(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    @GetMapping("/messages")
    public String messagesPage() {
        return "messages";
    }

    @GetMapping("/api/gmail/messages")
    @ResponseBody
    public List<Message> getMessages() throws IOException {
        return gmailService.getMessages("me", 10); // "me" refers to the authenticated user
    }

    @GetMapping("/api/gmail/messages/{messageId}")
    @ResponseBody
    public Message getMessage(@PathVariable String messageId) throws IOException {
        return gmailService.getMessageById("me", messageId);
    }
}
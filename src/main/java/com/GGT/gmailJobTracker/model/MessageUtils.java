package com.GGT.gmailJobTracker.model;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.apache.commons.codec.binary.Base64;

import java.util.List;

public class MessageUtils {

    public static String getSubject(Message message) {
        return getHeader(message, "Subject");
    }

    public static String getFrom(Message message) {
        return getHeader(message, "From");
    }

    private static String getHeader(Message message, String name) {
        for (MessagePartHeader header : message.getPayload().getHeaders()) {
            if (header.getName().equals(name)) {
                return header.getValue();
            }
        }
        return null;
    }

    public static String getMessageBody(Message message) {
        StringBuilder body = new StringBuilder();
        MessagePart payload = message.getPayload();

        if (payload.getParts() == null) {
            if (payload.getBody() != null && payload.getBody().getData() != null) {
                byte[] bodyBytes = Base64.decodeBase64(payload.getBody().getData());
                body.append(new String(bodyBytes));
            }
        } else {
            for (MessagePart part : payload.getParts()) {
                if (part.getBody() != null && part.getBody().getData() != null) {
                    byte[] bodyBytes = Base64.decodeBase64(part.getBody().getData());
                    body.append(new String(bodyBytes));
                }
            }
        }
        return body.toString();
    }
}
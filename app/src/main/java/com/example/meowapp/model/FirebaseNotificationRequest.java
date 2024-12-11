package com.example.meowapp.model;

import com.google.gson.annotations.SerializedName;

public class FirebaseNotificationRequest {

    @SerializedName("message")
    private Message message;

    public FirebaseNotificationRequest(String topic, String title, String body) {
        this.message = new Message(topic, new Notification(title, body));
    }

    public static class Message {
        @SerializedName("topic")
        private String topic;
        @SerializedName("notification")
        private Notification notification;

        public Message(String topic, Notification notification) {
            this.topic = topic;
            this.notification = notification;
        }
    }

    public static class Notification {
        @SerializedName("title")
        private String title;
        @SerializedName("body")
        private String body;

        public Notification(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }
}


package com.chat.chatapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(excludeName = {
		"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
		"org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
		"org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
		"org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
})
public class ChatappApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatappApplication.class, args);
	}

	@org.springframework.context.annotation.Bean
	public com.mongodb.client.MongoClient mongoClient(org.springframework.core.env.Environment env) {
		String uri = env.getProperty("app.mongodb.uri");

		if (uri == null || uri.contains("localhost") || uri.contains("127.0.0.1") || !uri.contains("mongodb+srv://")) {
			System.err.println("FATAL: Atlas Cloud URI is missing or invalid. Connection to LOCALHOST is BLOCKED.");
			System.exit(1);
			return null;
		}

		System.out.println("CLOUD-READY: Initializing Secure Atlas Connection...");
		return com.mongodb.client.MongoClients.create(uri);
	}

	@org.springframework.context.annotation.Bean
	public org.springframework.data.mongodb.core.MongoTemplate mongoTemplate(
			com.mongodb.client.MongoClient mongoClient) {
		return new org.springframework.data.mongodb.core.MongoTemplate(mongoClient, "chatapp");
	}
}

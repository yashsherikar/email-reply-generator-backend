package com.email.writer.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.email.writer.dto.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailGeneratorService {

	private final WebClient webClient;

	public EmailGeneratorService(WebClient webClient) {
		this.webClient = webClient;
	}

	@Value("${gemini.api.url}")
	private String geminiApiUrl;

	@Value("${gemini.api.key}")
	private String geminiApiKey;

	public String generateEmailReply(EmailRequest emailRequest) {
	    // Build the prompt
	    String prompt = buildPrompt(emailRequest);

	    // Craft the request body
	    Map<String, Object> requestBody = Map.of(
	        "contents", new Object[]{
	            Map.of("parts", new Object[]{
	                Map.of("text", prompt)
	            })
	        }
	    );

	    String fullUrl = geminiApiUrl + "?key=" + geminiApiKey;

	    log.info("Calling Gemini API: {}", fullUrl);

	    // Perform the request and get the response
	    String response = webClient.post()
	            .uri(fullUrl)
	            .header("Content-Type", "application/json")
	            .bodyValue(requestBody)
	            .retrieve()
	            .bodyToMono(String.class)
	            .block();

	    // Return the extracted response
	    return extractResponseContent(response);
	}

	private String extractResponseContent(String response) {
	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode rootNode = mapper.readTree(response);

	        return rootNode.path("candidates")
	                .get(0)
	                .path("content")
	                .path("parts")
	                .get(0)
	                .path("text")
	                .asText();

	    } catch (Exception e) {
	        return "Error processing request: " + e.getMessage();
	    }
	}

	private String buildPrompt(EmailRequest emailRequest) {
	    StringBuilder prompt = new StringBuilder();

	    prompt.append("Generate only ONE professional email reply for the following email content. ")
	          .append("Please do NOT include multiple options and it shoud have atleast 3 lines");

	    if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
	        prompt.append("Use a ").append(emailRequest.getTone()).append(" tone. ");
	    }

	    prompt.append("\nOriginal email:\n").append(emailRequest.getEmailContent());

	    log.info(prompt.toString());
	    return prompt.toString();
	}
}

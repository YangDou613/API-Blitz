package org.example.apiblitz.service;



import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;

@Service
public class SendEmailService {

	@Value("${DOMAIN_NAME}")
	private String DOMAIN_NAME;

	@Value("${API_KEY}")
	private String API_KEY;

	@Value("${SENDER}")
	private String SENDER;

	public JsonNode sendEmail(Object recipient, String apiUrl) throws UnirestException {
		HttpResponse<JsonNode> request = Unirest
				.post("https://api.mailgun.net/v3/" + DOMAIN_NAME + "/messages")
				.basicAuth("api", API_KEY)
				.queryString("from", SENDER)
				.queryString("to", recipient)
				.queryString("subject", "API test exception notification.")
				.queryString("text", "Your API has exceptions during automated testing: " + apiUrl + ".")
				.asJson();
		return request.getBody();
	}
}

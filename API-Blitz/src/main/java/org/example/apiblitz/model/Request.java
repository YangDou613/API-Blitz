package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request {
	private String APIUrl;
	private String apiurl;
	private String method;
	private Object queryParams;
	@Column(name = "headers")
	private Object requestHeaders;
	private String headers;
	@Column(name = "body")
	private Object requestBody;
	private String body;
	private Integer collectionDetailsId;
}

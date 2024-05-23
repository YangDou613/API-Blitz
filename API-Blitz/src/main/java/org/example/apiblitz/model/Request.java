package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request {
	private Integer id;
	private String requestName;
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

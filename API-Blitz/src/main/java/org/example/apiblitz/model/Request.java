package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request {
	private String APIUrl;
	private String method;
	private Object queryParams;
	private Object requestHeaders;
	private Object requestBody;
}

package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Collections {
	private Integer collectionId;
	private String collectionName;
	private String description;
	private String requestName;
	private String apiurl;
	private String url;
	private String method;
	private Object queryParams;
	private ArrayList<Object> paramsKey;
	private ArrayList<Object> paramsValue;
	private String authorizationKey;
	private String authorizationValue;
	private String headers;
	private ArrayList<String> headersKey;
	private ArrayList<String> headersValue;
	private String body;
	private Request request;
}

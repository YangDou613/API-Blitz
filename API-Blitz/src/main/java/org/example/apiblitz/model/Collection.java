package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Collection {
	private Integer collectionId;
	private String collectionName;
	private String description;
	private String requestName;
	@NotBlank
	private String url;
	private String method;
	private ArrayList<Object> paramsKey;
	private ArrayList<Object> paramsValue;
	private String authorizationKey;
	private String authorizationValue;
	private ArrayList<String> headersKey;
	private ArrayList<String> headersValue;
	private String body;
	private Request request;
}

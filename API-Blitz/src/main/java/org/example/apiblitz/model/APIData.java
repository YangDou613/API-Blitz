package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIData {
	private String method;
	@NotBlank
	private String url;
	private ArrayList<Object> paramsKey;
	private ArrayList<Object> paramsValue;
	private String authorizationKey;
	private String authorizationValue;
	private ArrayList<String> headersKey;
	private ArrayList<String> headersValue;
	private String body;
}

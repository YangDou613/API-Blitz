package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCase implements ITestCase {
	private String testItem;
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
	@NotNull
	private Integer statusCode;
	private String expectedResponseBody;
	@NotBlank
	private String intervalsTimeUnit;
	@NotNull
	private Integer intervalsTimeValue;
	@NotBlank
	private String notification;
	private ArrayList<String> email;
}

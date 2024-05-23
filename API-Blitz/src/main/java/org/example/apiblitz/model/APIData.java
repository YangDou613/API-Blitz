package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

	public static APIData setAPIData(ITestCase testCase) {

		APIData apiData = new APIData();

		apiData.setMethod(testCase.getMethod());
		apiData.setUrl(testCase.getUrl());
		apiData.setParamsKey(testCase.getParamsKey());
		apiData.setParamsValue(testCase.getParamsValue());
		apiData.setAuthorizationKey(testCase.getAuthorizationKey());
		apiData.setAuthorizationValue(testCase.getAuthorizationValue());
		apiData.setHeadersKey(testCase.getHeadersKey());
		apiData.setHeadersValue(testCase.getHeadersValue());
		apiData.setBody(testCase.getBody());

		return apiData;
	}
}

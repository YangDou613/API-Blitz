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
public class ResetTestCase implements ITestCase {
	private Integer id;
	private String testItem;
	private String method;
	private String url;
	private ArrayList<Object> paramsKey;
	private ArrayList<Object> paramsValue;
	private String authorizationKey;
	private String authorizationValue;
	private ArrayList<String> headersKey;
	private ArrayList<String> headersValue;
	private String body;
	private Integer statusCode;
	private String expectedResponseBody;
	private String intervalsTimeUnit;
	private Integer intervalsTimeValue;
	private String notification;
	private ArrayList<String> email;
}

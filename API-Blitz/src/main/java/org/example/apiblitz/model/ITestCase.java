package org.example.apiblitz.model;

import java.util.ArrayList;

public interface ITestCase {
	String getMethod();

	String getUrl();

	ArrayList<Object> getParamsKey();

	ArrayList<Object> getParamsValue();

	String getAuthorizationKey();

	String getAuthorizationValue();

	ArrayList<String> getHeadersKey();

	ArrayList<String> getHeadersValue();

	String getBody();
}

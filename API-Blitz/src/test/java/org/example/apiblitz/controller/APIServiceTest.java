package org.example.apiblitz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.example.apiblitz.model.APITestResult;
import org.example.apiblitz.repository.APIRepository;
import org.example.apiblitz.repository.CollectionsRepository;
import org.example.apiblitz.service.APIService;
import org.example.apiblitz.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = APIService.class)
public class APIServiceTest {

	@Autowired
	APIService apiService;

	@MockBean
	APIRepository apiRepository;

	@MockBean
	RestTemplate restTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	CollectionsRepository collectionsRepository;

	@MockBean
	private JwtUtil jwtUtil;

	String testResult = """
			{
			    "id": 1483,
			    "userId": 14,
			    "method": "GET",
			    "queryParams": "{\\"id\\": \\"30\\"}",
			    "headers": "{\\"Content-Type\\": [\\"application/json\\"]}",
			    "body": null,
			    "testDateTime": "2024-05-18T16:11:18",
			    "responseHeaders": "{\\"Date\\": [\\"Sat, 18 May 2024 08:11:19 GMT\\"], \\"Vary\\": [\\"Origin\\", \\"Access-Control-Request-Method\\", \\"Access-Control-Request-Headers\\"], \\"Server\\": [\\"nginx/1.18.0 (Ubuntu)\\"], \\"Connection\\": [\\"keep-alive\\"], \\"Content-Type\\": [\\"application/json\\"], \\"Content-Length\\": [\\"1029\\"], \\"Transfer-Encoding\\": [\\"chunked\\"], \\"Execution-Duration\\": [\\"41\\"]}",
			    "responseBody": "{\\"data\\": {\\"id\\": 30, \\"note\\": \\"實品顏色以單品照為主\\", \\"wash\\": \\"機洗\\", \\"place\\": \\"臺灣\\", \\"price\\": 870, \\"sizes\\": [\\"M\\", \\"L\\"], \\"story\\": \\"肩造型修飾肩膀線條更加顯瘦\\", \\"title\\": \\"削肩直紋洋裝\\", \\"colors\\": [{\\"code\\": \\"BDB76B\\", \\"name\\": \\"暗卡其色\\"}], \\"images\\": [\\"https://djynxfxojqrcz.cloudfront.net/image/3040dress1.jpeg\\", \\"https://djynxfxojqrcz.cloudfront.net/image/3041dress2.jpeg\\", \\"https://djynxfxojqrcz.cloudfront.net/image/3042dress3.jpeg\\", \\"https://djynxfxojqrcz.cloudfront.net/image/3048dress1.jpeg\\", \\"https://djynxfxojqrcz.cloudfront.net/image/3049dress2.jpeg\\", \\"https://djynxfxojqrcz.cloudfront.net/image/3050dress3.jpeg\\"], \\"texture\\": \\"棉、聚脂纖維\\", \\"category\\": \\"women\\", \\"variants\\": [{\\"size\\": \\"M\\", \\"stock\\": 11, \\"color_code\\": \\"BDB76B\\"}, {\\"size\\": \\"L\\", \\"stock\\": 10, \\"color_code\\": \\"BDB76B\\"}], \\"main_image\\": \\"https://djynxfxojqrcz.cloudfront.net/image/30dress.jpeg\\", \\"description\\": \\"露削肩版型\\"}}",
			    "statusCode": 200,
			    "apiurl": "https://yangdou.beauty/api/1.0/products/details?id=30"
			}""";

	@Test
	public void getApiTestResultByUserIdAndDateTime() throws Exception {

		String accessToken = """
			eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiYWxpY2UiLCJ1c2VySWQiOjExLCJlbWFpbCI6ImFsaWNlMDUxOUBnbWFpbC5jb20iLCJzdWI
			iOiJhbGljZSIsImlhdCI6MTcxNjA5MzMyOSwiZXhwIjoxNzE4Njg1MzI5fQ.XImZchCCCBxFTDn9VhUY6awY9brBIiapyJrA66WxkZw
		""";

		Integer userId = 11;

		String testDateTime = "2024-05-19T12:36:00";

		Claims claims = new DefaultClaims();
		claims.put("userId", userId);
		when(jwtUtil.parseToken(accessToken)).thenReturn(claims);

		when(apiRepository.getApiTestResultByUserIdAndDateTime(userId, testDateTime)).thenReturn(getMockTestResult());

		APITestResult apiTestResult = apiService.getApiTestResult(accessToken, testDateTime);
		assertNotNull(apiTestResult);
		assertEquals("https://yangdou.beauty/api/1.0/products/details?id=30", apiTestResult.getAPIUrl());
		assertEquals("GET", apiTestResult.getMethod());
		assertEquals("{\"id\": \"30\"}", apiTestResult.getQueryParams());
		assertEquals("{\"Content-Type\": [\"application/json\"]}", apiTestResult.getHeaders());
		assertNull(apiTestResult.getBody());
		assertEquals("2024-05-18T16:11:18", apiTestResult.getTestDateTime().toString());
		assertEquals("{\"Date\": [\"Sat, 18 May 2024 08:11:19 GMT\"], \"Vary\": [\"Origin\", \"Access-Control-Request-Method\", \"Access-Control-Request-Headers\"], \"Server\": [\"nginx/1.18.0 (Ubuntu)\"], \"Connection\": [\"keep-alive\"], \"Content-Type\": [\"application/json\"], \"Content-Length\": [\"1029\"], \"Transfer-Encoding\": [\"chunked\"], \"Execution-Duration\": [\"41\"]}", apiTestResult.getResponseHeaders());
		assertEquals("{\"data\": {\"id\": 30, \"note\": \"實品顏色以單品照為主\", \"wash\": \"機洗\", \"place\": \"臺灣\", \"price\": 870, \"sizes\": [\"M\", \"L\"], \"story\": \"肩造型修飾肩膀線條更加顯瘦\", \"title\": \"削肩直紋洋裝\", \"colors\": [{\"code\": \"BDB76B\", \"name\": \"暗卡其色\"}], \"images\": [\"https://djynxfxojqrcz.cloudfront.net/image/3040dress1.jpeg\", \"https://djynxfxojqrcz.cloudfront.net/image/3041dress2.jpeg\", \"https://djynxfxojqrcz.cloudfront.net/image/3042dress3.jpeg\", \"https://djynxfxojqrcz.cloudfront.net/image/3048dress1.jpeg\", \"https://djynxfxojqrcz.cloudfront.net/image/3049dress2.jpeg\", \"https://djynxfxojqrcz.cloudfront.net/image/3050dress3.jpeg\"], \"texture\": \"棉、聚脂纖維\", \"category\": \"women\", \"variants\": [{\"size\": \"M\", \"stock\": 11, \"color_code\": \"BDB76B\"}, {\"size\": \"L\", \"stock\": 10, \"color_code\": \"BDB76B\"}], \"main_image\": \"https://djynxfxojqrcz.cloudfront.net/image/30dress.jpeg\", \"description\": \"露削肩版型\"}}", apiTestResult.getResponseBody());
		assertEquals(200, apiTestResult.getStatusCode());
	}

	public APITestResult getMockTestResult() throws IOException {
		return objectMapper.readValue(testResult, APITestResult.class);
	}
}

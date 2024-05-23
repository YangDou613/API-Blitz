package org.example.apiblitz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.apiblitz.model.APITestResult;
import org.example.apiblitz.queue.Publisher;
import org.example.apiblitz.service.APIService;
import org.example.apiblitz.service.AutoTestService;
import org.example.apiblitz.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = APIController.class)
public class APIControllerTest {

	String testResult = "{\n" +
	                    "    \"id\": 1094,\n" +
	                    "    \"userId\": 11,\n" +
	                    "    \"method\": \"GET\",\n" +
	                    "    \"queryParams\": \"{\\\"id\\\": \\\"30\\\"}\",\n" +
	                    "    \"headers\": \"{\\\"Content-Type\\\": [\\\"application/json\\\"]}\",\n" +
	                    "    \"body\": null,\n" +
	                    "    \"testDateTime\": \"2024-05-19T12:36:00\",\n" +
	                    "    \"responseHeaders\": \"{\\\"Date\\\": [\\\"Tue, 07 May 2024 09:25:48 GMT\\\"], " +
	                    "\\\"Vary\\\": [\\\"Origin\\\", \\\"Access-Control-Request-Method\\\", " +
	                    "\\\"Access-Control-Request-Headers\\\"], " +
	                    "\\\"Server\\\": [\\\"nginx/1.18.0 (Ubuntu)\\\"], " +
	                    "\\\"Connection\\\": [\\\"keep-alive\\\"], " +
	                    "\\\"Content-Type\\\": [\\\"application/json\\\"], " +
	                    "\\\"Content-Length\\\": [\\\"1029\\\"], " +
	                    "\\\"Transfer-Encoding\\\": [\\\"chunked\\\"], " +
	                    "\\\"Execution-Duration\\\": [\\\"722\\\"]}\",\n" +
	                    "    \"responseBody\": \"{\\\"data\\\": " +
	                    "{\\\"id\\\": 30, \\\"note\\\": \\\"實品顏色以單品照為主\\\", \\\"wash\\\": \\\"機洗\\\", " +
	                    "\\\"place\\\": \\\"臺灣\\\", \\\"price\\\": 870, \\\"sizes\\\": [\\\"M\\\", \\\"L\\\"], " +
	                    "\\\"story\\\": \\\"肩造型修飾肩膀線條更加顯瘦\\\", \\\"title\\\": \\\"削肩直紋洋裝\\\", " +
	                    "\\\"colors\\\": [{\\\"code\\\": \\\"BDB76B\\\", \\\"name\\\": \\\"暗卡其色\\\"}], " +
	                    "\\\"images\\\": [\\\"https://djynxfxojqrcz.cloudfront.net/image/3040dress1.jpeg\\\", " +
	                    "\\\"https://djynxfxojqrcz.cloudfront.net/image/3041dress2.jpeg\\\", " +
	                    "\\\"https://djynxfxojqrcz.cloudfront.net/image/3042dress3.jpeg\\\", " +
	                    "\\\"https://djynxfxojqrcz.cloudfront.net/image/3048dress1.jpeg\\\", " +
	                    "\\\"https://djynxfxojqrcz.cloudfront.net/image/3049dress2.jpeg\\\", " +
	                    "\\\"https://djynxfxojqrcz.cloudfront.net/image/3050dress3.jpeg\\\"], " +
	                    "\\\"texture\\\": \\\"棉、聚脂纖維\\\", \\\"category\\\": \\\"women\\\", " +
	                    "\\\"variants\\\": [{\\\"size\\\": \\\"M\\\", \\\"stock\\\": 11, " +
	                    "\\\"color_code\\\": \\\"BDB76B\\\"}, {\\\"size\\\": \\\"L\\\", \\\"stock\\\": 10, " +
	                    "\\\"color_code\\\": \\\"BDB76B\\\"}], " +
	                    "\\\"main_image\\\": \\\"https://djynxfxojqrcz.cloudfront.net/image/30dress.jpeg\\\", " +
	                    "\\\"description\\\": \\\"露削肩版型\\\"}}\",\n" +
	                    "    \"statusCode\": 200,\n" +
	                    "    \"apiurl\": \"https://yangdou.beauty/api/1.0/products/details?id=30\"\n" +
	                    "}";
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private APIService apiService;
	@MockBean
	private AutoTestService autoTestService;
	@MockBean
	private JwtUtil jwtUtil;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private Publisher publisher;

	@Test
	public void getTestResult() throws Exception {

		String accessToken = """
					eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdDA1MTkiLCJ1c2VySWQiOjEyLCJlbWFpbCI6InRlc3QwNTE5QGdtYWlsLmNvbSIsInN1Yi
					I6InRlc3QwNTE5IiwiaWF0IjoxNzE2MTMzMzg5LCJleHAiOjE3MTg3MjUzODl9.iSqGL69roBrajndAqtkqkfclDQffQ479y8MYWmXo0Zs
				""";

		String authorizationHeader = "Bearer " + accessToken;

		String testDateTime = "2024-05-19T12:36:00";

		Claims claims = Jwts.claims();
		claims.put("userId", 11);

		when(jwtUtil.extractAccessToken(authorizationHeader)).thenReturn(accessToken);
		when(jwtUtil.parseToken(accessToken)).thenReturn(claims);

		when(apiService.getApiTestResult(accessToken, testDateTime)).thenReturn(getMockTestResult());

		mockMvc.perform(
						get("/api/1.0/APITest/testResult")
								.header("Authorization", authorizationHeader)
								.queryParam("testDateTime", testDateTime))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(testResult));
	}

	public APITestResult getMockTestResult() throws IOException {
		return objectMapper.readValue(testResult, APITestResult.class);
	}
}

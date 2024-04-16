package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductsDTO {
	@JsonIgnore
	private Integer autoId;
	@JsonProperty("id")
	private Integer productId;
	private String category;
	private String title;
	private String description;
	private Integer price;
	private String texture;
	private String wash;
	private String place;
	private String note;
	private String story;
	private List<Map<String, Object>> colors;
	private List sizes;
	private List variants;
	@JsonProperty("main_image")
	private String mainImage;
	private List images;
}

package org.example.apiblitz.controller;

import org.example.apiblitz.model.ProductsDTO;
import org.example.apiblitz.model.UserSignIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/1.0/test")
public class TestController {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@PatchMapping("/PATCH/{id}")
	public void updateUserByIdPATCH(@PathVariable Long id, @RequestBody UserSignIn userSignIn) {

		String deductStockSql = "UPDATE user SET name = ? WHERE id = ?";
		jdbcTemplate.update(deductStockSql, userSignIn.getName(), id);

		System.out.println("User updated successfully");
	}

	@PutMapping("/PUT/{id}")
	public void updateUserByIdPUT(@PathVariable Long id, @RequestBody UserSignIn userSignIn) {

		String updateSql = "UPDATE user SET name = ? WHERE id = ?";
		jdbcTemplate.update(updateSql, userSignIn.getName(), id);

		System.out.println("User updated successfully");
	}

	@RequestMapping("/DELETE/{id}")
	public void deleteUserById(@PathVariable Long id) {

		String deductStockSql = "DELETE FROM user WHERE id = ?";
		jdbcTemplate.update(deductStockSql, id);

		System.out.println("User updated successfully");
	}

	@GetMapping("/HEAD")
	public ProductsDTO getProductDetails() {

		String getProductSql;
		ProductsDTO product;

		getProductSql = "SELECT * FROM product WHERE productId = 1";
		product = jdbcTemplate.queryForObject(getProductSql, new BeanPropertyRowMapper<>(ProductsDTO.class));

		return product;
	}

}

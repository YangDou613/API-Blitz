package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSignIn {
	@Id
	private Integer id;
	private String name;
	private String email;
	private String password;
	private String access_token;
}

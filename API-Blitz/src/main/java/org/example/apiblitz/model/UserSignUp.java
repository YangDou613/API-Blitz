package org.example.apiblitz.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSignUp implements User {
	@Id
	private Integer id;
	@NotBlank
	private String name;
	@Email
	@NotBlank
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
	private String email;
	@NotBlank
	@Size(min = 8)
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$")
	private String password;
}

package org.example.apiblitz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
	private Integer userId;
	private String category;
	private Integer id;
	private Timestamp testDateTime;
	private Object content;
	private Date createdAt;
}

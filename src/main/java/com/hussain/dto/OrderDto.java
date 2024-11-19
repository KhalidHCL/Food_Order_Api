package com.hussain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
public class OrderDto {
	private String deliveryAddress;
	private Long itemId;
	private Long userId;
	@NotNull(message = "Number of items is required")
	@Min(value = 1, message = "Number of items must be at least 1")
	private Integer nosOfItem;
	private FundTransferDto fundTransferDto;
}

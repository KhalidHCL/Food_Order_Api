package com.hussain.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOrderDto {
	
	private long orderId;
	private String deliveryAddress;
	private String itemNames;
	private String userName;
	private LocalDate orderDate;
	private Integer nosOfItem;
	private double orderPrice;
}

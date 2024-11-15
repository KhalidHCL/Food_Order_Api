package com.hussain.dto;

import java.util.List;

import lombok.Data;

@Data
public class MultiOrderDto {
	private Long userId;
    private String deliveryAddress;
    public  List<ItemCollection> itemCollection;
}

package com.hussain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiOrderDto {
	private Long userId;
    private String deliveryAddress;
    public  List<ItemCollection> itemCollection;
}

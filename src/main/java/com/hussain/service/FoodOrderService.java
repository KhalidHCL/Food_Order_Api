package com.hussain.service;

import java.util.List;

import com.hussain.dto.MultiOrderDto;
import com.hussain.dto.OrderDto;
import com.hussain.dto.ResponseOrderDto;
import com.hussain.entites.FoodItem;
import com.hussain.exception.FoodNotAvailable;
import com.hussain.exception.OrderNotFoundException;
import com.hussain.exception.TransactionFailException;
import com.hussain.exception.UserNotFoundException;
import com.hussain.exception.VendorNameIsNotFount;

public interface FoodOrderService {
	 public List<FoodItem> searchFoodByName(String name) throws FoodNotAvailable;
	 public List<FoodItem> searchVendorByName(String name) throws VendorNameIsNotFount;
	 public ResponseOrderDto placeSingleItemOrder(OrderDto dto) throws FoodNotAvailable, UserNotFoundException, VendorNameIsNotFount, TransactionFailException;
	 public ResponseOrderDto placeMultipleItemOrder(MultiOrderDto multipleOrderDto) throws FoodNotAvailable, UserNotFoundException;
	 public List<ResponseOrderDto> viewOrders(Long id) throws OrderNotFoundException;
}

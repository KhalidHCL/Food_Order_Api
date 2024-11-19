package com.hussain.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hussain.dto.MultiOrderDto;
import com.hussain.dto.OrderDto;
import com.hussain.dto.ResponseOrderDto;
import com.hussain.entites.FoodItem;
import com.hussain.exception.FoodNotAvailable;
import com.hussain.exception.OrderNotFoundException;
import com.hussain.exception.TransactionFailException;
import com.hussain.exception.UserNotFoundException;
import com.hussain.exception.VendorNameIsNotFount;
import com.hussain.service.FoodOrderService;

@RestController
@RequestMapping("/order")
public class FoodOrderController {

	@Autowired
	private FoodOrderService foodOrderService;
	

	@GetMapping("itemBy/{name}")
	public ResponseEntity<Object> searchFoodByName(@PathVariable("name") String name) throws FoodNotAvailable {
		List<FoodItem> searchFoodByName = foodOrderService.searchFoodByName(name);
		return new ResponseEntity<>(searchFoodByName, HttpStatus.ACCEPTED);
	}

	@GetMapping("itemBy/vendor")
	public ResponseEntity<List<FoodItem>> searchFoodByVendor(@RequestParam String name) throws VendorNameIsNotFount {
		List<FoodItem> searchVendorByName = foodOrderService.searchVendorByName(name);
		return new ResponseEntity<>(searchVendorByName, HttpStatus.OK);
	}

	@PostMapping("/orderSingle")
	public ResponseEntity<ResponseOrderDto> placeSingleItemOrder(@RequestBody OrderDto orderDto)
			throws FoodNotAvailable, UserNotFoundException, VendorNameIsNotFount, TransactionFailException {
		ResponseOrderDto placeSingleItemOrder = foodOrderService.placeSingleItemOrder(orderDto);
		return new ResponseEntity<>(placeSingleItemOrder, HttpStatus.ACCEPTED);
	}

	@PostMapping("/orderMultiple")
	public ResponseOrderDto placeMultipleItemOrder(@RequestBody MultiOrderDto multiOrderDto)
			throws FoodNotAvailable, UserNotFoundException {
		return foodOrderService.placeMultipleItemOrder(multiOrderDto);
	}

	// View all orders by userid
	@GetMapping("/viewOrders")
	public List<ResponseOrderDto> viewOrders(@RequestParam Long id) throws OrderNotFoundException {
		return foodOrderService.viewOrders(id);
	}
	
	
	
}

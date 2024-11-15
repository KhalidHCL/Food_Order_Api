package com.hussain.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hussain.dto.ItemCollection;
import com.hussain.dto.MultiOrderDto;
import com.hussain.dto.OrderDto;
import com.hussain.dto.ResponseOrderDto;
import com.hussain.entites.FoodItem;
import com.hussain.entites.Order;
import com.hussain.entites.OrderItem;
import com.hussain.entites.User;
import com.hussain.exception.FoodNotAvailable;
import com.hussain.exception.OrderNotFoundException;
import com.hussain.exception.UserNotFoundException;
import com.hussain.exception.VendorNameIsNotFount;
import com.hussain.repository.FoodItemRepository;
import com.hussain.repository.OrderRepository;
import com.hussain.repository.UserRepository;
import com.hussain.repository.VendorRepository;

@Service
public class FoodOrderServiceImple implements FoodOrderService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private FoodItemRepository foodItemRepository;

	@Override
	public List<FoodItem> searchFoodByName(String name) throws FoodNotAvailable {

		List<FoodItem> byItemNameContaining = foodItemRepository.findByItemNameContaining(name);
		if (byItemNameContaining.isEmpty()) {
			throw new FoodNotAvailable("Food Name is not available");
		}
		return byItemNameContaining;
	}

	@Override
	public List<FoodItem> searchVendorByName(String name) throws VendorNameIsNotFount {
        List<FoodItem> vendorName = foodItemRepository.findByVendorName(name);
		if (vendorName.isEmpty() || vendorName.size() < 0) {
			throw new VendorNameIsNotFount("vender Name is not Available ");
		}
		return vendorName;
	}

	public ResponseOrderDto placeSingleItemOrder(OrderDto orderDto)
			throws FoodNotAvailable, UserNotFoundException, VendorNameIsNotFount {
		User user = userRepository.findById(orderDto.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User is not Exist"));
		FoodItem fooditem = foodItemRepository.findById(orderDto.getItemId())
				.orElseThrow(() -> new FoodNotAvailable("Food item not found"));
		
		Integer nosOfItem = orderDto.getNosOfItem();
		if (nosOfItem == null || nosOfItem <= 0) {
			throw new IllegalArgumentException("Number of items must be greater than zero");
		}
		Order order = new Order();
		order.setDeliveryAddress(orderDto.getDeliveryAddress());
		order.setNosOfItem(nosOfItem);
		order.setItemName(fooditem.getItemName()); // Use the name from the food item
		order.setItemPrice(Double.parseDouble(fooditem.getItemprice()) * nosOfItem); // Calculate price
		order.setTotalPrice(Double.parseDouble(fooditem.getItemprice()) * nosOfItem);
		order.setUserDetails(user);
		order.setStatus("ordered");
		order.setOrderDate(LocalDate.now());
		order.setItems(List.of(fooditem));
		Order saveOrder = orderRepository.save(order);

		ResponseOrderDto returnOrder = new ResponseOrderDto();
		returnOrder.setOrderId(saveOrder.getOrderId());
		returnOrder.setItemNames(saveOrder.getItemName());
		returnOrder.setDeliveryAddress(saveOrder.getDeliveryAddress());
		returnOrder.setOrderDate(saveOrder.getOrderDate());
		returnOrder.setOrderPrice(saveOrder.getItemPrice());
		returnOrder.setNosOfItem(saveOrder.getNosOfItem());
		returnOrder.setUserName(user.getUserName());
		return returnOrder;

	}

//	public ResponseOrderDto placeMultipleItemOrder(MultiOrderDto multiOrderDto)
//	        throws FoodNotAvailable, UserNotFoundException {
//	    User user = userRepository.findById(multiOrderDto.getUserId())
//	            .orElseThrow(() -> new UserNotFoundException("User does not exist"));
//
//	    Order order = new Order();
//	    order.setDeliveryAddress(multiOrderDto.getDeliveryAddress());
//	    order.setOrderDate(LocalDate.now());
//	    order.setUserDetails(user);
//	    order.setStatus("ordered");
//
//	    List<OrderItem> orderItems = new ArrayList<>();
//	    double totalOrderPrice = 0.0;
//
//	    for (ItemCollection item : multiOrderDto.getItemCollection()) {
//	        FoodItem foodItem = foodItemRepository.findById(item.getItemId())
//	                .orElseThrow(() -> new FoodNotAvailable("Food item with ID " + item.getItemId() + " not found"));
//
//	        Integer quantity = item.getNoofItem();
//	        double itemTotalPrice = Double.parseDouble(foodItem.getItemprice()) * quantity;
//
//	        totalOrderPrice += itemTotalPrice;
//
//	        // Create an OrderItem for each item in the order
//	        OrderItem orderItem = new OrderItem();
//	        orderItem.setFoodItem(foodItem);
//	        orderItem.setOrder(order);
//	        orderItem.setQuantity(quantity);
//	        orderItem.setItemTotalPrice(itemTotalPrice);
//
//	        orderItems.add(orderItem);
//	    }
//
//	    order.setOrderItems(orderItems);
//	    order.setTotalPrice(totalOrderPrice);
//
//	    Order savedOrder = orderRepository.save(order);
//
//	    // Build response DTO
//	    ResponseOrderDto responseOrderDto = new ResponseOrderDto();
//	    responseOrderDto.setOrderId(savedOrder.getOrderId());
//	    responseOrderDto.setDeliveryAddress(savedOrder.getDeliveryAddress());
//	    responseOrderDto.setOrderDate(savedOrder.getOrderDate());
//	    responseOrderDto.setOrderPrice(totalOrderPrice);
//	    responseOrderDto.setUserName(user.getUserName());
//
//	    return responseOrderDto;
//	}
	
	public ResponseOrderDto placeMultipleItemOrder(MultiOrderDto multiOrderDto)
	        throws FoodNotAvailable, UserNotFoundException {
	    // Find user or throw exception
	    User user = userRepository.findById(multiOrderDto.getUserId())
	            .orElseThrow(() -> new UserNotFoundException("User does not exist"));

	    // Create and initialize the order object
	    Order order = new Order();
	    order.setDeliveryAddress(multiOrderDto.getDeliveryAddress());
	    order.setOrderDate(LocalDate.now());
	    order.setUserDetails(user);
	    order.setStatus("ordered");

	    // Initialize order items and calculate total price
	    List<OrderItem> orderItems = multiOrderDto.getItemCollection().stream()
	            .map(item -> {
	            	System.out.println(item.getItemId());
	                FoodItem foodItem = foodItemRepository.findById(item.getItemId()).orElseThrow();
	                Integer quantity = item.getNoofItem();
	                double itemTotalPrice = Double.parseDouble(foodItem.getItemprice()) * quantity;

	                // Create OrderItem
	                OrderItem orderItem = new OrderItem();
	                orderItem.setFoodItem(foodItem);
	                orderItem.setOrder(order);
	                orderItem.setQuantity(quantity);
	                orderItem.setItemTotalPrice(itemTotalPrice);
	                return orderItem;
	            })
	            .collect(Collectors.toList());

	    Double totalOrderPrice = orderItems.stream()
	            .mapToDouble(OrderItem::getItemTotalPrice)
	            .sum();

	    // Set calculated values in the order object
	    order.setOrderItems(orderItems);
	    order.setTotalPrice(totalOrderPrice);

	    // Save order to the repository
	    Order savedOrder = orderRepository.save(order);

	    // Build and return the response DTO
	    return new ResponseOrderDto(
	            savedOrder.getOrderId(), 
	            savedOrder.getDeliveryAddress(),
	            savedOrder.getItemName(),
	            user.getUserName(), savedOrder.getOrderDate(),
	            null, totalOrderPrice
	    );
	}
	
	@Override
	public List<ResponseOrderDto> viewOrders(Long id) throws OrderNotFoundException {
		List<Order> order = orderRepository.findByUserId(id);
		if(order.size()<0) {
			throw new OrderNotFoundException("User id is not Found");
		}
		List<ResponseOrderDto> result = order.stream().map(o -> mapToResponseOrderDto(o)).collect(Collectors.toList());
		return result;
	}

	static ResponseOrderDto mapToResponseOrderDto(Order order) {
		ResponseOrderDto dto = new ResponseOrderDto();
		dto.setOrderId(order.getOrderId());
		dto.setItemNames(order.getItemName());
		dto.setOrderDate(order.getOrderDate());
		dto.setDeliveryAddress(order.getDeliveryAddress());
		dto.setNosOfItem(order.getNosOfItem());
		dto.setOrderPrice(order.getItemPrice());
		return dto;
	}

}

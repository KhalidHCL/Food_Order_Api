package com.hussain.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hussain.client.BankAPIClient;
import com.hussain.client.OrderStatus;
import com.hussain.dto.FundTransferDto;
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
import com.hussain.exception.TransactionFailException;
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

	@Autowired
	private BankAPIClient bankClient;

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
		if (vendorName.isEmpty() || vendorName.size() <= 0) {
			throw new VendorNameIsNotFount("vender Name is not Available ");
		}
		return vendorName;
	}

	public ResponseOrderDto placeSingleItemOrder(OrderDto orderDto)
			throws FoodNotAvailable, UserNotFoundException, VendorNameIsNotFount, TransactionFailException {
		User user = userRepository.findById(orderDto.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User is not Exist"));
		FoodItem fooditem = foodItemRepository.findById(orderDto.getItemId())
				.orElseThrow(() -> new FoodNotAvailable("Food item not found"));
		
		Integer nosOfItem = orderDto.getNosOfItem();
		if (nosOfItem == null || nosOfItem <= 0) {
			throw new IllegalArgumentException("Number of items must be greater than zero");
		}
		Double totalAmount =orderDto.getFundTransferDto().setAmount((Double.parseDouble(fooditem.getItemprice()) * nosOfItem));
		String success = bankClient.transferFunds(orderDto.getFundTransferDto());	
		if(!success.equalsIgnoreCase("success")) {
			throw new TransactionFailException("Transaction is failed please try again");
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
//	    int totalQuantity = 0;
//	    for (ItemCollection item : multiOrderDto.getItemCollection()) {
//	        FoodItem foodItem = foodItemRepository.findById(item.getItemId())
//	                .orElseThrow(() -> new FoodNotAvailable("Food item with ID " + item.getItemId() + " not found"));
//
//	        Integer quantity = item.getNoofItem();
//	        double itemTotalPrice = Double.parseDouble(foodItem.getItemprice()) * quantity;
//
//	        totalOrderPrice += itemTotalPrice;
//	        totalQuantity += quantity;
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
//        order.setNosOfItem(totalQuantity);
//	    Order savedOrder = orderRepository.save(order);
//
//	    // Build response DTO
//	    ResponseOrderDto responseOrderDto = new ResponseOrderDto();
//	    responseOrderDto.setOrderId(savedOrder.getOrderId());
//	    responseOrderDto.setDeliveryAddress(savedOrder.getDeliveryAddress());
//	    responseOrderDto.setOrderDate(savedOrder.getOrderDate());
//	    responseOrderDto.setNosOfItem(savedOrder.getNosOfItem());
//	    responseOrderDto.setOrderPrice(totalOrderPrice);
//	    responseOrderDto.setUserName(user.getUserName());
//
//	    return responseOrderDto;
//	}

	// using java 8

//	public ResponseOrderDto placeMultipleItemOrder(MultiOrderDto multiOrderDto)
//	        throws FoodNotAvailable, UserNotFoundException {
//	    // Find user or throw exception
//	    User user = userRepository.findById(multiOrderDto.getUserId())
//	            .orElseThrow(() -> new UserNotFoundException("User does not exist"));
//	    
//	    if (multiOrderDto.getItemCollection() == null || multiOrderDto.getItemCollection().isEmpty()) {
//	        throw new IllegalArgumentException("Item collection cannot be null or empty");
//	    }
//	    // Create and initialize the order object
//	    Order order = new Order();
//	    order.setDeliveryAddress(multiOrderDto.getDeliveryAddress());
//	    order.setOrderDate(LocalDate.now());
//	    order.setUserDetails(user);
//	    order.setStatus("ordered");
//	    
//	    // Process items using Java 8 streams
//	    List<OrderItem> orderItems = multiOrderDto.getItemCollection().stream()
//	            .map(item -> {
//	                // Find food item or throw exception
//	               
//					try {
//						 FoodItem  foodItem = foodItemRepository.findById(item.getItemId())
//						        .orElseThrow(() -> new FoodNotAvailable("Food item with ID " + item.getItemId() + " not found"));
//					} catch (FoodNotAvailable e) {
//						e.printStackTrace();
//					}
//
//	                // Validate quantity
//	                if (item.getNoofItem() == null || item.getNoofItem() <= 0) {
//	                    throw new IllegalArgumentException("Invalid quantity for item ID " + item.getItemId());
//	                }
//
//	                // Calculate item total price and create OrderItem
//	                double itemTotalPrice = Double.parseDouble(foodItem.getItemprice()) * item.getNoofItem();
//	                OrderItem orderItem = new OrderItem();
//	                orderItem.setFoodItem(foodItem);
//	                orderItem.setOrder(order);
//	                orderItem.setQuantity(item.getNoofItem());
//	                orderItem.setItemTotalPrice(itemTotalPrice);
//	                return orderItem;
//	            })
//	            .collect(Collectors.toList());
//
//	    // Calculate total order price using streams
//	    double totalOrderPrice = orderItems.stream()
//	            .mapToDouble(OrderItem::getItemTotalPrice)
//	            .sum();
//	    // Set calculated values in the order object
//	    int totalItemCount = orderItems.stream()
//	            .mapToInt(OrderItem::getQuantity)
//	            .sum();
//	    order.setOrderItems(orderItems);
//	    order.setNosOfItem(totalItemCount);
//	    order.setTotalPrice(totalOrderPrice);
//
//	    // Save order to the repository
//	    Order savedOrder = orderRepository.save(order);
//
//	    // Build and return the response DTO
//	    return new ResponseOrderDto(
//	            savedOrder.getOrderId(), 
//	            savedOrder.getDeliveryAddress(),
//	            savedOrder.getItemName(),
//	            user.getUserName(), savedOrder.getOrderDate(),
//	            savedOrder.getNosOfItem(), totalOrderPrice
//	    );
//	}

	public ResponseOrderDto placeMultipleItemOrder(MultiOrderDto multiOrderDto)
			throws UserNotFoundException, FoodNotAvailable {
		// Validate the input DTO
		validateMultiOrderDto(multiOrderDto);

		// Find and validate the user
		User user = userRepository.findById(multiOrderDto.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User does not exist"));

		// Initialize order object
		Order order = initializeOrder(multiOrderDto, user);

		// Process item collection
		List<OrderItem> orderItems = processOrderItems(multiOrderDto, order);

		// Calculate and set total price and total item count
		double totalOrderPrice = calculateTotalOrderPrice(orderItems);
		int totalItemCount = calculateTotalItemCount(orderItems);

		order.setOrderItems(orderItems);
		order.setNosOfItem(totalItemCount);
		order.setTotalPrice(totalOrderPrice);

		// Save and return response DTO
		Order savedOrder = orderRepository.save(order);
		return buildResponseOrderDto(savedOrder, user, totalOrderPrice);
	}

	private void validateMultiOrderDto(MultiOrderDto multiOrderDto) {
		if (multiOrderDto.getItemCollection() == null || multiOrderDto.getItemCollection().isEmpty()) {
			throw new IllegalArgumentException("Item collection cannot be null or empty");
		}
	}

	private OrderItem createOrderItem(ItemCollection item, Order order) throws FoodNotAvailable {
		// Find food item or throw exception
		FoodItem foodItem = foodItemRepository.findById(item.getItemId())
				.orElseThrow(() -> new FoodNotAvailable("Food item with ID " + item.getItemId() + " not found"));

		// Validate quantity
		if (item.getNoofItem() == null || item.getNoofItem() <= 0) {
			throw new IllegalArgumentException("Invalid quantity for item ID " + item.getItemId());
		}

		// Calculate item total price and create OrderItem
		double itemTotalPrice = Double.parseDouble(foodItem.getItemprice()) * item.getNoofItem();
		OrderItem orderItem = new OrderItem();
		orderItem.setFoodItem(foodItem);
		orderItem.setOrder(order);
		orderItem.setQuantity(item.getNoofItem());
		orderItem.setItemTotalPrice(itemTotalPrice);

		return orderItem;
	}

	private Order initializeOrder(MultiOrderDto multiOrderDto, User user) {
		Order order = new Order();
		order.setDeliveryAddress(multiOrderDto.getDeliveryAddress());
		order.setOrderDate(LocalDate.now());
		order.setUserDetails(user);
		order.setStatus("ordered");
		return order;
	}

	private List<OrderItem> processOrderItems(MultiOrderDto multiOrderDto, Order order) {
		return multiOrderDto.getItemCollection().stream().map(item -> {
			try {
				return createOrderItem(item, order);
			} catch (FoodNotAvailable e) {
				throw new IllegalArgumentException(e);
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private double calculateTotalOrderPrice(List<OrderItem> orderItems) {
		return orderItems.stream().mapToDouble(OrderItem::getItemTotalPrice).sum();
	}

	private int calculateTotalItemCount(List<OrderItem> orderItems) {
		return orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
	}

	private ResponseOrderDto buildResponseOrderDto(Order savedOrder, User user, double totalOrderPrice) {
		return new ResponseOrderDto(savedOrder.getOrderId(), savedOrder.getDeliveryAddress(), savedOrder.getItemName(),
				user.getUserName(), savedOrder.getOrderDate(), savedOrder.getNosOfItem(), totalOrderPrice);
	}

	// 5 view all the order
	@Override
	public List<ResponseOrderDto> viewOrders(Long id) throws OrderNotFoundException {
		List<Order> order = orderRepository.findByUserId(id);
		if (order.isEmpty()) {
			throw new OrderNotFoundException("User id is not Found");
		}
		List<ResponseOrderDto> result = order.stream().map(o -> mapToResponseOrderDto(o)).collect(Collectors.toList());
		return result;
	}

	public static ResponseOrderDto mapToResponseOrderDto(Order order) {
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

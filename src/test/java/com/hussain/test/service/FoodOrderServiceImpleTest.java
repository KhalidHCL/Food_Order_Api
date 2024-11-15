package com.hussain.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hussain.dto.OrderDto;
import com.hussain.dto.ResponseOrderDto;
import com.hussain.entites.FoodItem;
import com.hussain.entites.Order;
import com.hussain.entites.User;
import com.hussain.entites.Vendor;
import com.hussain.exception.FoodNotAvailable;
import com.hussain.exception.UserNotFoundException;
import com.hussain.exception.VendorNameIsNotFount;
import com.hussain.repository.FoodItemRepository;
import com.hussain.repository.OrderRepository;
import com.hussain.repository.UserRepository;
import com.hussain.repository.VendorRepository;
import com.hussain.service.FoodOrderServiceImple;

public class FoodOrderServiceImpleTest {
	@Mock
    private UserRepository userRepository;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @InjectMocks
    private FoodOrderServiceImple foodOrderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
     // 1 test for serch by item api 
    @Test
    public void testSearchFoodByName_FoodAvailable() throws FoodNotAvailable {
        FoodItem foodItem = new FoodItem();
        foodItem.setItemName("Pizza");

        when(foodItemRepository.findByItemNameContaining("Pizza")).thenReturn(List.of(foodItem));

        List<FoodItem> result = foodOrderService.searchFoodByName("Pizza");

        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getItemName());
    }
    
    @Test
    public void testSearchFoodByName_FoodNotAvailable() {
        when(foodItemRepository.findByItemNameContaining("Burger")).thenReturn(new ArrayList<>());

        assertThrows(FoodNotAvailable.class, () -> foodOrderService.searchFoodByName("Burger"));
    }
    
    // 2. serch item for the vendor name api
    
    @Test
    public void testSearchVendorByName_VendorAvailable() throws VendorNameIsNotFount {
    	Vendor vendor = new Vendor(1L, "ABC Foods", "123 Main Street");
        
        FoodItem foodItem1 = new FoodItem(1L, "Pizza", "12.50", vendor);
        FoodItem foodItem2 = new FoodItem(2L, "Burger", "8.50", vendor);

        // Mock the repository behavior
        when(foodItemRepository.findByVendorName("ABC Foods")).thenReturn(List.of(foodItem1, foodItem2));

        // Call the service method
        List<FoodItem> result = foodOrderService.searchVendorByName("ABC Foods");

        // Assertions
        assertEquals(2, result.size());
        assertEquals("Pizza", result.get(0).getItemName());
        assertEquals("Burger", result.get(1).getItemName());
    }
    
    @Test
    public void testSearchVendorByName_VendorHasNoItems() {
        // Set up the vendor
        Vendor vendor = new Vendor(2L, "Empty Vendor", "456 Oak Street");

        // Mock the repository behavior to return an empty list for this vendor
        when(foodItemRepository.findByVendorName("Empty Vendor")).thenReturn(new ArrayList<>());

        // Verify that an exception is thrown when the vendor has no items
        assertThrows(VendorNameIsNotFount.class, () -> foodOrderService.searchVendorByName("Empty Vendor"));
    }
    @Test
    public void testSearchVendorByName_VendorNotAvailable() {
        when(foodItemRepository.findByVendorName("XYZ")).thenReturn(new ArrayList<>());

        assertThrows(VendorNameIsNotFount.class, () -> foodOrderService.searchVendorByName("XYZ"));
    }
    
    //3 one order place order api test case
    
    @Test
    void testPlaceSingleItemOrder_Success() throws FoodNotAvailable, UserNotFoundException, VendorNameIsNotFount {
        // Prepare test data
        Long userId = 1L;
        Long itemId = 101L;
        Integer nosOfItem = 2;

        User user = new User(userId, "John Doe", "john@example.com", "1234567890", "123 Street");
        FoodItem foodItem = new FoodItem(itemId, "Burger", "5.99", null);
        Order savedOrder = new Order();
        savedOrder.setOrderId(1L);
        savedOrder.setOrderDate(LocalDate.now());
        savedOrder.setDeliveryAddress("456 Avenue");
        savedOrder.setNosOfItem(nosOfItem);
        savedOrder.setItemName("Burger");
        savedOrder.setItemPrice(11.98);
        savedOrder.setTotalPrice(11.98);
        savedOrder.setStatus("ordered");
        savedOrder.setUserDetails(user);

        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(userId);
        orderDto.setItemId(itemId);
        orderDto.setNosOfItem(nosOfItem);
        orderDto.setDeliveryAddress("456 Avenue");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(foodItemRepository.findById(itemId)).thenReturn(Optional.of(foodItem));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Execute the method
        ResponseOrderDto response = foodOrderService.placeSingleItemOrder(orderDto);

        // Verify the result
        assertNotNull(response);
        assertEquals(savedOrder.getOrderId(), response.getOrderId());
        assertEquals(savedOrder.getItemName(), response.getItemNames());
        assertEquals(savedOrder.getDeliveryAddress(), response.getDeliveryAddress());
        assertEquals(savedOrder.getTotalPrice(), response.getOrderPrice());
        assertEquals(savedOrder.getNosOfItem(), response.getNosOfItem());

        // Verify interactions with repositories
        verify(userRepository, times(1)).findById(userId);
        verify(foodItemRepository, times(1)).findById(itemId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testPlaceSingleItemOrder_UserNotFoundException() {
    	Long userId = 1L;
        Long itemId = 101L;

        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(userId);
        orderDto.setItemId(itemId);
        orderDto.setNosOfItem(1);

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(foodItemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Assert UserNotFoundException is thrown
        UserNotFoundException exception =assertThrows(UserNotFoundException.class, () -> foodOrderService.placeSingleItemOrder(orderDto));
        assertEquals("User is not Exist", exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(foodItemRepository);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void testPlaceSingleItemOrder_FoodNotAvailable() {
        // Prepare test data
        Long userId = 1L;
        Long itemId = 101L;
        Integer nosOfItem = 2;

        User user = new User(userId, "John Doe", "john@example.com", "1234567890", "123 Street");

        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(userId);
        orderDto.setItemId(itemId);
        orderDto.setNosOfItem(nosOfItem);
        orderDto.setDeliveryAddress("456 Avenue");

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(foodItemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Execute and assert exception
        assertThrows(FoodNotAvailable.class, () -> foodOrderService.placeSingleItemOrder(orderDto));

        // Verify interactions
        verify(userRepository, times(1)).findById(userId);
        verify(foodItemRepository, times(1)).findById(itemId);
        verifyNoInteractions(orderRepository);
    }
    
    
    
}

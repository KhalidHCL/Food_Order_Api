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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hussain.dto.ItemCollection;
import com.hussain.dto.MultiOrderDto;
import com.hussain.dto.OrderDto;
import com.hussain.dto.ResponseOrderDto;
import com.hussain.entites.FoodItem;
import com.hussain.entites.Order;
import com.hussain.entites.User;
import com.hussain.entites.Vendor;
import com.hussain.exception.FoodNotAvailable;
import com.hussain.exception.OrderNotFoundException;
import com.hussain.exception.UserNotFoundException;
import com.hussain.exception.VendorNameIsNotFount;
import com.hussain.repository.FoodItemRepository;
import com.hussain.repository.OrderRepository;
import com.hussain.repository.UserRepository;
import com.hussain.repository.VendorRepository;
import com.hussain.service.FoodOrderServiceImple;

@ExtendWith(MockitoExtension.class)
class FoodOrderServiceImpleTest {
	
	@Mock
    private UserRepository userRepository;
	
	 @Mock
	 private FoodItemRepository foodItemRepository;
	 
	 @Mock
	 private OrderRepository orderRepository;
	 
	 @InjectMocks
	 private FoodOrderServiceImple foodOrderService;


    @Mock
    private VendorRepository vendorRepository;
    
    private User user;
    private FoodItem foodItem;
    private Order order;
    private OrderDto orderDto;
    private MultiOrderDto multiOrderDto;
    
    

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "John Doe", "john@example.com", "1234567890", "123 Main St");
        Vendor vendor=new Vendor(1L,"zepto","patna");
        foodItem = new FoodItem(1L, "Pizza", "50.0", vendor);
        order = new Order();
        order.setOrderId(1L);
        order.setUserDetails(user);
        order.setItemName("Pizza");
        order.setItemPrice(100.0);
        order.setTotalPrice(100.0);
        order.setNosOfItem(2);
        order.setDeliveryAddress("123 Main St");
        order.setOrderDate(LocalDate.now());
        order.setStatus("ordered");
        
        orderDto = new OrderDto();
        orderDto.setUserId(1L);
        orderDto.setItemId(1L);
        orderDto.setDeliveryAddress("123 Main St");
        orderDto.setNosOfItem(2);

        multiOrderDto = new MultiOrderDto();
        multiOrderDto.setUserId(1L);
        multiOrderDto.setDeliveryAddress("123 Main St");
        ItemCollection item1 = new ItemCollection(1L, 2);
        ItemCollection item2 = new ItemCollection(2L, 3);
        multiOrderDto.setItemCollection(List.of(item1, item2));	
    }
     // 1 test for serch by item api 
    @Test
    void testSearchFoodByName_FoodAvailable() throws FoodNotAvailable {
        foodItem = new FoodItem();
        foodItem.setItemName("Pizza");

        when(foodItemRepository.findByItemNameContaining("Pizza")).thenReturn(List.of(foodItem));

        List<FoodItem> result = foodOrderService.searchFoodByName("Pizza");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getItemName());
    }
    
    @Test
    void testSearchFoodByName_FoodNotAvailable() {
        when(foodItemRepository.findByItemNameContaining("Burger")).thenReturn(new ArrayList<>());

        assertThrows(FoodNotAvailable.class, () -> foodOrderService.searchFoodByName("Burger"));
        
    }
    
    // 2. serch item for the vendor name api
    
    @Test
    void testSearchVendorByName_VendorAvailable() throws VendorNameIsNotFount {
    	Vendor vendor = new Vendor(1L, "ABC Foods", "123 Main Street");
        
        FoodItem foodItem1 = new FoodItem(1L, "Pizza", "12.50", vendor);
        FoodItem foodItem2 = new FoodItem(2L, "Burger", "8.50", vendor);

        // Mock the repository behavior
        when(foodItemRepository.findByVendorName("ABC Foods")).thenReturn(List.of(foodItem1, foodItem2));

        // Call the service method
        List<FoodItem> result = foodOrderService.searchVendorByName("ABC Foods");

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Pizza", result.get(0).getItemName());
        assertEquals("Burger", result.get(1).getItemName());
    }
    
    @Test
    void testSearchVendorByName_VendorHasNoItems() {
        // Set up the vendor
        Vendor vendor = new Vendor(2L, "Empty Vendor", "456 Oak Street");

        // Mock the repository behavior to return an empty list for this vendor
        when(foodItemRepository.findByVendorName("Empty Vendor")).thenReturn(new ArrayList<>());

        // Verify that an exception is thrown when the vendor has no items
        assertThrows(VendorNameIsNotFount.class, () -> foodOrderService.searchVendorByName("Empty Vendor"));
    }
    @Test
    void testSearchVendorByName_VendorNotAvailable() {
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

        user = new User(userId, "John Doe", "john@example.com", "1234567890", "123 Street");
        foodItem = new FoodItem(itemId, "Burger", "5.99", null);
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

        orderDto = new OrderDto();
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

        orderDto = new OrderDto();
        orderDto.setUserId(userId);
        orderDto.setItemId(itemId);
        orderDto.setNosOfItem(1);

        // Mock repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//        when(foodItemRepository.findById(itemId)).thenReturn(Optional.empty());

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

        user = new User(userId, "John Doe", "john@example.com", "1234567890", "123 Street");

        orderDto = new OrderDto();
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
    
    // 4 . order place multiple 
    @Test
    void testPlaceMultipleItemOrder_Success() throws Exception {
        // Arrange
        Long userId = 1L;

        multiOrderDto = new MultiOrderDto();
        multiOrderDto.setUserId(userId);
        multiOrderDto.setDeliveryAddress("123 Main Street");

        List<ItemCollection> itemCollections = List.of(
            new ItemCollection(101L, 2), // Item 1: 2 quantities
            new ItemCollection(102L, 3)  // Item 2: 3 quantities
        );
        multiOrderDto.setItemCollection(itemCollections);

        // Mock user
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUserName("John Doe");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Mock food items
        FoodItem item1 = new FoodItem(101L, "Burger", "5.0", null);
        FoodItem item2 = new FoodItem(102L, "Pizza", "10.0", null);

        when(foodItemRepository.findById(101L)).thenReturn(Optional.of(item1));
        when(foodItemRepository.findById(102L)).thenReturn(Optional.of(item2));

        // Mock order saving
        Order mockOrder = new Order();
        mockOrder.setOrderId(1L);
        mockOrder.setDeliveryAddress("123 Main Street");
        mockOrder.setUserDetails(mockUser);
        mockOrder.setOrderDate(LocalDate.now());
        mockOrder.setTotalPrice(40.0); // (5.0 * 2) + (10.0 * 3)

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // Act
        ResponseOrderDto response = foodOrderService.placeMultipleItemOrder(multiOrderDto);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals("123 Main Street", response.getDeliveryAddress());
        assertEquals("John Doe", response.getUserName());
        assertEquals(40.0, response.getOrderPrice());

        // Verify interactions
        verify(userRepository, times(1)).findById(userId);
        verify(foodItemRepository, times(1)).findById(101L);
        verify(foodItemRepository, times(1)).findById(102L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
     
    @Test
    void testPlaceMultipleItemOrder_UserNotFound() {
        // Arrange
        Long userId = 1L;

        multiOrderDto = new MultiOrderDto();
        multiOrderDto.setUserId(userId);
        multiOrderDto.setItemCollection(List.of(new ItemCollection(101L, 2)));
        multiOrderDto.setDeliveryAddress("123 Main Street");

        // Mock user not found
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, 
            () -> foodOrderService.placeMultipleItemOrder(multiOrderDto));
        assertEquals("User does not exist", exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(foodItemRepository);
        verifyNoInteractions(orderRepository);
    }
    
    @Test
    void testPlaceMultipleItemOrder_emptyItemCollection() {
        multiOrderDto = new MultiOrderDto();
        multiOrderDto.setUserId(1L);
        multiOrderDto.setItemCollection(new ArrayList<>()); // Empty collection

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            foodOrderService.placeMultipleItemOrder(multiOrderDto);
        });
        assertEquals("Item collection cannot be null or empty", thrown.getMessage());
    }
    
    @Test
    void testViewOrders_Success() throws OrderNotFoundException {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));

        List<ResponseOrderDto> result = foodOrderService.viewOrders(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getItemNames());
    }

    @Test
    void testViewOrders_OrderNotFound() {
        when(orderRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        assertThrows(OrderNotFoundException.class, () -> foodOrderService.viewOrders(1L));
    }

}

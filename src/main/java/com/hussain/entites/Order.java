package com.hussain.entites;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="order_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long orderId;
	private LocalDate orderDate;
	private String deliveryAddress;
	private String status;
	private String itemName;
	private Double itemPrice;
	private Double totalPrice;
	private Integer nosOfItem;
	
	@ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User userDetails;
	
	@ManyToMany
    @JoinTable(
            name = "order_food",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
	private List<FoodItem> items;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}

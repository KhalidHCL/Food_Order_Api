package com.hussain.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="FoodItem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FoodItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long itemId;
	private String itemName;
	private String itemprice;
	
	@ManyToOne 
	@JoinColumn(name = "vendor_id", referencedColumnName = "vendorId")
	private Vendor vendor;
	
}

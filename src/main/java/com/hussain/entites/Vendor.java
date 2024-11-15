package com.hussain.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="vendorDetails")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Vendor {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long vendorId;
	private String vendorName;
	private String venderAddress;
}

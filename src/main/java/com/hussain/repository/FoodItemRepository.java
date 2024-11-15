package com.hussain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hussain.entites.FoodItem;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem,Long>{
	List<FoodItem> findByItemNameContaining(String itemName);
	
	@Query("SELECT f FROM FoodItem f WHERE f.vendor.vendorName = :vendorName")
    List<FoodItem> findByVendorName(@Param("vendorName") String vendorName);
}

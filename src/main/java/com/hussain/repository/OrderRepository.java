package com.hussain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hussain.entites.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long>{

	 @Query("SELECT o FROM Order o WHERE o.userDetails.userId = :userId")
	  List<Order> findByUserId(Long userId);
}

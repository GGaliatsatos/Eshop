package com.videogamestore.repository;

import org.springframework.data.repository.CrudRepository;

import com.videogamestore.domain.Order;

public interface OrderRepository extends CrudRepository<Order, Long>{

	
}

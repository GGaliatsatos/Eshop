package com.videogamestore.repository;

import org.springframework.data.repository.CrudRepository;

import com.videogamestore.domain.ShoppingCart;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long>{

}

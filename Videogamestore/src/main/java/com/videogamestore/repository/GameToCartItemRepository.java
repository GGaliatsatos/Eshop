package com.videogamestore.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import com.videogamestore.domain.CartItem;
import com.videogamestore.domain.GameToCartItem;

@Transactional
public interface GameToCartItemRepository extends CrudRepository<GameToCartItem, Long>{
	void deleteByCartItem(CartItem cartItem);

}

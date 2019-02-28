package com.videogamestore.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.videogamestore.domain.CartItem;
import com.videogamestore.domain.Game;
import com.videogamestore.domain.GameToCartItem;
import com.videogamestore.domain.Order;
import com.videogamestore.domain.ShoppingCart;
import com.videogamestore.domain.User;
import com.videogamestore.repository.CartItemRepository;
import com.videogamestore.repository.GameToCartItemRepository;
import com.videogamestore.repository.UserRepository;
import com.videogamestore.service.CartItemService;

@Service
public class CartItemServiceImpl implements CartItemService{
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private GameToCartItemRepository gameToCartItemRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public List<CartItem> findByShoppingCart(ShoppingCart shoppingCart){
		return cartItemRepository.findByShoppingCart(shoppingCart);
	}
	
	public CartItem updateCartItem(CartItem cartItem) {
		BigDecimal multiplicant;
		if(cartItem.isRewardDiscount()) {
			multiplicant = new BigDecimal(0.9);
		}else {
			multiplicant = new BigDecimal(1.0);
		}
		BigDecimal bigDecimal = new BigDecimal(cartItem.getGame().getDiscountPrice())
									.multiply(multiplicant);
		
		bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
		cartItem.setSubtotal(bigDecimal);
		
		cartItemRepository.save(cartItem);
		return cartItem;
	}

	@Override
	public CartItem addGameToCartItem(Game game, User user, int qty, boolean rewardDiscount) {
		
		List<CartItem> cartItemList = findByShoppingCart(user.getShoppingCart());
		for(CartItem cartItem: cartItemList) {
			if(game.getId() == cartItem.getGame().getId()) {
				cartItemRepository.save(cartItem);
				return cartItem;
			}
		}
		
		CartItem cartItem = new CartItem();
		cartItem.setShoppingCart(user.getShoppingCart());
		cartItem.setGame(game);		
		cartItem.setQty(qty);
		if(rewardDiscount) {
			BigDecimal bigDecimal = new BigDecimal(cartItem.getGame().getDiscountPrice())
					.multiply(new BigDecimal(0.9));
			bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
			cartItem.setSubtotal(bigDecimal);
			user.setCredits(user.getCredits()-50);
			userRepository.save(user);
		}else {
			System.out.println("executed this code.");
			cartItem.setSubtotal(new BigDecimal(game.getDiscountPrice()));
		}
		cartItem.setRewardDiscount(rewardDiscount);
		cartItemRepository.save(cartItem);
		
		GameToCartItem gameToCartItem = new GameToCartItem();
		gameToCartItem.setGame(game);
		gameToCartItem.setCartItem(cartItem);
		gameToCartItemRepository.save(gameToCartItem);
		
		return cartItem;
	}

	@Override
	public CartItem findById(Long cartItemId) {
		
		return cartItemRepository.findOne(cartItemId);
	}

	@Override
	public void removeCartItem(CartItem cartItem) {
		gameToCartItemRepository.deleteByCartItem(cartItem);
		if(cartItem.isRewardDiscount()) {
			User user = cartItem.getShoppingCart().getUser();
			user.setCredits(user.getCredits()+50);
			userRepository.save(user);
		}
		cartItemRepository.delete(cartItem);		
	}

	@Override
	public CartItem save(CartItem cartItem) {
		return cartItemRepository.save(cartItem);
	}

	@Override
	public List<CartItem> findByOrder(Order order) {
		return cartItemRepository.findByOrder(order);
	}
	
	

	
	
	
	
	
	
	
}

package com.videogamestore.service;

import com.videogamestore.domain.BillingAddress;
import com.videogamestore.domain.Order;
import com.videogamestore.domain.Payment;
import com.videogamestore.domain.ShoppingCart;
import com.videogamestore.domain.User;

public interface OrderService {
	Order createOrder(ShoppingCart shoppingCart, BillingAddress billingAddress,
					Payment payment, User user);
	
	Order findOne(Long id);

}

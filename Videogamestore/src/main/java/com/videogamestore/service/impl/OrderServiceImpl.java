package com.videogamestore.service.impl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.videogamestore.domain.BillingAddress;
import com.videogamestore.domain.CartItem;
import com.videogamestore.domain.Game;
import com.videogamestore.domain.Order;
import com.videogamestore.domain.Payment;
import com.videogamestore.domain.ShoppingCart;
import com.videogamestore.domain.User;
import com.videogamestore.repository.OrderRepository;
import com.videogamestore.service.CartItemService;
import com.videogamestore.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService{

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Override
	public synchronized Order createOrder(ShoppingCart shoppingCart, BillingAddress billingAddress, Payment payment, User user) {
		Order order = new Order();
		order.setBillingAddress(billingAddress);
		order.setPayment(payment);
		
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		
		for(CartItem cartItem : cartItemList) {
			Game game = cartItem.getGame();
			cartItem.setOrder(order);
			game.setInStockNumber(game.getInStockNumber() - 1);
			
		}
		order.setCartItemList(cartItemList);
		order.setOrderDate(Calendar.getInstance().getTime());
		order.setOrderTotal(shoppingCart.getGrandTotal());
		billingAddress.setOrder(order);
		payment.setOrder(order);
		order.setUser(user);
		order = orderRepository.save(order);
		return order;
	}

	@Override
	public Order findOne(Long id) {
		return orderRepository.findOne(id);
	}

	
	
}

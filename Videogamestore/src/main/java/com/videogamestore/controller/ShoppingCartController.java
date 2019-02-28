package com.videogamestore.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.videogamestore.domain.CartItem;
import com.videogamestore.domain.Game;
import com.videogamestore.domain.ShoppingCart;
import com.videogamestore.domain.User;
import com.videogamestore.service.CartItemService;
import com.videogamestore.service.GameService;
import com.videogamestore.service.ShoppingCartService;
import com.videogamestore.service.UserService;

@Controller
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private GameService gameService;
	
	@Autowired
	private ShoppingCartService shoppingCartService;
	
	@RequestMapping("/cart")
	public String shoppingCart(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		ShoppingCart shoppingCart = user.getShoppingCart();
		
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		shoppingCartService.updateShoppingCart(shoppingCart);
		
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", shoppingCart);
		return "shoppingCart";
	}
	
	@RequestMapping("/addItem")
	public String addItem(@ModelAttribute("game") Game game,							
							Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		game = gameService.findOne(game.getId());
		if(1 > game.getInStockNumber()) {
			model.addAttribute("notEnoughStock", true);
			return "forward:/game?id=+"+game.getId();
		}
		if(user.getOwnedGames().contains(game)) {
			model.addAttribute("alreadyPurchased", true);
			return "redirect:/game?id=+"+game.getId();
		}
		if(user.getCredits() > 50 && (game.getGenre().equals(user.getPurchaseBasedFavoriteGenre()) 
				|| game.getGenre().equals(user.getFavoriteGenre())) ) {
			CartItem cartItem = cartItemService.addGameToCartItem(game, user, 1, true);
		}else {			
			CartItem cartItem = cartItemService.addGameToCartItem(game, user, 1, false);
		}
		model.addAttribute("addGameSuccess", true);
		
		return "redirect:/game?id=+"+game.getId();
	}
	
	@RequestMapping("/updateCartItem")
	public String updateItem(@ModelAttribute("id") Long cartItemId,
							 @ModelAttribute("qty") int qty) {
		CartItem cartItem = cartItemService.findById(cartItemId);
		cartItem.setQty(qty);
		cartItemService.updateCartItem(cartItem);
		
		return "forward:/shoppingCart/cart";
	}
	
	@RequestMapping("/removeItem")
	public String removeItem(@RequestParam("id") Long id) {
		cartItemService.removeCartItem(cartItemService.findById(id));
		return "forward:/shoppingCart/cart";
	}
}

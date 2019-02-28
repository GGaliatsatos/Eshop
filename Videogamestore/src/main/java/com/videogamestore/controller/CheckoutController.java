package com.videogamestore.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import com.videogamestore.domain.BillingAddress;
import com.videogamestore.domain.CartItem;
import com.videogamestore.domain.Game;
import com.videogamestore.domain.Order;
import com.videogamestore.domain.Payment;
import com.videogamestore.domain.ShoppingCart;
import com.videogamestore.domain.User;
import com.videogamestore.domain.UserBilling;
import com.videogamestore.domain.UserPayment;
import com.videogamestore.domain.enumeration.Genre;
import com.videogamestore.service.BillingAddressService;
import com.videogamestore.service.CartItemService;
import com.videogamestore.service.OrderService;
import com.videogamestore.service.PaymentService;
import com.videogamestore.service.ShoppingCartService;
import com.videogamestore.service.UserPaymentService;
import com.videogamestore.service.UserService;
import com.videogamestore.utility.MailConstructor;

@Controller
public class CheckoutController {

	private BillingAddress billingAddress = new BillingAddress();
	private Payment payment = new Payment();
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private MailConstructor mailConstructor;
		
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private ShoppingCartService shoppingCartService;
	
	
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private UserPaymentService userPaymentService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private BillingAddressService billingAddressService;
	
	@RequestMapping("/checkout")
	private String checkout(@RequestParam("id") Long cartId,
							@RequestParam(value="missingRequiredField", required=false) boolean missingRequiredField,
							Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());		
		model.addAttribute("user",user);
		
		if(cartId != user.getShoppingCart().getId()) {
			return "badRequestPage";
		}
		
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());
		
		if(cartItemList.size() == 0 ) {
			model.addAttribute("emptyCart", true);
			return "forward:/shoppingCart/cart";
		}
		
		for(CartItem cartItem : cartItemList) {
			if(cartItem.getGame().getInStockNumber() < 1L) {
			//if(cartItem.getGame().getInStockNumber() < cartItem.getQty()) {
				model.addAttribute("notEnoughStock", true);
				return "forward:/shoppingCart/cart";
			}
			if(user.getOwnedGames().contains(cartItem.getGame())) {
			//if(cartItem.getGame().getInStockNumber() < cartItem.getQty()) {
				model.addAttribute("alreadyOwned", true);
				return "forward:/shoppingCart/cart";
			}
		}
		
		List<UserPayment> userPaymentList = user.getUserPaymentList();
		
		model.addAttribute("userPaymentList",userPaymentList);
		if(userPaymentList.size() == 0) {
			model.addAttribute("emptyPaymentList", true);
			
		}else {
			model.addAttribute("emptyPaymentList", false);
		}
		
		ShoppingCart shoppingCart = user.getShoppingCart();
		
		for(UserPayment userPayment: userPaymentList) {
			if(userPayment.isDefaultPayment()) {
				paymentService.setByUserPayment(userPayment, payment);
				billingAddressService.setByUserBilling(userPayment.getUserBilling(), billingAddress);
			}
		}
		
		model.addAttribute("payment", payment);
		model.addAttribute("billingAddress", billingAddress);
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", user.getShoppingCart());
		
		if(missingRequiredField) {
			model.addAttribute("missingRequiredField", true);
		}
		
		return "checkout";
	}
	
	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public String checkoutPost(Principal principal, Model model,
			@ModelAttribute("billingAddress") BillingAddress billingAddress, 
			@ModelAttribute("payment") Payment payment
			) {
		ShoppingCart shoppingCart = userService.findByUsername(principal.getName()).getShoppingCart();

		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		model.addAttribute("cartItemList", cartItemList);

		if (payment.getHolderName().isEmpty()
				|| payment.getCardNumber().isEmpty()
				|| payment.getCvc() == 0 || billingAddress.getBillingAddressStreet1().isEmpty()
				|| billingAddress.getBillingAddressCity().isEmpty() 
				|| billingAddress.getBillingAddressName().isEmpty()
				|| billingAddress.getBillingAddressZipcode().isEmpty())
			return "redirect:/checkout?id=" + shoppingCart.getId() + "&missingRequiredField=true";
		
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		for (CartItem cartItem: cartItemList) {
			Game game = cartItem.getGame();
			game.setSalesNumber(game.getSalesNumber()+1);
			if(!user.getOwnedGames().contains(game)) {				
				// add games to user owned games set
				user.getOwnedGames().add(game);
				// add user to owned games games set
				game.getOwnerList().add(user);
				// reward the user with credits for future discounts
				user.setCredits(user.getCredits()+25);
			}
		}
		List<Game> ownedGames =  new ArrayList<>();
		ownedGames.addAll(user.getOwnedGames());
		user.setPurchaseBasedFavoriteGenre(updatePurchaseBasedFavoriteGenre(ownedGames));
		
		
		Order order = orderService.createOrder(shoppingCart, billingAddress, payment, user);
		
		mailSender.send(mailConstructor.constructOrderConfirmationEmail(user, order, Locale.ENGLISH));
		
		shoppingCartService.clearShoppingCart(shoppingCart);
		
		return "orderSubmittedPage";
	}
	
	@RequestMapping("/setPaymentMethod")
	public String setPaymentMethod(@RequestParam("userPaymentId") Long userPaymentId, Principal principal,
			Model model) {
		
		User user = userService.findByUsername(principal.getName());
		UserPayment userPayment = userPaymentService.findById(userPaymentId);
		UserBilling userBilling = userPayment.getUserBilling();

		model.addAttribute("user", user);
		if (userPayment.getUser().getId() != user.getId()) {
			return "badRequestPage";
		} else {
			paymentService.setByUserPayment(userPayment, payment);

			List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

			billingAddressService.setByUserBilling(userBilling, billingAddress);

			
			model.addAttribute("payment", payment);
			model.addAttribute("billingAddress", billingAddress);
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("shoppingCart", user.getShoppingCart());


			List<UserPayment> userPaymentList = user.getUserPaymentList();

			model.addAttribute("userPaymentList", userPaymentList);


			model.addAttribute("classActivePayment", true);

			model.addAttribute("emptyPaymentList", false);

			return "checkout";
		}
	}
	
		
	String updatePurchaseBasedFavoriteGenre(List<Game> ownedGames) {
		int countFavorite = 0;
		String purchaseBasedFavoriteGenre= "";	
		ListIterator<Game> games = ownedGames.listIterator();
		// for each genre count occurrences on list and remove games that matched a genre.
		for(Genre genre: Genre.values()) {
			int genreOccurences = 0;
			if(games.hasNext()) {// "forward" list iteration			
				while(games.hasNext()) {
					if(games.next().getGenre().equals(genre.toString())) {					
						genreOccurences++;				
						games.remove();
						if(genreOccurences > countFavorite) {
							countFavorite = genreOccurences;
							purchaseBasedFavoriteGenre = genre.toString();
						}						
					}
				}
			}else if(games.hasPrevious()){//"backward" list iteration
				while(games.hasPrevious()) {
					if(games.previous().getGenre().equals(genre.toString())) {					
						genreOccurences++;				
						games.remove();
						if(genreOccurences > countFavorite) {
							countFavorite = genreOccurences;
							purchaseBasedFavoriteGenre = genre.toString();
						}						
					}
				}				
			}else {
				break; // all games have been matched with available genres.
			}
		}
		return purchaseBasedFavoriteGenre;
	}
	
	
}

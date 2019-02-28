package com.videogamestore.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.videogamestore.domain.Game;
import com.videogamestore.domain.ShoppingCart;
import com.videogamestore.domain.User;
import com.videogamestore.domain.UserBilling;
import com.videogamestore.domain.UserPayment;
import com.videogamestore.domain.enumeration.Genre;
import com.videogamestore.domain.security.PasswordResetToken;
import com.videogamestore.domain.security.UserRole;
import com.videogamestore.repository.GameRepository;
import com.videogamestore.repository.PasswordResetTokenRepository;
import com.videogamestore.repository.RoleRepository;
import com.videogamestore.repository.UserPaymentRepository;
import com.videogamestore.repository.UserRepository;
import com.videogamestore.service.UserService;


@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserPaymentRepository userPaymentRepository;
	
	@Autowired
	private RoleRepository roleRepository;
		
	@Autowired
	private GameRepository gameRepository;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Override
	public PasswordResetToken getPasswordResetToken(String token) {
		return passwordResetTokenRepository.findByToken(token);
	}

	@Override
	public void createPasswordResetTokenForUser(final User user, final String token) {
		
		final PasswordResetToken myToken = new PasswordResetToken(token, user);
		passwordResetTokenRepository.save(myToken);
		
	}

	@Override
	public User findByUsername(String username) {
		
		return userRepository.findByUsername(username);	
	}

	@Override
	public User findByEmail(String email) {
		
		return userRepository.findByEmail(email);	
	}	

	@Override
	public User findById(Long id) {		
		return userRepository.findById(id);
	}

	@Override
	@Transactional
	public User createUser(User user, Set<UserRole> userRoles) throws Exception{
		
		User localUser = userRepository.findByUsername(user.getUsername());
		
		if(localUser != null) {
			throw new Exception("user already exists. Nothing will be done.");
		}else {
			for (UserRole ur : userRoles) {
				roleRepository.save(ur.getRole());
			}
			
			user.getUserRoles().addAll(userRoles);
			
			ShoppingCart shoppingCart = new ShoppingCart();
			shoppingCart.setUser(user);
			user.setShoppingCart(shoppingCart);
			user.setUserPaymentList(new ArrayList<UserPayment>());
			
			
			localUser = userRepository.save(user);
		}
		
		return localUser;
	}
	
	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user) {
		userPayment.setUser(user);
		userPayment.setUserBilling(userBilling);
		userPayment.setDefaultPayment(true);
		userBilling.setUserPayment(userPayment);
		user.getUserPaymentList().add(userPayment);
		save(user);
		
	}

	@Override
	public void setUserDefaultPayment(Long defaultPaymentId, User user) {
		List<UserPayment> userPaymentList = (List<UserPayment>) userPaymentRepository.findAll();
		
		for (UserPayment userPayment : userPaymentList) {
			if(userPayment.getId() == defaultPaymentId) {
				userPayment.setDefaultPayment(true);
				userPaymentRepository.save(userPayment);
			} else {
				userPayment.setDefaultPayment(false);
				userPaymentRepository.save(userPayment);
			}
		}
		
	}

	@Override
	public void updatePurchaseBasedFavoriteGenre(User user) {
		int countFavorite = 0;
		String purchaseBasedFavoriteGenre= "";
		// get all user owned games
		Set<Game> ownedGames = user.getOwnedGames();
		// for each genre count occurences on list
		for(Genre genre: Genre.values()) {
			int genreOccurences = 0;
			if(!ownedGames.isEmpty()) {				
				for(Game game: ownedGames) {
					if(game.getGenre().equals(genre.toString())) {					
						genreOccurences++;				
						ownedGames.remove(game);
						if(genreOccurences > countFavorite) {
							countFavorite = genreOccurences;
							purchaseBasedFavoriteGenre = genre.toString();
						}			
						if(ownedGames.isEmpty())
							break;
					}
				}
			}
		}
		user.setPurchaseBasedFavoriteGenre(purchaseBasedFavoriteGenre);
		userRepository.save(user);
		
	}
	
	
	
	
	
	
	
	
	
}

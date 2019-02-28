package com.videogamestore.service;

import java.util.Set;

import com.videogamestore.domain.User;
import com.videogamestore.domain.UserBilling;
import com.videogamestore.domain.UserPayment;
import com.videogamestore.domain.security.PasswordResetToken;
import com.videogamestore.domain.security.UserRole;

public interface UserService {
	
	PasswordResetToken getPasswordResetToken(final String token);
	
	void createPasswordResetTokenForUser(final User user, final String token);
	
	User findByUsername(String username);
	User findByEmail(String email);
	User findById(Long id);
	User createUser(User user, Set<UserRole> userRoles) throws Exception;
	User save(User user);
	void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user);

	void setUserDefaultPayment(Long defaultPaymentId, User user);

	void updatePurchaseBasedFavoriteGenre(User user);
}

package com.videogamestore.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.videogamestore.domain.CartItem;
import com.videogamestore.domain.Game;
import com.videogamestore.domain.Order;
import com.videogamestore.domain.User;
import com.videogamestore.domain.UserBilling;
import com.videogamestore.domain.UserPayment;
import com.videogamestore.domain.enumeration.Genre;
import com.videogamestore.domain.enumeration.Language;
import com.videogamestore.domain.enumeration.PEGI;
import com.videogamestore.domain.security.PasswordResetToken;
import com.videogamestore.domain.security.Role;
import com.videogamestore.domain.security.UserRole;
import com.videogamestore.form_object.UserForm;
import com.videogamestore.service.CartItemService;
import com.videogamestore.service.GameService;
import com.videogamestore.service.OrderService;
import com.videogamestore.service.UserPaymentService;
import com.videogamestore.service.UserService;
import com.videogamestore.service.impl.UserSecurityService;
import com.videogamestore.utility.MailConstructor;
import com.videogamestore.utility.PreferencesUtility;
import com.videogamestore.utility.SecurityUtility;


@Controller
public class HomeController {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private MailConstructor mailConstructor;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private GameService gameService;
	
	@Autowired
	private UserPaymentService userPaymentService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private UserSecurityService userSecurityService;
	
	@RequestMapping("/")
	public String index(Model model, Principal principal) {
		if(principal!= null) {			
			User user = userService.findByUsername(principal.getName());
			model.addAttribute("user",user);
		}
		List<Game> gameListFeatured = gameService.findFeatured();
		
		model.addAttribute("gameListFeatured",gameListFeatured);

		Pageable top4 = new PageRequest(0, 4);
		List<Game> gameListOffers = gameService.findSpecialOffers(true, top4);
		model.addAttribute("gameListOffers",gameListOffers);
		
		
		return "index";
	}
	
	@RequestMapping("/store")
	public String store(Model model, @ModelAttribute(value="preferences") PreferencesUtility preferences,Principal principal) {
		if(principal!= null) {			
			User user = userService.findByUsername(principal.getName());
			model.addAttribute("user",user);
		}
		List<Game> gameList = gameService.findAll();
		model.addAttribute("gameList",gameList);
		model.addAttribute("entries", gameService.count());
		preferences = new PreferencesUtility();
		List<String> checkedGenres = new ArrayList<>();
		List<String> checkedLanguages = new ArrayList<>();
		model.addAttribute("checkedGenres", checkedGenres );
		model.addAttribute("checkedLanguages", checkedLanguages);
		return "store";
	}
	
	@RequestMapping(value="/store", method=RequestMethod.POST)
	public String storePost(
			@ModelAttribute(value="preferences") PreferencesUtility preferences,			
			Model model, Principal principal) {
		if(principal!= null) {			
			User user = userService.findByUsername(principal.getName());
			model.addAttribute("user",user);
		}
		// Get values of checked boxes
		List<String> checkedLanguages = preferences.getSupportedLanguages();
		List<String> checkedGenres = preferences.getPreferredGenres();
		List<String> checkedPEGI = preferences.getPegiRatings();
		List<Game> gameList;
		List<String> pegiRatings;
		if(checkedPEGI.isEmpty()) {
			pegiRatings = new ArrayList<>();
			for(PEGI pegi: PEGI.values()) {
				pegiRatings.add(pegi.toString());
			}
		}else {
			pegiRatings = checkedPEGI;
		}
		if(checkedLanguages.isEmpty() && checkedGenres.isEmpty()) {			
			gameList = gameService.findByPEGI(pegiRatings);
		}else if(checkedLanguages.isEmpty()){
			gameList = gameService.findByGenresAndPEGI(checkedGenres, pegiRatings);			
		}else if(checkedGenres.isEmpty()){
			gameList = gameService.findByLanguagesAndPEGI(checkedLanguages, pegiRatings);					
		}else {
			gameList = gameService.findByLanguagesAndGenresAndPEGI(checkedLanguages, checkedGenres, pegiRatings);			
		}
		model.addAttribute("gameList",gameList);
		model.addAttribute("entries", gameList.size());
		model.addAttribute("checkedGenres", checkedGenres );
		model.addAttribute("checkedLanguages", checkedLanguages);
		return "store";
	}
	
	@RequestMapping("/searchGame")
	public String searchGame(@ModelAttribute("term") String term, 
			Principal principal, Model model,
			@ModelAttribute(value="preferences") PreferencesUtility preferences) {
		if(principal!= null) {			
			User user = userService.findByUsername(principal.getName());
			model.addAttribute("user",user);
		}
		
		List<Game> gameList = gameService.searchByTitle(term);
		if(gameList.isEmpty()) {
			model.addAttribute("emptyList",true);
			return "store";
		}
		model.addAttribute("gameList", gameList);
		model.addAttribute("entries", gameList.size());
		preferences = new PreferencesUtility();
		List<String> checkedGenres = new ArrayList<>();
		List<String> checkedLanguages = new ArrayList<>();
		model.addAttribute("checkedGenres", checkedGenres );
		model.addAttribute("checkedLanguages", checkedLanguages);

		return "store";
	}

	
	@RequestMapping("/game")
	public String game(Model model,
			@PathParam("id") Long id, Principal principal){
		if(principal != null) {
			String username = principal.getName();
			User user = userService.findByUsername(username);
			model.addAttribute("user",user);
		}
		Game game = gameService.findOne(id);
		
		//Date format conversion
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat shownDF = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Date date = df.parse(game.getReleaseDate());
			String releaseDate = shownDF.format(date);
			game.setReleaseDate(releaseDate); // show with 'dd-MM-yyyy' format.			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.addAttribute("game",game);
		List<Integer> qtyList = new ArrayList<>();
		qtyList.clear();
		if(game.getInStockNumber() < 10) {			
			for(int i = 1; i <= game.getInStockNumber(); i++) {				
				qtyList.add(i);}			
		}else {
			qtyList = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
		}
		model.addAttribute("qtyList",qtyList);
		model.addAttribute("qty",1);
		return "game";
	}
	// not used. just reserved for later use.
	@RequestMapping("/about")
	public String about(Model model, Principal principal) {
		if(principal!= null) {			
			User user = userService.findByUsername(principal.getName());
			model.addAttribute("user",user);
		}
		return "about";
	}
	// not used. just reserved for later use.
	@RequestMapping("/terms")
	public String terms(Model model, Principal principal) {
		if(principal!= null) {			
			User user = userService.findByUsername(principal.getName());
			model.addAttribute("user",user);
		}
		return "terms";
	}
	// not used. just reserved for later use.
	@RequestMapping("/contact")
	public String contact(Model model, Principal principal) {
		if(principal!= null) {			
			User user = userService.findByUsername(principal.getName());
			model.addAttribute("user",user);
		}
		return "contact";
	}
	
	@RequestMapping("/myProfile") 
	public String myProfile(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("orderList", user.getOrderList());
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveUserSettings", true);
		List<Game> recommendedGames = recommendGames(user);
		model.addAttribute("recommendedGames", recommendedGames);
		model.addAttribute("ownedGames", user.getOwnedGames());	
		
		return "myProfile";
	}
	
	@RequestMapping(value="/updateUserInfo", method=RequestMethod.POST)
	public String updateUserInfo(Principal principal,
			@ModelAttribute("user") User user,
			@ModelAttribute("newPassword") String newPassword,
			@ModelAttribute("confirmPassword") String confirmPassword,
			Model model
			) throws Exception {
		User authenticated = userService.findByUsername(principal.getName());
		
		User currentUser = userService.findById(user.getId());
		if(currentUser == null) {
			throw new Exception ("User not found");
		}		

		if(currentUser.getId() != authenticated.getId()) {
			System.out.println("bad request");
			return "redirect:/badRequest"; // Authenticated user is requesting changes for another one.
		}
		model.addAttribute("user", authenticated);
		model.addAttribute("userPaymentList", authenticated.getUserPaymentList());
		model.addAttribute("orderList", authenticated.getOrderList());
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveUserSettings", true);
		List<Game> recommendedGames = recommendGames(authenticated);
		model.addAttribute("recommendedGames", recommendedGames);
		model.addAttribute("ownedGames", authenticated.getOwnedGames());			
		
		/*check email already exists*/
		if (userService.findByEmail(user.getEmail())!=null) {
			if(userService.findByEmail(user.getEmail()).getId() != currentUser.getId()) {
				model.addAttribute("emailExists", true);
				return "myProfile";
			}
		}		
		/*check username already exists*/
		if (userService.findByUsername(user.getUsername())!=null) {
			if(userService.findByUsername(user.getUsername()).getId() != currentUser.getId()) {
				model.addAttribute("usernameExists", true);
				return "myProfile";
			}
		}		
		/*update password*/
		if (newPassword != null && !newPassword.isEmpty()&& newPassword.equals(confirmPassword) && !newPassword.equals("")){
			BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
			String dbPassword = currentUser.getPassword();
			if(passwordEncoder.matches(user.getPassword(), dbPassword)){
				currentUser.setPassword(passwordEncoder.encode(newPassword));
			} else {
				model.addAttribute("incorrectPassword", true);
				
				return "myProfile";
			}
		}
		
		currentUser.setFirstName(user.getFirstName());
		currentUser.setLastName(user.getLastName());
		currentUser.setUsername(user.getUsername());
		currentUser.setEmail(user.getEmail());
		
		userService.save(currentUser);
		
		model.addAttribute("updateSuccess", true);
		UserDetails userDetails = userSecurityService.loadUserByUsername(currentUser.getUsername());

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);

		
		return "/myProfile";
	}
	
	
	@RequestMapping("/myPreferences") 
	public String myPreferences(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("orderList", user.getOrderList());


//		model.addAttribute("userCreditCardsList", user.getCreditCards());
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveUserPreferences", true);
		
		// TODO: Implement a GameService method for game recommendations
		List<String> genres = new ArrayList<>();
		genres.add(user.getFavoriteGenre());
		List<String> languages = new ArrayList<>();
		languages.add(user.getFavoriteLanguage());
		Pageable top4 = new PageRequest(0, 4);
		List<Game> recommendedGames = gameService.findByLanguagesAndGenresNotOwnedBy(languages, genres,user, top4);
		if(recommendedGames.size()<4) {
			recommendedGames = gameService.findNotOwnedBy( user, top4);
		}
		model.addAttribute("recommendedGames", recommendedGames);
		model.addAttribute("ownedGames", user.getOwnedGames());	
		
		return "myProfile";
	}
	
	@RequestMapping(value="/updateUserPreferences", method=RequestMethod.POST)
	public String updateUserPreferences(Model model, Principal principal,
			@ModelAttribute("language") String language,
			@ModelAttribute("genre") String genre) {
		
		User user = userService.findByUsername(principal.getName());
		user.setFavoriteGenre(genre);
		user.setFavoriteLanguage(language);
		userService.save(user);
		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("orderList", user.getOrderList());
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveUserSettings", true);
		List<Game> recommendedGames = recommendGames(user);
		model.addAttribute("recommendedGames", recommendedGames);
		model.addAttribute("ownedGames", user.getOwnedGames());	
		
		
		
		
		return "myProfile";
	}
	
	@RequestMapping("/listOfCreditCards")
	public String listOfCreditCards(
			Model model, Principal principal, HttpServletRequest request
			) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("orderList", user.getOrderList());
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);
		
		List<Game> recommendedGames = recommendGames(user);
		model.addAttribute("recommendedGames", recommendedGames);
		model.addAttribute("ownedGames", user.getOwnedGames());	
		
		return "myProfile";
	}
	
	@RequestMapping("/addNewCreditCard")
	public String addNewCreditCard(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("addNewCreditCard", true);
		model.addAttribute("classActiveBilling", true);
		
		UserBilling userBilling = new UserBilling();
		UserPayment userPayment = new UserPayment();
		
		model.addAttribute("userBilling", userBilling);
		model.addAttribute("userPayment", userPayment);
		model.addAttribute("orderList", user.getOrderList());
		
		
		List<Game> recommendedGames = recommendGames(user);
		model.addAttribute("recommendedGames", recommendedGames);
		model.addAttribute("ownedGames", user.getOwnedGames());	
				
		return "myProfile";
	}
	
	@RequestMapping(value = "/addNewCreditCard", method=RequestMethod.POST)
	public String addNewCreditCardPost(
			@ModelAttribute("userPayment") UserPayment userPayment,
			@ModelAttribute("userBilling") UserBilling userBilling,
			Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());
		userService.updateUserBilling(userBilling,userPayment,user);
				
		model.addAttribute("user", user);
		
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("orderList", user.getOrderList());
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);
		
		List<Game> recommendedGames = recommendGames(user);
		model.addAttribute("recommendedGames", recommendedGames);
		model.addAttribute("ownedGames", user.getOwnedGames());	
			
		return "myProfile";
	}
	
	
	@RequestMapping("/updateCreditCard")
	public String updateCreditCard(
			Model model, Principal principal,
			@ModelAttribute("id") Long creditCardId) {
		
		User user = userService.findByUsername(principal.getName());
		UserPayment userPayment = userPaymentService.findById(creditCardId);
		
		//check  if user id is the same as the user id of the credit card 
		model.addAttribute("user", user);
		if(user.getId() != userPayment.getUser().getId()) {
			return "badRequestPage";// 
		}else {
			UserBilling userBilling = userPayment.getUserBilling();
			model.addAttribute("userPayment",userPayment);
			model.addAttribute("userBilling", userBilling);
			
			model.addAttribute("addNewCreditCard", true);
			model.addAttribute("classActiveBilling", true);
			
			model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("orderList", user.getOrderList());
			
			List<Game> recommendedGames = recommendGames(user);
			model.addAttribute("recommendedGames", recommendedGames);
			model.addAttribute("ownedGames", user.getOwnedGames());	
			
			return "myProfile";
			
		}
	}
	
	
	@RequestMapping("/removeCreditCard")
	public String removeCreditCard(
			Model model, Principal principal,
			@ModelAttribute("id") Long creditCardId) {
		
		User user = userService.findByUsername(principal.getName());
		UserPayment userPayment = userPaymentService.findById(creditCardId);
		
		//check  if user id is the same as the user id of the credit card 
		if(user.getId() != userPayment.getUser().getId()) {
			return "badRequestPage";// 
		}else {
			model.addAttribute("user", user);
			userPaymentService.removeById(creditCardId);
			
			model.addAttribute("userPayment",userPayment);
			model.addAttribute("listOfCreditCards", true);
			
			model.addAttribute("classActiveBilling", true);
			
			model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("orderList", user.getOrderList());
			
			
			List<Game> recommendedGames = recommendGames(user);
			model.addAttribute("recommendedGames", recommendedGames);
			model.addAttribute("ownedGames", user.getOwnedGames());	
			
			return "myProfile";
			
		}
	}	
	
	@RequestMapping(value="/setDefaultPayment", method=RequestMethod.POST)
	public String setDefaultPayment(
			@ModelAttribute("defaultUserPaymentId") Long defaultPaymentId, 
			Principal principal, Model model) {
		
		User user = userService.findByUsername(principal.getName());
		userService.setUserDefaultPayment(defaultPaymentId, user);
		
		model.addAttribute("user", user);
		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("classActiveBilling", true);		
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("orderList", user.getOrderList());
		
		
		List<Game> recommendedGames = recommendGames(user);
		model.addAttribute("recommendedGames", recommendedGames);
		model.addAttribute("ownedGames", user.getOwnedGames());	

		
		return "myProfile";
	}
	
	@RequestMapping("/orderDetail")
	public String orderDetail(
			@RequestParam("id") Long orderId,
			Principal principal, Model model) {
		
		User user = userService.findByUsername(principal.getName());
		Order order = orderService.findOne(orderId);
		
		// check if requested order belongs to user
		if(order.getUser().getId() != user.getId()) {
			return "badRequestPage";
		}else {
			List<CartItem> cartItemList = cartItemService.findByOrder(order);
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("user", user);
			model.addAttribute("order", order);
			
			model.addAttribute("userPaymentList", user.getUserPaymentList());
			model.addAttribute("orderList", user.getOrderList());

			model.addAttribute("classActiveOrders", true);
			model.addAttribute("listOfCreditCards", true);
			model.addAttribute("displayOrderDetail", true);
			
			List<Game> recommendedGames = recommendGames(user);
			model.addAttribute("recommendedGames", recommendedGames);
			model.addAttribute("ownedGames", user.getOwnedGames());	
			
			return "myProfile";			
		}
	}
	
	@RequestMapping("/login") 
	public String login(Model model, UserForm userForm) {
		model.addAttribute("classActiveLogin", true);
		
		return "myAccount";
	}
	
	@RequestMapping(value="/newUser", method = RequestMethod.POST)
	public String newUserPost(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult result, 
						Errors errors, Model model,
						HttpServletRequest request) throws Exception {
		model.addAttribute("classActiveNewAccount", true);
		model.addAttribute("user", userForm);
		if(errors.hasErrors()) {
			return "myAccount";
		}
		
		if (userService.findByUsername(userForm.getUsername()) != null) {
			model.addAttribute("usernameExists", true);			
			return "myAccount";
		}
		
		if (userService.findByEmail(userForm.getEmail()) != null) {
			model.addAttribute("emailExists", true);			
			return "myAccount";
		}
		
		boolean validGenre = false;
		for(Genre genre: Genre.values()) {
			if(genre.toString().equals(userForm.getFavoriteGenre())) {
				validGenre = true;
				break;
			}
		}
		if(validGenre == false) {
			model.addAttribute("invalidGenre", true);
			return "myAccount";
		}
		
		boolean validLanguage = false;
		for(Language language: Language.values()) {
			if(language.toString().equals(userForm.getFavoriteLanguage())) {
				validLanguage = true;
				break;
			}
		}
		if(validLanguage == false) {
			model.addAttribute("invalidLanguage", true);
			return "myAccount";
		}	
		
		User user = new User();
		user.setEmail(userForm.getEmail());
		user.setUsername(userForm.getUsername());
		user.setFavoriteGenre(userForm.getFavoriteGenre());
		user.setFavoriteLanguage(userForm.getFavoriteLanguage());

		String encryptedPassword = SecurityUtility.passwordEncoder().encode(userForm.getPassword());
		user.setPassword(encryptedPassword);
		
		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(user, role));
		userService.createUser(user, userRoles);
		
		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);
		
		String appUrl = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
		
		SimpleMailMessage email = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, userForm.getPassword());
		
		mailSender.send(email);
		
		model.addAttribute("emailSent", "true");
		
		
		return "myAccount";
	}
	
	
	@RequestMapping("/newUser")
	public String newUser(Model model,Locale locale, 
			@RequestParam("token") String token, UserForm userForm) {
		PasswordResetToken passToken = userService.getPasswordResetToken(token);
		
		if(passToken == null) {
			String message = "invalid Token";
			model.addAttribute("message", message);
			return "redirect:/badRequest";			
		}
		
		User user = passToken.getUser();
		String username = user.getUsername();
		
		UserDetails userDetails = userSecurityService.loadUserByUsername(username);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, 
											userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		model.addAttribute("user", user);

		model.addAttribute("classActiveEdit", true);
		
		return "forward:/myProfile";
	}
	
	@RequestMapping("/forgetPassword")
	public String forgetPassword( @ModelAttribute("email") String email, BindingResult result,
			HttpServletRequest request,	UserForm userForm,		
			Model model
			) {

		model.addAttribute("classActiveForgetPassword", true);

		User user = userService.findByEmail(email);
		
		if (user == null) {
			model.addAttribute("emailNotExist", true);
			return "myAccount";
		}
		
		String password = SecurityUtility.randomPassword();
		
		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);
		
		userService.save(user);
		
		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);
		
		String appUrl = "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
		
		SimpleMailMessage newEmail = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, password);
		
		mailSender.send(newEmail);
		
		model.addAttribute("forgetPasswordEmailSent", "true");
		
		
		return "myAccount";
	}
	
	public List<Game> recommendGames(User user){
		List<String> genres = new ArrayList<>();
		genres.add(user.getFavoriteGenre());
		genres.add(user.getPurchaseBasedFavoriteGenre());
		List<String> languages = new ArrayList<>();
		languages.add(user.getFavoriteLanguage());
		List<Long> ownerId = new ArrayList<>();
		ownerId.add(user.getId());
		Pageable top4 = new PageRequest(0, 4);
		//List<Game> recommendedGames = gameService.findByLanguagesAndGenresNotOwnedBy(languages, genres,user);		
		List<Game> recommendedGames = gameService.findByLanguagesAndGenresNotOwnedBy(languages, genres,user, top4);
		if(recommendedGames.size()<4) {
			recommendedGames = gameService.findNotOwnedBy( user, top4);
		}
		return recommendedGames;
	}
}

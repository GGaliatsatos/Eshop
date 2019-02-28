package com.videogamestore.domain;


import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.videogamestore.domain.security.Authority;
import com.videogamestore.domain.security.UserRole;

@Entity
public class User implements UserDetails{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false, updatable=false)
	private Long id;
	private String username;
	private String password;
	@Column(name="email", nullable = false, updatable = false)
	private String email;
	private String favoriteGenre;
	private String favoriteLanguage;

	private int credits;
	private String firstName;
	private String lastName;
	private boolean enabled=true;
	private String purchaseBasedFavoriteGenre="";
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<UserRole> userRoles = new HashSet<>();	
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="user")
	private List<UserPayment> userPaymentList;
	
	@OneToMany(mappedBy="user")
	private List<Order> orderList;
	
	@OneToOne(cascade=CascadeType.ALL, mappedBy="user")
	private ShoppingCart shoppingCart;
	
	@ManyToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE},
				fetch=FetchType.LAZY)
	@JoinTable(name="user_owned_game",
				joinColumns={@JoinColumn(name="user_id")},
				inverseJoinColumns={@JoinColumn(name="game_id")})
	private Set<Game> ownedGames = new HashSet<>();
	


	
	

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
 
	public int getCredits() {
		return credits;
	}
	public void setCredits(int credits) {
		this.credits = credits;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Set<UserRole> getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		Set<GrantedAuthority> authorities = new HashSet<>();
		userRoles.forEach(ur -> authorities.add(new Authority(ur.getRole().getName())));
		return authorities;
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	public List<UserPayment> getUserPaymentList() {
		return userPaymentList;
	}
	public void setUserPaymentList(List<UserPayment> userPaymentList) {
		this.userPaymentList = userPaymentList;
	}	
	public List<Order> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}
	
	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}
	public void setShoppingCart(ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}
	public String getFavoriteGenre() {
		return favoriteGenre;
	}
	public void setFavoriteGenre(String favoriteGenre) {
		this.favoriteGenre = favoriteGenre;
	}
	public String getFavoriteLanguage() {
		return favoriteLanguage;
	}
	public void setFavoriteLanguage(String favoriteLanguage) {
		this.favoriteLanguage = favoriteLanguage;
	}	
	public String getPurchaseBasedFavoriteGenre() {
		return purchaseBasedFavoriteGenre;
	}
	public void setPurchaseBasedFavoriteGenre(String purchaseBasedFavoriteGenre) {
		this.purchaseBasedFavoriteGenre = purchaseBasedFavoriteGenre;
	}
	public Set<Game> getOwnedGames() {
		return ownedGames;
	}
	public void setOwnedGames(Set<Game> ownedGames) {
		this.ownedGames = ownedGames;
	}
	
	

	
	
	
	
	

	
	
	

	

}

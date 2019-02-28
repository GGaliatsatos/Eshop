package com.videogamestore.form_object;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class UserForm {

	@NotEmpty
	@Email( regexp="^(.+)@(.+)$", message="Please enter a valid email.")
	private String email;
	
	@NotEmpty
	@Size(min=5,max=12,message="Username should be 5 to 12 characters long.")
	@Pattern(regexp="^[a-zA-Z0-9_.-]*$", message="Can contain latin letters, numbers, fullstops or dashes.")
	private String username;
	
	@NotEmpty
	@Size(min=5,max=12,message="Password should be 5 to 12 characters long.")
	@Pattern(regexp="^[a-zA-Z0-9_.-]*$", message="Can contain latin letters, numbers, fullstops or dashes.")
	private String password;

	@NotEmpty
	private String favoriteGenre;
	
	@NotEmpty
	private String favoriteLanguage;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	
	
}

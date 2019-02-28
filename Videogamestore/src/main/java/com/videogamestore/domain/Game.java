package com.videogamestore.domain;

import java.util.ArrayList;
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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Game {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String title;
	private String company;
	private String publisher;
	private String genre;
	private String releaseDate;
	private String pegiRating;
	private double listPrice;
	private double discountPrice;
	private boolean onSale=false;
	private boolean active=true;
	private int inStockNumber;
	private int salesNumber = 0;
	//Game's System Requirements
	private String winOS;
	private String linOS;
	private String macOS;
	private int memory;
	private boolean processorType;
	private double processorSpeed;
	private double graphicsMemory;	
	private double size;
	
	@Column(columnDefinition="text")
	private String description;
	
	@Transient
	private MultipartFile gameImage;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="game")
	private List<Language> languageList = new ArrayList<>();
	//@JoinTable(name="game_language", joinColumns=@JoinColumn(name="game_id"),
	//			inverseJoinColumns=@JoinColumn(name="language_id"))
	
	@OneToMany(mappedBy="game")
	@JsonIgnore
	private List<GameToCartItem> gameToCartItemList;
	
	@ManyToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE},
			fetch=FetchType.LAZY, mappedBy="ownedGames")
	private Set<User> ownerList = new HashSet<>();
		
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getPegiRating() {
		return pegiRating;
	}
	public void setPegiRating(String pegiRating) {
		this.pegiRating = pegiRating;
	}
	public double getListPrice() {
		return listPrice;
	}
	public void setListPrice(double listPrice) {
		this.listPrice = listPrice;
	}
	public double getDiscountPrice() {
		return discountPrice;
	}
	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}	
	public boolean isOnSale() {
		return onSale;
	}
	public void setOnSale(boolean onSale) {
		this.onSale = onSale;
	}
	public int getInStockNumber() {
		return inStockNumber;
	}
	public void setInStockNumber(int inStockNumber) {
		this.inStockNumber = inStockNumber;
	}	
	public int getSalesNumber() {
		return salesNumber;
	}
	public void setSalesNumber(int salesNumber) {
		this.salesNumber = salesNumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public MultipartFile getGameImage() {
		return gameImage;
	}
	public void setGameImage(MultipartFile gameImage) {
		this.gameImage = gameImage;
	}	
	
	
	public List<Language> getLanguageList() {
		return languageList;
	}
	public void setLanguageList(List<Language> languageList) {
		this.languageList = languageList;
	}
	public List<GameToCartItem> getGameToCartItemList() {
		return gameToCartItemList;
	}
	public void setGameToCartItemList(List<GameToCartItem> gameToCartItemList) {
		this.gameToCartItemList = gameToCartItemList;
	}
	public Set<User> getOwnerList() {
		return ownerList;
	}
	public void setOwnerList(Set<User> ownerList) {
		this.ownerList = ownerList;
	}

	public String getWinOS() {
		return winOS;
	}
	public void setWinOS(String winOS) {
		this.winOS = winOS;
	}
	public String getLinOS() {
		return linOS;
	}
	public void setLinOS(String linOS) {
		this.linOS = linOS;
	}
	public String getMacOS() {
		return macOS;
	}
	public void setMacOS(String macOS) {
		this.macOS = macOS;
	}
	public int getMemory() {
		return memory;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	public boolean isProcessorType() {
		return processorType;
	}
	public void setProcessorType(boolean processorType) {
		this.processorType = processorType;
	}
	public double getProcessorSpeed() {
		return processorSpeed;
	}
	public void setProcessorSpeed(double processorSpeed) {
		this.processorSpeed = processorSpeed;
	}
	public double getGraphicsMemory() {
		return graphicsMemory;
	}
	public void setGraphicsMemory(double graphicsMemory) {
		this.graphicsMemory = graphicsMemory;
	}
	public double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}
	
	

	
	
			
	
}

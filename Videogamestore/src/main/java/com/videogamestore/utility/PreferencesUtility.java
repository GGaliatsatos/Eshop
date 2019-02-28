package com.videogamestore.utility;

import java.util.List;

public class PreferencesUtility {

	private List<String> supportedLanguages;
	private List<String> preferredGenres;
	private List<String> pegiRatings;
	
	public List<String> getSupportedLanguages() {
		return supportedLanguages;
	}
	public void setSupportedLanguages(List<String> supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}
	public List<String> getPreferredGenres() {
		return preferredGenres;
	}
	public void setPreferredGenres(List<String> preferredGenres) {
		this.preferredGenres = preferredGenres;
	}
	public List<String> getPegiRatings() {
		return pegiRatings;
	}
	public void setPegiRatings(List<String> pegiRatings) {
		this.pegiRatings = pegiRatings;
	}
	
	
}

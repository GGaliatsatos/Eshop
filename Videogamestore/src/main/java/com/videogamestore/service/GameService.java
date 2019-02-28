package com.videogamestore.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.videogamestore.domain.Game;
import com.videogamestore.domain.User;


public interface GameService {
	List<Game> findAll();
	Long count();
	Game findOne(Long id);
	List<Game> findByLanguagesAndGenres(List<String> languages, List<String> genres);
	List<Game> findByLanguages(List<String> languages);
	List<Game> findByGenres(List<String> genres);
	List<Game> findByLanguagesAndGenresNotOwnedBy(List<String> languages, List<String> genres, User user, Pageable pageable);
	List<Game> findNotOwnedBy(User user,Pageable pageable);
	List<Game> findFeatured();
	List<Game> findSpecialOffers(boolean b, Pageable pageable);
	List<Game> searchByTitle(String title);
	List<Game> findByGenresAndPEGI(List<String> checkedGenres, List<String> checkedPEGI);
	List<Game> findByLanguagesAndPEGI(List<String> checkedLanguages, List<String> checkedPEGI);
	List<Game> findByLanguagesAndGenresAndPEGI(List<String> checkedLanguages, List<String> checkedGenres,
			List<String> checkedPEGI);
	List<Game> findByPEGI(List<String> checkedPEGI);
	
}

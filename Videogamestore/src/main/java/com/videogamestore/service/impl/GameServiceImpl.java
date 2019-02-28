package com.videogamestore.service.impl;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.videogamestore.domain.Game;
import com.videogamestore.domain.User;
import com.videogamestore.repository.GameRepository;
import com.videogamestore.service.GameService;

@Service
public class GameServiceImpl implements GameService{
	@Autowired
	private GameRepository gameRepository;
	
	public List<Game> findAll(){
		return (List<Game>)gameRepository.findAll();
	}
	
	public Long count() {
		return gameRepository.count();
	}
	
	public Game findOne(Long id) {
		return gameRepository.findOne(id);
	}

	@Override
	public List<Game> findByLanguagesAndGenres(List<String> languages, List<String> genres) {		
		//return gameRepository.findByLanguageInAndGenreIn(languages, genres);
		return gameRepository.findDistinctByLanguageList_nameInAndGenreIn(languages, genres);
	}

	@Override
	public List<Game> findByLanguages(List<String> languages) {
		//return gameRepository.findByLanguageIn(languages);
		return gameRepository.findDistinctByLanguageList_nameIn(languages);
	}

	@Override
	public List<Game> findByGenres(List<String> genres) {
		return gameRepository.findByGenreIn(genres);
	}
	

	@Override
	public List<Game> findByGenresAndPEGI(List<String> genres, List<String> pegiRatings) {
		return gameRepository.findByGenreInAndPegiRatingIn(genres, pegiRatings);
	}

	@Override
	public List<Game> findByLanguagesAndPEGI(List<String> languages, List<String> pegiRatings) {
		return gameRepository.findDistinctByLanguageList_nameInAndPegiRatingIn(languages, pegiRatings);
	}

	@Override
	public List<Game> findByLanguagesAndGenresAndPEGI(List<String> languages, List<String> genres,
			List<String> pegiRatings) {
		return gameRepository.findDistinctByLanguageList_nameInAndGenreInAndPegiRatingIn(languages, genres, pegiRatings);
	}

	public GameRepository getGameRepository() {
		return gameRepository;
	}

	@Override
	public List<Game> findByLanguagesAndGenresNotOwnedBy(List<String> languages, List<String> genres, User user, Pageable pageable) {		
		return gameRepository.findDistinctByLanguageList_nameInAndGenreInAndOwnerListNotContaining(languages, genres, user, pageable);
	}

	@Override
	public List<Game> findNotOwnedBy(User user, Pageable pageable) {		
		return gameRepository.findDistinctByOwnerListNotContaining(user, pageable);
	}

	@Override
	public List<Game> findFeatured() {
		return gameRepository.findTop4ByOrderBySalesNumberDesc();
	}

	@Override
	public List<Game> findSpecialOffers(boolean b, Pageable pageable) {
		return gameRepository.findByOnSaleOrderByDiscountPriceAsc(b, pageable);
				
	}

	@Override
	public List<Game> searchByTitle(String title) {
		return gameRepository.findByTitleContaining(title);
	}

	@Override
	public List<Game> findByPEGI(List<String> checkedPEGI) {
		return gameRepository.findByPegiRatingIn(checkedPEGI);
	}
	
	
	
	
	
	
	
	


	
	
	
	
	
	
}

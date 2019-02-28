package com.adminportal.service;

import java.util.List;

import com.adminportal.domain.Game;


public interface GameService {
	
	Game save(Game game);
	
	

	List<Game> findAll();
	
	Game findOne(Long id);
	
	void delete(Long id);
}

package com.adminportal.service.impl;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminportal.domain.Game;

import com.adminportal.repository.GameRepository;
import com.adminportal.service.GameService;

@Service
public class GameServiceImpl implements GameService{
	
	@Autowired
	private GameRepository gameRepository;
	
	public Game save(Game game) {
		return gameRepository.save(game);
	}
	
	public List<Game> findAll() {
		return (List<Game>) gameRepository.findAll();
	}
	
	public Game findOne(Long id) {
		return gameRepository.findOne(id);
	}
	
	public void delete(Long id) {
		gameRepository.delete(id);
	}


	
	
}

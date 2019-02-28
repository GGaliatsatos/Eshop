package com.adminportal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminportal.domain.Game;
import com.adminportal.repository.LanguageRepository;
import com.adminportal.service.LanguageService;

@Service
public class LanguageServiceImpl implements LanguageService {

	@Autowired
	private LanguageRepository languageRepository;
	
	@Override
	public int deleteByGame(Game game) {
		return languageRepository.deleteByGame(game);
	}

	
}

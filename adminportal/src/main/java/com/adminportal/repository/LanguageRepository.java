package com.adminportal.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import com.adminportal.domain.Game;
import com.adminportal.domain.Language;

public interface LanguageRepository extends CrudRepository<Language, Long>{
	  @Transactional
	  @Modifying
	  int deleteByGame(Game game);

}

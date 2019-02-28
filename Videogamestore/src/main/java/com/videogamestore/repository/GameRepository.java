package com.videogamestore.repository;

import java.util.List;


import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.videogamestore.domain.Game;
import com.videogamestore.domain.User;
@Transactional
public interface GameRepository extends CrudRepository<Game, Long>{
	
	List<Game> findByTitleContaining(String title);
	List<Game> findByGenreIn(List<String> genres);
	List<Game> findByPegiRatingIn(List<String> pegiRating);
	List<Game> findDistinctByLanguageList_nameInAndPegiRatingIn(List<String> languages, List<String> pegiRatings);
	List<Game> findByGenreInAndPegiRatingIn(List<String> genres, List<String> pegiRatings);
	List<Game> findDistinctByLanguageList_nameInAndGenreInAndPegiRatingIn(List<String> languages, List<String> genres, List<String> pegiRatings);
	List<Game> findDistinctByLanguageList_nameIn(List<String> languages);
	List<Game> findDistinctByLanguageList_nameInAndGenreIn(List<String> languages, List<String> genres);
	List<Game> findDistinctByLanguageList_nameInAndGenreInAndOwnerListNotContaining(List<String> languages, List<String> genres,User user, Pageable pageable);
	List<Game> findDistinctByOwnerListNotContaining(User user, Pageable pageable);
	List<Game> findTop4ByOrderBySalesNumberDesc();
	List<Game> findByOnSaleOrderByDiscountPriceAsc(boolean onSale, Pageable pageable);
	//List<Game> findDistinctByLanguageList_nameInAndGenreInAndOwnerListNotContaining(List<String> languages, List<String> genres,User user);
	@Query(nativeQuery=true, value = "SELECT COUNT(*) FROM game g JOIN user u "
			   + "WHERE g.genre = :gameGenre AND u.id = :userId")
	int countByGenreAndUser(@Param("gameGenre")String gameGenre, @Param("userId") Long userId);
 }

package com.videogamestore.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.videogamestore.domain.User;

public interface UserRepository extends CrudRepository<User, Long>{
	User findByUsername(String username);
	User findByEmail(String email);
	User findById(Long id);
	@Query(nativeQuery=true, value = "SELECT COUNT(*) FROM game g JOIN user u "
								   + "WHERE g.genre = :gameGenre AND u.id = :userId")
	int countByGenreAndUser(@Param("gameGenre")String gameGenre, @Param("userId") Long userId);
	
}

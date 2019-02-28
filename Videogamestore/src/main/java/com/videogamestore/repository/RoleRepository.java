package com.videogamestore.repository;

import org.springframework.data.repository.CrudRepository;

import com.videogamestore.domain.security.Role;

public interface RoleRepository extends CrudRepository<Role, Long>{
	
	Role findByname(String name);
	
}

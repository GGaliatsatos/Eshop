package com.videogamestore.repository;

import org.springframework.data.repository.CrudRepository;

import com.videogamestore.domain.UserPayment;

public interface UserPaymentRepository extends CrudRepository<UserPayment, Long>{

}

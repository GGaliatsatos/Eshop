package com.videogamestore.service;

import com.videogamestore.domain.Payment;
import com.videogamestore.domain.UserPayment;

public interface PaymentService {
	Payment setByUserPayment(UserPayment userPayment, Payment payment);
}

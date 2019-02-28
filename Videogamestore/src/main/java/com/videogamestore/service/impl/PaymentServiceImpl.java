package com.videogamestore.service.impl;

import org.springframework.stereotype.Service;

import com.videogamestore.domain.Payment;
import com.videogamestore.domain.UserPayment;
import com.videogamestore.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService{

	@Override
	public Payment setByUserPayment(UserPayment userPayment, Payment payment) {
		payment.setType(userPayment.getType());
		payment.setHolderName(userPayment.getHolderName());
		payment.setCardNumber(userPayment.getCardNumber());
		payment.setCardName(userPayment.getCardName());
		payment.setCvc(userPayment.getCvc());
		payment.setExpiryMonth(userPayment.getExpiryMonth());
		payment.setExpiryYear(userPayment.getExpiryYear());
		
		return payment;
	}
	
	

}

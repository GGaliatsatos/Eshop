package com.videogamestore.service;

import com.videogamestore.domain.BillingAddress;
import com.videogamestore.domain.UserBilling;

public interface BillingAddressService {

	BillingAddress setByUserBilling(UserBilling userBilling, BillingAddress billingAddress );
}

package com.zomato.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.zomato.billing.model.OrderEvent;
import com.zomato.billing.service.BillingService;

@RestController
public class BillingController{
    private BillingService billingService;
    @Autowired 
    public BillingController(BillingService bs){
        this.billingService = bs;
    }
    @PostMapping("/bill")
    public void bill(@RequestBody OrderEvent order){
        billingService.bill(order);
    }
}

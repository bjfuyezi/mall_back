package com.example.demo.service;

import com.example.demo.mapper.UserCouponMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCouponService {
    @Autowired
    private UserCouponMapper userCouponMapper;
}

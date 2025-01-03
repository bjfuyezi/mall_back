package com.example.demo.service;

import com.example.demo.mapper.AddressesMapper;
import com.example.demo.pojo.Addresses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AddressesService {
    @Autowired
    private AddressesMapper addressesMapper;

    public List<Addresses> getAllAddresses() {
        return addressesMapper.selectAll();
    }

    public Addresses getAddressById(int id) {
        return addressesMapper.selectById(id);
    }

    public List<Addresses> getAddressesByUserId(int userId) {
        return addressesMapper.selectByUserId(userId);
    }

    @Transactional
    public boolean addAddress(Addresses address) {
        try {
            address.setCreatedTime(new Date());
            if (address.getIsDefault() != null && address.getIsDefault() == 1) {
                addressesMapper.resetDefaultAddress(address.getUserId());
            }
            addressesMapper.insert(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean updateAddress(Addresses address) {
        try {
            if (address.getIsDefault() != null && address.getIsDefault() == 1) {
                addressesMapper.resetDefaultAddress(address.getUserId());
            }
            addressesMapper.update(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean deleteAddress(int id) {
        try {
            Addresses address = addressesMapper.selectById(id);
            if (address == null) {
                return false;
            }
            addressesMapper.deleteById(id);
            if (address.getIsDefault() != null && address.getIsDefault() == 1) {
                List<Addresses> addresses = addressesMapper.selectByUserId(address.getUserId());
                if (!addresses.isEmpty()) {
                    addressesMapper.setDefaultAddress(addresses.get(0).getAddressId());
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean setDefaultAddress(int addressId, int userId) {
        try {
            addressesMapper.resetDefaultAddress(userId);
            addressesMapper.setDefaultAddress(addressId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Addresses getDefaultAddress(int userId) {
        return addressesMapper.getDefaultAddress(userId);
    }
}

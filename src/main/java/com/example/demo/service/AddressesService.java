package com.example.demo.service;

import com.example.demo.mapper.AddressesMapper;
import com.example.demo.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressesService {
    @Autowired
    private AddressesMapper addressesMapper;

    public List<Address> getAllAddresses() {
        return addressesMapper.selectAll();
    }

    public Address getAddressById(int id) {
        return addressesMapper.selectById(id);
    }

    public boolean addAddress(Address address) {
        addressesMapper.insert(address);
        return true;
    }

    public boolean updateAddress(Address address) {
        Address existing = addressesMapper.selectById(address.getAddressId());
        if (existing == null) {
            return false;
        }
        addressesMapper.update(address);
        return true;
    }

    public boolean deleteAddress(int id) {
        Address existing = addressesMapper.selectById(id);
        if (existing == null) {
            return false;
        }
        addressesMapper.deleteById(id);
        return true;
    }
}

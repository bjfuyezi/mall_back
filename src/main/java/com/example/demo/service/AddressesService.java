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

    public List<Addresses> getAddressesByUser_id(int user_id) {
        try {
            List<Addresses> addresses = addressesMapper.selectByUser_id(user_id);
            System.out.println("Service layer - addresses found: " + addresses);
            return addresses;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public boolean addAddress(Addresses address) {
        try {
            address.setCreate_time(new Date());
            if (address.getIs_default() != null && address.getIs_default() == 1) {
                addressesMapper.resetDefaultAddress(address.getUser_id());
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
            if (address.getIs_default() != null && address.getIs_default() == 1) {
                addressesMapper.resetDefaultAddress(address.getUser_id());
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
            if (address.getIs_default() != null && address.getIs_default() == 1) {
                List<Addresses> addresses = addressesMapper.selectByUser_id(address.getUser_id());
                if (!addresses.isEmpty()) {
                    addressesMapper.setDefaultAddress(addresses.get(0).getAddress_id());
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean setDefaultAddress(int address_id, int user_id) {
        try {
            addressesMapper.resetDefaultAddress(user_id);
            addressesMapper.setDefaultAddress(address_id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Addresses getDefaultAddress(int user_id) {
        return addressesMapper.getDefaultAddress(user_id);
    }
}

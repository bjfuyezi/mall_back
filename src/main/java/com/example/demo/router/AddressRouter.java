package com.example.demo.router;

import com.example.demo.pojo.Addresses;
import com.example.demo.pojo.User;
import com.example.demo.service.AddressesService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/addresses")
@CrossOrigin
public class AddressRouter {
    
    @Autowired
    private AddressesService addressesService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserAddresses(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Addresses> addresses = addressesService.getAddressesByUserId(Integer.parseInt(userId));
            response.put("status", "success");
            response.put("data", addresses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addAddress(@RequestBody Addresses address) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (address.getIsDefault() == null) {
                address.setIsDefault(0);
            }
            boolean success = addressesService.addAddress(address);
            response.put("status", success ? "success" : "error");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateAddress(@RequestBody Addresses address) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = addressesService.updateAddress(address);
            response.put("status", success ? "success" : "error");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteAddress(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = addressesService.deleteAddress(id);
            response.put("status", success ? "success" : "error");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/default/{addressId}")
    public ResponseEntity<Map<String, Object>> setDefaultAddress(
        @PathVariable int addressId,
        @RequestParam int userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = addressesService.setDefaultAddress(addressId, userId);
            response.put("status", success ? "success" : "error");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

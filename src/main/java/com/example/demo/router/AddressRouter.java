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

    @GetMapping("/user/{user_id}")
    public ResponseEntity<Map<String, Object>> getUserAddresses(@PathVariable Integer user_id) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("Fetching addresses for user_id: " + user_id);

            List<Addresses> addresses = addressesService.getAddressesByUser_id(user_id);

            System.out.println("Found addresses: " + addresses);

            response.put("status", "success");
            response.put("data", addresses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addAddress(@RequestBody Addresses address) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 打印接收到的地址信息
            System.out.println("Received address: " + address);

            if (address.getUser_id() == null) {
                response.put("status", "error");
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (address.getAddress_content() == null || address.getAddress_content().trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "地址内容不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 设置默认值
            if (address.getIs_default() == null) {
                address.setIs_default(0);
            }

            boolean success = addressesService.addAddress(address);
            response.put("status", success ? "success" : "error");
            if (!success) {
                response.put("message", "添加地址失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
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

    @PutMapping("/default/{address_id}")
    public ResponseEntity<Map<String, Object>> setDefaultAddress(
        @PathVariable int address_id,
        @RequestParam int user_id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = addressesService.setDefaultAddress(address_id, user_id);
            response.put("status", success ? "success" : "error");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

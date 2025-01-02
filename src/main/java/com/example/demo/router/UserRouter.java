package com.example.demo.router;

import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关的路由（Controller），处理用户登录和用户信息的请求
 */
@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserRouter {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果，成功返回 200 OK，失败返回 401 Unauthorized
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
        @RequestParam("username") String username,
        @RequestParam("password") String password) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.validateUserAndGetId(username, password);
            if (user != null) {
                response.put("status", "success");
                response.put("userId", user.getUser_id());
                System.out.println(user);
                System.out.println(user.getUser_id());
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "用户名或密码错误");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "服务器错误");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 用户注册接口
     *
     * @param user 注册的用户信息
     * @param code 验证码
     * @return 注册结果，成功返回 201 Created，失败返回 400 Bad Request
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user, @RequestParam("code") String code) {
        try {
            // 验证邮箱验证码
            boolean isCodeValid = userService.verifyCode(user.getEmail(), code);
            if (!isCodeValid) {
                return new ResponseEntity<>("验证码错误", HttpStatus.BAD_REQUEST); // 400 Bad Request
            }

            // 检查邮箱是否已经存在
            if (userService.isEmailTaken(user.getEmail())) {
                return new ResponseEntity<>("邮箱已存在", HttpStatus.BAD_REQUEST); // 400 Bad Request
            }

            // 检查用户名是否已经存在
            if (userService.isUsernameTaken(user.getUsername())) {
                return new ResponseEntity<>("用户名已存在", HttpStatus.BAD_REQUEST); // 400 Bad Request
            }

            // 添加用户到数据库
            boolean success = userService.addUser(user);
            if (success) {
                return new ResponseEntity<>("注册成功", HttpStatus.CREATED); // 201 Created
            } else {
                return new ResponseEntity<>("注册失败，请稍后重试", HttpStatus.BAD_REQUEST); // 400 Bad Request
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e);
            return new ResponseEntity<>("服务器错误", HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    /**
     * 发送邮箱验证码
     *
     * @param email 用户的邮箱
     * @return 发送结果，成功返回 200 OK，失败返回 400 Bad Request
     */
    @PostMapping("/sendCode")
    public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            boolean sent = userService.sendVerificationCode(email);
            if (sent) {
                return new ResponseEntity<>("验证码发送成功", HttpStatus.OK); // 200 OK
            } else {
                return new ResponseEntity<>("验证码发送失败", HttpStatus.BAD_REQUEST); // 400 Bad Request
            }
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("服务器错误", HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }


    /**
     * 获取指定用户ID的用户信息
     *
     * @param userId 用户的唯一标识符
     * @return 如果成功找到用户返回 200 OK，否则返回 404 Not Found
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userId == null) {
                response.put("status", "error");
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userService.getUserById(userId);
            if (user != null) {
                response.put("status", "success");
                response.put("data", user);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新用户信息
     *
     * @param user 更新的用户对象
     * @return 如果更新成功返回 200 OK，否则返回 400 Bad Request
     */
    @PutMapping("/")
    public ResponseEntity<Void> updateUser(@RequestBody User user) {
        try {
            boolean updated = userService.updateUser(user);
            if (updated) {
                return new ResponseEntity<>(HttpStatus.OK); // 200 OK
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    @PutMapping("/profile/update")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 确保用户ID存在
            if (user.getUser_id() == null) {
                response.put("status", "error");
                response.put("message", "用户ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            boolean success = userService.updateUserProfile(user);
            response.put("status", success ? "success" : "error");
            if (!success) {
                response.put("message", "更新失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

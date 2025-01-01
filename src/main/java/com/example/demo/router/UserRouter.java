package com.example.demo.router;

import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户相关的路由（Controller），处理用户登录和用户信息的请求
 */
@RestController
@RequestMapping("/users")
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
    public ResponseEntity<String> login(@RequestParam("username") String username,
                                        @RequestParam("password") String password) {
        try {
            boolean loginSuccess = userService.validateUser(username, password);
            if (loginSuccess) {
                return new ResponseEntity<>("登录成功", HttpStatus.OK); // 200 OK
            } else {
                return new ResponseEntity<>("用户名或密码错误", HttpStatus.UNAUTHORIZED); // 401 Unauthorized
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e);
            return new ResponseEntity<>("服务器错误", HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    /**
     * 获取指定用户ID的用户信息
     *
     * @param id 用户的唯一标识符
     * @return 如果成功找到用户返回 200 OK，否则返回 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK); // 200 OK
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            }
        } catch (Exception e) {
            // 记录异常信息到日志
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
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
}

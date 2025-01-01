package com.example.demo.service;

import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户相关的业务逻辑处理类
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取所有用户信息
     *
     * @return 所有用户列表
     */
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public User getUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * 用户登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @return 如果用户名和密码匹配，返回true，否则返回false
     */
    public boolean validateUser(String username, String password) {
        User user = userMapper.selectByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    /**
     * 更新用户信息
     *
     * @param user 更新的用户对象
     * @return 更新成功返回true，失败返回false
     */
    public boolean updateUser(User user) {
        int rowsAffected = userMapper.updateUser(user);
        return rowsAffected > 0;
    }
    /**
     * 检查用户名是否已经存在
     *
     * @param username 用户名
     * @return 如果用户名已存在返回 true，否则返回 false
     */
    public boolean isUsernameTaken(String username) {
        User existingUser = userMapper.selectByUsername(username);
        return existingUser != null;
    }

    /**
     * 添加新用户
     *
     * @param user 用户信息
     * @return 如果添加成功返回 true，否则返回 false
     */
    public boolean addUser(User user) {
        // 这里可以对密码进行加密，例如使用 BCrypt 或 MD5
        int rowsAffected = userMapper.insertUser(user);
        return rowsAffected > 0;
    }

}

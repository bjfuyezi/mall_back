package com.example.demo.service;

import com.example.demo.enums.UserRole;
import com.example.demo.enums.UserStatus;
import com.example.demo.mapper.UserMapper;
import com.example.demo.pojo.User;
import com.example.demo.pojo.Addresses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 用户相关的业务逻辑处理类
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressesService addressesService;
    private final Map<String, String> emailVerificationCodes = new HashMap<>();
    private final Map<String, String> resetPasswordCodes = new HashMap<>();

    // 生成并发送验证码
    public boolean sendVerificationCode(String email) {
        String code = String.valueOf(new Random().nextInt(899999) + 100000); // 生成6位随机验证码
        emailVerificationCodes.put(email, code);

        // 模拟发送邮件（实际使用时需接入第三方邮件服务）
        System.out.println("验证码发送到邮箱：" + email + "，验证码：" + code);
        return true;
    }

    // 验证验证码
    public boolean verifyCode(String email, String code) {
        return code.equals(emailVerificationCodes.get(email));
    }

    public boolean sendResetCode(String username, String email) {
        try {
            User user = userMapper.selectByUsername(username);
            if (user == null || !user.getEmail().equals(email)) {
                System.out.println("User not found or email mismatch"); // 调试日志
                return false;
            }

            String code = String.valueOf(new Random().nextInt(899999) + 100000);
            resetPasswordCodes.put(username, code);
            
            // 输出验证码用于调试
            System.out.println("==========================================");
            System.out.println("Generated reset code for " + username + ": " + code);
            System.out.println("==========================================");
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyResetCode(String username, String code) {
        String savedCode = resetPasswordCodes.get(username);
        return savedCode != null && savedCode.equals(code);
    }

    public boolean resetPassword(String username, String newPassword) {
        try {
            if (!validatePassword(newPassword)) {
                return false;
            }
            
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                return false;
            }
            
            user.setPassword(newPassword);
            int result = userMapper.updatePassword(user);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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
    public User getUserById(Integer user_id) {
        if (user_id == null) {
            return null;
        }
        return userMapper.selectById(user_id);
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
        return userMapper.updateUser(user) > 0;
    }

    public boolean updateStatus(Integer id, String role) {
        User user = userMapper.selectById(id);
        if ( user == null )
            return false;
        user.setRole(role);
        return userMapper.updateUser(user) > 0;
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
        try {
            // 设置默认值
            user.setRole(UserRole.buyer.toString());
            user.setStatus(UserStatus.active.toString());
            
            // 添加用户到数据库
            int rowsAffected = userMapper.insertUser(user);
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 检查邮箱是否已经存在
     *
     * @param email 用户邮箱
     * @return 如果邮箱已存在返回 true，否则返回 false
     */
    public boolean isEmailTaken(String email) {
        User existingUser = userMapper.selectByEmail(email);
        return existingUser != null;
    }

//    /**
//     * 获取用户详细信息
//     */
//    public User getUserDetail(String username) {
//        return userMapper.getUserDetail(username);
//    }

    /**
     * 更新用户个人资料
     */
    public boolean updateUserProfile(User user) {
        try {
            // 验证用户是否存在
            User existingUser = userMapper.selectById(user.getUser_id());
            if (existingUser == null) {
                return false;
            }

            // 如果密码为空，不更新密码
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }

            return userMapper.updateUserProfile(user) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取用户的默认地址
     */
//    public Addresses getDefaultAddress(String username) {
//        User user = getUserDetail(username);
//        if (user == null) {
//            return null;
//        }
//        return addressesService.getDefaultAddress(user.getUser_id());
//    }

    /**
     * 验证用户并返回用户信息
     */
    public User validateUserAndGetId(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    public User getUserByUsername(String username) {
        if (username == null) {
            return null;
        }
        return userMapper.selectByUsername(username);
    }

    public boolean validateUserEmail(String username, String email) {
        User user = userMapper.selectByUsername(username);
        return user != null && user.getEmail().equals(email);
    }

    public boolean validatePassword(String password) {
        // 密码必须包含大小写字母和数字，且长度至少为8位
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
        return password.matches(passwordRegex);
    }

    // 更新用户信息
    public void updateUser(Integer userId, User user) {
        user.setUser_id(userId); // 确保设置用户ID
        userMapper.updateUser(user);
    }

    // 删除用户
    public void deleteUser(Integer userId) {
        userMapper.deleteUser(userId);
    }

}

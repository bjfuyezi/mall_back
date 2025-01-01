package com.example.demo.mapper;
import com.example.demo.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据库操作接口
 */
@Mapper
public interface UserMapper {

    /**
     * 获取所有用户
     *
     * @return 所有用户列表
     */
    List<User> selectAll();

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户对象
     */
    User selectById(@Param("id") int id);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     * @return 受影响的行数
     */
    int updateUser(@Param("user") User user);
}

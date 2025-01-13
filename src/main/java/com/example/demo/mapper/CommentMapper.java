package com.example.demo.mapper;

import com.example.demo.pojo.Comment;
import com.example.demo.pojo.Vo.CommentVo;
import com.example.demo.pojo.Vo.OrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface CommentMapper {
    void addComment(Comment comment);

    List<CommentVo> selectAllCommentVo(int userid);
}

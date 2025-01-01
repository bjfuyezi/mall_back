package com.example.demo.service;

import com.example.demo.mapper.CommentMapper;
import com.example.demo.pojo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    CommentMapper commentMapper;
    public void addComment(Comment comment)
    {
        commentMapper.addComment(comment);
    }
}

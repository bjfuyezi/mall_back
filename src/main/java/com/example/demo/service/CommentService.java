package com.example.demo.service;

import com.example.demo.mapper.CommentMapper;
import com.example.demo.mapper.PictureMapper;
import com.example.demo.pojo.Comment;
import com.example.demo.pojo.Vo.CommentVo;
import com.example.demo.pojo.Vo.OrderVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    PictureMapper pictureMapper;
    public void addComment(Comment comment)
    {
        commentMapper.addComment(comment);
    }

    public List<CommentVo> selectAllCommentVo(int order_id) throws JsonProcessingException{
        List<CommentVo> comVo = commentMapper.selectAllCommentVo(order_id);
        for(CommentVo commentVo:comVo){
            commentVo.product_firimg=pictureMapper.selectById(Integer.parseInt(commentVo.getimgAsList().get(0))).getUrl();
        }
        for(CommentVo commentVo:comVo){
            for(String id:commentVo.getimgAsList2()){
                commentVo.imgList.add(pictureMapper.selectById(Integer.parseInt(id)).getUrl());
            }
        }
        return comVo;
    }
    public List<CommentVo> selectProductCommentVo(int product_id) throws JsonProcessingException{
        List<CommentVo> comVo = commentMapper.selectProductCommentVo(product_id);
        for(CommentVo commentVo:comVo){
            commentVo.product_firimg=pictureMapper.selectById(Integer.parseInt(commentVo.getimgAsList().get(0))).getUrl();
        }
        for(CommentVo commentVo:comVo){
            for(String id:commentVo.getimgAsList2()){
                commentVo.imgList.add(pictureMapper.selectById(Integer.parseInt(id)).getUrl());
            }
        }
        return comVo;
    }


}

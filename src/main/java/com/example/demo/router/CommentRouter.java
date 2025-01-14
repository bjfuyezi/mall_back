package com.example.demo.router;

import com.example.demo.pojo.Comment;
import com.example.demo.pojo.Vo.CommentVo;
import com.example.demo.pojo.Vo.OrderVo;
import com.example.demo.service.CommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/comment")
public class CommentRouter {
    @Autowired
    private CommentService commentService;
    @PostMapping("/create")
    public ResponseEntity<Void> insertComment(
            @RequestBody Comment comment){
        try {
            System.out.println("Received order data: " + comment);
            comment.setStatus("已评价");
            LocalDateTime now = LocalDateTime.now();
            Date createtime = java.sql.Timestamp.valueOf(now);;
            comment.setCreated_time(createtime);

            String trimmed = comment.getPicture_id().substring(1, comment.getPicture_id().length() - 1);
            String[] pictureIdArray = trimmed.split(",");
            Gson gson = new Gson();
            String picidjson = gson.toJson(pictureIdArray);
            comment.setPicture_id(picidjson);
            commentService.addComment(comment);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println("Error inserting order: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/getCommentVo")
    public ResponseEntity<List<CommentVo>> getCommentVo(@RequestBody Map<String, Object> request){
        Integer user_id = (Integer) request.get("user_id");
        System.out.println("Received order data: " + user_id);
        List<CommentVo> commentvolist = null;
        try {
            commentvolist = commentService.selectAllCommentVo(user_id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(commentvolist);
    }

    /*pruduct详情下的商品评论
    @PostMapping("/getProCommentVo")
    public ResponseEntity<List<CommentVo>> getProCommentVo(@RequestBody Map<String, Object> request){
        Integer user_id = (Integer) request.get("product_id");
        System.out.println("Received order data: " + user_id);
        List<CommentVo> commentvolist = null;
        try {
            commentvolist = commentService.selectAllCommentVo(user_id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(commentvolist);
    }*/
}

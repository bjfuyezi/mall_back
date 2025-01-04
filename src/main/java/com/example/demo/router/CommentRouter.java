package com.example.demo.router;

import com.example.demo.pojo.Comment;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;


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
            System.out.println(comment.getComment_id()+comment.getUser_id()+comment.getOrder_id()+comment.getContent());
            commentService.addComment(comment);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println("Error inserting order: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

package com.example.demo.router;

import com.example.demo.pojo.Comment;
import com.example.demo.pojo.Orders;
import com.example.demo.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentRouter {
    private CommentService commentService;
    @PostMapping("/create")
    public ResponseEntity<Void> insertComment(
            @RequestBody Comment comment){
        try {
            System.out.println("Received order data: " + comment);
            comment.setStatus("已完成");
            LocalDateTime now = LocalDateTime.now();
            Date createtime = java.sql.Timestamp.valueOf(now);;
            comment.setCreated_time(createtime);
            System.out.println(comment.getComment_id()+comment.getUser_id()+comment.getOrder_id()+comment.getContent());
            commentService.addComment(comment);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

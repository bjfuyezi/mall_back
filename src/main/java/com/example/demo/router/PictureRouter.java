package com.example.demo.router;

import com.example.demo.Exception.NameException;
import com.example.demo.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/pic")
public class PictureRouter {

    @Autowired
    private PictureService pictureService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            int k = pictureService.save_picture(file);
            return new ResponseEntity<>(String.valueOf(k),HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("图片无法解析",HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NameException e) {
            return new ResponseEntity<>("图片名称有误",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{picture_id}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable("picture_id") int picture_id) {
        try {
            System.out.println(picture_id);
            byte[] imageContent = pictureService.getImage(picture_id);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageContent);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // 根据实际情况调整媒体类型

            return new ResponseEntity<>(new InputStreamResource(bis), headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/getName")
    public ResponseEntity<String> getName(@RequestBody Map<String, Object> payload) {
        Integer pictureId = (Integer) payload.get("id"); // 从请求体中获取id
        try {
            String name = pictureService.getImageName(pictureId);
            return new ResponseEntity<>(name, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/getUrl")
    public ResponseEntity<String> getUrl(@RequestBody Map<String, Object> payload) {
        Integer pictureId = (Integer) payload.get("id"); // 从请求体中获取id
        try {
            String url = pictureService.getImageUrl(pictureId);
            return new ResponseEntity<>(url, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/getManyUrl")
    public ResponseEntity<String> getManyUrl(@RequestBody Map<String, Object> ids) {
        String picturesId = (String) ids.get("id"); // 从请求体中获取id
        System.out.println(picturesId+"z");
        try {
            String urls = pictureService.getManyImageUrl(picturesId);
            System.out.println(urls);
            return new ResponseEntity<>(urls, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/uploadAndId")
    public ResponseEntity<String> uploadAndId(@RequestParam("file") MultipartFile file) {
        try {
            String id = String.valueOf(pictureService.save_picture(file));
            return new ResponseEntity<>(id, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("图片无法解析",HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NameException e) {
            return new ResponseEntity<>("图片名称有误",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

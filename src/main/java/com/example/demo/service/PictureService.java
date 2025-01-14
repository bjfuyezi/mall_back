package com.example.demo.service;

import com.example.demo.Exception.NameException;
import com.example.demo.mapper.PictureMapper;
import com.example.demo.pojo.Picture;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class PictureService {

    @Autowired
    private PictureMapper pictureMapper;
    private static final String UPLOAD_DIR = "../img/";

    public Integer save_picture(MultipartFile file) throws IOException, NameException {
        String originalFilename = file.getOriginalFilename();//获取原始文件名称
        if(originalFilename == null) {
            throw new NameException();
        }
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));//得到后缀
        String fileName = UUID.randomUUID() + fileExtension;//新的文件名
        Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);//保存文件

        Files.createDirectories(filePath.getParent());//创建上一级文件夹，如果没有
        Files.write(filePath, file.getBytes());//写入文件

        Picture picture = new Picture();
        picture.setName(fileName);
        picture.setUrl("/img/" + fileName); // 相对路径

        pictureMapper.insert(picture);
        return picture.getPicture_id();
    }

    public byte[] getImage(int id) throws IOException {
        Picture picture = pictureMapper.selectById(id);
        if (picture == null){
            return null;
        }
        Path imagePath = Paths.get(UPLOAD_DIR).resolve(picture.getName());//根据名称计算路径
        return Files.readAllBytes(imagePath);//读文件
    }

    public String getImageName(int id) throws IOException {
        Picture picture = pictureMapper.selectById(id);
        if (picture == null){
            return null;
        }
        return picture.getName();
    }

    public String getImageUrl(int id) throws IOException {
        Picture picture = pictureMapper.selectById(id);
        if (picture == null){
            return null;
        }
        return picture.getUrl();
    }

    public String getManyImageUrl(String ids) throws IOException {
        List<String> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        if ( ids.contains(",") ) {
            List<String> idList = objectMapper.readValue(ids, List.class);
            for (String id : idList) {
                Picture picture = pictureMapper.selectById(Integer.parseInt(id));
                if (picture != null) {
                    result.add(picture.getUrl());
                }
            }
        } else {
            List<Integer> idList = objectMapper.readValue(ids, new TypeReference<List<Integer>>() {});
            for (Integer id : idList) {
                Picture picture = pictureMapper.selectById(id);
                if (picture != null) {
                    result.add(picture.getUrl());
                }
            }
        }

        return objectMapper.writeValueAsString(result);
    }

}

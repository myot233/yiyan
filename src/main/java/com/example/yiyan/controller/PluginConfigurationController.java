package com.example.yiyan.controller;

import com.example.yiyan.baidu.BaiDuApiCaller;
import com.example.yiyan.model.dto.CityWeatherRequest;
import com.example.yiyan.model.vo.MessageResponse;
import com.sun.org.apache.bcel.internal.generic.RET;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

@RestController
@Slf4j
@CrossOrigin
public class PluginConfigurationController {
    @RequestMapping("/.well-known/{fileName}")
    public ResponseEntity<String> Config(@PathVariable String fileName) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        InputStream inputStream
                = PluginConfigurationController.class.getResourceAsStream("/.well-known/" + fileName);
        if (inputStream != null) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            try {
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                return  new ResponseEntity<>(buffer.toString("UTF-8"),headers, HttpStatus.OK);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return new ResponseEntity<>("",headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("",headers, HttpStatus.NOT_FOUND);
    }
}

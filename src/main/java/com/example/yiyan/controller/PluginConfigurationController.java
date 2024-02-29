package com.example.yiyan.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@Slf4j
@CrossOrigin
public class PluginConfigurationController {

    //TODO: 等待修复
    @RequestMapping("/.well-known/{fileName}")
    public ResponseEntity<String> Config(@PathVariable String fileName) {
        log.info("call {}",fileName);
        HttpHeaders headers = new HttpHeaders();
        if(fileName.endsWith("json")){
            headers.setContentType(MediaType.APPLICATION_JSON);
        }else{
            headers.setContentType(MediaType.parseMediaType("text/yaml; charset=UTF-8"));
        }

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

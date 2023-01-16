package com.spring.largefileupload.service;

import com.spring.largefileupload.config.ServerProperties;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StudentService {

    private final ServerProperties serverProperties;
    public StudentService(ServerProperties serverProperties){
        this.serverProperties = serverProperties;
    }

    public Map<String, String> getMap(){
        return this.serverProperties.getApplication();
    }
}

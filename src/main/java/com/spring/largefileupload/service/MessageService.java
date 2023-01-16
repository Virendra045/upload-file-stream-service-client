package com.spring.largefileupload.service;

import com.spring.largefileupload.model.Student;
import com.spring.largefileupload.utils.ApplicationConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private KafkaTemplate<String, Student> tenant2Template;

    @Autowired
    private KafkaTemplate<String, Student> tenant1Template;

    public void sendMessage(String tenantName, Student student){
          switch (tenantName) {
            case "t1"-> tenant1Template.send(ApplicationConstant.TOPIC_NAME, student) ;
            case "t2"-> tenant2Template.send(ApplicationConstant.TOPIC_NAME, student) ;
        }
    }

}

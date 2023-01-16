package com.spring.largefileupload.service;

import com.spring.largefileupload.model.Student;
import com.spring.largefileupload.utils.ApplicationConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

@Autowired
private MessageService messageService;

    public String sendMessage(Student message, String tenant) {

        try {
            messageService.sendMessage(tenant, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "json message sent succuessfully";
    }
}

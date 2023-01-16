package com.spring.largefileupload.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.spring.largefileupload.model.Student;
import com.spring.largefileupload.service.KafkaService;
import com.spring.largefileupload.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.FormPartEvent;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

@RestController
@RequestMapping("/upload")
@Slf4j
public class FileUploadRestApi {

    @Autowired
    private StudentService studentService;

    @Autowired
    private KafkaService kafkaService;


    @RequestMapping(produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(
            @RequestPart("file") MultipartFile file) {

        try {
            File testFile = new File("/Users/virendrasingh/Desktop/documents/newfile/test.pdf");
            FileUtils.writeByteArrayToFile(testFile, file.getBytes());
            List<String> lines = FileUtils.readLines(testFile);
            lines.forEach(line -> System.out.println(line));
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("Done", HttpStatus.OK);
    }

    @GetMapping("/value")
    public ResponseEntity<Map<String, String>> getFile() {
        return new ResponseEntity<Map<String, String>>(studentService.getMap(), HttpStatus.OK);
    }

    @PostMapping("/message/{tenant}")
    public ResponseEntity<String> postMessage(@RequestBody Student student, @PathVariable String tenant) {
        return new ResponseEntity<String>(kafkaService.sendMessage(student, tenant), HttpStatus.OK);
    }

    @PostMapping("/upload")
    public Mono<String> uploadFile(@RequestPart("file") FilePart filePartFlux) throws JsonProcessingException {

        var buffers = filePartFlux.content().map(dataBuffer ->  dataBuffer);
        WebClient client = WebClient.create();
        var student =Student.builder().id(1L).name("virendra").rollNumber("23").build();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return client.post().uri("http://localhost:8080/upload-file-with-part-events")
                .contentType(MULTIPART_FORM_DATA)
                .body(
                        Flux.concat(
                                FormPartEvent.create("name", ow.writeValueAsString(student)),
                                FilePartEvent.create("file", new ClassPathResource("10840.pdf")),
                                FilePartEvent.create("file1", "test.pdf", MediaType.APPLICATION_PDF, buffers)
                        ),
                        PartEvent.class
                )
                .retrieve()
                .bodyToMono(String.class);
    }


}
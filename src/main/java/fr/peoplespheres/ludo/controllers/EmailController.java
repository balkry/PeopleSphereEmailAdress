package fr.peoplespheres.ludo.controllers;

import fr.peoplespheres.ludo.dto.Dat;
import fr.peoplespheres.ludo.dto.Data;
import fr.peoplespheres.ludo.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/emails-generator")
public class EmailController {

    @Autowired
    EmailService emailService;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Data> getEmail(
            @RequestParam(value = "input1", defaultValue = "") String input1,
            @RequestParam(value = "input2", defaultValue = "") String input2,
            @RequestParam(value = "input3", defaultValue = "") String input3,
            @RequestParam(value = "input4", defaultValue = "") String input4,
            @RequestParam(value = "input5", defaultValue = "") String input5,
            @RequestParam(value = "input6", defaultValue = "") String input6,
            @RequestParam(value = "expression", defaultValue = "") String expression
    ) {

        Map<String, String> inputsList = new LinkedHashMap<>();
        inputsList.put("input1", input1);
        inputsList.put("input2", input2);
        inputsList.put("input3", input3);
        inputsList.put("input4", input4);
        inputsList.put("input5", input5);
        inputsList.put("input6", input6);

        String generatedEmail = emailService.getEmailFromArgs(inputsList, expression);

        Data data = new Data();
        data.getData().add(new Dat(generatedEmail, generatedEmail));
        return new ResponseEntity<Data>(data, HttpStatus.OK);
    }
}

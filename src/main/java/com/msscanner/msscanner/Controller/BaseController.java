package com.msscanner.msscanner.Controller;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class BaseController {

    @RequestMapping(value = "/base", method = RequestMethod.GET)
    public String populateCourse(){
        return "Hello World!!!!!";
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/noAPIGateway", method = RequestMethod.POST, produces = "application/json; charset=UTF-8", consumes = {"text/plain", "application/*"})
    public boolean getNoAPIGateway(){
        return true;
    }

//   evaluateAPIGateway

}

package com.msscanner.msscanner.Controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    @RequestMapping(value = "/base", method = RequestMethod.GET)
    public String populateCourse(){
        return "Hello World!!!!!";
    }

}

package com.koordinator.app1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@SpringBootApplication
@RestController
public class App1Application {

	public static void main(String[] args) {
		SpringApplication.run(App1Application.class, args);
	}

	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public void insert(@RequestBody Map<String, Object> payload){
		//TODO: implement insert
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void delete(@RequestBody Map<String, Object> payload){
		//TODO: implement delete
	}
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public void search(@RequestBody Map<String, Object> payload){
		//TODO: implement search
	}
	@RequestMapping(value = "/range", method = RequestMethod.POST)
	public void range(@RequestBody Map<String, Object> payload){
		//TODO: implement range
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test(){
		//TODO: implement range
		return "Koordinator is running";
	}

}

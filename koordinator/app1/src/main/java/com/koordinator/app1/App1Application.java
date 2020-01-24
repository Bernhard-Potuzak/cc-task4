package com.koordinator.app1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@SpringBootApplication
@RestController
public class App1Application {

	public static void main(String[] args) {
		SpringApplication.run(App1Application.class, args);
	}

	@PostMapping("/insert/{k}/{a}")
	public void insert(@PathVariable byte[] a,@PathVariable byte[] k){
		//TODO: implement insert
	}

	@PostMapping("/delete/{k}")
	public void delete(@PathVariable byte[] k){
		//TODO: implement delete
	}
	@PostMapping("/search")
	public void search(@PathVariable byte[] k){
		//TODO: implement search
	}
	@PostMapping("/range{k1}/{k2}")
	public void range(@PathVariable byte[] k1,@PathVariable byte[] k2){
		//TODO: implement range
	}

}

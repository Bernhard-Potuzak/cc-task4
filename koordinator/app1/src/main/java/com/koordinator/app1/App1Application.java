package com.koordinator.app1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

@SpringBootApplication
@RestController
public class App1Application {

    public static String[] WorkerAddresses = {
            "http://localhost:10101/",
            "http://localhost:10102/",
            "http://localhost:10103/",
            "http://localhost:10104/"
    };

    public static String[] AssignedAddresses = new String[16];

    public static void setAssignedAddresses() {
        for (int i = 0; i < AssignedAddresses.length; i++) {
            if (i % 4 == 0) {
                AssignedAddresses[i] = WorkerAddresses[i % 4];
            }
        }
    }

    public static String computeAddress(String key) {
        long hash = new FNVHash().hash(key.getBytes());
        int initialIndex = (int) (hash % 16);
        while (AssignedAddresses[initialIndex] == null) {
            if (initialIndex != 0) {
                initialIndex -= 1;
            } else {
                initialIndex = 16;
            }
        }
        return AssignedAddresses[initialIndex];
    }

    public static String getResponse(String address, Map<String, Object> payload, HttpServletRequest request) throws URISyntaxException {
        try {
            URL url = new URL(address);
            URI uri = new URI(url.getProtocol(), null, url.getHost(), url.getPort(), request.getRequestURI(), request.getQueryString(), null);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<Map<String, Object>>(payload), String.class);
            return responseEntity.getBody();
        } catch (MalformedURLException e) {
            return "";
        }
    }

    public static void main(String[] args) {
        setAssignedAddresses();
        SpringApplication.run(App1Application.class, args);
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public String insert(@RequestBody Map<String, Object> payload, HttpServletRequest request) throws URISyntaxException {
        String address = "http://localhost:10101/";
        return getResponse(address, payload, request);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(@RequestBody Map<String, Object> payload, HttpServletRequest request) throws URISyntaxException {
        String address = "http://localhost:10101/";
        return getResponse(address, payload, request);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestBody Map<String, Object> payload, HttpServletRequest request) throws URISyntaxException {
        String address = "http://localhost:10101/";
        return getResponse(address, payload, request);
    }

    @RequestMapping(value = "/range", method = RequestMethod.POST)
    public String range(@RequestBody Map<String, Object> payload, HttpServletRequest request) throws URISyntaxException {
        String address = "http://localhost:10101/";
        return getResponse(address, payload, request);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        //TODO: implement range
        return "Koordinator is running";
    }

}

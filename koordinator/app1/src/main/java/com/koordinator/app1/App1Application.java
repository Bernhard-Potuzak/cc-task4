package com.koordinator.app1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.core.io.ClassPathResource;



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
            "http://worker1:8080/",
            "http://worker2:8080/",
            "http://worker3:8080/",
            "http://worker4:8080/"
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
    public static String getResponse(String address,String k,String v,HttpServletRequest request) throws URISyntaxException {
        try {
            URL url = new URL(address);
            String requestUri = "/Worker" + request.getRequestURI();
            URI uri = new URI(url.getProtocol(), null, url.getHost(), url.getPort(), requestUri, request.getQueryString(), null);
            RestTemplate restTemplate = new RestTemplate();
            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("k", k);
            map.add("v", v);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
                    map, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
            return responseEntity.getBody();
        } catch (MalformedURLException e) {
            return "";
        }
    }

    public static String getResponse(String address,String k,HttpServletRequest request) throws URISyntaxException {
        try {
            URL url = new URL(address);
            String requestUri = "/Worker" + request.getRequestURI();
            URI uri = new URI(url.getProtocol(), null, url.getHost(), url.getPort(), requestUri, request.getQueryString(), null);
            RestTemplate restTemplate = new RestTemplate();
            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("k", k);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
                    map, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
            return responseEntity.getBody();
        } catch (MalformedURLException e) {
            return "";
        }
    }

    public static void main(String[] args) {
        setAssignedAddresses();
        SpringApplication.run(App1Application.class, args);
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ResponseBody
    public String insert(@RequestParam("k") String k,
                         @RequestParam("v") String jsonRaw, HttpServletRequest request) throws URISyntaxException {
        String address = computeAddress(k);
        return getResponse(address, k, jsonRaw, request);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ResponseBody
    public String delete(@RequestParam("k") String k, HttpServletRequest request) throws URISyntaxException {
        String address = computeAddress(k);
        return getResponse(address, k, request);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "multipart/form-data")
    public String search(@RequestParam("k") String k, HttpServletRequest request) throws URISyntaxException {
        String address = computeAddress(k);
        return getResponse(address, k, request);
    }

/*    @RequestMapping(value = "/range", method = RequestMethod.POST)
    public String range(@RequestBody Map<String, Object> payload, HttpServletRequest request) throws URISyntaxException {
        String address = "http://localhost:10101/";
        return getResponse(address, k, jsonRaw, request);
    }*/

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        //TODO: implement range
        return "Koordinator is running";
    }

}

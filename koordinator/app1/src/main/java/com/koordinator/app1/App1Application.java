package com.koordinator.app1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;


import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        int workerCounter = 0;
        for (int i = 0; i < AssignedAddresses.length; i++) {
            if (i % 4 == 0) {
                AssignedAddresses[i] = WorkerAddresses[workerCounter];
                workerCounter++;
            }
        }
    }

    public static String computeAddress(String key) {
        long hash = new FNVHash().hash(key.getBytes());
        int initialIndex = Math.abs((int) (hash % 16));
        while (AssignedAddresses[initialIndex] == null) {
            if (initialIndex > 0) {
                initialIndex -= 1;
            } else {
                initialIndex = 16;
            }
        }
        return AssignedAddresses[initialIndex];
    }


    public static AppResponseInsert getResponseInsert(String address,String k,String v,HttpServletRequest request) throws URISyntaxException {
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
            ResponseEntity<AppResponseInsert> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, AppResponseInsert.class);
            return responseEntity.getBody();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static AppResponseSearch getResponseSearch(String address, String k, HttpServletRequest request) throws URISyntaxException {
        try {
            URL url = new URL(address);
            String requestUri = "/Worker/search";
            URI uri = new URI(url.getProtocol(), null, url.getHost(), url.getPort(), requestUri, request.getQueryString(), null);
            RestTemplate restTemplate = new RestTemplate();
            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("k", k);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
                    map, headers);
            ResponseEntity<AppResponseSearch> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, AppResponseSearch.class);
            return responseEntity.getBody();
        } catch (MalformedURLException | HttpClientErrorException e) {
            return null;
        }
    }

    public static AppResponseDelete getResponseDelete(String address,String k,HttpServletRequest request) throws URISyntaxException {
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
            ResponseEntity<AppResponseDelete> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, AppResponseDelete.class);
            return responseEntity.getBody();
        } catch (MalformedURLException | HttpClientErrorException e) {
            return null;
        }
    }

    public static String[] getResponseRange(String address, String k1, String k2, HttpServletRequest request) throws URISyntaxException {
        try {
            URL url = new URL(address);
            String requestUri = "/Worker" + request.getRequestURI();
            URI uri = new URI(url.getProtocol(), null, url.getHost(), url.getPort(), requestUri, request.getQueryString(), null);
            RestTemplate restTemplate = new RestTemplate();
            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("k1", k1);
            map.add("k2", k2);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
                    map, headers);
            ResponseEntity<String[]> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String[].class);
            return responseEntity.getBody();
        } catch (MalformedURLException | HttpClientErrorException e) {
            return null;
        }
    }

    public static List<String> getResponsesRange(String k1, String k2, HttpServletRequest request) throws URISyntaxException {
        List<String> responses = new ArrayList<String>();

        try {
            int k1int = Integer.parseInt(k1);
            int k2int = Integer.parseInt(k2);
            if (k1int > k2int) {
                String temp = k1;
                k1 = k2;
                k2 = temp;
            }
        } catch (NumberFormatException e) {
            return null;
        }

        for (String address : WorkerAddresses) {
            try {
                String[] response = getResponseRange(address, k1, k2, request);
                if (response != null) {
                    responses.addAll(Arrays.asList(response));
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        return responses;
    }

    public static void main(String[] args) {
        setAssignedAddresses();
        SpringApplication.run(App1Application.class, args);
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ResponseBody
    public AppResponseInsert insert(@RequestParam("k") String k,
                         @RequestParam("v") String jsonRaw, HttpServletRequest request) throws URISyntaxException {
        String address = computeAddress(k);
        return getResponseInsert(address, k, jsonRaw, request);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ResponseBody
    public AppResponseDelete delete(@RequestParam("k") String k, HttpServletRequest request) throws URISyntaxException {
        String address = computeAddress(k);
        return getResponseDelete(address, k, request);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ResponseBody
    public AppResponse search(@RequestParam("k") String k, HttpServletRequest request) throws URISyntaxException {
        String address = computeAddress(k);
        return getResponseSearch(address, k, request);
    }

    @RequestMapping(value = "/range", method = RequestMethod.POST, consumes = "multipart/form-data")
    public List<String> range(@RequestParam("k1") String k1, @RequestParam("k2") String k2, HttpServletRequest request) throws URISyntaxException {
        return getResponsesRange(k1, k2, request);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        //TODO: implement range
        return "Koordinator is running";
    }

}

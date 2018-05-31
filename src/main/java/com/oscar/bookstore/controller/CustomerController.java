package com.oscar.bookstore.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.oscar.bookstore.model.BCrypt;
import com.oscar.bookstore.model.Customer;
import com.oscar.bookstore.repository.CustomerRepository;
import com.oscar.bookstore.service.UserLoginInfo;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;


@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @ResponseBody
    @PostMapping(value = "/login", produces = "application/json;charset=UTF-8")
    public Map<String, String> login(@RequestBody String user) {

        if(user!=null) {
            JSONObject jsonObject = new JSONObject(user);

            String email = (String) jsonObject.get("email");

            String password = (String) jsonObject.get("password");

            Customer customer = customerRepository.findByEmail(email);

            if (customer!=null) {
                if (isCorrectPassword(customer, password)) {
                    String token = createToken(customer.getId());
                    if(!token.equals("Can't create token")) {
                        Map<String, String> json = new LinkedHashMap<>();
                        json.put("success", "true");
                        json.put("message", "Login Success!");
                        json.put("token", token);

                        return json;
                    } else {
                        return jsonForActionSuccess(false, "Can't create token");
                    }
                } else {
                    return jsonForActionSuccess(false, "Password not correct");
                }
            } else {
                return jsonForActionSuccess(false, "Can not find user");
            }
        } else {
            return jsonForActionSuccess(false, "Can not receive login data");
        }

    }


    @GetMapping("/profile")
    public ResponseEntity<Customer> showProfile(HttpServletRequest request) {

        Integer userId = UserLoginInfo.getInstance().getUserId();

        if(userId !=null) {

            Customer customer= customerRepository.findById(userId).orElse(null);

            return new ResponseEntity<>(customer, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }


    }

    @PostMapping("/new")
    public ResponseEntity<String> addNewCustomer(@RequestBody Customer customer) {

        if(customer!=null) {
            customer = hashPassword(customer);
            System.out.println("Customer saved!");
            try {
                customerRepository.save(customer);
                return ResponseEntity.status(HttpStatus.CREATED).body("Register User success!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error content!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can not receive object!");
        }

    }

    private Map<String, String> jsonForActionSuccess(boolean success, String reason) {
        Map<String, String> json = new LinkedHashMap<>();

        json.put("success", String.valueOf(success));
        json.put("message", reason);

        return json;
    }

    private boolean isCorrectPassword(Customer customer, String password) {
      return BCrypt.checkpw(password, customer.getCustomerSecurity().getPassword());
    }

    private Customer hashPassword(Customer customer) {

        String password = customer.getCustomerSecurity().getPassword();

        String hashpw = BCrypt.hashpw(password, BCrypt.gensalt());

        customer.getCustomerSecurity().setPassword(hashpw);

        return customer;
    }

    private String createToken(Integer userId) {
        Date date = new Date();
        long t= date.getTime();
        Date expireTime = new Date(t + 86400000);
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                    .withClaim("userId", userId)
                    .withExpiresAt(expireTime)
                    .withIssuer("auth0")
                    .sign(algorithm);
            return token;
        } catch (UnsupportedEncodingException | JWTCreationException exception){
            //UTF-8 encoding not supported
        }
        return "Can't create token";
    }

}

package com.api.gestion.apigestionfacturas.rest;

import com.api.gestion.apigestionfacturas.constants.BillConstants;
import com.api.gestion.apigestionfacturas.service.UserService;
import com.api.gestion.apigestionfacturas.util.BillUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap){

        try {
            return userService.signUp(requestMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return BillUtils.getResponseEntity(BillConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap){

        try {
            return userService.login(requestMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return BillUtils.getResponseEntity(BillConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

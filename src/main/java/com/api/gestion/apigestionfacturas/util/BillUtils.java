package com.api.gestion.apigestionfacturas.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BillUtils {

    private BillUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus httpStatus){
        return new ResponseEntity<String>("Mensaje: " + message, httpStatus);
    }
}

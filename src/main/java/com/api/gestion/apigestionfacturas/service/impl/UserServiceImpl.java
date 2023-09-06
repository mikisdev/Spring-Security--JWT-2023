package com.api.gestion.apigestionfacturas.service.impl;

import com.api.gestion.apigestionfacturas.constants.BillConstants;
import com.api.gestion.apigestionfacturas.dao.UserDAO;
import com.api.gestion.apigestionfacturas.models.UserModel;
import com.api.gestion.apigestionfacturas.security.CustomerDetailsService;
import com.api.gestion.apigestionfacturas.security.jwt.JwtUtil;
import com.api.gestion.apigestionfacturas.service.UserService;
import com.api.gestion.apigestionfacturas.util.BillUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Registro interno de un usuario {}", requestMap);

        try {
            if (validateSignUpMap(requestMap)){
                UserModel user = userDAO.findByEmail(requestMap.get("email"));
                if (Objects.isNull(user)){
                    userDAO.save(getUSerFromMap(requestMap));
                    return BillUtils.getResponseEntity("Usuario registrado con éxito", HttpStatus.CREATED);
                }else {
                    return BillUtils.getResponseEntity("El usuario con ese email ya existe", HttpStatus.BAD_REQUEST);
                }
            }else {
                return BillUtils.getResponseEntity(BillConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return BillUtils.getResponseEntity(BillConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Dentro del login");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );

            if (authentication.isAuthenticated()){
                if (customerDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\"" + jwtUtil.generateToken(customerDetailsService.getUserDetail().getEmail(),
                            customerDetailsService.getUserDetail().getRole()) + "\"}",
                            HttpStatus.OK);
                }else{
                    return new ResponseEntity<String>("{\"mensaje\":\""+"Espere a la aprobación del administrador" + "\"}", HttpStatus.BAD_REQUEST);
                }
            }

        }catch (Exception e){
            log.error("{}",e);
        }

        return new ResponseEntity<String>("{\"mensaje\":\""+"Credenciales incorrectas" + "\"}", HttpStatus.BAD_REQUEST);
    }



    private boolean validateSignUpMap(Map<String, String> requestMap){
        if(requestMap.containsKey("name") && requestMap.containsKey("telephone") && requestMap.containsKey("email") && requestMap.containsKey("password")){
            return true;
        }
        return false;
    }
    private UserModel getUSerFromMap(Map<String, String> requestMap){
        UserModel user = new UserModel();

        user.setName(requestMap.get("name"));
        user.setTelephone(requestMap.get("telephone"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");

        return user;
    }
}

package com.api.gestion.apigestionfacturas.security;

import com.api.gestion.apigestionfacturas.dao.UserDAO;
import com.api.gestion.apigestionfacturas.models.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class CustomerDetailsService implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;  // Acceso a la capa de datos para recuperar información del usuario.

    private UserModel userDetail;  // Almacena los detalles del usuario recuperados de la base de datos.

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        log.info("Dentro de loadUserByUsername");

        // Busca un usuario en la base de datos por su dirección de correo electrónico.
        userDetail = userDAO.findByEmail(userName);

        if (!Objects.isNull(userDetail)){
            // Si se encuentra un usuario con el correo electrónico especificado, crea un UserDetails basado en ese usuario.
            // Se utiliza una lista vacía para los roles/autoridades en este caso.
            return new User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
        } else {
            // Si no se encuentra un usuario, lanza una excepción de "UsernameNotFoundException".
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
    }

    // Método para obtener los detalles del usuario actual.
    public UserModel getUserDetail(){
        return userDetail;
    }
}

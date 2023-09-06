package com.api.gestion.apigestionfacturas.security.jwt;

import com.api.gestion.apigestionfacturas.security.CustomerDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;  // Utilidad para trabajar con tokens JWT.

    @Autowired
    private CustomerDetailsService customerDetailsService;  // Servicio para cargar detalles de usuario.

    private String userName = null;  // Almacena el nombre de usuario extraído del token JWT.
    private Claims claims = null;  // Almacena las reclamaciones (claims) extraídas del token JWT.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Comprueba si la solicitud es para rutas de autenticación, y si es así, permite el acceso sin autenticación.
        if (request.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")){
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader("Authorization");
            String token = null;

            // Extrae el token JWT del encabezado "Authorization" si existe.
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                token = authorizationHeader.substring(7);
                userName = jwtUtil.extractUsername(token);  // Extrae el nombre de usuario del token JWT.
                claims = jwtUtil.extractAllClaims(token);  // Extrae todas las reclamaciones del token JWT.
            }

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
                // Carga los detalles del usuario correspondientes al nombre de usuario extraído.
                UserDetails userDetails = customerDetailsService.loadUserByUsername(userName);

                // Valida el token JWT y, si es válido, autentica al usuario.
                if (jwtUtil.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // Establece los detalles de autenticación basados en la solicitud.
                    new WebAuthenticationDetailsSource().buildDetails(request);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        }
    }

    // Verifica si el usuario tiene un rol de administrador.
    public boolean isAdmin(){
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }

    // Verifica si el usuario tiene un rol de usuario.
    public boolean isUser(){
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }

    // Obtiene el nombre de usuario actual.
    public String getCurrentUser(){
        return userName;
    }
}

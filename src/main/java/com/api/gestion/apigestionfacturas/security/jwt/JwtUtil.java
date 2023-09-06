package com.api.gestion.apigestionfacturas.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${app.jwt-secret}")
    private String secret;  // Almacena la clave secreta utilizada para firmar y verificar tokens JWT.

    @Value("${app.jwt-expiration-milliseconds}")
    private int jwtExpirationInMs;  // Almacena la duración de expiración de los tokens JWT en milisegundos.

    // Extrae el nombre de usuario del token JWT.
    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    // Extrae la fecha de expiración del token JWT.
    public Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    // Utilidad genérica para extraer información de las reclamaciones de un token JWT.
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrae todas las reclamaciones del token JWT.
    public Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJwt(token).getBody();
    }

    // Verifica si un token JWT ha expirado.
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // Genera un nuevo token JWT con las reclamaciones proporcionadas.
    public String generateToken(String username, String role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return  createToken(claims, username);
    }

    // Crea un token JWT con las reclamaciones y el tema especificados.
    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    // Valida si un token JWT es válido para un UserDetails dado.
    public boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return  (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

package net.atopecode.javalinvue.auth.token;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import net.atopecode.javalinvue.auth.context.IUserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

/**
 * Esta clase crea, parsea y comprueba si es válido un token JWT.
 * Cuando el token expira y ya no es válido (lo comprueba el método 'validateToken()') se lanza una excepción y no se puede
 * Authenticar al Usuario. El cliente web debe pedir otro token para poder seguir realizando llamadas.
 */
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private String jwtSecret = "yourSecretCodeJWT";

    private int jwtExpirationInMs = 3600000; //1 Hora.

    public String generateToken(IUserPrincipal userPrincipal) throws JsonProcessingException {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setPayload(generatePayloadJWT(userPrincipal, userPrincipal.getId(), now, expiryDate))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private String generatePayloadJWT(IUserPrincipal userPrincipal,
                                      String subject, Date issuedAt, Date expiration) throws JsonProcessingException {
        ObjectMapper mapperJson = new ObjectMapper();
        JwtPayload payload = new JwtPayload(subject, issuedAt.getTime(), expiration.getTime(),
                userPrincipal.getName(), userPrincipal.getRoles());
        String payloadJson = mapperJson.writeValueAsString(payload);

        return payloadJson;
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = getClaimsFromJWT(token);
        return Long.parseLong(claims.getSubject());
    }

    public Claims getClaimsFromJWT(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims;
    }

    public JwtPayload getPayloadFromJWT(String token) throws JsonProcessingException {
        Object body = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parse(token)
                .getBody();
        ObjectMapper mapperJson = new ObjectMapper();
        //String bodyString = body.toString();
        String bodyJsonString = mapperJson.writeValueAsString(body);
        JwtPayload payload = mapperJson.readValue(bodyJsonString, JwtPayload.class);
        return payload;
    }

    public boolean validateToken(String authToken) {
        try {
            if(StringUtils.isBlank(authToken)){
                return false;
            }

            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}

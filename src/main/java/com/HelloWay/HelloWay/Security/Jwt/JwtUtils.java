package com.HelloWay.HelloWay.Security.Jwt;


import com.HelloWay.HelloWay.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${employeemanager.app.jwtSecret}")
    private String jwtSecret;

    @Value("${employeemanager.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${employeemanager.app.jwtCookieName}")
    private String jwtCookie;

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null && StringUtils.hasText(cookie.getValue())) {
            return cookie.getValue();
        } else {
            return null;
        }
    }


    // my it s a related problem there :

   /* public String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(jwtCookie)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    } */


    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        boolean activated = userPrincipal.isActivated(); // Get the account activation status
        String jwt = generateTokenFromUsername(userPrincipal.getUsername(), activated);
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();
        return cookie;
    }

    public ResponseCookie getCleanJwtCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
        return cookie;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public String generateTokenFromUsername(String username, boolean activated) {
        return Jwts.builder()
                .setSubject(username)
                .claim("activated", activated) // Include the account activation status as a claim
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }


    private UserDetailsImpl setUserActivationStatus(UserDetailsImpl userDetails, String jwt) {
        if (jwt != null) {
            try {
                Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();
                boolean activated = (boolean) claims.getOrDefault("activated", false);
                userDetails.setActivated(activated); // Set the account activation status in UserDetailsImpl
            } catch (ExpiredJwtException e) {
                // Handle expired token exception, if needed
            } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
                // Handle other token-related exceptions, if needed
            }
        }
        return userDetails;
    }

  /*  public String generateTemporaryToken(String username, String tableIdentifier) {
        // Create a new claims object
        Claims claims = Jwts.claims().setSubject(username);

        // Set the additional claims
        claims.put("tableIdentifier", tableIdentifier);

        // Generate the temporary token with the specified expiration time
        Date expiration = new Date(System.currentTimeMillis() + jwtExpirationMs);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    } */

  /*  public String generateTokenFromTableIdentifier(String tableIdentifier) {
        Claims claims = Jwts.claims().setSubject(tableIdentifier);
        claims.put("role", "GUEST"); // Set the user role as needed

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    } */
   /* public String getTableIdentifierFromJwtToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.get("tableIdentifier", String.class);
    } */
}

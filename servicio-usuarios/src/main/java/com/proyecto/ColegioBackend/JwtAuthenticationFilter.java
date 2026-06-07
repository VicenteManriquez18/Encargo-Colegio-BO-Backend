package com.proyecto.ColegioBackend;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String secret = "MiClaveSecretaSuperSeguraYLargaParaEsteProyectoDeSpring123";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                    // Parse JSON payload (simple way)
                    String sub = extractFromPayload(payload, "sub");
                    String rol = extractFromPayload(payload, "rol");
                    long exp = Long.parseLong(extractFromPayload(payload, "exp"));

                    if (System.currentTimeMillis() / 1000 < exp) {
                        // Validate signature
                        String header = new String(java.util.Base64.getUrlDecoder().decode(parts[0]));
                        String data = parts[0] + "." + parts[1];
                        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
                        mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
                        String expectedSignature = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes("UTF-8")));

                        if (expectedSignature.equals(parts[2])) {
                            // Token is valid
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                sub, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                            );
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
            } catch (Exception e) {
                // Invalid token, continue without authentication
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractFromPayload(String payload, String key) {
        // Simple JSON extraction, assumes format {"key":"value",...}
        String search = "\"" + key + "\":\"";
        int start = payload.indexOf(search);
        if (start != -1) {
            start += search.length();
            int end = payload.indexOf("\"", start);
            return payload.substring(start, end);
        }
        search = "\"" + key + "\":";
        start = payload.indexOf(search);
        if (start != -1) {
            start += search.length();
            int end = payload.indexOf(",", start);
            if (end == -1) end = payload.indexOf("}", start);
            return payload.substring(start, end).trim();
        }
        return null;
    }
}
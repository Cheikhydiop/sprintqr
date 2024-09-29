package com.dette.filter;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final String secretKey;

    public JwtAuthorizationFilter(UserDetailsService userDetailsService, String secretKey) {
        this.userDetailsService = userDetailsService;
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationToken = request.getHeader("Authorization");

        // Ignorer la validation JWT pour la route /login
        if (request.getRequestURI().equals("/login")) {
            filterChain.doFilter(request, response); // Continuez sans valider le token
            return; // Sortir de la méthode pour éviter le traitement supplémentaire
        }

        // Vérifier la présence et la validité du token JWT
        if (authorizationToken != null && authorizationToken.startsWith("Bearer ")) {
            String token = authorizationToken.substring(7); // Retirer le préfixe "Bearer "

            try {
                // Valider le token et extraire les claims
                Claims claims = validateToken(token); // Appel à la méthode validateToken

                String username = claims.getSubject();

                // Si le nom d'utilisateur est valide et non authentifié, charger les détails de l'utilisateur
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Si les détails de l'utilisateur sont valides, créer un token d'authentification
                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authenticationToken = 
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (Exception e) {
                System.err.println("Token invalide : " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // Interrompre la chaîne de filtres si le token est invalide
            }
        }

        // Continuer avec la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    private Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}

package com.example.CoreBack.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Set;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

  private final KeyStore store;

  public ApiKeyFilter(KeyStore store) {
    this.store = store;
  }

  
  @Override
  protected boolean shouldNotFilter(HttpServletRequest req) {
    String method = req.getMethod();
    if ("OPTIONS".equalsIgnoreCase(method)) return true; // CORS preflight
    if ("GET".equalsIgnoreCase(method)) return true;     // deja GETs libres

    // Ruta sin context path (p. ej., si usás /api)
    String path = req.getRequestURI();
    String context = req.getContextPath() == null ? "" : req.getContextPath();
    if (!context.isEmpty() && path.startsWith(context)) {
      path = path.substring(context.length());
    }

    // 🔒 Solo estas rutas quedan protegidas (POST/PUT/DELETE)
    Set<String> protectedPrefixes = Set.of(
      "/events/receive",
      "/events/publish",
      "/publish"
    );
    boolean matches = protectedPrefixes.stream().anyMatch(path::startsWith);
    return !matches; // true = no filtrar, false = filtrar
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    String apiKey = req.getHeader("X-API-KEY");
    if (apiKey != null) apiKey = apiKey.trim();

    if (apiKey == null || apiKey.isBlank() || !store.isValidKey(apiKey)) {
      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      res.setContentType("application/json");
      res.getWriter().write("{\"error\":\"Missing or invalid X-API-KEY\"}");
      return;
    }

    req.setAttribute("AUTH_API_KEY", apiKey);
    chain.doFilter(req, res);
  }
}

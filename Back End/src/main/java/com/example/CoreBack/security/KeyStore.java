package com.example.CoreBack.security;



import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class KeyStore {

  
  public static final String KEY_USUARIOS       = "sk_core_usuarios_u1Kp7W2fQ9aX6mDb3Tz8NrVy";
  public static final String KEY_MOVIES         = "sk_core_movies_m4Gs9Qp1Lk8Jz2Hb7Xt5VwRa";
  public static final String KEY_RATINGS        = "sk_core_ratings_r7Dz3Kq9Tf2Lm8Pw5Xa1NvGe";
  public static final String KEY_SOCIAL         = "sk_core_social_s2Qw8Vn5Jk1Mz7Lp4Rt9HbXe";
  public static final String KEY_DISCOVERY      = "sk_core_discovery_d9Lt2Xw6Qp3Va8Jm5Kr1ZyTc";
  public static final String KEY_ANALYTICS      = "sk_core_analytics_a6Np1Zv8Kr3Qw7Tx2Lm9HdRe";
  

  // Key → source autorizado (debe matchear con el `source` del body)
  private final Map<String, String> keyToSource = Map.of(
    KEY_USUARIOS,        "/usuarios/api",
    KEY_MOVIES,          "/movies/api",
    KEY_RATINGS,         "/ratings/api",
    KEY_SOCIAL,          "/social/api",
    KEY_DISCOVERY,       "/discovery/api",
    KEY_ANALYTICS,       "/analytics/api"
  );

  // Key → prefijos válidos de `type` (autorización por dominio)
  private final Map<String, Set<String>> keyToTypePrefixes = Map.of(
    KEY_USUARIOS,        Set.of("usuarios."),
    KEY_MOVIES,          Set.of("peliculas.", "movies."),
    KEY_RATINGS,         Set.of("resenas.", "ratings."),
    KEY_SOCIAL,          Set.of("social."),
    KEY_DISCOVERY,       Set.of("discovery."),
    KEY_ANALYTICS,       Set.of("analytics.")
  );

  public boolean isValidKey(String apiKey) { return keyToSource.containsKey(apiKey); }
  public Optional<String> sourceOf(String apiKey) { return Optional.ofNullable(keyToSource.get(apiKey)); }

  public boolean isTypeAllowed(String apiKey, String eventType) {
    var allowed = keyToTypePrefixes.getOrDefault(apiKey, Set.of());
    return eventType != null && allowed.stream().anyMatch(eventType::startsWith);
  }
}


package com.example.CoreBack.service;

import com.example.CoreBack.entity.EventEnvelope;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;

@Component
public class EventBusinessValidator {

  private final Clock clock = Clock.systemUTC();
  private static final Duration SKEW = Duration.ofMinutes(10); // ventana aceptada

  /** Valida que sysDate esté dentro de ±10 min del ahora (UTC). */
  public void validateSkew(EventEnvelope ev) {
    OffsetDateTime now = OffsetDateTime.now(clock);
    Duration diff = Duration.between(ev.getSysDate(), now).abs();
    if (diff.compareTo(SKEW) > 0) {
      throw new IllegalArgumentException("sysDate fuera de ventana de " + SKEW.toMinutes() + " minutos");
    }
  }

  /** Regla simple: si type comienza con 'usuarios.' el source debe contener '/usuario'. */
  public void validateTypeSourceConsistency(EventEnvelope ev) {
    if (ev.getType() != null && ev.getType().startsWith("usuarios.")
        && (ev.getSource() == null || !ev.getSource().contains("/usuario"))) {
      throw new IllegalArgumentException("source no es consistente con el type usuarios.*");
    }
  }
}
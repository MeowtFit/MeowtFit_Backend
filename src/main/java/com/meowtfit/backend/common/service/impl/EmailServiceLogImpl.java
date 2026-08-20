package com.meowtfit.backend.common.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.meowtfit.backend.common.service.EmailService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de respaldo para desarrollo local: en vez de enviar el correo por SMTP,
 * lo registra en el log del backend. Se activa por defecto (app.mail.enabled=false o ausente),
 * así el proyecto corre sin necesitar credenciales de Gmail. Útil para copiar manualmente el
 * enlace de recuperación de contraseña durante pruebas.
 *
 * Para enviar correos reales, define MAIL_ENABLED=true y las credenciales SMTP en
 * .env.properties (ver EmailServiceImpl).
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "false", matchIfMissing = true)
public class EmailServiceLogImpl implements EmailService {

    @Override
    @Async("taskExecutor")
    public void sendEmail(String to, String subject, String body) {
        log.info(
            "\n===== CORREO NO ENVIADO (app.mail.enabled=false) =====\n" +
            "Para: {}\nAsunto: {}\nContenido:\n{}\n" +
            "========================================================",
            to, subject, body
        );
    }
}

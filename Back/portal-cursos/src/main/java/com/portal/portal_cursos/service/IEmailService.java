package com.portal.portal_cursos.service;

public interface IEmailService {

    public void enviarCorreo(String destinatario, String asunto, String cuerpoHtml);
}

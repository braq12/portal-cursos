package com.portal.portal_cursos.templates;

public class EmailTemplates {

    public static String cursoCompletado(
            String nombreUsuario,
            String tituloCurso,
            String fechaTexto,
            String urlInsignia,
            String enlaceInsignia,
            String enlacePortal
    ) {
        String tpl = template(); // devuelve el HTML con ${TOKENS}

        String hasBadgeDisplay = (urlInsignia != null && !urlInsignia.isBlank()) ? "block" : "none";
        String cta = (enlaceInsignia != null && !enlaceInsignia.isBlank()) ? enlaceInsignia : enlacePortal;

        return tpl
                .replace("${NOMBRE}", escape(nombreUsuario))
                .replace("${TITULO_CURSO}", escape(tituloCurso))
                .replace("${FECHA}", escape(fechaTexto))
                .replace("${BADGE_DISPLAY}", hasBadgeDisplay)
                .replace("${URL_INSIGNIA}", urlInsignia == null ? "" : escape(urlInsignia))
                .replace("${CTA_LINK}", escape(cta))
                .replace("${ANIO}", String.valueOf(java.time.Year.now().getValue()));
    }

    private static String template() {
        return """
<!doctype html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title>¡Curso completado!</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <style>
    img { border: 0; line-height: 100%; outline: none; text-decoration: none; }
    table { border-collapse: collapse !important; }
    body { margin: 0; padding: 0; background-color: #f5f7fb; }
    @media only screen and (max-width:600px){
      .container { width: 100% !important; }
      .px-32 { padding-left:16px !important; padding-right:16px !important; }
      .py-32 { padding-top:16px !important; padding-bottom:16px !important; }
      .btn { width: 100% !important; }
    }
  </style>
</head>
<body style="background:#f5f7fb;">
  <div style="display:none; font-size:1px; color:#f5f7fb; line-height:1px; max-height:0; max-width:0; opacity:0; overflow:hidden;">
    ¡Felicitaciones! Has completado un curso y ganaste una insignia.
  </div>

  <table role="presentation" cellpadding="0" cellspacing="0" width="100%">
    <tr>
      <td align="center" style="padding:24px;">
        <table role="presentation" class="container" width="600" style="width:600px; max-width:600px; background:#ffffff; border-radius:12px; overflow:hidden;">
          <tr>
            <td style="background:linear-gradient(135deg,#3b82f6,#6366f1); padding:28px 24px; color:#ffffff;">
              <table width="100%">
                <tr>
                  <td align="left" style="font-family:Arial,Helvetica,sans-serif; font-size:20px; font-weight:700;">
                    Portal Cursos
                  </td>
                  <td align="right">
                    <img src="https://cdn-icons-png.flaticon.com/512/2991/2991112.png" width="36" height="36" alt="Trofeo" style="display:block;" />
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <tr>
            <td class="px-32 py-32" style="padding:32px; font-family:Arial,Helvetica,sans-serif; color:#111827;">
              <h1 style="margin:0 0 12px; font-size:22px; line-height:1.3;">¡Felicitaciones, ${NOMBRE}! 🎉</h1>
              <p style="margin:0 0 8px; font-size:14px; color:#374151;">
                Completaste el curso <strong>${TITULO_CURSO}</strong>.
              </p>
              <p style="margin:0 0 16px; font-size:13px; color:#6b7280;">
                Fecha de finalización: ${FECHA}
              </p>

              <div style="display:${BADGE_DISPLAY}; margin:20px 0;">
                <table role="presentation" width="100%">
                  <tr>
                    <td align="center" style="padding:12px;">
                      <img src="${URL_INSIGNIA}" alt="Insignia del curso" width="140" style="border-radius:12px; box-shadow:0 6px 18px rgba(0,0,0,0.10); max-width:100%;" />
                      <div style="margin-top:10px; font-size:12px; color:#6b7280;">Tu insignia del curso</div>
                    </td>
                  </tr>
                </table>
              </div>

              <div style="margin-top:8px; margin-bottom:24px;">
                <a href="${CTA_LINK}" target="_blank"
                   class="btn"
                   style="display:inline-block; background:#6366f1; color:#ffffff; text-decoration:none; font-size:14px; font-weight:600; padding:12px 18px; border-radius:8px;">
                  Ver mi insignia
                </a>
              </div>

              <hr style="border:0; border-top:1px solid #e5e7eb; margin:20px 0;" />

              <p style="margin:0; font-size:12px; color:#6b7280;">
                Desde tu panel en el portal puedes descargar tu insignia, compartirla o continuar con nuevos cursos.
              </p>
            </td>
          </tr>

          <tr>
            <td style="padding:18px 24px; background:#f9fafb; font-family:Arial,Helvetica,sans-serif; text-align:center; color:#9ca3af; font-size:12px;">
              © ${ANIO} Portal Cursos • Este es un mensaje automático, no respondas este correo.
            </td>
          </tr>
        </table>

        <div style="font-family:Arial,Helvetica,sans-serif; font-size:11px; color:#9ca3af; margin-top:12px;">
          Enviado de forma segura por tu plataforma.
        </div>
      </td>
    </tr>
  </table>
</body>
</html>
""";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'","&#39;");
    }
}

# MindSolutions

App de bienestar para estudiantes universitarios. Spring Boot 3.5 + Thymeleaf +
htmx + Alpine + Tailwind. Server-rendered, sin SPA.

Modulos: chat con Lia (Spring AI / OpenAI), meditaciones con audio, sesiones con
profesionales (pendiente de backend), panel de administracion.

## Como correrlo

```bash
cp .env.example .env      # y llenar los valores
npm install && npm run build   # vendor JS + fuentes + CSS
./mvnw spring-boot:run
```

`npm run css:watch` deja Tailwind recompilando mientras trabaja.

## Configuracion y secretos

- **Nunca** poner credenciales en `application.properties`. Todo entra por
  variables: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`,
  `AI_API_KEY`, `JWT_KEY`, `JWT_EXPIRATION`.
- En local se leen del `.env` de la raiz via
  `spring.config.import=optional:file:./.env[.properties]`. En el servidor se
  definen como variables de entorno reales, que tienen precedencia.
- `.env` esta en `.gitignore`. `.env.example` documenta las claves y se commitea.
- `JWT_KEY` debe ser Base64 de minimo 32 bytes o `Keys.hmacShaKeyFor` revienta
  al arrancar.
- `application.properties` va en **UTF-8**. Si se guarda en Windows-1252, el
  plugin de recursos de Maven falla antes de compilar.

## Arquitectura

Paquetes por feature (`ai`, `auth`, `user`, `dashboard`, `infra`), y dentro:

- `domain/` — entidades, repositorios, reglas y excepciones del modulo
- `application/` — casos de uso. `XApplicationService` escribe, `XQueryService`
  lee y devuelve DTOs
- `dto/request` y `dto/response` — las vistas **solo** reciben DTOs, nunca
  entidades JPA
- `web/` — controladores. Sin logica de negocio

Reglas que no se rompen:

- **Los servicios no conocen HTTP.** Nada de `HttpSession`, `Model` ni nombres
  de vista dentro de `application/` o `domain/`.
- **La identidad sale del `SecurityContext`**, via `@AuthenticationPrincipal
  AuthUser`. No se guardan entidades `User` en la sesion HTTP.
- **Autorizacion de conversaciones:** siempre por `@AuthorizedUUIDConversation`.
  El controlador recibe una `Conversation` ya verificada; nunca un UUID crudo.
  Este es el mecanismo que evita el IDOR, no lo puentee.
- Las marcas de tiempo se guardan como `Instant` y los DTOs de respuesta las
  convierten a `LocalDateTime`: Thymeleaf no puede formatear un `Instant`.

## Front

- **Un solo `<head>`**, en `layout/base.html`. Las paginas hacen
  `<head th:replace="~{layout/base :: head('Titulo')}">`.
- **Los fragmentos htmx se devuelven SIEMPRE como `"archivo :: fragmento"`.**
  Sin `::`, Thymeleaf inyecta el documento HTML completo dentro del contenedor.
- **Cero `<script>` dentro de fragmentos.** Toda la logica vive en
  `static/js/app.js` con delegacion de eventos. Un `<script>` en un fragmento
  vuelve a registrar sus listeners en cada swap y los acumula sin liberarlos.
- Convencion de nombres: `index.html` / `conversation.html` = pantalla completa,
  `_algo.html` = pieza reutilizable. La carpeta de templates espeja los paquetes
  de Java.
- **A una peticion htmx se le responde con `HX-Redirect`, no con `redirect:`.**
  Un 302 lo sigue htmx y termina pintando la pagina entera dentro del fragmento.
- Las librerias JS y las fuentes se sirven desde `/vendor` y `/fonts`, no desde
  un CDN. Se generan con `npm run vendor`.

## Diseño

El contexto de diseño completo esta en **`.impeccable.md`**: audiencia, tono,
paleta, anti-referencias y principios. Leerlo antes de tocar cualquier
interfaz.

Resumen operativo:

- Direccion: **refugio calido**. Terroso y textil, papel tintado, bordes de 1px.
- **Prohibido:** glassmorphism, degradados azul-morado, sombras difusas
  decorativas, texto con degradado, rachas o insignias que presionen.
- Tokens en OKLCH en `input.css`, tema claro y oscuro completos segun
  `prefers-color-scheme`. Neutros tintados hacia el naranja de la arcilla.
- Prioridad de accesibilidad: **legible en pantallas malas**. Ante la duda,
  mas contraste antes que mas sutileza.

## Pendientes conocidos

- El system prompt de Lia no maneja senales de crisis. Antes de desplegar con
  usuarios reales hay que definir que pasa ante ideacion suicida.
- `ddl-auto=update`: migrar a Flyway antes de que crezca el esquema.
- Sin tests.
- Separar `User` (auth) de un `Profile` (datos visibles) esta pendiente.

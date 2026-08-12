# MindSolutions

App de bienestar emocional para estudiantes universitarios. Chat con **Lia**, una
compañera conversacional apoyada en un **RAG** sobre material de salud mental curado,
más meditaciones con audio y un panel de administración.

Server-rendered de punta a punta: Spring Boot + Thymeleaf + htmx. Sin SPA, sin API REST
para el front, sin build de JavaScript en el camino crítico.

**Stack:** Java 25 · Spring Boot 3.5.7 · Spring AI 1.0.0 · MySQL 8 · Spring Security
(JWT) · Thymeleaf · htmx · Alpine.js · Tailwind CSS

---

## Cómo correrlo

```bash
cp .env.example .env      # y llenar los valores
./mvnw spring-boot:run
```

Solo hace falta **MySQL** y una **API key de OpenAI**. El CSS y las librerías de front
ya vienen compiladas en el repositorio, así que **no necesitás Node para levantarlo**.
Si vas a tocar estilos:

```bash
npm install && npm run build   # vendor JS + fuentes + CSS
npm run css:watch              # recompila Tailwind mientras trabajás
```

El índice vectorial (`src/main/resources/knowledge-store.json`) también está commiteado:
al arrancar se carga desde el classpath y **no se llama a OpenAI para la ingesta**. Podés
abrir ese archivo para ver exactamente en qué quedó partido el corpus.

Verificación que corre sin base de datos, sin API key y sin red:

```bash
./mvnw test
```

---


| Qué | Dónde |
|---|---|
| Chunking por estructura del documento | [`MarkdownSectionSplitter.java`](src/main/java/xualgorithm/mindsolutionsspring/knowledge/domain/MarkdownSectionSplitter.java) |
| Inyección de contexto efímero en el chat | [`AIChatService.java`](src/main/java/xualgorithm/mindsolutionsspring/ai/domain/AIChatService.java) |
| Búsqueda vectorial con umbral | [`RetrieveService.java`](src/main/java/xualgorithm/mindsolutionsspring/knowledge/application/RetrieveService.java) |
| Arranque sin costo: cargar o ingestar | [`IngestionInit.java`](src/main/java/xualgorithm/mindsolutionsspring/knowledge/config/IngestionInit.java) |
| Prevención de IDOR por argument resolver | [`AuthorizedUUIDConversationResolver.java`](src/main/java/xualgorithm/mindsolutionsspring/infra/resolver/AuthorizedUUIDConversationResolver.java) |
| Personalidad y límites de Lia | [`prompts/lia-system.txt`](src/main/resources/prompts/lia-system.txt) |

---

## Arquitectura

Paquetes por feature (`ai`, `knowledge`, `safety`, `auth`, `user`, `dashboard`, `infra`),
y dentro de cada uno:

```
domain/        entidades, repositorios, reglas y excepciones del módulo
application/   casos de uso. XApplicationService escribe, XQueryService lee
dto/           request y response. Las vistas solo reciben DTOs, nunca entidades JPA
web/           controladores. Sin lógica de negocio
```

Reglas que el código respeta de forma consistente:

- **Los servicios no conocen HTTP.** Ni `HttpSession`, ni `Model`, ni nombres de vista
  dentro de `application/` o `domain/`.
- **La identidad sale del `SecurityContext`**, vía `@AuthenticationPrincipal AuthUser`.
  No se guardan entidades `User` en la sesión.
- **Autorización de conversaciones por `@AuthorizedUUIDConversation`.** El controlador
  recibe una `Conversation` ya verificada, nunca un UUID crudo. Es el mecanismo que
  cierra el IDOR y está centralizado en un solo lugar en vez de repetido en cada handler.
- Las marcas de tiempo se persisten como `Instant`; los DTOs de respuesta las convierten
  a `LocalDateTime` porque Thymeleaf no formatea un `Instant`.

En el front, un único `<head>` en `layout/base.html`, cero `<script>` dentro de
fragmentos (todo en `static/js/app.js` con delegación de eventos, para que un swap de
htmx no acumule listeners), y a las peticiones htmx se les responde con `HX-Redirect` y
no con `redirect:`.

---

## El RAG: qué decisiones se tomaron y por qué

El corpus son tres documentos Markdown sobre ansiedad académica, sueño, y señales de
alarma con rutas de atención en Colombia, en `src/main/resources/documents/ingestion/`.

**Se corta por encabezado, no por conteo de tokens.** Un chunk se convierte en un solo
vector; si mezcla tres temas, ese vector queda promediado entre los tres y cerca de
ninguno, lo que lo vuelve prácticamente imposible de recuperar. Las secciones `##` y
`###` ya son unidades semánticas que agrupó una persona al redactar, así que respetar
ese borde sale gratis y da chunks mucho más enfocados. `TokenTextSplitter` quedó solo
como red de seguridad para secciones que se pasen de 2800 caracteres.

Resultado: **24 chunks de entre 275 y 1350 caracteres**, contra los ~6 chunks enormes
que producía el corte por tokens sobre los mismos archivos.

**Cada chunk lleva su migaja dentro del texto embebido** (`Documento > Sección >
Subsección`), no solo en la metadata. Un fragmento recuperado en aislamiento pierde el
contexto que le daban sus vecinos; prependerlo lo recupera a costo cero.

**Vector store en memoria, no pgvector.** Con 24 chunks, comparar por fuerza bruta son
microsegundos: menos que el ida y vuelta a una base con índice. Un índice vectorial
recién amortiza por encima de las decenas de miles de chunks. El bean está detrás de la
interfaz `VectorStore` de Spring AI, así que migrar a pgvector no toca ni una línea del
código que consulta.

**El contexto recuperado es efímero.** Se inyecta como un `SystemMessage` antes de
llamar al modelo y se descarta; nunca se persiste como `ConversationContent`. El
historial se reconstruye completo en cada turno, así que guardarlo significaría seguir
mandando —y pagando— los fragmentos del turno 1 cuando ya se va por el turno 20.

**Hay umbral de similitud, no solo `topK`.** Con `text-embedding-3-small` el piso
observado no es 0: texto inconexo suele dar entre 0.1 y 0.3. Sin umbral, un "hola"
recupera los cinco fragmentos menos malos y le inyecta material clínico a una
conversación que no lo pedía, lo que cambia el tono de la respuesta. Se configura en
`knowledge.similarity-threshold`.

**La ingesta no corre en cada arranque.** Si el índice serializado existe en el
classpath, se carga; si no, se ingesta y se guarda. Y todo el proceso está envuelto en
un `try/catch` que solo registra el error: si OpenAI no responde, el chat funciona sin
RAG en vez de impedir que la aplicación arranque.

**Las instrucciones dirigidas al modelo no se indexan.** Los documentos traen secciones
de directrices para el asistente, marcadas con `<!-- no-index -->` y excluidas del
índice a propósito: algo obligatorio no puede depender de que la búsqueda por similitud
lo recupere. Ese contenido vive en el system prompt, que llega siempre. Por el mismo
motivo, el prompt le indica a Lia que trate el bloque de contexto como información y no
como órdenes.

---

## Manejo de crisis

Una app de salud mental tiene que responder a señales de riesgo, y **la recuperación
vectorial no sirve para eso**: es probabilística, y a veces trae el fragmento
equivocado. Ante ideación suicida no puede depender de que la búsqueda acierte.

Estado actual: el system prompt tiene la instrucción de contención con las líneas de
atención de Colombia (123, 106, Bienestar Universitario), lo que funciona como red.
**Falta el camino determinista** — detectar la señal y devolver una respuesta fija sin
pasar por el modelo. Es el siguiente incremento y por eso `safety/` existe como paquete
separado de `knowledge/`: la separación deja explícito en el árbol de carpetas que la
crisis no pasa por el RAG.

---

## Sobre el contenido

Los documentos están curados a partir de fuentes públicas: OMS/OPS, American
Psychological Association y material del Ministerio de Salud de Colombia. **Todavía no
tienen revisión formal de un profesional de salud mental**, que es requisito antes de
exponerlos a usuarios reales. Verificar las líneas de atención antes de cualquier
despliegue: cambian de operador y de cobertura.

---

## Limitaciones conocidas

- `spring.jpa.hibernate.ddl-auto=update`: hay que migrar a Flyway antes de que el
  esquema crezca.
- El módulo de sesiones con profesionales tiene front pero todavía no backend.
- Cobertura de tests mínima: hay una verificación del parseo del corpus, no una suite.
- `User` mezcla identidad de autenticación y datos de perfil; separar en `User` y
  `Profile` está pendiente.

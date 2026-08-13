# PROMPT — Implementar animações e efeitos visuais (padrão Ritter&Co) em projeto Thymeleaf + Spring Boot

> Versão adaptada do prompt original (React + MUI + framer-motion) para um projeto
> **server-side rendered** com **Thymeleaf**. Não há bundler nem npm no classpath de
> templates — então toda a técnica é reproduzida com **CSS puro** (keyframes,
> transitions, custom properties) + um punhado de **JavaScript vanilla** (~150 linhas,
> sem dependências). Os valores numéricos (durações, easings, deslocamentos) são os
> mesmos do original para manter a mesma "sensação" de movimento.

---

Você é um engenheiro frontend trabalhando num projeto Spring Boot + Thymeleaf (sem
React/build step). Sua tarefa é adicionar um conjunto de animações e efeitos visuais
replicando o padrão descrito abaixo, usando apenas CSS e JS vanilla, mantendo o
estilo já existente em `static/css/*.css` (glass-morphism, `Plus Jakarta Sans`,
verde `rgba(22,78,54)`).

## 1. Stack (nada para instalar)

Sem npm. Tudo entra em dois arquivos novos, carregados em todas as páginas:

- `static/css/animacoes.css` — keyframes, transitions, variáveis de tema.
- `static/js/animacoes.js` — stagger via `IntersectionObserver`, toast, fechar/abrir
  suave, e (opcional, progressive enhancement) transição de página via
  **View Transitions API** nativa do Chrome/Edge (`document.startViewTransition`),
  com fallback silencioso em navegadores sem suporte (Firefox/Safari antigos
  simplesmente não animam a troca de página — nada quebra).

Incluir no `<head>` de cada página (ou num fragment Thymeleaf `head.html`):

```html
<link rel="stylesheet" href="/css/animacoes.css">
<script src="/js/animacoes.js" defer></script>
```

## 2. Princípios das animações (mesmos valores do original)

- **Entrada de página:** `opacity 0→1` + `translateY 16px→0`, saída invertida,
  `duration: 300ms`, `ease: ease-out` (CSS: `cubic-bezier(0, 0, 0.2, 1)`).
- **Entrada de tela cheia (login):** `opacity 0→1` + `translateY 24px→0`,
  `duration: 400ms`, `ease: ease-out`.
- **Cards em stagger:** `opacity 0→1` + `scale 0.9→1`, `delay: indice * 80ms`,
  `duration: 350ms`; hover `translateY(-6px)`.
- **Seções em sequência:** entrada `translateY 12px→0` com delays 100/200/300ms.
- **Loading:** skeleton (placeholder cinza pulsante) enquanto Thymeleaf renderiza
  dados vindos do banco síncrono; como o Thymeleaf já renderiza tudo no servidor,
  o "skeleton" aqui se aplica a conteúdo carregado via `fetch`/AJAX depois do load
  (ex.: listas que buscam dados assíncronos). Para páginas 100% server-rendered,
  o skeleton não é necessário — a entrada da página (item acima) já cobre a
  percepção de carregamento.

## 3. Componentes/Setup a criar

### 3.1. CSS base — `animacoes.css`

```css
/* animacoes.css */

:root {
    --anim-fast: 300ms;
    --anim-slow: 400ms;
    --ease-out: cubic-bezier(0, 0, 0.2, 1);
}

@media (prefers-reduced-motion: reduce) {
    *, *::before, *::after {
        animation-duration: 0.001ms !important;
        transition-duration: 0.001ms !important;
    }
}

/* Entrada de página (aplicar em <main> ou no wrapper de conteúdo) */
@keyframes page-enter {
    from { opacity: 0; transform: translateY(16px); }
    to   { opacity: 1; transform: translateY(0); }
}
.page-anim {
    animation: page-enter var(--anim-fast) var(--ease-out) both;
}

/* Entrada de tela cheia (login/hero) */
@keyframes hero-enter {
    from { opacity: 0; transform: translateY(24px); }
    to   { opacity: 1; transform: translateY(0); }
}
.hero-anim {
    animation: hero-enter var(--anim-slow) var(--ease-out) both;
}

/* Card com stagger — delay vem de --i via style inline do Thymeleaf */
@keyframes card-enter {
    from { opacity: 0; transform: scale(0.9); }
    to   { opacity: 1; transform: scale(1); }
}
.card-anim {
    opacity: 0;
    animation: card-enter 350ms var(--ease-out) forwards;
    animation-delay: calc(var(--i, 0) * 80ms);
    transition: transform 200ms var(--ease-out);
}
.card-anim:hover {
    transform: translateY(-6px);
}

/* Seções em sequência */
.section-anim {
    opacity: 0;
    animation: card-enter var(--anim-fast) var(--ease-out) forwards;
}
.section-anim:nth-of-type(1) { animation-delay: 100ms; }
.section-anim:nth-of-type(2) { animation-delay: 200ms; }
.section-anim:nth-of-type(3) { animation-delay: 300ms; }

/* Skeleton (para listas carregadas via fetch) */
@keyframes skeleton-pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.4; }
}
.skeleton {
    background: #e3e3e3;
    border-radius: 8px;
    animation: skeleton-pulse 1.2s ease-in-out infinite;
}

/* Toast / feedback */
@keyframes toast-in {
    from { opacity: 0; transform: translate(-50%, 12px); }
    to   { opacity: 1; transform: translate(-50%, 0); }
}
.toast {
    position: fixed;
    left: 50%;
    bottom: 24px;
    transform: translateX(-50%);
    min-width: 260px;
    max-width: 90vw;
    padding: 14px 20px;
    border-radius: 12px;
    color: #fff;
    font-family: 'Plus Jakarta Sans', sans-serif;
    font-weight: 600;
    box-shadow: 0 8px 24px rgba(0,0,0,0.2);
    z-index: 1000;
    animation: toast-in 250ms var(--ease-out) both;
}
.toast--success { background: #164e36; }
.toast--error   { background: #b3261e; }
.toast--info    { background: #2b5b8c; }

/* View Transitions API (progressive enhancement) */
@view-transition {
    navigation: auto;
}
::view-transition-old(root) {
    animation: page-exit var(--anim-fast) var(--ease-out) both;
}
::view-transition-new(root) {
    animation: page-enter var(--anim-fast) var(--ease-out) both;
}
@keyframes page-exit {
    from { opacity: 1; transform: translateY(0); }
    to   { opacity: 0; transform: translateY(-16px); }
}
```

### 3.2. JS vanilla — `animacoes.js`

```js
// animacoes.js

// 1) Stagger automático: qualquer .card-anim recebe --i = índice dentro do pai
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-stagger]').forEach((grupo) => {
        [...grupo.children].forEach((el, i) => el.style.setProperty('--i', i));
    });
});

// 2) Toast global — chamar window.toast('Mensagem', 'success' | 'error' | 'info')
window.toast = function (mensagem, tipo = 'success') {
    const el = document.createElement('div');
    el.className = `toast toast--${tipo}`;
    el.textContent = mensagem;
    document.body.appendChild(el);
    setTimeout(() => el.remove(), 4000);
};

// 3) Renderiza toast automaticamente a partir de flash attributes do Spring
//    (ver 3.4 — o fragment injeta um <script> chamando isso quando há mensagem)
</script>
```

### 3.3. Fragment de página animada — `fragments/pagina.html`

Equivalente ao `PaginaAnimada.jsx`: um fragment Thymeleaf reutilizável que toda
página inclui como wrapper, padronizando cabeçalho (título + subtítulo + ação) e
aplicando a classe de entrada:

```html
<!-- fragments/pagina.html -->
<div th:fragment="cabecalho(titulo, subtitulo)" class="page-anim">
    <div class="page-header">
        <div>
            <h1 th:text="${titulo}">Título</h1>
            <p th:if="${subtitulo}" th:text="${subtitulo}" class="page-subtitulo"></p>
        </div>
    </div>
</div>
```

Uso em qualquer página:

```html
<main class="container page-anim">
    <div th:replace="~{fragments/pagina :: cabecalho(titulo='Fichas Técnicas', subtitulo='Gerencie suas fichas')}"></div>
    <!-- conteúdo da página -->
</main>
```

### 3.4. Cards com stagger + hover (grids de estatística/opções)

```html
<div class="container" data-stagger>
    <div class="card card-anim" th:each="item, iter : ${itens}"
         th:style="'--i:' + ${iter.index}">
        <!-- conteúdo do card -->
    </div>
</div>
```

`data-stagger` faz o JS setar `--i` automaticamente por filho — funciona também
com `th:each` sem precisar calcular índice manualmente no template (ambas as formas
funcionam, use a que for mais legível no template).

### 3.5. Feedback (toast) via Spring flash attributes

No `Controller`, use `RedirectAttributes` normalmente:

```java
redirectAttributes.addFlashAttribute("sucesso", "Ficha técnica criada com sucesso!");
return "redirect:/dashboard";
```

No layout/fragment de página (uma vez só, no `body` de todas as páginas):

```html
<script th:if="${sucesso}" th:inline="javascript">
    window.addEventListener('DOMContentLoaded', () => toast(/*[[${sucesso}]]*/ '', 'success'));
</script>
<script th:if="${erro}" th:inline="javascript">
    window.addEventListener('DOMContentLoaded', () => toast(/*[[${erro}]]*/ '', 'error'));
</script>
```

Chame `redirectAttributes.addFlashAttribute("erro", "...")` nos catch dos
controllers para reaproveitar o mesmo mecanismo em erros.

### 3.6. Entrada da tela de Login

No `login.html`, aplicar a classe de entrada de tela cheia no card do formulário:

```html
<div class="login-card hero-anim">
    <!-- formulário -->
</div>
```

### 3.7. Transição entre páginas (equivalente ao AnimatePresence)

Como não há SPA/router, a transição "de verdade" entre documentos só existe via
**View Transitions API cross-document** (seção `@view-transition` do CSS acima —
suportada em Chromium recente; degrada graciosamente sem erro nos demais). Isso
já dá fade + slight slide ao clicar em qualquer link, sem JS adicional.

Se quiser a transição também nos navegadores sem suporte, alternativa mais simples
e 100% compatível: aplicar a classe `.page-anim` no `<main>` (já cobre a entrada) e,
opcionalmente, interceptar cliques em links internos para aplicar uma classe de
saída (`opacity:0; transform:translateY(-16px)`) por 300ms antes de navegar — mas
isso é opcional e adiciona complexidade; recomenda-se começar só com `.page-anim`
+ `@view-transition` e avaliar se vale a pena o polyfill manual.

## 4. Efeitos de tema (CSS custom properties) — adaptar ao verde já usado no projeto

O projeto já usa verde `rgba(22, 78, 54, ...)` e `Plus Jakarta Sans` — os efeitos
abaixo replicam o padrão Ritter&Co usando essa paleta já existente, sem trocar
identidade visual:

```css
:root {
    --cor-primaria: #164e36;
    --cor-primaria-dark: #0f3625;
    --cor-primaria-light: #2f7a54;
    --radius: 18px;
}

/* Cantos arredondados generosos */
.card, .btn, .login-card { border-radius: var(--radius); }

/* Botões pílula */
.btn, .navbar-btn, button[type="submit"] {
    border-radius: 999px;
    padding-inline: 20px;
    text-transform: none;
    font-weight: 600;
    box-shadow: 0 6px 18px rgba(22, 78, 54, 0.30);
    transition: transform 150ms var(--ease-out), box-shadow 150ms var(--ease-out);
}
.btn:hover, .navbar-btn:hover, button[type="submit"]:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 22px rgba(22, 78, 54, 0.38);
}

/* Sombra suave e colorida nos cards */
.card {
    box-shadow: 0 10px 30px rgba(22, 78, 54, 0.10);
    border: 1px solid rgba(22, 78, 54, 0.12);
}

/* Hero com gradiente diagonal (ex.: header/login) */
.hero, .login-hero {
    background: linear-gradient(120deg, var(--cor-primaria) 0%, var(--cor-primaria-dark) 60%, #08251a 100%);
    color: #fff;
}
.hero .chip, .login-hero .chip {
    background: rgba(255,255,255,0.14);
    color: #fff;
    border-radius: 999px;
    padding: 4px 12px;
}

/* Item de menu ativo */
.navbar-btn.ativo, nav a.ativo {
    background: var(--cor-primaria);
    color: #fff;
}
.navbar-btn.ativo:hover, nav a.ativo:hover {
    background: var(--cor-primaria-dark);
}
```

## 5. Fontes

O projeto já importa `Plus Jakarta Sans` em `navbar.css`. Para reproduzir o
contraste serif/sans do original (opcional), mover o `@import` para um `fonts.css`
compartilhado e adicionar uma fonte serifada só para títulos:

```css
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap');

h1, h2, .titulo-destaque { font-family: 'Playfair Display', serif; font-weight: 700; }
```

## 6. Como aplicar (passo a passo)

1. Criar `static/css/animacoes.css` (seção 3.1) e `static/js/animacoes.js` (3.2).
2. Incluir os dois em todas as páginas (via `<link>`/`<script>` direto ou, melhor,
   extrair um fragment `fragments/head.html` com `th:replace` se ainda não existir).
3. Aplicar `.page-anim` no `<main>`/container de cada página (3.3).
4. Aplicar `.card-anim` + `data-stagger` nos grids de cards existentes (`dashboard.html`,
   `visualizarFichaTecnica.html`, etc.) (3.4).
5. Trocar mensagens de sucesso/erro dos controllers para `RedirectAttributes` +
   flash attributes, e adicionar o snippet de toast no fragment comum (3.5).
6. Aplicar `.hero-anim` no card de `login.html` (3.6).
7. Adicionar a regra `@view-transition` no CSS para transição nativa entre páginas (3.7).
8. Aplicar as variáveis/efeitos de tema (seção 4) em `navbar.css`/CSS específicos
   sem quebrar a paleta verde já usada.

## 7. Critérios de aceite

- Toda página carrega com fade + leve subida (`page-enter`), sem "pulo" de layout.
- Cards de grid aparecem em sequência (stagger) e sobem 6px no hover.
- Cards têm cantos arredondados e sombra suave colorida; botões são pílulas.
- Após criar/editar/excluir algo, aparece um toast de sucesso ou erro (via flash
  attribute do Spring).
- Login exibe entrada suave de tela cheia.
- `prefers-reduced-motion: reduce` desliga as animações (já coberto pela media
  query em `animacoes.css`).
- Nenhuma dependência nova no `pom.xml` — tudo é CSS/JS estático servido pelo
  próprio Spring Boot.

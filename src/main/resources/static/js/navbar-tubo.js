/* ═══════════════════════════════════════════════════════
   NAVBAR TUBELIGHT — posiciona a "lâmpada" sobre o item ativo.

   Cada clique aqui recarrega a página inteira, então não dá
   para animar em JavaScript como o Framer Motion faz na versão
   React: a animação seria cortada no meio pela navegação.

   Ao carregar, a lâmpada é colocada no lugar SEM animação — se
   houvesse transição ela atravessaria a barra a cada troca de
   página, inclusive ao paginar dentro da mesma seção. O
   deslizamento de uma seção para outra fica por conta da View
   Transition declarada no CSS.
═══════════════════════════════════════════════════════ */
(function () {
    const nav = document.getElementById('navbarTubo');
    if (!nav) return;

    const lampada = nav.querySelector('.navbar-lampada');
    const itens = Array.from(nav.querySelectorAll('.navbar-item'));
    if (!lampada || itens.length === 0) return;

    /** `suave` só é usado no resize, onde o movimento é esperado. */
    function posicionar(suave) {
        const ativo = nav.querySelector('.navbar-item.ativo') || itens[0];
        if (!ativo) return;

        lampada.classList.toggle('lampada-suave', suave === true);
        lampada.style.width = ativo.offsetWidth + 'px';
        lampada.style.transform = 'translateX(' + ativo.offsetLeft + 'px)';
        nav.classList.add('pronta');
    }

    // Roda antes do primeiro quadro: o script fica no fim da nav, sem defer
    posicionar(false);

    let redimensionando;
    window.addEventListener('resize', function () {
        clearTimeout(redimensionando);
        redimensionando = setTimeout(function () { posicionar(true); }, 80);
    });

    // As fontes chegam depois do primeiro cálculo e mudam a largura dos
    // rótulos; o reajuste também é instantâneo para não virar animação.
    if (document.fonts && document.fonts.ready) {
        document.fonts.ready.then(function () { posicionar(false); });
    }
})();

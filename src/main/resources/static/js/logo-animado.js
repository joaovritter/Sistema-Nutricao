/* ═════════════════════════════════════════════════════
   ABERTURA EM TELA CHEIA

   O vídeo cobre a janela inteira e, ao terminar, a camada
   some revelando a página de login. Roda a cada carregamento,
   inclusive no F5.

   A página abaixo já está montada e pronta o tempo todo: se
   qualquer coisa der errado com o vídeo, a camada simplesmente
   desaparece e o login está lá. Com preferência por menos
   movimento o arquivo nem chega a ser baixado (preload="none").
═════════════════════════════════════════════════════ */
(function () {
    const camada = document.getElementById('introVideo');
    if (!camada) return;

    const video = camada.querySelector('.intro-video__midia');
    if (!video) return;

    /* Quanto tempo a abertura deve durar, em segundos. O arquivo tem
       ~8 s, então ele toca acelerado para caber aqui: a animação inteira
       aparece, do primeiro quadro ao logo final, só que mais rápida.
       A taxa é calculada a partir da duração real, então trocar o vídeo
       por um mais curto ou mais longo continua respeitando este alvo. */
    const DURACAO_ALVO = 4;

    let encerrado = false;

    /** Tira a camada da frente de vez, sem transição. */
    function remover() {
        encerrado = true;
        camada.classList.add('oculto');
        document.body.classList.remove('intro-ativa');
        video.pause();
    }

    /** Fim natural ou pulo: desvanece e só então sai do caminho. */
    function encerrar() {
        if (encerrado) return;
        encerrado = true;
        camada.classList.add('encerrando');
        document.body.classList.remove('intro-ativa');

        // Espera o fade terminar antes de esconder, para não cortar a transição.
        // O timer é a rede de segurança caso transitionend não dispare.
        const sair = function () {
            camada.classList.add('oculto');
            video.pause();
        };
        camada.addEventListener('transitionend', sair, { once: true });
        setTimeout(sair, 800);
    }

    const semMovimento = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (semMovimento) {
        remover();
        return;
    }

    document.body.classList.add('intro-ativa');

    video.addEventListener('loadedmetadata', function () {
        const dur = video.duration;
        if (isFinite(dur) && dur > DURACAO_ALVO) {
            video.playbackRate = dur / DURACAO_ALVO;
        }
    });

    video.addEventListener('ended', encerrar);
    video.addEventListener('error', remover);

    // Pular: clique em qualquer lugar da camada ou Esc
    camada.addEventListener('click', encerrar);
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') encerrar();
    });

    // Rede de segurança: sem os metadados em 5 s, não vale mais a pena esperar
    const desistir = setTimeout(function () {
        if (video.readyState === 0) remover();
    }, 5000);
    video.addEventListener('loadeddata', function () { clearTimeout(desistir); });

    // preload="none" no HTML: o download só começa aqui, quando vamos mesmo tocar
    video.preload = 'auto';
    video.load();

    const reproducao = video.play();
    if (reproducao && typeof reproducao.catch === 'function') {
        reproducao.catch(remover);
    }
})();

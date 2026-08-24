/* ═══════════════════════════════════════════════════════
   MOSTRAR / ESCONDER SENHA

   Monta o botão de olho dentro de qualquer [data-senha] que
   envolva um input de senha. É montado por JS de propósito:
   sem script o campo continua um password comum, em vez de
   ficar um botão morto na tela.

   Serve para qualquer tela — basta envolver o input com
   <div class="campo__caixa" data-senha>.
═══════════════════════════════════════════════════════ */
(function () {
    // Ícones no traço do resto do sistema (1.75, cantos arredondados)
    const OLHO = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.75" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true" focusable="false"><path d="M2.5 12S6 5.5 12 5.5 21.5 12 21.5 12 18 18.5 12 18.5 2.5 12 2.5 12Z"/><circle cx="12" cy="12" r="3"/></svg>';
    const OLHO_CORTADO = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.75" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true" focusable="false"><path d="M9.9 5.7A9.5 9.5 0 0 1 12 5.5c6 0 9.5 6.5 9.5 6.5a16.3 16.3 0 0 1-2.9 3.7M6.3 6.8A16.3 16.3 0 0 0 2.5 12S6 18.5 12 18.5c1.5 0 2.8-.4 4-1"/><path d="M10 10a2.8 2.8 0 0 0 4 4"/><path d="m3 3 18 18"/></svg>';

    document.querySelectorAll('[data-senha]').forEach(function (caixa) {
        const input = caixa.querySelector('input');
        if (!input || input.type !== 'password') return;

        const botao = document.createElement('button');
        botao.type = 'button';           // nunca envia o formulário
        botao.className = 'campo__olho';
        botao.innerHTML = OLHO;
        botao.setAttribute('aria-label', 'Mostrar senha');
        botao.setAttribute('aria-pressed', 'false');

        botao.addEventListener('click', function () {
            const revelando = input.type === 'password';
            input.type = revelando ? 'text' : 'password';
            botao.innerHTML = revelando ? OLHO_CORTADO : OLHO;
            botao.setAttribute('aria-label', revelando ? 'Esconder senha' : 'Mostrar senha');
            botao.setAttribute('aria-pressed', revelando ? 'true' : 'false');

            /* Devolve o cursor para o fim do texto: trocar o type move o
               caret para o começo, e quem estava digitando perde o lugar. */
            const fim = input.value.length;
            input.focus();
            try {
                input.setSelectionRange(fim, fim);
            } catch (e) {
                // Alguns navegadores recusam setSelectionRange em certos tipos
            }
        });

        caixa.classList.add('campo__caixa--com-olho');
        caixa.appendChild(botao);
    });
})();

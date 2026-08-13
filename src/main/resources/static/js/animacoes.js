// Animações — padrão Ritter&Co (ver docs/ANIMACOES.md)

document.addEventListener('DOMContentLoaded', () => {
    // Stagger automático: cards dentro de um container [data-stagger] recebem --i por índice
    document.querySelectorAll('[data-stagger]').forEach((grupo) => {
        [...grupo.children].forEach((el, i) => el.style.setProperty('--i', i));
    });
});

// Toast global — window.toast('Mensagem', 'success' | 'error' | 'info')
window.toast = function (mensagem, tipo = 'success') {
    const el = document.createElement('div');
    el.className = `toast toast--${tipo}`;
    el.textContent = mensagem;
    document.body.appendChild(el);
    setTimeout(() => {
        el.classList.add('toast--fechando');
        setTimeout(() => el.remove(), 200);
    }, 4000);
};

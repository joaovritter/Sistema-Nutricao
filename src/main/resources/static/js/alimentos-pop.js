/* ═══════════════════════════════════════════════════════
   POP-UP DE ALIMENTO — abre ao clicar num cartão da grade.

   Além dos macronutrientes, traz o atalho de inserir o
   alimento numa ficha técnica já cadastrada. O atalho não
   inventa valores: os campos da linha (pesos, fator de
   correção e custos) continuam sendo preenchidos por quem
   está inserindo, exatamente como na tela da ficha.
═══════════════════════════════════════════════════════ */
(function () {
    const fundo = document.getElementById('popAlimento');
    if (!fundo) return;

    const caixa = fundo.querySelector('.pop-caixa');
    const titulo = document.getElementById('popTitulo');
    const selo = document.getElementById('popSelo');
    const referencia = document.getElementById('popReferencia');
    const macros = document.getElementById('popMacros');
    const distribuicao = document.getElementById('popDistribuicao');
    const legenda = document.getElementById('popLegenda');
    const barras = {
        proteina: document.getElementById('popBarraProteina'),
        carboidrato: document.getElementById('popBarraCarboidrato'),
        lipidio: document.getElementById('popBarraLipidio')
    };
    const linkParecidos = document.getElementById('popParecidos');

    // ── Atalho de inserção ──────────────────────────────
    const botaoAdicionar = document.getElementById('popAdicionar');
    const painelInserir = document.getElementById('popInserir');
    const nomeNoPainel = document.getElementById('inserirNome');
    const seletorFicha = document.getElementById('inserirFicha');
    const erroInserir = document.getElementById('inserirErro');
    const sucessoInserir = document.getElementById('inserirSucesso');
    const campos = {
        medidaCaseira: document.getElementById('inserirMedida'),
        pesoBruto: document.getElementById('inserirPesoBruto'),
        pesoLiquido: document.getElementById('inserirPesoLiquido'),
        fatorCorrecao: document.getElementById('inserirFator'),
        custoCompra: document.getElementById('inserirCustoCompra'),
        pesoCompra: document.getElementById('inserirPesoCompra'),
        custoUtilizado: document.getElementById('inserirCustoUtilizado')
    };

    let alimentoAtual = null;
    let elementoAnterior = null;
    let fichasCarregadas = false;

    function numero(valor, casas) {
        return (valor == null ? 0 : valor).toLocaleString('pt-BR', {
            minimumFractionDigits: casas, maximumFractionDigits: casas
        });
    }

    function escapar(texto) {
        const alvo = document.createElement('span');
        alvo.textContent = texto == null ? '' : texto;
        return alvo.innerHTML;
    }

    function cartaoMacro(rotulo, valor, unidade, destaque) {
        return '<div class="macro-card surge' + (destaque ? ' destaque' : '') + '">'
            + '<div class="macro-rotulo">' + rotulo + '</div>'
            + '<div class="macro-valor">' + valor
            + '<span class="macro-unidade">' + unidade + '</span></div>'
            + '</div>';
    }

    function preencher(a) {
        alimentoAtual = a;
        titulo.textContent = a.nome;

        selo.textContent = a.personalizado ? 'Personalizado' : 'Tabela TACO';
        selo.className = 'selo ' + (a.personalizado ? 'selo--personalizado' : 'selo--taco');

        referencia.innerHTML = 'Valores por <strong>100 g</strong> do alimento.'
            + (a.autor ? ' Cadastrado por ' + escapar(a.autor) + '.' : '');

        macros.innerHTML =
            cartaoMacro('Energia', numero(a.kcal, 0), 'kcal', true)
            + cartaoMacro('Proteína', numero(a.proteina, 2), 'g')
            + cartaoMacro('Carboidrato', numero(a.carboidrato, 2), 'g')
            + cartaoMacro('Lipídio', numero(a.lipidio, 2), 'g')
            + cartaoMacro('Gordura saturada', numero(a.gorduraSaturada, 2), 'g')
            + cartaoMacro('Sódio', numero(a.sodio, 1), 'mg');

        // Quanto cada macro representa da energia total (4/4/9 kcal por grama)
        const kcal = a.kcal || 0;
        if (kcal > 0) {
            const partes = {
                proteina: (a.proteina || 0) * 4 * 100 / kcal,
                carboidrato: (a.carboidrato || 0) * 4 * 100 / kcal,
                lipidio: (a.lipidio || 0) * 9 * 100 / kcal
            };
            Object.keys(barras).forEach(function (chave) {
                barras[chave].style.width = partes[chave].toFixed(2) + '%';
            });
            legenda.innerHTML =
                '<span><i class="legenda-cor parte-proteina"></i> Proteína <strong>'
                + numero(partes.proteina, 0) + '%</strong></span>'
                + '<span><i class="legenda-cor parte-carboidrato"></i> Carboidrato <strong>'
                + numero(partes.carboidrato, 0) + '%</strong></span>'
                + '<span><i class="legenda-cor parte-lipidio"></i> Lipídio <strong>'
                + numero(partes.lipidio, 0) + '%</strong></span>';
            distribuicao.style.display = '';
        } else {
            distribuicao.style.display = 'none';
        }

        linkParecidos.href = '/alimentos?termo=' + encodeURIComponent(a.nome);
        nomeNoPainel.textContent = a.nome;
        botaoAdicionar.hidden = !window.PODE_EDITAR_FICHA;
    }

    // ── Abrir e fechar ──────────────────────────────────
    function abrir(id, origem) {
        elementoAnterior = origem || null;

        titulo.textContent = 'Carregando…';
        macros.innerHTML = '';
        legenda.innerHTML = '';
        recolherPainel();

        fundo.hidden = false;
        document.body.style.overflow = 'hidden';
        // Reflow antes da classe para a transição de entrada acontecer
        void fundo.offsetWidth;
        fundo.classList.add('aberto');

        fetch('/api/busca/alimentos/' + id, { credentials: 'include' })
            .then(function (r) {
                if (!r.ok) throw new Error('falhou');
                return r.json();
            })
            .then(function (alimento) {
                preencher(alimento);
                // Reinicia as animações agora que o conteúdo existe
                fundo.classList.remove('aberto');
                void fundo.offsetWidth;
                fundo.classList.add('aberto');
                caixa.focus();
            })
            .catch(function () {
                // Sem os dados, cai para a página completa em vez de mostrar um vazio
                window.location.href = '/alimentos/' + id;
            });
    }

    function fechar() {
        fundo.classList.remove('aberto');
        document.body.style.overflow = '';
        setTimeout(function () { fundo.hidden = true; }, 240);
        if (elementoAnterior) elementoAnterior.focus();
    }

    // ── Painel de inserção ──────────────────────────────
    function recolherPainel() {
        painelInserir.hidden = true;
        erroInserir.hidden = true;
        sucessoInserir.hidden = true;
    }

    function carregarFichas() {
        if (fichasCarregadas) return Promise.resolve();
        return fetch('/api/receitas/minhas', { credentials: 'include' })
            .then(function (r) {
                if (!r.ok) throw new Error('falhou');
                return r.json();
            })
            .then(function (fichas) {
                seletorFicha.innerHTML = '';
                if (fichas.length === 0) {
                    seletorFicha.innerHTML = '<option value="">Você ainda não tem fichas cadastradas</option>';
                    seletorFicha.disabled = true;
                } else {
                    seletorFicha.disabled = false;
                    fichas.forEach(function (f) {
                        const opcao = document.createElement('option');
                        opcao.value = f.id;
                        opcao.textContent = f.nome;
                        seletorFicha.appendChild(opcao);
                    });
                }
                fichasCarregadas = true;
            });
    }

    /** Deriva fator de correção e custo utilizado, que o usuário pode sobrescrever. */
    function sugerirDerivados() {
        const bruto = parseFloat(campos.pesoBruto.value);
        const liquido = parseFloat(campos.pesoLiquido.value);
        if (bruto > 0 && liquido > 0 && !campos.fatorCorrecao.dataset.tocado) {
            campos.fatorCorrecao.value = (bruto / liquido).toFixed(2);
        }

        const custo = parseFloat(campos.custoCompra.value);
        const pesoCompra = parseFloat(campos.pesoCompra.value);
        if (custo > 0 && pesoCompra > 0 && bruto > 0 && !campos.custoUtilizado.dataset.tocado) {
            campos.custoUtilizado.value = (custo / pesoCompra * bruto).toFixed(2);
        }
    }

    [campos.fatorCorrecao, campos.custoUtilizado].forEach(function (campo) {
        campo.addEventListener('input', function () { campo.dataset.tocado = '1'; });
    });
    [campos.pesoBruto, campos.pesoLiquido, campos.custoCompra, campos.pesoCompra]
        .forEach(function (campo) { campo.addEventListener('input', sugerirDerivados); });

    botaoAdicionar.addEventListener('click', function () {
        erroInserir.hidden = true;
        sucessoInserir.hidden = true;
        painelInserir.hidden = false;
        carregarFichas()
            .then(function () { campos.medidaCaseira.focus(); })
            .catch(function () {
                erroInserir.textContent = 'Não foi possível carregar suas fichas técnicas.';
                erroInserir.hidden = false;
            });
    });

    document.getElementById('inserirCancelar').addEventListener('click', recolherPainel);

    document.getElementById('inserirConfirmar').addEventListener('click', function () {
        erroInserir.hidden = true;
        sucessoInserir.hidden = true;

        if (!seletorFicha.value) {
            erroInserir.textContent = 'Escolha uma ficha técnica.';
            erroInserir.hidden = false;
            return;
        }
        if (!campos.medidaCaseira.value.trim()) {
            erroInserir.textContent = 'Informe a medida caseira.';
            erroInserir.hidden = false;
            return;
        }

        // Todos os campos numéricos da linha são obrigatórios no banco
        const numericos = ['pesoBruto', 'pesoLiquido', 'fatorCorrecao',
                           'custoCompra', 'pesoCompra', 'custoUtilizado'];
        const corpo = {
            ingredienteId: alimentoAtual.id,
            ingredienteNome: alimentoAtual.nome,
            medidaCaseira: campos.medidaCaseira.value.trim()
        };
        for (const chave of numericos) {
            const valor = parseFloat(campos[chave].value);
            if (isNaN(valor) || valor < 0) {
                erroInserir.textContent = 'Preencha todos os campos com valores válidos.';
                erroInserir.hidden = false;
                campos[chave].focus();
                return;
            }
            corpo[chave] = valor;
        }

        fetch('/api/receitas/' + seletorFicha.value + '/ingredientes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(corpo),
            credentials: 'include'
        })
            .then(function (r) {
                if (r.ok) return null;
                return r.json().then(function (dados) {
                    throw new Error(dados.message || 'Não foi possível adicionar.');
                }).catch(function (e) {
                    throw new Error(e.message || 'Não foi possível adicionar.');
                });
            })
            .then(function () {
                const nomeFicha = seletorFicha.options[seletorFicha.selectedIndex].textContent;
                sucessoInserir.textContent = 'Adicionado à ficha "' + nomeFicha + '".';
                sucessoInserir.hidden = false;
                numericos.concat(['medidaCaseira']).forEach(function (chave) {
                    campos[chave].value = '';
                    delete campos[chave].dataset.tocado;
                });
            })
            .catch(function (e) {
                erroInserir.textContent = e.message;
                erroInserir.hidden = false;
            });
    });

    // ── Gatilhos ────────────────────────────────────────
    document.querySelectorAll('[data-alimento]').forEach(function (gatilho) {
        gatilho.addEventListener('click', function (evento) {
            evento.preventDefault();
            abrir(gatilho.dataset.alimento, gatilho);
        });
    });

    document.getElementById('popFechar').addEventListener('click', fechar);

    fundo.addEventListener('click', function (evento) {
        if (evento.target === fundo) fechar();
    });

    document.addEventListener('keydown', function (evento) {
        if (evento.key === 'Escape' && fundo.classList.contains('aberto')) fechar();
    });
})();

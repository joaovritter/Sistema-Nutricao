-- ─────────────────────────────────────────────────────────────────────────────
-- Dados de exemplo: Fichas Técnicas (Receita) + Refeição
-- Usa ingredientes já inseridos pelo script 02-taco-data.sql (tabela TACO).
-- Vincula as receitas ao usuário "nutricionista" criado pelo DataInitializer.
-- ─────────────────────────────────────────────────────────────────────────────

USE nutricao;

-- ── Receita 1: Arroz Branco (Guarnição) ────────────────────────────────────
INSERT INTO receita (nome, categoria, modo_preparo, tempo_preparo, peso_porcao, rendimento,
                      equipamentos, numero_porcoes, fcc, agua_quantidade, agua_fcc, medida_caseira, nutricionista_id)
VALUES ('Arroz Branco', 'GUARNICAO',
        'Refogar o arroz no óleo, adicionar água fervente e sal, cozinhar em fogo baixo até secar.',
        25, 100.00, 2000.00, 'Panela, fogão', 20, 2.40, 1200.00, 1.00, '4 colheres de sopa (100g)',
        (SELECT id FROM user WHERE username = 'nutricionista'));

SET @receita_arroz_id = LAST_INSERT_ID();

INSERT INTO receita_ingrediente (receita_id, ingrediente_id, medida_caseira, peso_bruto, peso_liquido,
                                  fator_correcao, custo_compra, peso_compra, custo_utilizado, custo_total, custo_percapita)
VALUES
(@receita_arroz_id, (SELECT id FROM ingrediente WHERE nome = 'Arroz, tipo 1, cru' LIMIT 1),
 '4 xícaras', 830.00, 830.00, 1.00, 6.00, 1000.00, 4.98, 4.98, 0.25),
(@receita_arroz_id, (SELECT id FROM ingrediente WHERE nome = 'Óleo, de soja' LIMIT 1),
 '2 colheres de sopa', 30.00, 30.00, 1.00, 8.00, 900.00, 0.27, 0.27, 0.01);

-- ── Receita 2: Feijão Carioca Cozido (Guarnição) ────────────────────────────
INSERT INTO receita (nome, categoria, modo_preparo, tempo_preparo, peso_porcao, rendimento,
                      equipamentos, numero_porcoes, fcc, agua_quantidade, agua_fcc, medida_caseira, nutricionista_id)
VALUES ('Feijão Carioca Cozido', 'GUARNICAO',
        'Deixar o feijão de molho por 8h, cozinhar em panela de pressão com água até amaciar, temperar.',
        50, 90.00, 1800.00, 'Panela de pressão, fogão', 20, 2.20, 1500.00, 1.00, '1 concha (90g)',
        (SELECT id FROM user WHERE username = 'nutricionista'));

SET @receita_feijao_id = LAST_INSERT_ID();

INSERT INTO receita_ingrediente (receita_id, ingrediente_id, medida_caseira, peso_bruto, peso_liquido,
                                  fator_correcao, custo_compra, peso_compra, custo_utilizado, custo_total, custo_percapita)
VALUES
(@receita_feijao_id, (SELECT id FROM ingrediente WHERE nome = 'Feijão, carioca, cru' LIMIT 1),
 '2 xícaras', 400.00, 400.00, 1.00, 9.00, 1000.00, 3.60, 3.60, 0.18),
(@receita_feijao_id, (SELECT id FROM ingrediente WHERE nome = 'Óleo, de soja' LIMIT 1),
 '1 colher de sopa', 15.00, 15.00, 1.00, 8.00, 900.00, 0.13, 0.13, 0.01);

-- ── Receita 3: Frango Grelhado (Prato Principal) ────────────────────────────
INSERT INTO receita (nome, categoria, modo_preparo, tempo_preparo, peso_porcao, rendimento,
                      equipamentos, numero_porcoes, fcc, agua_quantidade, agua_fcc, medida_caseira, nutricionista_id)
VALUES ('Peito de Frango Grelhado', 'PRATO_PRINCIPAL',
        'Temperar o peito de frango, grelhar em chapa quente com fio de óleo até dourar dos dois lados.',
        20, 120.00, 2400.00, 'Chapa/frigideira, fogão', 20, 1.15, NULL, NULL, '1 filé médio (120g)',
        (SELECT id FROM user WHERE username = 'nutricionista'));

SET @receita_frango_id = LAST_INSERT_ID();

INSERT INTO receita_ingrediente (receita_id, ingrediente_id, medida_caseira, peso_bruto, peso_liquido,
                                  fator_correcao, custo_compra, peso_compra, custo_utilizado, custo_total, custo_percapita)
VALUES
(@receita_frango_id, (SELECT id FROM ingrediente WHERE nome = 'Frango, peito, sem pele, cru' LIMIT 1),
 '2,4 kg', 2760.00, 2400.00, 1.15, 18.00, 1000.00, 49.68, 49.68, 2.48),
(@receita_frango_id, (SELECT id FROM ingrediente WHERE nome = 'Óleo, de soja' LIMIT 1),
 '1 colher de sopa', 15.00, 15.00, 1.00, 8.00, 900.00, 0.13, 0.13, 0.01);

-- ── Receita 4: Salada de Alface e Tomate ────────────────────────────────────
INSERT INTO receita (nome, categoria, modo_preparo, tempo_preparo, peso_porcao, rendimento,
                      equipamentos, numero_porcoes, fcc, agua_quantidade, agua_fcc, medida_caseira, nutricionista_id)
VALUES ('Salada de Alface e Tomate', 'SALADA',
        'Higienizar as folhas e o tomate, picar e montar a salada, temperar com azeite e sal a gosto.',
        15, 80.00, 1600.00, 'Faca, tábua', 20, 1.30, NULL, NULL, '1 pegador (80g)',
        (SELECT id FROM user WHERE username = 'nutricionista'));

SET @receita_salada_id = LAST_INSERT_ID();

INSERT INTO receita_ingrediente (receita_id, ingrediente_id, medida_caseira, peso_bruto, peso_liquido,
                                  fator_correcao, custo_compra, peso_compra, custo_utilizado, custo_total, custo_percapita)
VALUES
(@receita_salada_id, (SELECT id FROM ingrediente WHERE nome = 'Alface, crespa, crua' LIMIT 1),
 '1 pé', 500.00, 425.00, 1.18, 3.00, 500.00, 3.00, 3.00, 0.15),
(@receita_salada_id, (SELECT id FROM ingrediente WHERE nome = 'Tomate, salada' LIMIT 1),
 '5 unidades', 700.00, 630.00, 1.11, 6.00, 1000.00, 4.20, 4.20, 0.21),
(@receita_salada_id, (SELECT id FROM ingrediente WHERE nome = 'Óleo, de soja' LIMIT 1),
 '1 colher de sopa', 15.00, 15.00, 1.00, 8.00, 900.00, 0.13, 0.13, 0.01);

-- ── Perfil Nutricional de cada receita ──────────────────────────────────────
-- Kcal calculado pelo fator de Atwater: proteína*4 + carboidrato*4 + lipídio*9
-- (a tabela ingrediente não guarda valor energético, só os macros da TACO).
INSERT INTO perfil_nutricional (per_capita, total_gramas, total_kcal, total_porcentagem, vct, receita_id)
SELECT
  ROUND(SUM(ri.peso_liquido) / r.numero_porcoes, 2)                                                     AS per_capita,
  ROUND(SUM(ri.peso_liquido), 2)                                                                         AS total_gramas,
  ROUND(SUM((i.proteina*4 + i.carboidrato*4 + i.lipidio*9) * ri.peso_liquido/100), 2)                     AS total_kcal,
  100.00                                                                                                  AS total_porcentagem,
  ROUND(SUM((i.proteina*4 + i.carboidrato*4 + i.lipidio*9) * ri.peso_liquido/100) / r.numero_porcoes, 2)  AS vct,
  r.id
FROM receita r
JOIN receita_ingrediente ri ON ri.receita_id = r.id
JOIN ingrediente i ON i.id = ri.ingrediente_id
WHERE r.id IN (@receita_arroz_id, @receita_feijao_id, @receita_frango_id, @receita_salada_id)
GROUP BY r.id, r.numero_porcoes;

-- ── Refeição: Almoço ─────────────────────────────────────────────────────────
INSERT INTO refeicao (nome) VALUES ('Almoço');
SET @refeicao_almoco_id = LAST_INSERT_ID();

INSERT INTO refeicao_receita (refeicao_id, receita_id) VALUES
(@refeicao_almoco_id, @receita_arroz_id),
(@refeicao_almoco_id, @receita_feijao_id),
(@refeicao_almoco_id, @receita_frango_id),
(@refeicao_almoco_id, @receita_salada_id);

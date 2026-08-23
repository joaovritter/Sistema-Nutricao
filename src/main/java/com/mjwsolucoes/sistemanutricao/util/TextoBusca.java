package com.mjwsolucoes.sistemanutricao.util;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

/**
 * Comparação de texto tolerante a acentos e a erros de digitação, usada para
 * sugerir alimentos "relacionados" quando a busca exata não encontra nada.
 */
public final class TextoBusca {

    private TextoBusca() {
    }

    /** Minúsculas, sem acentos e sem pontuação — a forma usada nas comparações. */
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return semAcento.toLowerCase().replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
    }

    public static boolean contem(String texto, String termo) {
        return normalizar(texto).contains(normalizar(termo));
    }

    /**
     * Grau de parecença entre 0 e 1. Considera o nome inteiro e também cada
     * palavra dele — assim "abacati" ainda encontra "Abacate, cru" e
     * "arros integral" encontra "Arroz, integral, cozido".
     */
    public static double similaridade(String nome, String termo) {
        String a = normalizar(nome);
        String b = normalizar(termo);
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        if (a.contains(b)) {
            return 1.0;
        }

        double melhor = jaccardTrigramas(a, b);

        // Compara termo a termo: nomes da TACO são longos ("Arroz, integral, cru")
        for (String palavraNome : a.split(" ")) {
            for (String palavraTermo : b.split(" ")) {
                if (palavraTermo.length() < 3) {
                    continue;
                }
                melhor = Math.max(melhor, levenshteinSimilaridade(palavraNome, palavraTermo));
            }
        }
        return melhor;
    }

    private static Set<String> trigramas(String texto) {
        Set<String> resultado = new HashSet<>();
        String preenchido = "  " + texto + "  ";
        for (int i = 0; i < preenchido.length() - 2; i++) {
            resultado.add(preenchido.substring(i, i + 3));
        }
        return resultado;
    }

    private static double jaccardTrigramas(String a, String b) {
        Set<String> ta = trigramas(a);
        Set<String> tb = trigramas(b);
        if (ta.isEmpty() || tb.isEmpty()) {
            return 0.0;
        }
        Set<String> intersecao = new HashSet<>(ta);
        intersecao.retainAll(tb);
        Set<String> uniao = new HashSet<>(ta);
        uniao.addAll(tb);
        return (double) intersecao.size() / uniao.size();
    }

    private static double levenshteinSimilaridade(String a, String b) {
        int distancia = levenshtein(a, b);
        int maior = Math.max(a.length(), b.length());
        return maior == 0 ? 1.0 : 1.0 - ((double) distancia / maior);
    }

    private static int levenshtein(String a, String b) {
        int[] anterior = new int[b.length() + 1];
        int[] atual = new int[b.length() + 1];

        for (int j = 0; j <= b.length(); j++) {
            anterior[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            atual[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int custo = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                atual[j] = Math.min(Math.min(atual[j - 1] + 1, anterior[j] + 1), anterior[j - 1] + custo);
            }
            int[] troca = anterior;
            anterior = atual;
            atual = troca;
        }
        return anterior[b.length()];
    }
}

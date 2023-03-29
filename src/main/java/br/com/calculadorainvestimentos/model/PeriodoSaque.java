package br.com.calculadorainvestimentos.model;

public class PeriodoSaque {
    public int anos;
    public int meses;

    public PeriodoSaque(final int qtdeMaxSaques) {
        anos = qtdeMaxSaques / 12;
        meses = qtdeMaxSaques % 12;
    }
}

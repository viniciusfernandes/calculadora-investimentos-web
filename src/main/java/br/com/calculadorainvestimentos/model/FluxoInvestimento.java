package br.com.calculadorainvestimentos.model;

public class FluxoInvestimento {
    public final ProjecaoInvestimento projecaoInvestimento;
    public final ProjecaoSaque projecaoSaque;

    public FluxoInvestimento(final ProjecaoInvestimento projecaoInvestimento, final ProjecaoSaque projecaoSaque) {
        this.projecaoInvestimento = projecaoInvestimento;
        this.projecaoSaque = projecaoSaque;
    }
}

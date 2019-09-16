package br.com.calculadorainvestimentos.model;

public class FluxoInvestimento {
    private final ProjecaoInvestimento projecaoInvestimento;
    private final ProjecaoSaque projecaoSaque;

    public FluxoInvestimento(final ProjecaoInvestimento projecaoInvestimento, final ProjecaoSaque projecaoSaque) {
        this.projecaoInvestimento = projecaoInvestimento;
        this.projecaoSaque = projecaoSaque;
    }

    public ProjecaoInvestimento getProjecaoInvestimento() {
        return projecaoInvestimento;
    }

    public ProjecaoSaque getProjecaoSaque() {
        return projecaoSaque;
    }
}

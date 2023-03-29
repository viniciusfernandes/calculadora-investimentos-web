package br.com.calculadorainvestimentos.algoritmo;

public class SimulacaoSaques {
    public int quantidadeMaxSaques;
    public double valorIR;
    public double valorRestante;
    public double valorUltimoSaque;
    public boolean limiteCalculoExcedido;

    public SimulacaoSaques(int quantidadeMaxSaques, double valorIR, double valorRestante, double valorUltimoSaque,
                           boolean limiteCalculoExcedido) {
        this.quantidadeMaxSaques = quantidadeMaxSaques;
        this.valorIR = valorIR;
        this.valorRestante = valorRestante;
        this.valorUltimoSaque = valorUltimoSaque;
        this.limiteCalculoExcedido = limiteCalculoExcedido;
    }
}

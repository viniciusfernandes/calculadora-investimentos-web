package br.com.calculadorainvestimentos.algoritmo;

public class SimulacaoSaques {
    public int quantidadeMaxSaques;
    public double valorIR;
    public double valorRestante;
    public double valorUltimoSaque;

    public SimulacaoSaques(int quantidadeMaxSaques, double valorIR, double valorRestante, double valorUltimoSaque) {
        this.quantidadeMaxSaques = quantidadeMaxSaques;
        this.valorIR = valorIR;
        this.valorRestante = valorRestante;
        this.valorUltimoSaque = valorUltimoSaque;
    }
}

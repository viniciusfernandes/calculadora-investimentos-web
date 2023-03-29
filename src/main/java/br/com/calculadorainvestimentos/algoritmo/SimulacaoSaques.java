package br.com.calculadorainvestimentos.algoritmo;

public class SimulacaoSaques {
    public int quantidadeMaxSaques;
    public double valorIR;
    public double valorRestante;

    public SimulacaoSaques(int quantidadeMaxSaques, double valorIR, double valorRestante) {
        this.quantidadeMaxSaques = quantidadeMaxSaques;
        this.valorIR = valorIR;
        this.valorRestante = valorRestante;
    }
}

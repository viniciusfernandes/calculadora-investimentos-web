package br.com.calculadorainvestimentos.model;

public class ProjecaoSaque {
    private double aliquotaReaplicacaoMes;
    private PeriodoSaque periodoSaque;
    private int qtdeMaxSaques;
    private double valorPrimeiroSaque;
    private double valorRestante;
    private double valorUltimoSaque;

    public double getAliquotaReaplicacaoMes() {
        return aliquotaReaplicacaoMes;
    }

    public PeriodoSaque getPeriodoSaque() {
        return periodoSaque;
    }

    public int getQtdeMaxSaques() {
        return qtdeMaxSaques;
    }

    public double getValorPrimeiroSaque() {
        return valorPrimeiroSaque;
    }

    public double getValorRestante() {
        return valorRestante;
    }

    public double getValorUltimoSaque() {
        return valorUltimoSaque;
    }

    public void setAliquotaReaplicacaoMes(final double aliquotaReaplicacaoMes) {
        this.aliquotaReaplicacaoMes = aliquotaReaplicacaoMes;
    }

    public void setPeriodoSaque(final PeriodoSaque periodoSaque) {
        this.periodoSaque = periodoSaque;
    }

    public void setQtdeMaxSaques(final int qtdeMaxSaques) {
        this.qtdeMaxSaques = qtdeMaxSaques;
    }

    public void setValorPrimeiroSaque(final double valorPrimeiroSaque) {
        this.valorPrimeiroSaque = valorPrimeiroSaque;
    }

    public void setValorRestante(final double valorRestante) {
        this.valorRestante = valorRestante;
    }

    public void setValorUltimoSaque(final double valorUltimoSaque) {
        this.valorUltimoSaque = valorUltimoSaque;
    }

}

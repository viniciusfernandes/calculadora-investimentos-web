package br.com.calculadorainvestimentos.model;

public class ProjecaoSaque {
    private double valorPrimeiroSaque;
    private double valorUltimoSaque;
    private int qtdeMaxSaques;
    private PeriodoSaque periodoSaque;
    private double valorRestante;
    private double aliquotaReaplicacaoMes;

    public double getAliquotaReaplicacaoMes() {
        return aliquotaReaplicacaoMes;
    }

    public void setAliquotaReaplicacaoMes(final double aliquotaReaplicacaoMes) {
        this.aliquotaReaplicacaoMes = aliquotaReaplicacaoMes;
    }

    public double getValorPrimeiroSaque() {
        return valorPrimeiroSaque;
    }

    public void setValorPrimeiroSaque(final double valorPrimeiroSaque) {
        this.valorPrimeiroSaque = valorPrimeiroSaque;
    }

    public double getValorUltimoSaque() {
        return valorUltimoSaque;
    }

    public void setValorUltimoSaque(final double valorUltimoSaque) {
        this.valorUltimoSaque = valorUltimoSaque;
    }

    public int getQtdeMaxSaques() {
        return qtdeMaxSaques;
    }

    public void setQtdeMaxSaques(final int qtdeMaxSaques) {
        this.qtdeMaxSaques = qtdeMaxSaques;
    }

    public double getValorRestante() {
        return valorRestante;
    }

    public void setValorRestante(final double valorRestante) {
        this.valorRestante = valorRestante;
    }

    public PeriodoSaque getPeriodoSaque() {
        return periodoSaque;
    }

    public void setPeriodoSaque(final PeriodoSaque periodoSaque) {
        this.periodoSaque = periodoSaque;
    }

}

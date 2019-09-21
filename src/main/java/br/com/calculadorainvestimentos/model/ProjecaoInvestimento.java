package br.com.calculadorainvestimentos.model;

public class ProjecaoInvestimento {
    private double aliquotaAplicacaoMes;
    private double aliquotaGanhoFinal;
    private double aliquotaGanhoReal;
    private double aliquotaInflacaoAcumulada;
    private double aliquotaInflacaoAcumuladaMes;
    private double aliquotaInflacaoMes;
    private double aliquotaReal;
    private int qtdeAportes;
    private double valorFinal;
    private double valorInvestido;
    private double valorInvestidoDepreciado;
    private double valorReal;

    public double getAliquotaAplicacaoMes() {
        return aliquotaAplicacaoMes;
    }

    public double getAliquotaGanhoFinal() {
        return aliquotaGanhoFinal;
    }

    public double getAliquotaGanhoReal() {
        return aliquotaGanhoReal;
    }

    public double getAliquotaInflacaoAcumulada() {
        return aliquotaInflacaoAcumulada;
    }

    public double getAliquotaInflacaoAcumuladaMes() {
        return aliquotaInflacaoAcumuladaMes;
    }

    public double getAliquotaInflacaoMes() {
        return aliquotaInflacaoMes;
    }

    public double getAliquotaReal() {
        return aliquotaReal;
    }

    public int getQtdeAportes() {
        return qtdeAportes;
    }

    public double getValorFinal() {
        return valorFinal;
    }

    public double getValorInvestido() {
        return valorInvestido;
    }

    public double getValorInvestidoDepreciado() {
        return valorInvestidoDepreciado;
    }

    public double getValorReal() {
        return valorReal;
    }

    public void setAliquotaAplicacaoMes(final double aliquotaAplicacaoMes) {
        this.aliquotaAplicacaoMes = aliquotaAplicacaoMes;
    }

    public void setAliquotaGanhoFinal(final double aliquotaGanhoFinal) {
        this.aliquotaGanhoFinal = aliquotaGanhoFinal;
    }

    public void setAliquotaGanhoReal(final double aliquotaGanhoReal) {
        this.aliquotaGanhoReal = aliquotaGanhoReal;
    }

    public void setAliquotaInflacaoAcumulada(final double aliquotaInflacaoAcumulada) {
        this.aliquotaInflacaoAcumulada = aliquotaInflacaoAcumulada;
    }

    public void setAliquotaInflacaoAcumuladaMes(final double aliquotaInflacaoAcumuladaMes) {
        this.aliquotaInflacaoAcumuladaMes = aliquotaInflacaoAcumuladaMes;
    }

    public void setAliquotaInflacaoMes(final double aliquotaInflacaoMes) {
        this.aliquotaInflacaoMes = aliquotaInflacaoMes;
    }

    public void setAliquotaReal(final double aliquotaReal) {
        this.aliquotaReal = aliquotaReal;
    }

    public void setQtdeAportes(final int qtdeAportes) {
        this.qtdeAportes = qtdeAportes;
    }

    public void setValorFinal(final double valorFinal) {
        this.valorFinal = valorFinal;
    }

    public void setValorInvestido(final double valorInvestido) {
        this.valorInvestido = valorInvestido;
    }

    public void setValorInvestidoDepreciado(final double valorInvestidoDepreciado) {
        this.valorInvestidoDepreciado = valorInvestidoDepreciado;
    }

    public void setValorReal(final double valorReal) {
        this.valorReal = valorReal;
    }

}

package br.com.calculadorainvestimentos.algoritmo;

import br.com.calculadorainvestimentos.model.FluxoInvestimento;
import br.com.calculadorainvestimentos.model.Investimento;
import br.com.calculadorainvestimentos.model.PeriodoSaque;
import br.com.calculadorainvestimentos.model.ProjecaoInvestimento;
import br.com.calculadorainvestimentos.model.ProjecaoSaque;

import static java.lang.Math.pow;

public class CalculadoraInvestimento {

    public static final int QTDE_MAX_SAQUES = 12 * 100;
    private double indiceAplicacao;

    private double indiceAplicacaoMes;
    private double indiceGanhoFinal;
    private double indiceGanhoReal;
    private double indiceInflacao;
    private double indiceInflacaoAcumulada;
    private double indiceInflacaoAcumuladaMes;

    private double indiceInflacaoMes;
    private double indiceIR;

    private double indiceRealMes;

    private int qtdeAportes;
    private int qtdeMaxSaques;
    private double valorAporte;
    private double valorFinalAplicado;
    private double valorInicial;
    private double valorTotalInvestido;

    private double valorInvestidoDepreciado;
    private double valorPrimeiroSaque;
    private double valorReal;
    private double valorIR;

    private double valorRestante;

    private double valorSaque;
    private double valorUltimoSaque;

    public FluxoInvestimento calcular(final Investimento investimento) {
        inicializarIndices(investimento);
        calcularIndicesMensaisEValores();
        calcularQtdeMaxSaquesEValorRestante();

        final ProjecaoInvestimento projInvest = new ProjecaoInvestimento();
        projInvest.aliquotaGanhoFinal = indiceGanhoFinal * 100;
        projInvest.aliquotaGanhoReal = indiceGanhoReal * 100;
        projInvest.aliquotaInflacaoAcumulada = indiceInflacaoAcumulada * 100;
        projInvest.aliquotaInflacaoAcumuladaMes = indiceInflacaoAcumuladaMes * 100;
        projInvest.aliquotaInflacaoMes = indiceInflacaoMes * 100;
        projInvest.qtdeAportes = qtdeAportes;
        projInvest.valorFinal = valorFinalAplicado;
        projInvest.valorInvestido = valorTotalInvestido;
        projInvest.valorInvestidoDepreciado = valorInvestidoDepreciado;
        projInvest.valorIR = valorIR;
        projInvest.valorReal = valorReal;
        projInvest.aliquotaAplicacaoMes = indiceAplicacaoMes * 100;
        projInvest.aliquotaReal = indiceRealMes * 100;

        final ProjecaoSaque projSaque = new ProjecaoSaque();
        projSaque.qtdeMaxSaques = qtdeMaxSaques;
        projSaque.valorPrimeiroSaque = valorPrimeiroSaque;
        projSaque.valorRestante = valorRestante;
        projSaque.valorUltimoSaque = valorUltimoSaque;
        projSaque.periodoSaque = new PeriodoSaque(qtdeMaxSaques);

        return new FluxoInvestimento(projInvest, projSaque);
    }

    @Deprecated
    private double calcularIndiceAcumulado(final int quantidade, final double idxRendimento) {
        return calcularIndiceAcumulado(quantidade, idxRendimento, 0);
    }

    @Deprecated
    private double calcularIndiceAcumulado(final int quantidade, final double idxRendimento, final double idxInflacao) {
        double idxAcumulado = 0;

        for (int i = 0; i < quantidade; i++) {
            idxAcumulado += pow(1 + idxInflacao, i) * pow(1 + idxRendimento, quantidade - i);
        }

        return idxAcumulado;
    }

    public double calcularIndiceMensalEquivalente(final double indicePeriodo, final int periodo) {
        if (periodo <= 0) {
            return 0;
        }
        return Math.pow(1 + indicePeriodo, 1d / periodo) - 1;
    }

    public double calcularIndiceRendimentoMedio(double valorFinalAplicado, double valorTotalInvestido, int qtdeAportes) {
        if (valorFinalAplicado <= 0 || valorTotalInvestido <= 0) {
            return 0;
        }
        final double indiceLucro = valorFinalAplicado / valorTotalInvestido - 1;
        return calcularIndiceMensalEquivalente(indiceLucro, qtdeAportes);
    }

    public double calcularIndiceMensalEquivalente(final double indiceAnual) {
        return calcularIndiceMensalEquivalente(indiceAnual, 12);
    }

    public double calcularIndiceReal(final double indice, final double indiceInflacao) {
        return (1 + indice) / (1 + indiceInflacao) - 1;
    }

    private void calcularIndicesMensaisEValores() {
        indiceAplicacaoMes = calcularIndiceMensalEquivalente(indiceAplicacao);
        indiceInflacaoMes = calcularIndiceMensalEquivalente(indiceInflacao);
        indiceRealMes = calcularIndiceReal(indiceAplicacaoMes, indiceInflacaoMes);

        valorFinalAplicado = calcularValorFinalAplicacao(valorAporte, valorInicial, indiceAplicacaoMes, qtdeAportes);
        valorTotalInvestido = calcularValorTotalInvestido();
        valorReal = calcularValorReal();
        valorInvestidoDepreciado = valorAporte * calcularIndiceAcumulado(qtdeAportes, -indiceInflacaoMes);

        indiceGanhoFinal = (valorFinalAplicado - valorTotalInvestido) / valorTotalInvestido;
        indiceGanhoReal = (valorReal - valorTotalInvestido) / valorTotalInvestido;

        if (valorInvestidoDepreciado != 0d) {
            indiceInflacaoAcumulada = (valorTotalInvestido - valorInvestidoDepreciado) / valorInvestidoDepreciado;
        } else {
            indiceInflacaoAcumulada = 0;
        }
        indiceInflacaoAcumuladaMes = calcularIndiceMensalEquivalente(indiceInflacaoAcumulada, qtdeAportes);
        valorPrimeiroSaque = valorSaque * pow(1 + indiceInflacaoMes, qtdeAportes);
    }

    private void calcularQtdeMaxSaques(double valorFinal, double valorSaque, double valorImpRenda,
                                       final double indiceInflacao, final double indiceLucro, int numSaque) {
        valorRestante = valorFinal;
        qtdeMaxSaques = ++numSaque;
        valorIR += valorImpRenda;

        valorImpRenda = valorSaque * indiceLucro * indiceIR;

        // Subtranido o valor do IR incidente no saque.
        valorFinal -= (valorSaque + valorImpRenda);

        if (valorFinal < 0) {
            qtdeMaxSaques = --numSaque;
            return;
        } else if (valorFinal == 0 || numSaque >= QTDE_MAX_SAQUES) {
            return;
        }

        valorSaque *= (1 + indiceInflacao);
        calcularQtdeMaxSaques(valorFinal, valorSaque, valorImpRenda, indiceInflacao, indiceLucro,
            numSaque);
    }

    private void calcularQtdeMaxSaquesEValorRestante() {
        final double valorSaqueFuturo = valorSaque * pow((1 + indiceInflacaoMes), qtdeAportes);
        final double indiceRendMedio = calcularIndiceRendimentoMedio(valorFinalAplicado, valorTotalInvestido, qtdeAportes);

        calcularQtdeMaxSaques(valorFinalAplicado, valorSaqueFuturo, 0, indiceInflacaoMes, indiceRendMedio, 0);
        valorUltimoSaque = valorPrimeiroSaque * pow(1 + indiceInflacaoMes, qtdeMaxSaques - 1d);
    }

    public double calcularValorRendimentoInvestimentoInicial(double valorInicial, double indiceAplicacaoMes,
                                                             int qtdeAportes) {
        return valorInicial * pow(1 + indiceAplicacaoMes, qtdeAportes);
    }


    public double calcularValorFinalAplicacao(double valorAporte, double valorInicial, double indiceAplicacaoMes,
                                              int qtdeAportes) {
        double valorRendimentoInvestimentoInicial = calcularValorRendimentoInvestimentoInicial(valorInicial, indiceAplicacaoMes, qtdeAportes);
        double mes = 1d;
        double valorAportesInvestidos = 0d;
        do {
            valorAportesInvestidos += valorAporte * pow(1 + indiceAplicacaoMes, mes);
            mes++;
        } while (mes <= qtdeAportes);
        return valorRendimentoInvestimentoInicial + valorAportesInvestidos;
    }


    private double calcularValorTotalInvestido() {
        double val = 0;
        for (int i = 1; i <= qtdeAportes; i++) {
            val += valorAporte;
        }
        return val;
    }

    private double calcularValorReal() {
        return valorAporte * calcularIndiceAcumulado(qtdeAportes, indiceRealMes);
    }

    private void inicializarIndices(final Investimento investimento) {
        indiceAplicacao = investimento.aliquotaAplicacao / 100;
        indiceIR = investimento.aliquotaIR / 100;
        indiceInflacao = investimento.aliquotaInflacao / 100;
        qtdeAportes = investimento.qtdeAportes;
        valorAporte = investimento.valorAporte;
        valorSaque = investimento.valorSaque;
        valorInicial = investimento.valorInicial;
    }
}

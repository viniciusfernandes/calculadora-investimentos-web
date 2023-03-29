package br.com.calculadorainvestimentos.algoritmo;

import br.com.calculadorainvestimentos.model.FluxoInvestimento;
import br.com.calculadorainvestimentos.model.Investimento;
import br.com.calculadorainvestimentos.model.PeriodoSaque;
import br.com.calculadorainvestimentos.model.ProjecaoInvestimento;
import br.com.calculadorainvestimentos.model.ProjecaoSaque;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.Math.pow;

public class CalculadoraInvestimento {

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
    private double valorTotalRendimento;

    private double valorInvestidoDepreciado;
    private double valorPrimeiroSaque;
    private double valorReal;
    private double valorIR;

    private double valorRestante;

    private double valorSaque;
    private double valorUltimoSaque;
    private boolean limiteCalculoExcedido;
    public static final int LIMITE_CALCULO_SAQUES = 800;

    public FluxoInvestimento calcular(final Investimento investimento) {
        inicializarIndices(investimento);
        calcularIndicesMensaisEValores();
        calcularQtdeMaxSaquesEValorRestante();

        final ProjecaoInvestimento projInvest = new ProjecaoInvestimento();
        projInvest.aliquotaGanhoFinal = arredondar(indiceGanhoFinal * 100);
        projInvest.aliquotaGanhoReal = arredondar(indiceGanhoReal * 100);
        projInvest.aliquotaInflacaoAcumulada = arredondar(indiceInflacaoAcumulada * 100);
        projInvest.aliquotaInflacaoAcumuladaMes = arredondar(indiceInflacaoAcumuladaMes * 100);
        projInvest.aliquotaInflacaoMes = arredondar(indiceInflacaoMes * 100);
        projInvest.qtdeAportes = qtdeAportes;
        projInvest.valorFinal = arredondar(valorFinalAplicado);
        projInvest.valorInvestido = arredondar(valorTotalInvestido);
        projInvest.valorInvestidoDepreciado = arredondar(valorInvestidoDepreciado);
        projInvest.valorIR = arredondar(valorIR);
        projInvest.valorReal = arredondar(valorReal);
        projInvest.aliquotaAplicacaoMes = arredondar(indiceAplicacaoMes * 100);
        projInvest.aliquotaReal = arredondar(indiceRealMes * 100);

        final ProjecaoSaque projSaque = new ProjecaoSaque();
        projSaque.qtdeMaxSaques = qtdeMaxSaques;
        projSaque.valorPrimeiroSaque = arredondar(valorPrimeiroSaque);
        projSaque.valorRestante = arredondar(valorRestante);
        projSaque.valorUltimoSaque = arredondar(valorUltimoSaque);
        projSaque.periodoSaque = new PeriodoSaque(qtdeMaxSaques);
        projSaque.limiteCalculoExcedido = limiteCalculoExcedido;
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
        if (indice <= indiceInflacao) {
            return 0;
        }
        return (1 + indice) / (1 + indiceInflacao) - 1;
    }

    private void calcularIndicesMensaisEValores() {
        indiceAplicacaoMes = calcularIndiceMensalEquivalente(indiceAplicacao);
        indiceInflacaoMes = calcularIndiceMensalEquivalente(indiceInflacao);
        indiceRealMes = calcularIndiceReal(indiceAplicacaoMes, indiceInflacaoMes);

        var valoresTotais = calcularValorTotalRendimento(valorAporte, valorInicial, indiceAplicacaoMes, qtdeAportes);
        valorFinalAplicado = valoresTotais.valorTotalAplicacao;
        valorTotalInvestido = valoresTotais.valorTotalInvestido;
        valorTotalRendimento = valoresTotais.valorTotalRendimento;

        valorReal = calcularValorReal();
        valorInvestidoDepreciado = valorAporte * calcularIndiceAcumulado(qtdeAportes, -indiceInflacaoMes);

        indiceGanhoFinal = valorTotalRendimento / valorTotalInvestido;
        indiceGanhoReal = (valorReal - valorTotalInvestido) / valorTotalInvestido;

        if (valorInvestidoDepreciado != 0d) {
            indiceInflacaoAcumulada = (valorTotalInvestido - valorInvestidoDepreciado) / valorInvestidoDepreciado;
        } else {
            indiceInflacaoAcumulada = 0;
        }
        indiceInflacaoAcumuladaMes = calcularIndiceMensalEquivalente(indiceInflacaoAcumulada, qtdeAportes);
        valorPrimeiroSaque = valorSaque * pow(1 + indiceInflacaoMes, qtdeAportes);
    }

    public ValoresTotaisInvestimento calcularValorTotalRendimento(double valorAporte, double valorInicial, double indiceAplicacaoMes,
                                                                  int qtdeAportes) {
        var valoresTotais = new ValoresTotaisInvestimento();
        valoresTotais.valorTotalAplicacao = calcularValorTotalAplicacao(valorAporte, valorInicial, indiceAplicacaoMes, qtdeAportes);
        valoresTotais.valorTotalInvestido = calcularValorTotalInvestido(qtdeAportes, valorAporte, valorInicial);
        valoresTotais.valorTotalRendimento = valoresTotais.valorTotalAplicacao - valoresTotais.valorTotalInvestido;
        return valoresTotais;
    }

    public SimulacaoSaques calcularQuantidadeMaxSaques(double valorTotalInvestido, double valorTotalRendimento,
                                                       double valorSaque, double indiceInflacaoMes,
                                                       double indiceRendimentoMes, double indiceIR) {

        if (valorSaque <= 0 || valorTotalInvestido <= 0 ||
            valorTotalInvestido < 0 || indiceInflacaoMes < 0 ||
            indiceRendimentoMes < 0 || indiceIR < 0) {
            return new SimulacaoSaques(0, 0, 0, 0, false);
        }
        return calcularQuantidadeMaxSaques(valorTotalInvestido, valorTotalRendimento, valorSaque,
            indiceInflacaoMes, indiceRendimentoMes, indiceIR, 0);
    }

    private double valorTotalDisponivel;

    private SimulacaoSaques calcularQuantidadeMaxSaques(double valorTotalInvestido, double valorTotalRendimento,
                                                        double valorSaque, double indiceInflacao,
                                                        double indiceRendimento, double indiceIR, int numSaque) {
        valorTotalDisponivel = valorTotalInvestido + valorTotalRendimento;
        if (valorTotalDisponivel < valorSaque || numSaque >= LIMITE_CALCULO_SAQUES) {
            return new SimulacaoSaques(numSaque, valorIR, valorTotalDisponivel, valorSaque,
                numSaque >= LIMITE_CALCULO_SAQUES);
        }

        if (valorSaque <= valorTotalRendimento) {
            valorTotalRendimento -= valorSaque;
            valorIR += valorSaque * indiceIR;
        } else if (valorSaque > valorTotalRendimento && valorTotalRendimento > 0) {
            valorIR += valorTotalRendimento * indiceIR;
            valorTotalInvestido -= (valorSaque - valorTotalRendimento);
            valorTotalRendimento = 0;
        } else {
            valorTotalInvestido -= valorSaque;
        }

        numSaque++;
        // Reajustando o valor do saque para refletir os efeitos da inflacao na sil=mulacao
        valorSaque *= (1 + indiceInflacao);
        // Aplicando a taxa de rendimento novamente pois esses valores permanecerao investidos
        valorTotalRendimento = valorTotalRendimento * (1 + indiceRendimento) + valorTotalInvestido * indiceRendimento;
        return calcularQuantidadeMaxSaques(valorTotalInvestido, valorTotalRendimento, valorSaque, indiceInflacao,
            indiceRendimento, indiceIR, numSaque);
    }

    private void calcularQtdeMaxSaquesEValorRestante() {
        var simulacao = calcularQuantidadeMaxSaques(valorTotalInvestido, valorTotalRendimento,
            valorSaque, indiceInflacaoMes, indiceAplicacaoMes, indiceIR);
        qtdeMaxSaques = simulacao.quantidadeMaxSaques;
        valorUltimoSaque = simulacao.valorUltimoSaque;
        valorRestante = simulacao.valorRestante;
        valorIR = simulacao.valorIR;
        limiteCalculoExcedido = simulacao.limiteCalculoExcedido;
    }

    public double calcularValorRendimentoInvestimentoInicial(double valorInicial, double indiceAplicacaoMes,
                                                             int qtdeAportes) {
        return valorInicial * pow(1 + indiceAplicacaoMes, qtdeAportes);
    }


    public double calcularValorTotalAplicacao(double valorAporte, double valorInicial, double indiceAplicacaoMes,
                                              int qtdeAportes) {
        double valorRendimentoInvestimentoInicial = calcularValorRendimentoInvestimentoInicial(
            valorInicial, indiceAplicacaoMes, qtdeAportes);
        double mes = 1d;
        double valorAportesInvestidos = 0d;
        while (mes <= qtdeAportes) {
            valorAportesInvestidos += valorAporte * pow(1 + indiceAplicacaoMes, mes);
            mes++;
        }
        return valorRendimentoInvestimentoInicial + valorAportesInvestidos;
    }


    public double calcularValorTotalInvestido(int qtdeAportes, double valorAporte, double valorInicial) {
        return valorAporte * qtdeAportes + valorInicial;
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

    private double arredondar(double valor) {
        var bd = BigDecimal.valueOf(valor);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

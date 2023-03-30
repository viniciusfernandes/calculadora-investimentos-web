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

    private double indiceInflacaoMes;
    private double indiceIR;

    private double indiceRealMes;

    private int qtdeAportes;
    private int qtdeMaxSaques;
    private double valorAporte;
    private double valorTotalAplicacao;
    private double valorInicial;
    private double valorTotalInvestido;
    private double valorTotalRendimento;

    private double valorPrimeiroSaque;
    private double valorTotalReal;
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
        projInvest.aliquotaInflacaoMes = arredondar(indiceInflacaoMes * 100);
        projInvest.qtdeAportes = qtdeAportes;
        projInvest.valorFinal = arredondar(valorTotalAplicacao);
        projInvest.valorInvestido = arredondar(valorTotalInvestido);
        projInvest.valorIR = arredondar(valorIR);
        projInvest.valorReal = arredondar(valorTotalReal);
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
        final double indiceLucro = calcularIndiceRelativo(valorFinalAplicado, valorTotalInvestido);
        return calcularIndiceMensalEquivalente(indiceLucro, qtdeAportes);
    }

    public double calcularIndiceRelativo(double valorFinal, double valorInicial) {
        if (valorInicial == 0) {
            return 0;
        }
        return valorFinal / valorInicial - 1;
    }

    public double calcularIndiceMensalEquivalente(final double indiceAnual) {
        return calcularIndiceMensalEquivalente(indiceAnual, 12);
    }

    public double calcularIndiceReal(final double indiceAplicacao, final double indiceInflacao) {
        return (1 + indiceAplicacao) / (1 + indiceInflacao) - 1;
    }

    private void calcularIndicesMensaisEValores() {
        indiceAplicacaoMes = calcularIndiceMensalEquivalente(indiceAplicacao);
        indiceInflacaoMes = calcularIndiceMensalEquivalente(indiceInflacao);
        indiceRealMes = calcularIndiceReal(indiceAplicacaoMes, indiceInflacaoMes);

        var valoresTotais = calcularValoresTotaisInvestimento(valorAporte, valorInicial,
            indiceAplicacaoMes, indiceInflacaoMes, qtdeAportes);

        valorTotalAplicacao = valoresTotais.valorTotalAplicacao;
        valorTotalInvestido = valoresTotais.valorTotalInvestido;
        valorTotalRendimento = valoresTotais.valorTotalRendimento;
        valorTotalReal = valoresTotais.valorTotalReal;


        indiceGanhoFinal = calcularIndiceRelativo(valorTotalAplicacao, valorTotalInvestido);
        indiceGanhoReal = calcularIndiceRelativo(valorTotalReal, valorTotalInvestido);
        valorPrimeiroSaque = valorSaque * pow(1 + indiceInflacaoMes, qtdeAportes);
    }

    public ValoresTotaisInvestimento calcularValoresTotaisInvestimento(double valorAporte, double valorInicial, double indiceAplicacaoMes,
                                                                       double indiceInflacaoMes, int qtdeAportes) {
        var valoresTotais = new ValoresTotaisInvestimento();
        valoresTotais.valorTotalAplicacao = calcularValorTotalAplicacao(valorAporte, valorInicial, indiceAplicacaoMes, qtdeAportes);
        valoresTotais.valorTotalInvestido = calcularValorTotalInvestido(qtdeAportes, valorAporte, valorInicial);
        valoresTotais.valorTotalInflacao = calcularValorTotalAplicacao(valorAporte, valorInicial, indiceInflacaoMes, qtdeAportes);
        var indiceReal = calcularIndiceReal(indiceAplicacaoMes, indiceInflacaoMes);
        valoresTotais.valorTotalReal = calcularValorTotalAplicacao(valorAporte, valorInicial, indiceReal, qtdeAportes);
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

        double valorTotalDisponivel = 0;
        double valorIR = 0;
        int numSaque = 0;
        while (true) {
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
            valorSaque *= (1 + indiceInflacaoMes);
            // Aplicando a taxa de rendimento novamente pois esses valores permanecerao investidos
            valorTotalRendimento = valorTotalRendimento * (1 + indiceRendimentoMes) + valorTotalInvestido * indiceRendimentoMes;
        }
    }


    private void calcularQtdeMaxSaquesEValorRestante() {
        var simulacao = calcularQuantidadeMaxSaques(valorTotalInvestido, valorTotalRendimento,
            valorPrimeiroSaque, indiceInflacaoMes, indiceAplicacaoMes, indiceIR);
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

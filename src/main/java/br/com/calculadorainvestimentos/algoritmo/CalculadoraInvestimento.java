package br.com.calculadorainvestimentos.algoritmo;

import br.com.calculadorainvestimentos.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.Math.pow;

public class CalculadoraInvestimento {
    public static final int LIMITE_CALCULO_SAQUES = 1000;

    public FluxoInvestimento calcular(final Investimento investimento) {
        var indices = calculadarIndicesMes(investimento);

        var valoresTotais = calcularValoresTotaisInvestimento(
                investimento.valorAporte, investimento.valorInicial,
                indices.indiceAplicacaoMes, indices.indiceInflacaoMes,
                investimento.qtdeAportes);

        var indiceGanhoFinal = calcularIndiceRelativo(valoresTotais.valorTotalAplicacao, valoresTotais.valorTotalInvestido);
        var indiceGanhoReal = calcularIndiceRelativo(valoresTotais.valorTotalReal, valoresTotais.valorTotalInvestido);
        // Levando o valor do primeiro saque para o futuro
        var valorPrimeiroSaque = calcularValorJurosCompostos(investimento.valorSaque, indices.indiceInflacaoMes, investimento.qtdeAportes);

        var valoresMaximos = calcularQuantidadeMaxSaques(
                valoresTotais.valorTotalInvestido,
                valoresTotais.valorTotalAplicacao,
                valorPrimeiroSaque,
                indices.indiceInflacaoMes,
                indices.indiceAplicacaoMes,
                indices.indiceIR);

        final ProjecaoInvestimento projInvest = new ProjecaoInvestimento();
        projInvest.aliquotaGanhoFinal = arredondar(indiceGanhoFinal * 100);
        projInvest.aliquotaGanhoReal = arredondar(indiceGanhoReal * 100);
        projInvest.aliquotaInflacaoMes = arredondar(indices.indiceInflacaoMes * 100);
        projInvest.qtdeAportes = investimento.qtdeAportes;
        projInvest.valorFinal = arredondar(valoresTotais.valorTotalAplicacao);
        projInvest.valorInvestido = arredondar(valoresTotais.valorTotalInvestido);
        projInvest.valorIR = arredondar(valoresMaximos.valorIR);
        projInvest.valorReal = arredondar(valoresTotais.valorTotalReal);
        projInvest.aliquotaAplicacaoMes = arredondar(indices.indiceAplicacaoMes * 100);
        projInvest.aliquotaReal = arredondar(indices.indiceRealMes * 100);

        final ProjecaoSaque projSaque = new ProjecaoSaque();
        projSaque.qtdeMaxSaques = valoresMaximos.quantidadeMaxSaques;
        projSaque.valorPrimeiroSaque = arredondar(valorPrimeiroSaque);
        projSaque.valorRestante = arredondar(valoresMaximos.valorRestante);
        projSaque.valorUltimoSaque = arredondar(valoresMaximos.valorUltimoSaque);
        projSaque.periodoSaque = new PeriodoSaque(valoresMaximos.quantidadeMaxSaques);
        projSaque.limiteCalculoExcedido = valoresMaximos.limiteCalculoExcedido;
        return new FluxoInvestimento(projInvest, projSaque);
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

    public CalculoValoresMaximos calcularQuantidadeMaxSaques(final double valorTotalInvestido, final double valorTotalAplicacao,
                                                             final double valorSaque, final double indiceInflacaoMes,
                                                             final double indiceRendimentoMes, final double indiceIR) {


        if (valorSaque < 0 || indiceInflacaoMes < 0 || indiceRendimentoMes < 0 || indiceIR < 0) {
            return new CalculoValoresMaximos(0, 0, 0, 0, false);
        }

        double valorTotalIR = 0;
        double valorIR = 0;
        double valorUltimoSaque = valorSaque;
        double valorDisponivelSaque = valorTotalAplicacao;
        double rendimento = valorDisponivelSaque - valorTotalInvestido;
        int numSaque = 0;
        while (true) {
            if (valorUltimoSaque > valorDisponivelSaque || numSaque >= LIMITE_CALCULO_SAQUES) {
                return new CalculoValoresMaximos(numSaque, valorTotalIR, valorDisponivelSaque, valorUltimoSaque,
                        numSaque >= LIMITE_CALCULO_SAQUES);
            }
            if (rendimento > 0) {
                if (valorUltimoSaque <= rendimento) {
                    valorIR = valorUltimoSaque * indiceIR;
                } else {
                    valorIR = rendimento * indiceIR;
                }
            }
            valorTotalIR += valorIR;
            valorDisponivelSaque -= (valorUltimoSaque + valorIR);
            // Aplicando a taxa de rendimento novamente pois esses valores permanecerao investidos
            valorDisponivelSaque *= (1 + indiceRendimentoMes);
            // Reajustando o valor do saque para refletir os efeitos da inflacao na sil=mulacao
            valorUltimoSaque *= (1 + indiceInflacaoMes);

            rendimento = valorDisponivelSaque - valorTotalInvestido;
            numSaque++;
            valorIR = 0;
        }
    }

    public double calcularValorJurosCompostos(double valor, double indiceAplicacaoMes, int qtdeAportes) {
        return valor * pow(1 + indiceAplicacaoMes, qtdeAportes);
    }


    public double calcularValorTotalAplicacao(double valorAporte, double valorInicial, double indiceAplicacaoMes,
                                              int qtdeAportes) {
        double valorRendimentoInvestimentoInicial = calcularValorJurosCompostos(
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

    private IndicesInvestimento calculadarIndicesMes(final Investimento investimento) {
        var indices = new IndicesInvestimento();
        indices.indiceAplicacaoMes = calcularIndiceMensalEquivalente(investimento.aliquotaAplicacao / 100);
        indices.indiceIR = investimento.aliquotaIR / 100;
        indices.indiceInflacaoMes = calcularIndiceMensalEquivalente(investimento.aliquotaInflacao / 100);
        indices.indiceRealMes = calcularIndiceReal(indices.indiceAplicacaoMes, indices.indiceInflacaoMes);
        return indices;
    }

    private double arredondar(double valor) {
        var bd = BigDecimal.valueOf(valor);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


}

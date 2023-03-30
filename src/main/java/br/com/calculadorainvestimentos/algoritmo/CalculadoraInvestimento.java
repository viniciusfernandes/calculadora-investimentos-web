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
    public static final int LIMITE_CALCULO_SAQUES = 800;

    public FluxoInvestimento calcular(final Investimento investimento) {
        var indices = calculadarIndicesMes(investimento);

        var valoresTotais = calcularValoresTotaisInvestimento(
            investimento.valorAporte, investimento.valorInicial,
            indices.indiceAplicacaoMes, indices.indiceInflacaoMes,
            investimento.qtdeAportes);

        var indiceGanhoFinal = calcularIndiceRelativo(valoresTotais.valorTotalAplicacao, valoresTotais.valorTotalInvestido);
        var indiceGanhoReal = calcularIndiceRelativo(valoresTotais.valorTotalReal, valoresTotais.valorTotalInvestido);
        var valorPrimeiroSaque = calcularValorJurosCompostos(investimento.valorSaque, indices.indiceInflacaoMes, investimento.qtdeAportes);

        var valoresMaximos = calcularQuantidadeMaxSaques(
            valoresTotais.valorTotalInvestido,
            valoresTotais.valorTotalRendimento,
            valorPrimeiroSaque, indices.indiceInflacaoMes,
            indices.indiceAplicacaoMes, indices.indiceIR);


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

    public CalculoValoresMaximos calcularQuantidadeMaxSaques(double valorTotalInvestido, double valorTotalRendimento,
                                                             double valorSaque, double indiceInflacaoMes,
                                                             double indiceRendimentoMes, double indiceIR) {


        if (valorSaque <= 0 || valorTotalInvestido <= 0 ||
            valorTotalInvestido < 0 || indiceInflacaoMes < 0 ||
            indiceRendimentoMes < 0 || indiceIR < 0) {
            return new CalculoValoresMaximos(0, 0, 0, 0, false);
        }

        double valorTotalDisponivel = 0;
        double valorIR = 0;
        int numSaque = 0;
        while (true) {
            valorTotalDisponivel = valorTotalInvestido + valorTotalRendimento;
            if (valorTotalDisponivel < valorSaque || numSaque >= LIMITE_CALCULO_SAQUES) {
                return new CalculoValoresMaximos(numSaque, valorIR, valorTotalDisponivel, valorSaque,
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

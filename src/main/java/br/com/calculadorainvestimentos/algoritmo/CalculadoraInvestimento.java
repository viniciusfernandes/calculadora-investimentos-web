package br.com.calculadorainvestimentos.algoritmo;

import static java.lang.Math.pow;

import br.com.calculadorainvestimentos.model.FluxoInvestimento;
import br.com.calculadorainvestimentos.model.Investimento;
import br.com.calculadorainvestimentos.model.PeriodoSaque;
import br.com.calculadorainvestimentos.model.ProjecaoInvestimento;
import br.com.calculadorainvestimentos.model.ProjecaoSaque;

public class CalculadoraInvestimento {

    private double indiceAplicacao;
    private double indiceInflacao;

    private double indiceIR;
    private double indiceReaplicacao;
    private int qtdeAportes;
    private double valorAporte;
    private double valorSaque;
    public static final int QTDE_MAX_SAQUES = 12 * 100;

    private double indiceAplicacaoMes;
    private double indiceReaplicacaoMes;

    private double indiceInflacaoMes;
    private double indiceReal;

    private double valorInicial;
    private double valorFinal;
    private double valorInvestido;
    private double valorReal;
    private double valorInvestidoDepreciado;
    private double valorPrimeiroSaque;
    private double valorUltimoSaque;

    private double indiceGanhoFinal;
    private double indiceGanhoReal;
    private double indiceInflacaoAcumulada;

    private double indiceInflacaoAcumuladaMes;

    private int qtdeMaxSaques;
    private double valorRestante;

    private void inicializarIndices(final Investimento investimento) {
        indiceAplicacao = investimento.getAliquotaAplicacao() / 100;
        indiceReaplicacao = investimento.getAliquotaReaplicacao() / 100;
        indiceIR = investimento.getAliquotaIR() / 100;
        indiceInflacao = investimento.getAliquotaInflacao() / 100;
        qtdeAportes = investimento.getQtdeAportes();
        valorAporte = investimento.getValorAporte();
        valorSaque = investimento.getValorSaque();
        valorInicial = investimento.getValorInicial();
    }

    private void calcularIndicesMensaisEValores() {
        indiceAplicacaoMes = calcularIndiceMensal(indiceAplicacao);
        indiceInflacaoMes = calcularIndiceMensal(indiceInflacao);
        indiceReal = calcularIndiceReal(indiceAplicacaoMes, indiceInflacaoMes);
        indiceReaplicacaoMes = calcularIndiceMensal(indiceReaplicacao);

        valorFinal = calcularValorFinal();
        valorInvestido = calcularValorInvestido();
        valorReal = valorAporte * calcularIndiceAcumulado(qtdeAportes, indiceReal);
        valorInvestidoDepreciado = valorAporte * calcularIndiceAcumulado(qtdeAportes, -indiceInflacaoMes);

        indiceGanhoFinal = (valorFinal - valorInvestido) / valorInvestido;
        indiceGanhoReal = (valorReal - valorInvestido) / valorInvestido;

        if (valorInvestidoDepreciado != 0d) {
            indiceInflacaoAcumulada = (valorInvestido - valorInvestidoDepreciado) / valorInvestidoDepreciado;
        } else {
            indiceInflacaoAcumulada = 0;
        }
        indiceInflacaoAcumuladaMes = calcularIndiceEquivalente(indiceInflacaoAcumulada, qtdeAportes);
        valorPrimeiroSaque = valorSaque * pow(1 + indiceInflacaoMes, qtdeAportes);
    }

    public FluxoInvestimento calcular(final Investimento investimento) {
        inicializarIndices(investimento);
        calcularIndicesMensaisEValores();
        calcularQtdeMaxSaquesEValorRestante();

        final ProjecaoInvestimento projInvest = new ProjecaoInvestimento();
        projInvest.setAliquotaGanhoFinal(indiceGanhoFinal * 100);
        projInvest.setAliquotaGanhoReal(indiceGanhoReal * 100);
        projInvest.setAliquotaInflacaoAcumulada(indiceInflacaoAcumulada * 100);
        projInvest.setAliquotaInflacaoAcumuladaMes(indiceInflacaoAcumuladaMes * 100);
        projInvest.setAliquotaInflacaoMes(indiceInflacaoMes * 100);
        projInvest.setQtdeAportes(qtdeAportes);
        projInvest.setValorFinal(valorFinal);
        projInvest.setValorInvestido(valorInvestido);
        projInvest.setValorInvestidoDepreciado(valorInvestidoDepreciado);
        projInvest.setValorReal(valorReal);
        projInvest.setAliquotaAplicacaoMes(indiceAplicacaoMes * 100);
        projInvest.setAliquotaReal(indiceReal * 100);

        final ProjecaoSaque projSaque = new ProjecaoSaque();
        projSaque.setQtdeMaxSaques(qtdeMaxSaques);
        projSaque.setValorPrimeiroSaque(valorPrimeiroSaque);
        projSaque.setValorRestante(valorRestante);
        projSaque.setValorUltimoSaque(valorUltimoSaque);
        projSaque.setAliquotaReaplicacaoMes(indiceReaplicacaoMes * 100);
        projSaque.setPeriodoSaque(new PeriodoSaque(qtdeMaxSaques));

        return new FluxoInvestimento(projInvest, projSaque);
    }

    private double calcularIndiceAcumulado(final int quantidade, final double idxRendimento) {
        return calcularIndiceAcumulado(quantidade, idxRendimento, 0);
    }

    private double calcularIndiceAcumulado(final int quantidade, final double idxRendimento, final double idxInflacao) {
        double idxAcumulado = 0;

        for (int i = 0; i < quantidade; i++) {
            idxAcumulado += pow(1 + idxRendimento, i) * pow(1 + idxRendimento, quantidade - i);
        }

        return idxAcumulado;
    }

    private double calcularIndiceMensal(final double indice) {
        return calcularIndiceEquivalente(indice, 12);
    }

    private double calcularIndiceEquivalente(final double indice, final int periodo) {
        return Math.pow(1 + indice, 1d / periodo) - 1;
    }

    private double calcularIndiceReal(final double indiceRendimento, final double indiceInflacao) {
        return (1 + indiceRendimento) / (1 + indiceInflacao) - 1;
    }

    private double calcularValorInvestido() {
        double val = 0;
        for (int i = 1; i <= qtdeAportes; i++) {
            val += valorAporte;

        }
        return val;
    }

    private void calcularQtdeMaxSaquesEValorRestante() {
        final int idxSaqueInicial = qtdeAportes;

        final double valorSaqueFuturo = valorSaque * pow((1 + indiceInflacaoMes), idxSaqueInicial);
        final double indiceLucro = calcularIndiceLucroMedio();

        calcularQtdeMaxSaques(valorFinal, valorSaqueFuturo, indiceInflacaoMes, indiceReaplicacaoMes, indiceLucro, 0);
        valorUltimoSaque = valorSaque * pow(1 + indiceInflacaoMes, qtdeAportes + qtdeMaxSaques - 1d);
    }

    private void calcularQtdeMaxSaques(double valorFinal, double valorSaque, final double indiceInflacao, final double indiceReaplicacao,
                    final double indiceLucro, int numSaque) {
        valorRestante = valorFinal;
        qtdeMaxSaques = ++numSaque;
        // Subtranido o valor do IR incidente no saque.
        valorFinal -= (valorSaque + valorSaque * indiceLucro * indiceIR);

        if (valorFinal < 0) {
            qtdeMaxSaques = --numSaque;
            return;
        } else if (valorFinal == 0 || numSaque >= QTDE_MAX_SAQUES) {
            return;
        }

        valorSaque *= (1 + indiceInflacao);
        valorFinal *= (1 + indiceReaplicacao);

        calcularQtdeMaxSaques(valorFinal, valorSaque, indiceInflacao, indiceReaplicacao, indiceLucro, numSaque);
    }

    private double calcularIndiceLucroMedio() {
        final double indiceLucro = (valorFinal / valorInvestido - 1);
        return calcularIndiceEquivalente(indiceLucro, qtdeAportes);
    }

    private double calcularValorFinal() {
        final double idxAplicMes = calcularIndiceMensal(indiceAplicacao);
        return valorInicial * pow(1 + idxAplicMes, qtdeAportes - 1d) + valorAporte * calcularIndiceAcumulado(qtdeAportes, idxAplicMes);
    }
}

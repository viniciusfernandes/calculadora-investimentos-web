package br.com.calculadorainvestimentos.algoritmo;

import static java.lang.Math.pow;

import br.com.calculadorainvestimentos.model.FluxoInvestimento;
import br.com.calculadorainvestimentos.model.Investimento;
import br.com.calculadorainvestimentos.model.PeriodoSaque;
import br.com.calculadorainvestimentos.model.ProjecaoInvestimento;
import br.com.calculadorainvestimentos.model.ProjecaoSaque;

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

	private double indiceReal;
	private double indiceReaplicacao;

	private double indiceReaplicacaoMes;
	private int qtdeAportes;
	private int qtdeMaxSaques;
	private double valorAporte;
	private double valorFinal;
	private double valorInicial;
	private double valorInvestido;

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
		projInvest.setAliquotaGanhoFinal(indiceGanhoFinal * 100);
		projInvest.setAliquotaGanhoReal(indiceGanhoReal * 100);
		projInvest.setAliquotaInflacaoAcumulada(indiceInflacaoAcumulada * 100);
		projInvest.setAliquotaInflacaoAcumuladaMes(indiceInflacaoAcumuladaMes * 100);
		projInvest.setAliquotaInflacaoMes(indiceInflacaoMes * 100);
		projInvest.setQtdeAportes(qtdeAportes);
		projInvest.setValorFinal(valorFinal);
		projInvest.setValorInvestido(valorInvestido);
		projInvest.setValorInvestidoDepreciado(valorInvestidoDepreciado);
		projInvest.setValorIR(valorIR);
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
			idxAcumulado += pow(1 + idxInflacao, i) * pow(1 + idxRendimento, quantidade - i);
		}

		return idxAcumulado;
	}

	private double calcularIndiceEquivalente(final double indice, final int periodo) {
		return Math.pow(1 + indice, 1d / periodo) - 1;
	}

	private double calcularIndiceLucroMedio() {
		final double indiceLucro = (valorFinal / valorInvestido - 1);
		return calcularIndiceEquivalente(indiceLucro, qtdeAportes);
	}

	private double calcularIndiceMensal(final double indice) {
		return calcularIndiceEquivalente(indice, 12);
	}

	private double calcularIndiceReal(final double indiceRendimento, final double indiceInflacao) {
		return (1 + indiceRendimento) / (1 + indiceInflacao) - 1;
	}

	private void calcularIndicesMensaisEValores() {
		indiceAplicacaoMes = calcularIndiceMensal(indiceAplicacao);
		indiceInflacaoMes = calcularIndiceMensal(indiceInflacao);
		indiceReal = calcularIndiceReal(indiceAplicacaoMes, indiceInflacaoMes);
		indiceReaplicacaoMes = calcularIndiceMensal(indiceReaplicacao);

		valorFinal = calcularValorFinal();
		valorInvestido = calcularValorInvestido();
		valorReal = calcularValorReal();
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

	private void calcularQtdeMaxSaques(double valorFinal, double valorSaque, double valorImpRenda,
			final double indiceInflacao, final double indiceReaplicacao, final double indiceLucro, int numSaque) {
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
		valorFinal *= (1 + indiceReaplicacao);

		calcularQtdeMaxSaques(valorFinal, valorSaque, valorImpRenda, indiceInflacao, indiceReaplicacao, indiceLucro,
				numSaque);
	}

	private void calcularQtdeMaxSaquesEValorRestante() {
		final int idxSaqueInicial = qtdeAportes;

		final double valorSaqueFuturo = valorSaque * pow((1 + indiceInflacaoMes), idxSaqueInicial);
		final double indiceLucro = calcularIndiceLucroMedio();

		calcularQtdeMaxSaques(valorFinal, valorSaqueFuturo, 0, indiceInflacaoMes, indiceReaplicacaoMes, indiceLucro, 0);
		valorUltimoSaque = valorPrimeiroSaque * pow(1 + indiceInflacaoMes, qtdeMaxSaques - 1d);
	}

	private double calcularValorFinal() {
		final double idxAplicMes = calcularIndiceMensal(indiceAplicacao);
		return valorInicial * pow(1 + idxAplicMes, qtdeAportes - 1d)
				+ valorAporte * calcularIndiceAcumulado(qtdeAportes, idxAplicMes);
	}

	private double calcularValorInvestido() {
		double val = 0;
		for (int i = 1; i <= qtdeAportes; i++) {
			val += valorAporte;

		}
		return val;
	}

	private double calcularValorReal() {
		return valorAporte * calcularIndiceAcumulado(qtdeAportes, indiceReal);
	}

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
}

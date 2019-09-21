package br.com.calculadorainvestimentos.model;

import java.io.Serializable;

public class Investimento implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8135938062344365852L;
	private double aliquotaAplicacao;
	private double aliquotaInflacao;
	private double aliquotaIR;
	private double aliquotaReaplicacao;

	private int qtdeAportes;
	private double valorAporte;
	private double valorInicial;
	private double valorSaque;

	public Investimento() {
	}

	public double getAliquotaAplicacao() {
		return aliquotaAplicacao;
	}

	public double getAliquotaInflacao() {
		return aliquotaInflacao;
	}

	public double getAliquotaIR() {
		return aliquotaIR;
	}

	public double getAliquotaReaplicacao() {
		return aliquotaReaplicacao;
	}

	public int getQtdeAportes() {
		return qtdeAportes;
	}

	public double getValorAporte() {
		return valorAporte;
	}

	public double getValorInicial() {
		return valorInicial;
	}

	public double getValorSaque() {
		return valorSaque;
	}

	public void setAliquotaAplicacao(final double aliquotaAplicacao) {
		this.aliquotaAplicacao = aliquotaAplicacao;
	}

	public void setAliquotaInflacao(final double aliquotaInflacao) {
		this.aliquotaInflacao = aliquotaInflacao;
	}

	public void setAliquotaIR(final double aliquotaIR) {
		this.aliquotaIR = aliquotaIR;
	}

	public void setAliquotaReaplicacao(final double aliquotaReaplicacao) {
		this.aliquotaReaplicacao = aliquotaReaplicacao;
	}

	public void setQtdeAportes(final int qtdeAportes) {
		this.qtdeAportes = qtdeAportes;
	}

	public void setValorAporte(final double valorAporte) {
		this.valorAporte = valorAporte;
	}

	public void setValorInicial(double valorInicial) {
		this.valorInicial = valorInicial;
	}

	public void setValorSaque(final double valorSaque) {
		this.valorSaque = valorSaque;
	}

}

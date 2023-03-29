package br.com.calculadorainvestimentos.model;

import java.io.Serializable;

public class Investimento implements Serializable {
    private static final long serialVersionUID = -8135938062344365852L;
    public double aliquotaAplicacao;
    public double aliquotaInflacao;
    public double aliquotaIR;
    public int qtdeAportes;
    public double valorAporte;
    public double valorInicial;
    public double valorSaque;
}

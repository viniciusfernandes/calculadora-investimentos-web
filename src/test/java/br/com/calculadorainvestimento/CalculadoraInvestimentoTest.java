package br.com.calculadorainvestimento;

import br.com.calculadorainvestimentos.algoritmo.CalculadoraInvestimento;
import br.com.calculadorainvestimentos.model.Investimento;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CalculadoraInvestimentoTest {
    private final CalculadoraInvestimento calculadora = new CalculadoraInvestimento();
    private final double delta = 0.00001;

    @Test
    public void testCalculoIndiceEquivalente() {
        assertValue(0.00000, calculadora.calcularIndiceMensalEquivalente(0.05, 0));
        assertValue(0.050000, calculadora.calcularIndiceMensalEquivalente(0.05, 1));
        assertValue(0.024695, calculadora.calcularIndiceMensalEquivalente(0.05, 2));
        assertValue(0.016396, calculadora.calcularIndiceMensalEquivalente(0.05, 3));
        assertValue(0.012272, calculadora.calcularIndiceMensalEquivalente(0.05, 4));
    }

    @Test
    public void testCalculoIndiceReal() {
        assertValue(0.05000, calculadora.calcularIndiceReal(0.05, 0));
        assertValue(0.00000, calculadora.calcularIndiceReal(0.05, 0.05));
        assertValue(0.02941, calculadora.calcularIndiceReal(0.05, 0.02));
        assertValue(0.01941, calculadora.calcularIndiceReal(0.05, 0.03));
        assertValue(0.00000, calculadora.calcularIndiceReal(0.05, 0.3));
        assertValue(0.00000, calculadora.calcularIndiceReal(0.05, 0.05));
    }

    @Test
    public void testCalculoIndiceMensal() {
        assertValue(5000, calculadora.calcularValorRendimentoInvestimentoInicial(5000, 0, 12));
        assertValue(5000, calculadora.calcularValorRendimentoInvestimentoInicial(5000, 0.02, 0));
        assertValue(0, calculadora.calcularValorRendimentoInvestimentoInicial(0, 0.02, 0));
        assertValue(6094.97210, calculadora.calcularValorRendimentoInvestimentoInicial(5000, 0.02, 10));
        assertValue(328.38885, calculadora.calcularValorRendimentoInvestimentoInicial(50, 0.019, 100));
    }

    @Test
    public void testCalculoValorFinalAplicacao() {
        assertValue(0, calculadora.calcularValorTotalAplicacao(0, 0, 0.02, 10));
        assertValue(6094.97210, calculadora.calcularValorTotalAplicacao(0, 5000, 0.02, 10));
        assertValue(5000, calculadora.calcularValorTotalAplicacao(0, 5000, 0, 10));
        assertValue(51000, calculadora.calcularValorTotalAplicacao(4000, 11000, 0, 10));
        assertValue(15300, calculadora.calcularValorTotalAplicacao(4000, 11000, 0.02, 1));
        assertValue(24159.72, calculadora.calcularValorTotalAplicacao(4000, 11000, 0.02, 3));
    }

    @Test
    public void testCalculoIndiceRendimentoMedio() {
        assertValue(0.00000, calculadora.calcularIndiceRendimentoMedio(0, 28000, 120));
        assertValue(0.00000, calculadora.calcularIndiceRendimentoMedio(0, 0, 120));
        assertValue(0.00000, calculadora.calcularIndiceRendimentoMedio(33000, 0, 120));
        assertValue(0.00000, calculadora.calcularIndiceRendimentoMedio(33000, 28000, 0));
        assertValue(0.00137, calculadora.calcularIndiceRendimentoMedio(33000, 28000, 120));
        assertValue(0.01333, calculadora.calcularIndiceRendimentoMedio(1200000, 50000, 240));
    }

    @Test
    public void testCalculoQuantidadeMaximaSaques() {
        var simulacao = calculadora.calcularQuantidadeMaxSaques(0, 100, 400, 0.045, 0.01, 0.15);
        assertEquals("A quantidade maxima de saques esta errada", 0, simulacao.quantidadeMaxSaques);

        simulacao = calculadora.calcularQuantidadeMaxSaques(500, 100, 0, 0.045, 0.01, 0.15);
        assertEquals("A quantidade maxima de saques esta errada", 0, simulacao.quantidadeMaxSaques);

        simulacao = calculadora.calcularQuantidadeMaxSaques(500, 100, 400, -1, 0.01, 0.15);
        assertEquals("A quantidade maxima de saques esta errada", 0, simulacao.quantidadeMaxSaques);

        simulacao = calculadora.calcularQuantidadeMaxSaques(500, 100, 0, 0.045, -1, 0.15);
        assertEquals("A quantidade maxima de saques esta errada", 0, simulacao.quantidadeMaxSaques);

        simulacao = calculadora.calcularQuantidadeMaxSaques(500, 100, 0, 0.045, 0.01, -1);
        assertEquals("A quantidade maxima de saques esta errada", 0, simulacao.quantidadeMaxSaques);

        simulacao = calculadora.calcularQuantidadeMaxSaques(500, 100, 400, 0.045, 0.01, 0.15);
        assertEquals("A quantidade maxima de saques esta errada", 1, simulacao.quantidadeMaxSaques);

        simulacao = calculadora.calcularQuantidadeMaxSaques(500, 100, 700, 0.045, 0.01, 0.15);
        assertEquals("A quantidade maxima de saques esta errada", 0, simulacao.quantidadeMaxSaques);

        simulacao = calculadora.calcularQuantidadeMaxSaques(500, 100, 300, 0.045, 0.01, 0.15);
        assertEquals("A quantidade maxima de saques esta errada", 1, simulacao.quantidadeMaxSaques);

        simulacao = calculadora.calcularQuantidadeMaxSaques(500, 100, 250, 0.045, 0.01, 0.15);
        assertEquals("A quantidade maxima de saques esta errada", 2, simulacao.quantidadeMaxSaques);
    }

    @Test
    public void testCalculoValorTotalInvestido() {
        assertValue(2200, calculadora.calcularValorTotalInvestido(12, 100, 1000));
        assertValue(1000, calculadora.calcularValorTotalInvestido(0, 100, 1000));
        assertValue(1000, calculadora.calcularValorTotalInvestido(12, 0, 1000));
        assertValue(1200, calculadora.calcularValorTotalInvestido(12, 100, 0));
        assertValue(0, calculadora.calcularValorTotalInvestido(0, 0, 0));
    }

    @Test
    public void testCalculoValorTotalRendimento() {
        var valoresTotais = calculadora.calcularValorTotalRendimento(4000, 11000, 0.02, 3);
        assertValue(1159.72000, valoresTotais.valorTotalRendimento);

        valoresTotais = calculadora.calcularValorTotalRendimento(0, 11000, 0.02, 3);
        assertValue(673.28800, valoresTotais.valorTotalRendimento);

        valoresTotais = calculadora.calcularValorTotalRendimento(4000, 11000, 0.02, 0);
        assertValue(0, valoresTotais.valorTotalRendimento);

        valoresTotais = calculadora.calcularValorTotalRendimento(4000, 11000, 0, 3);
        assertValue(0, valoresTotais.valorTotalRendimento);

        valoresTotais = calculadora.calcularValorTotalRendimento(0, 11000, 0, 3);
        assertValue(0, valoresTotais.valorTotalRendimento);

        valoresTotais = calculadora.calcularValorTotalRendimento(0, 0, 0, 3);
        assertValue(0, valoresTotais.valorTotalRendimento);

        valoresTotais = calculadora.calcularValorTotalRendimento(0, 0, 0, 0);
        assertValue(0, valoresTotais.valorTotalRendimento);
    }

    private Investimento buildInvestimento() {
        var investimento = new Investimento();
        investimento.aliquotaAplicacao = 12;
        investimento.aliquotaIR = 10;
        investimento.aliquotaInflacao = 5;
        investimento.qtdeAportes = 4;
        investimento.valorAporte = 4000;
        investimento.valorSaque = 3500;
        investimento.valorInicial = 50000;
        return investimento;
    }

    private void assertValue(double expected, double actual) {
        assertEquals("O valor nao está dentro do intervalo de aceitacao", expected, actual, delta);
    }
}

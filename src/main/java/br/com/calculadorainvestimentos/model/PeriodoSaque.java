package br.com.calculadorainvestimentos.model;

public class PeriodoSaque {
    private int qtdeMaxSaque;
    private int anos;

    private int meses;

    public PeriodoSaque() {

    }

    public PeriodoSaque(final int qtdeMaxSaques) {
        qtdeMaxSaque = qtdeMaxSaques;
        calcularPeriodo();
    }

    public int getAnos() {
        return anos;
    }

    public int getMeses() {
        return meses;
    }

    public void setAnos(final int anos) {
        this.anos = anos;
    }

    public int getQtdeMaxSaque() {
        return qtdeMaxSaque;
    }

    public void setQtdeMaxSaque(final int qtdeMaxSaque) {
        this.qtdeMaxSaque = qtdeMaxSaque;
        calcularPeriodo();
    }

    public void calcularPeriodo() {
        anos = qtdeMaxSaque / 12;
        meses = qtdeMaxSaque % 12;
    }
}

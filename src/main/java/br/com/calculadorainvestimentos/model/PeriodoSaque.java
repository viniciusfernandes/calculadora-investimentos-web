package br.com.calculadorainvestimentos.model;

public class PeriodoSaque {
    private int anos;
    private int meses;

    private int qtdeMaxSaque;

    public PeriodoSaque() {

    }

    public PeriodoSaque(final int qtdeMaxSaques) {
        qtdeMaxSaque = qtdeMaxSaques;
        calcularPeriodo();
    }

    public void calcularPeriodo() {
        anos = qtdeMaxSaque / 12;
        meses = qtdeMaxSaque % 12;
    }

    public int getAnos() {
        return anos;
    }

    public int getMeses() {
        return meses;
    }

    public int getQtdeMaxSaque() {
        return qtdeMaxSaque;
    }

    public void setAnos(final int anos) {
        this.anos = anos;
    }

    public void setQtdeMaxSaque(final int qtdeMaxSaque) {
        this.qtdeMaxSaque = qtdeMaxSaque;
        calcularPeriodo();
    }
}

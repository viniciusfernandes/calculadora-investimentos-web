package br.com.calculadorainvestimentos;

import static java.lang.System.out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.Scanner;

import br.com.calculadorainvestimentos.algoritmo.CalculadoraInvestimento;
import br.com.calculadorainvestimentos.model.FluxoInvestimento;
import br.com.calculadorainvestimentos.model.Investimento;
import br.com.calculadorainvestimentos.model.ProjecaoInvestimento;
import br.com.calculadorainvestimentos.model.ProjecaoSaque;

public class Main {
    private static final String MARGEM = "---------------------------------------";
    private static final String MARGEM_DUPLA = MARGEM + MARGEM;
    private static final String ESPACO = "\n+\n+\n+\n+\n+\n+\n+\n+\n+\n+\n";
    private static final DecimalFormat DF = new DecimalFormat("#.###");
    private static final String pathDir = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private static final File dir = new File(pathDir).getParentFile();
    private static final File dadosInvestimento = new File(dir.getAbsolutePath() + File.separator + "investimento.txt");
    private static boolean hasFile = false;
    private static final Scanner scanner = new Scanner(System.in);
    private static boolean isPrimeiraExecucao = true;
    private static Properties props = null;

    public static void main(final String[] args) {
        gerarArquivoInvestimento();

        System.out.println(MARGEM_DUPLA);
        System.out.println("Preencha o arquivo: investimento.txt");
        System.out.println(MARGEM_DUPLA);

        String comando = "";
        boolean leituraOk = true;
        while (true) {
            if (leituraOk) {
                printInstrucaoInicial();
            }

            comando = scanner.nextLine();
            if (!"s".equalsIgnoreCase(comando)) {
                scanner.close();
                System.out.println("\n" + MARGEM);
                System.out.println("FIM");
                System.out.println(MARGEM);
                return;
            }
            try {
                gerarFluxoInvestimento();
                leituraOk = true;
            } catch (final Exception e) {
                System.out.println(ESPACO);
                System.out.println("HOUVE UMA FALHA NA LEITURA DO ARQUIVO DE INVESTIMENTO.");
                leituraOk = false;
                dadosInvestimento.delete();
                gerarArquivoInvestimento();

                System.out.println("O arquivo foi gerado no mesmo diretorio. Preencha os valores novamente.");
                printInstrucaoInicial();
            }

        }
    }

    private static void print(final ProjecaoInvestimento projecaoInvest) {
        out.println("\n\nInvestimentos\n" + MARGEM);
        out.println("Tempo Invest.  : " + formatarTempo(projecaoInvest.getQtdeAportes()));

        out.println("Val. Investido : " + NumberFormat.getCurrencyInstance().format(projecaoInvest.getValorInvestido()));
        out.println("Val. Depreci.  : " + NumberFormat.getCurrencyInstance().format(projecaoInvest.getValorInvestidoDepreciado()));
        out.println("Val. Final     : " + NumberFormat.getCurrencyInstance().format(projecaoInvest.getValorFinal()));
        out.println("Val. Real      : " + NumberFormat.getCurrencyInstance().format(projecaoInvest.getValorReal()));

        out.println("Rend. Mensal   : " + formatarAliquota(projecaoInvest.getAliquotaAplicacaoMes()));
        out.println("Rend. Real     : " + formatarAliquota(projecaoInvest.getAliquotaReal()));

        out.println("Infl. Mes      : " + formatarAliquota(projecaoInvest.getAliquotaInflacaoMes()));
        out.println("Infl. Acum.    : " + formatarAliquota(projecaoInvest.getAliquotaInflacaoAcumulada()));
        out.println("Infl. Acum. Mes: " + formatarAliquota(projecaoInvest.getAliquotaInflacaoAcumuladaMes()));

        out.println("Rend. Final    : " + formatarAliquota(projecaoInvest.getAliquotaGanhoFinal()));
        out.println("Rend. Real     : " + formatarAliquota(projecaoInvest.getAliquotaGanhoReal()));
    }

    private static void print(final ProjecaoSaque projSaque) {

        out.println("\n\nSaques\n" + MARGEM);
        out.println("Primeiro Saque : " + NumberFormat.getCurrencyInstance().format(projSaque.getValorPrimeiroSaque()));
        out.println("Ultimo Saque   : " + NumberFormat.getCurrencyInstance().format(projSaque.getValorUltimoSaque()));
        out.println("Reaplic. Mes   : " + formatarAliquota(projSaque.getAliquotaReaplicacaoMes()));
        out.println("Val. Restante  : " + NumberFormat.getCurrencyInstance().format(projSaque.getValorRestante()));

        out.println("Qtde. Max. Saq.: "
            + (projSaque.getQtdeMaxSaques() >= CalculadoraInvestimento.QTDE_MAX_SAQUES ? "SEM LIMITES" : projSaque.getQtdeMaxSaques()));
        out.println("Tempo Max. Saq.: " + (projSaque.getQtdeMaxSaques() >= CalculadoraInvestimento.QTDE_MAX_SAQUES ? "SEM LIMITES"
                        : formatarTempo(projSaque.getQtdeMaxSaques())));
        out.println(MARGEM);
    }

    private static void print(final Investimento invest) {
        out.println(MARGEM + "\nValores Iniciais\n" + MARGEM);
        out.println("Valor Inicial  : " + NumberFormat.getCurrencyInstance().format(invest.getValorInicial()));
        out.println("Aporte Mensal  : " + NumberFormat.getCurrencyInstance().format(invest.getValorAporte()));
        out.println("Saque Mensal   : " + NumberFormat.getCurrencyInstance().format(invest.getValorSaque()));
        out.println("Qtde Aportes   : " + invest.getQtdeAportes());
        out.println("Aliq. Aplic.   : " + formatarAliquota(invest.getAliquotaAplicacao()));
        out.println("Aliq. Reaplic. : " + formatarAliquota(invest.getAliquotaReaplicacao()));
        out.println(MARGEM);
    }

    private static String formatarAliquota(final double aliquota) {
        return (DF.format(aliquota) + "%").replace(".", ",");
    }

    private static String formatarTempo(final int qtdeMeses) {
        return qtdeMeses / 12 + " anos e " + qtdeMeses % 12 + " meses";
    }

    private static void gerarArquivoInvestimento() {
        for (final File arq : dir.listFiles()) {
            if (hasFile = arq.getName().equals("investimento.txt")) {
                break;
            }
        }
        if (!hasFile) {
            try {
                final StringBuilder descricao = new StringBuilder();

                descricao.append(MARGEM_DUPLA).append("\n");
                descricao.append("# O QUE EH O SISTEMA?\r\n");
                descricao.append(MARGEM_DUPLA).append("\n");
                descricao.append("# O objetivo principal eh simular um cenario simplificado de investimentos para\r\n");
                descricao.append("# se ter um fluxo de saques para a aposentadoria privada.\r\n");
                descricao.append(
                                "# A modelagem financeira pode ser exemplificada como a seguir: o investidor possui um valor inicial de R$100.000,00,\r\n");
                descricao.append(
                                "# e ele pretende aplicar esse montante com um rendimento anual de 8%. Novos aportes mensais de R$4.000,00 serao efetuados\r\n");
                descricao.append("# mensalmente com a mesma taxa de rendimento. Isso sera feito por 20 anos (240 aportes),\r\n");
                descricao.append(
                                "# e ao termino desse periodo, a quantia final sera direcionada para um investimento mais conservador que rende 5% ao ano.\r\n");
                descricao.append(
                                "# A partir desse momento, o investidor efetuara saques no valor de R$3.000,00 mensalmente. Alem disso, .\r\n");
                descricao.append("# o Imposto de Renda incidira sobre o lucro de cada saque numa aliquota media de 10%.\r\n");
                descricao.append("# Lembrando que em cada saque, eh efetuada a correcao da inflacao anual media de 4.5%.\r\n");
                descricao.append(MARGEM_DUPLA).append("\n");

                descricao.append(MARGEM_DUPLA).append("\n");
                descricao.append("# DESCRICAO DOS CAMPOS DO ARQUIVO\r\n");
                descricao.append(MARGEM_DUPLA).append("\n");

                descricao.append(
                                "# aliquotaAplicacao => Eh a aliquota anual da aplicacao que se espera fazer no inicio dos investimentos\r\n");
                descricao.append(
                                "# aliquotaReaplicacao => Eh a aliquota anual da aplicacao que se espera fazer no inicio dos investimentos\r\n");
                descricao.append("# aliquotaIR => Eh a aliquota media de Imposto de Renda sobre os saques efetuados.\r\n");
                descricao.append("# aliquotaInflacao => Eh a aliquota media da inflacao anual.\r\n");
                descricao.append(
                                "# qtdeAportes => Eh o numero de aportes que se deseja efetuar ate o inicio dos saques (inicio da aposentadoria).\r\n");
                descricao.append("# valorInicial => Eh o valor do montante que se possui no momento do primeiro aporte.\r\n");
                descricao.append("# valorAporte => Eh o valor do aporte mensal que se deseja fazer.\r\n");
                descricao.append("# valorSaque => Eh o valor do saque mensal que se deseja fazer durante toda a aposentadoria.\r\n");

                descricao.append(MARGEM_DUPLA).append("\n");
                descricao.append("# PREENCHA OS VALORES ABAIXO USANDO APENAS \".\" PARA SEPARAR AS CASAS DECIMAIS.\r\n");
                descricao.append("# CASO QUEIRA SIMULAR NOVAMENTE, ALTERE OS VALOS DO ARQUIVO E DIGITE \"s\".\r\n");
                descricao.append("# NAO EH NECESSARIO FECHAR O PROGRAMA.\r\n");
                descricao.append(MARGEM_DUPLA).append("\n\n");

                descricao.append("aliquotaAplicacao=10.0\r\n");
                descricao.append("aliquotaReaplicacao=6.0\r\n");
                descricao.append("aliquotaIR=12.0\r\n");
                descricao.append("aliquotaInflacao=4.5\r\n");
                descricao.append("qtdeAportes=240\r\n");
                descricao.append("valorInicial=100000\r\n");
                descricao.append("valorAporte=3000.0\r\n");
                descricao.append("valorSaque=4000.0\r\n");

                final BufferedWriter writer = new BufferedWriter(new FileWriter(dadosInvestimento));
                writer.write(descricao.toString());
                writer.close();
            } catch (final IOException e) {
                System.out.println(MARGEM);
                System.out.println(
                                "Falha na leitura/geracao do arquivo de investimentos. Delete o arquivo do diretorio para que seja gerado automaticamente.");
            }
        }
    }

    private static Investimento gerarInvetimento() throws Exception {
        props = new Properties();
        FileInputStream file;
        file = new FileInputStream(dadosInvestimento);
        props.load(file);
        file.close();

        final Investimento investimento = new Investimento();
        investimento.setAliquotaAplicacao(parse("aliquotaAplicacao"));
        investimento.setAliquotaInflacao(parse("aliquotaInflacao"));
        investimento.setAliquotaIR(parse("aliquotaIR"));
        investimento.setAliquotaReaplicacao(parse("aliquotaReaplicacao"));
        investimento.setQtdeAportes(parse("qtdeAportes").intValue());
        investimento.setValorAporte(parse("valorAporte"));
        investimento.setValorInicial(parse("valorInicial"));
        investimento.setValorSaque(parse("valorSaque"));

        return investimento;

    }

    private static void gerarFluxoInvestimento() throws Exception {
        final Investimento investimento = gerarInvetimento();
        final FluxoInvestimento fluxo = new CalculadoraInvestimento().calcular(investimento);

        if (!isPrimeiraExecucao) {
            System.out.println(ESPACO);
        }

        isPrimeiraExecucao = false;

        print(investimento);
        print(fluxo.getProjecaoInvestimento());
        print(fluxo.getProjecaoSaque());

    }

    private static Double parse(final String property) {
        return Double.parseDouble(props.getProperty(property));
    }

    public static void printInstrucaoInicial() {
        System.out.print("\nDigite \"s\" para gerar o fluxo de investimentos ou qualquer letra para sair: ");
    }
}

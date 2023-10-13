function calcularIndiceRendimentoMedio(valorFinalAplicado, valorTotalInvestido, qtdeAportes) {
    if (valorFinalAplicado <= 0 || valorTotalInvestido <= 0) {
        return 0;
    }
    var indiceLucro = calcularIndiceRelativo(valorFinalAplicado, valorTotalInvestido);
    return calcularIndiceMensalEquivalente(indiceLucro, qtdeAportes);
}
function calcularIndiceRelativo(valorFinal, valorInicial) {
    if (valorInicial == 0) {
        return 0;
    }
    return valorFinal / valorInicial - 1;
}
function calcularIndiceAnualEquivalente(indiceAnual) {
    return calcularIndiceMensalEquivalente(indiceAnual, 12);
}
function calcularIndiceMensalEquivalente(indicePeriodo, periodo) {
    if (periodo <= 0) {
        return 0;
    }
    return Math.pow(1 + indicePeriodo, 1 / periodo) - 1;
}
function calcularIndiceReal(indiceAplicacao, indiceInflacao) {
    return (1 + indiceAplicacao) / (1 + indiceInflacao) - 1;
}
function calcularValoresTotaisInvestimento(valorAporte, valorInicial, indiceAplicacaoMes, indiceInflacaoMes, qtdeAportes) {
    var valoresTotais = {
        valorTotalAplicacao: calcularValorTotalAplicacao(valorAporte, valorInicial, indiceAplicacaoMes, qtdeAportes),
        valorTotalInvestido: calcularValorTotalInvestido(qtdeAportes, valorAporte, valorInicial),
        valorTotalInflacao: calcularValorTotalAplicacao(valorAporte, valorInicial, indiceInflacaoMes, qtdeAportes),
        valorTotalReal: 0,
        valorTotalRendimento: 0, // This will be calculated below
    };
    var indiceReal = calcularIndiceReal(indiceAplicacaoMes, indiceInflacaoMes);
    valoresTotais.valorTotalReal = calcularValorTotalAplicacao(valorAporte, valorInicial, indiceReal, qtdeAportes);
    valoresTotais.valorTotalRendimento = valoresTotais.valorTotalAplicacao - valoresTotais.valorTotalInvestido;
    return valoresTotais;
}
function calcularQuantidadeMaxSaques(valorTotalInvestido, valorTotalAplicacao, valorSaque, indiceInflacaoMes, indiceRendimentoMes, indiceIR) {
    if (valorSaque < 0 || indiceInflacaoMes < 0 || indiceRendimentoMes < 0 || indiceIR < 0) {
        return {
            quantidadeMaxSaques: 0,
            valorIR: 0,
            valorRestante: 0,
            valorUltimoSaque: 0,
            limiteCalculoExcedido: false,
        };
    }
    var LIMITE_CALCULO_SAQUES = 1000;
    var valorTotalIR = 0;
    var valorIR = 0;
    var valorUltimoSaque = valorSaque;
    var valorDisponivelSaque = valorTotalAplicacao;
    var rendimento = valorDisponivelSaque - valorTotalInvestido;
    var numSaque = 0;
    while (true) {
        if (valorUltimoSaque > valorDisponivelSaque || numSaque >= LIMITE_CALCULO_SAQUES) {
            return {
                quantidadeMaxSaques: numSaque,
                valorIR: valorTotalIR,
                valorRestante: valorDisponivelSaque,
                valorUltimoSaque: valorUltimoSaque,
                limiteCalculoExcedido: numSaque >= LIMITE_CALCULO_SAQUES,
            };
        }
        if (rendimento > 0) {
            if (valorUltimoSaque <= rendimento) {
                valorIR = valorUltimoSaque * indiceIR;
            }
            else {
                valorIR = rendimento * indiceIR;
            }
        }
        valorTotalIR += valorIR;
        valorDisponivelSaque -= (valorUltimoSaque + valorIR);
        valorDisponivelSaque *= (1 + indiceRendimentoMes);
        valorUltimoSaque *= (1 + indiceInflacaoMes);
        rendimento = valorDisponivelSaque - valorTotalInvestido;
        numSaque++;
        valorIR = 0;
    }
}
function calcularValorJurosCompostos(valor, indiceAplicacaoMes, qtdeAportes) {
    return valor * Math.pow(1 + indiceAplicacaoMes, qtdeAportes);
}
function calcularValorTotalAplicacao(valorAporte, valorInicial, indiceAplicacaoMes, qtdeAportes) {
    var valorRendimentoInvestimentoInicial = calcularValorJurosCompostos(valorInicial, indiceAplicacaoMes, qtdeAportes);
    var mes = 1;
    var valorAportesInvestidos = 0;
    while (mes <= qtdeAportes) {
        valorAportesInvestidos += valorAporte * Math.pow(1 + indiceAplicacaoMes, mes);
        mes++;
    }
    return valorRendimentoInvestimentoInicial + valorAportesInvestidos;
}
function calcularValorTotalInvestido(qtdeAportes, valorAporte, valorInicial) {
    //alert('ap='+valorAporte+' qt='+qtdeAportes+' ini='+valorInicial+ ' tot='+  ((valorAporte * qtdeAportes )+ valorInicial))
    return parseFloat(valorAporte * qtdeAportes )+ parseFloat(valorInicial);
}
function calcularIndicesMes(investimento) {
    var indices = {
        indiceAplicacaoMes: calcularIndiceAnualEquivalente(investimento.aliquotaAplicacao / 100),
        indiceIR: investimento.aliquotaIR / 100,
        indiceInflacaoMes: calcularIndiceAnualEquivalente(investimento.aliquotaInflacao / 100),
        indiceRealMes: calcularIndiceReal(calcularIndiceAnualEquivalente(investimento.aliquotaAplicacao / 100), calcularIndiceAnualEquivalente(investimento.aliquotaInflacao / 100))
    };
    return indices;
}
function arredondar(valor) {
    //alert('linha12='+valor )
    var x =  parseFloat(valor.toFixed(2));
    //alert('linha12 depois' )
    return x;
}
function calcular(investimento) {
    var indices = calcularIndicesMes(investimento);
    //alert('indices'+JSON.stringify(indices))
    var valoresTotais = calcularValoresTotaisInvestimento(investimento.valorAporte, investimento.valorInicial, indices.indiceAplicacaoMes, indices.indiceInflacaoMes, investimento.qtdeAportes);
    //alert('valores totais'+JSON.stringify(valoresTotais))
    var indiceGanhoFinal = calcularIndiceRelativo(valoresTotais.valorTotalAplicacao, valoresTotais.valorTotalInvestido);
    var indiceGanhoReal = calcularIndiceRelativo(valoresTotais.valorTotalReal, valoresTotais.valorTotalInvestido);
    var valorPrimeiroSaque = calcularValorJurosCompostos(investimento.valorSaque, indices.indiceInflacaoMes, investimento.qtdeAportes);
    var valoresMaximos = calcularQuantidadeMaxSaques(valoresTotais.valorTotalInvestido, valoresTotais.valorTotalAplicacao, valorPrimeiroSaque, indices.indiceInflacaoMes, indices.indiceAplicacaoMes, indices.indiceIR);
    //alert('valores max'+JSON.stringify(valoresMaximos))
    var projInvest = {
        aliquotaGanhoFinal: arredondar(indiceGanhoFinal * 100),
        aliquotaGanhoReal: arredondar(indiceGanhoReal * 100),
        aliquotaInflacaoMes: arredondar(indices.indiceInflacaoMes * 100),
        qtdeAportes: investimento.qtdeAportes,
        valorFinal: arredondar(valoresTotais.valorTotalAplicacao),
        valorInvestido: arredondar(valoresTotais.valorTotalInvestido),
        valorIR: arredondar(valoresMaximos.valorIR),
        valorReal: arredondar(valoresTotais.valorTotalReal),
        aliquotaAplicacaoMes: arredondar(indices.indiceAplicacaoMes * 100),
        aliquotaReal: arredondar(indices.indiceRealMes * 100),
    };
    //alert('linha9'+JSON.stringify(valoresMaximos))

    var projSaque = {
        qtdeMaxSaques: valoresMaximos.quantidadeMaxSaques,
        valorPrimeiroSaque: arredondar(valorPrimeiroSaque),
        valorRestante: arredondar(valoresMaximos.valorRestante),
        valorUltimoSaque: arredondar(valoresMaximos.valorUltimoSaque),
        periodoSaque: {
            anos: Math.floor(valoresMaximos.quantidadeMaxSaques / 12),
            meses: valoresMaximos.quantidadeMaxSaques % 12,
        },
        limiteCalculoExcedido: valoresMaximos.limiteCalculoExcedido,
    };

    //alert('linha10'+JSON.stringify({ projecaoInvestimento: projInvest, projecaoSaque: projSaque }))

    return { projecaoInvestimento: projInvest, projecaoSaque: projSaque };
}

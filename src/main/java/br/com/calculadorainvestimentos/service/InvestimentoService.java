package br.com.calculadorainvestimentos.service;

import org.springframework.stereotype.Service;

import br.com.calculadorainvestimentos.algoritmo.CalculadoraInvestimento;
import br.com.calculadorainvestimentos.model.FluxoInvestimento;
import br.com.calculadorainvestimentos.model.Investimento;

@Service
public class InvestimentoService {

	public FluxoInvestimento calcularFluxoInvestimento(Investimento investimento) {
		return new CalculadoraInvestimento().calcular(investimento);
	}
	
}

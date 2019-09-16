package br.com.calculadorainvestimentos.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.calculadorainvestimentos.model.FluxoInvestimento;
import br.com.calculadorainvestimentos.model.Investimento;
import br.com.calculadorainvestimentos.service.InvestimentoService;

@RestController 
@RequestMapping("/investimentos")
public class InvestimentosRest {

	private InvestimentoService investimentoService;

	@Autowired
	public InvestimentosRest(InvestimentoService investimentoService) {
		this.investimentoService = investimentoService;
	}


	@PostMapping("/calculo")
	public ResponseEntity<FluxoInvestimento> calcularFluxoInvestimentos(@RequestBody Investimento investimento) {
		return ResponseEntity.ok(investimentoService.calcularFluxoInvestimento(investimento));
	} 
}

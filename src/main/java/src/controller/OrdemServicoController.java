package src.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import src.domain.exception.EntidadeNaoEncontradaException;
import src.domain.exception.NegocioException;
import src.domain.model.OrdemServico;
import src.domain.repository.OrdemServicoRepository;
import src.domain.service.GestaoOrdemServicoService;
import src.model.OrdemServicoInput;
import src.model.OrdemServicoModel;

@RestController
@RequestMapping("/ordem-servico")
public class OrdemServicoController {

	@Autowired
	private OrdemServicoRepository ordemServicoRepository;
	@Autowired
	private GestaoOrdemServicoService gestaoOrdemServicoService;
	@Autowired
	private ModelMapper modelMapper;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrdemServicoModel criar(@Valid @RequestBody OrdemServicoInput ordemServicoInput) {
		OrdemServico ordemServico = toEntity(ordemServicoInput);
		return toModel(gestaoOrdemServicoService.criar(ordemServico));
	}

	@GetMapping
	public List<OrdemServicoModel> listar() {
		return toCollectionModel(ordemServicoRepository.findAll());
	}

	@GetMapping("/{ordemServicoId}")
	public ResponseEntity<OrdemServicoModel> buscar(@PathVariable Long ordemServicoId) {
		Optional<OrdemServico> ordemServico = ordemServicoRepository.findById(ordemServicoId);

		if (ordemServico.isPresent()) {
			OrdemServicoModel ordemServicoModel = toModel(ordemServico.get());
			return ResponseEntity.ok(ordemServicoModel);
		}

		return ResponseEntity.notFound().build();
	}

	@PutMapping("/{ordemServicoId}/finalizacao")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void finalizar(@PathVariable Long ordemServicoId) {
		gestaoOrdemServicoService.finalizar(ordemServicoId);
	}

	@GetMapping("/{ordemServicoId}/finalizacao")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public OrdemServicoModel listarFinalizacao(@PathVariable Long ordemServicoId) {
		OrdemServico ordemServico = ordemServicoRepository.findById(ordemServicoId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException("Ordem de serviço não encontrada"));
		if(ordemServico.podeSerFinalizada()) {
			throw new NegocioException("Ordem de Serviço ainda não foi finalizada.");
		}
		return toModel(ordemServico);
	}

	private OrdemServicoModel toModel(OrdemServico ordemServico) {
		return modelMapper.map(ordemServico, OrdemServicoModel.class);
	}

	private List<OrdemServicoModel> toCollectionModel(List<OrdemServico> ordensServico) {
		return ordensServico.stream().map(ordemServico -> toModel(ordemServico)).collect(Collectors.toList());
	}

	private OrdemServico toEntity(OrdemServicoInput ordemServicoInput) {
		return modelMapper.map(ordemServicoInput, OrdemServico.class);
	}
}

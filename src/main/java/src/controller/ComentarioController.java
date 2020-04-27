package src.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import src.domain.exception.EntidadeNaoEncontradaException;
import src.domain.model.Comentario;
import src.domain.model.OrdemServico;
import src.domain.repository.OrdemServicoRepository;
import src.domain.service.GestaoOrdemServicoService;
import src.model.ComentarioInput;
import src.model.ComentarioModel;

@RestController
@RequestMapping("/ordem-servico/{ordemServicoId}/comentarios")
public class ComentarioController {

	@Autowired
	GestaoOrdemServicoService gestaoOrdemServico;
	@Autowired
	OrdemServicoRepository ordemServicoRepository;
	@Autowired
	ModelMapper modelMapper;

	@GetMapping
	public List<ComentarioModel> listar(@PathVariable Long ordemServicoId) {
		OrdemServico ordemServico = ordemServicoRepository.findById(ordemServicoId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException("Ordem de serviço não encontrada."));

		return toCollectionModel(ordemServico.getComentarios());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ComentarioModel adicionar(@PathVariable Long ordemServicoId,
			@RequestBody @Valid ComentarioInput comentarioInput) {
		Comentario comentario = gestaoOrdemServico.adicionarComentario(ordemServicoId, comentarioInput.getDescricao());
		return toModel(comentario);
	}

	private ComentarioModel toModel(Comentario comentario) {
		return modelMapper.map(comentario, ComentarioModel.class);
	}

	private List<ComentarioModel> toCollectionModel(List<Comentario> comentarios) {
		return comentarios.stream().map(comentario -> toModel(comentario)).collect(Collectors.toList());
	}
}

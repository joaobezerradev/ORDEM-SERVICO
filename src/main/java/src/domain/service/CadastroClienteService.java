package src.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import src.domain.exception.NegocioException;
import src.domain.model.Cliente;
import src.domain.repository.ClienteRepository;

@Service
public class CadastroClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	public Cliente salvar(Cliente cliente) {
		Cliente clienteExiste = clienteRepository.findByEmail(cliente.getEmail());
		if (clienteExiste!= null && !clienteExiste.equals(cliente)) {
			throw new NegocioException("Já existe um cliente cadastrado com este e-mail");
		}
		return clienteRepository.save(cliente);
	}

	public void excluir(Long clienteId) {
		clienteRepository.deleteById(clienteId);
	}

}

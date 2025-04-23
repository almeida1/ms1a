package com.fatec.sigvs.service;


import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fatec.sigvs.model.Cliente;
import com.fatec.sigvs.producer.ClienteProducer;

import jakarta.transaction.Transactional;

@Service
public class ClienteService implements IClienteServico {
	Logger logger = LogManager.getLogger(this.getClass());
	final ClienteRepository clienteRepository;
	final ClienteProducer clienteProducer;
	private IEnderecoService enderecoService;

	// Injeção de dependências pelo construtor
	public ClienteService(ClienteRepository clienteRepository, ClienteProducer clienteProducer,
			IEnderecoService enderecoService) {
		this.clienteRepository = clienteRepository;
		this.clienteProducer = clienteProducer;
		this.enderecoService = enderecoService;
	}

	@Transactional
	public ClienteResponse cadastrar(Cliente cliente) {
		try {
			// Verifica se o cliente já existe com base no CPF
			if (clienteRepository.findByCpf(cliente.getCpf()).isPresent()) {
				logger.info(">>>>>> clienteservico - cliente já cadastrado");
				return new ClienteResponse(false, "Cliente já cadastrado.", null);
			}

			Optional<String> endereco = enderecoService.obtemLogradouroPorCep(cliente.getCep());
			if (endereco.isEmpty()) {
				logger.info(">>>>>> Endereço não encontrado para o CEP");
				return new ClienteResponse(false, "Endereço não encontrado.", null);
			} else {
				cliente.setDataCadastro();
				cliente.setEndereco(endereco.get());
				Cliente novoCliente = clienteRepository.save(cliente);
				logger.info(">>>>>> clienteservico - cliente salvo com sucesso no repositório");
				clienteProducer.publishMessageEmail(cliente);
				logger.info(">>>>>> clienteservico - mensagem enviada");
				return new ClienteResponse(true, "Cliente cadastrado com sucesso.", novoCliente);
			}
		} catch (Exception e) {
			logger.info(">>>>>> clienteservico - erro nao esperado metodo cadastrar " + e.getMessage());
			return new ClienteResponse(false, "Erro não esperado ao cadastrar cliente." + e.getMessage(), null);
		}
	}

	@Override
	public List<Cliente> consultaTodos() {
		return clienteRepository.findAll();
	}

	@Override
	public ClienteResponse consultarPorCpf(String cpf) {
		Optional<Cliente> c = clienteRepository.findByCpf(cpf);
		if (c.isPresent()) {
			return new ClienteResponse(true, null, c.get());
		} else {
			return new ClienteResponse(true, "Cliente não cadastrado", c.get());
		}
	}

	@Override
	public ClienteResponse atualizar(String cpf, Cliente cliente) {
		return new ClienteResponse(false, "Nao implementado", null);
	}

	@Override
	public ClienteResponse excluir(String cpf) {
		Optional<Cliente> c = clienteRepository.findByCpf(cpf);
		if (c.isEmpty()) {
			return new ClienteResponse(false, "Cliente não cadastrado", null);
		} else {
			clienteRepository.deleteByCpf(cpf);
			return new ClienteResponse(true, "Cliente excluido", null);
		}
	}

	@Override
	public Double estoqueImobilizado() {
		return null;
	}

}
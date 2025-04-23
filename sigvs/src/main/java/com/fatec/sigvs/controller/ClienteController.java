package com.fatec.sigvs.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.sigvs.model.Cliente;
import com.fatec.sigvs.model.ClienteRecordDTO;
import com.fatec.sigvs.service.ClienteResponse;
import com.fatec.sigvs.service.IClienteServico;

import jakarta.validation.Valid;

@CrossOrigin("*") // desabilita o cors do spring security
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
	Logger logger = LogManager.getLogger(this.getClass());
	final IClienteServico clienteService;
	
	public ClienteController(IClienteServico servico) {
		this.clienteService = servico;
	}
   /*
    * obj - manupula requisicoes post http
    */
	@PostMapping
	public ResponseEntity<Object> saveCliente(@RequestBody @Valid ClienteRecordDTO cliente) {
		logger.info(">>>>>> apicontroller cadastro de cliente iniciado...");
		Cliente novoCliente = new Cliente();
		novoCliente.setCpf(cliente.cpf());
		novoCliente.setNome(cliente.nome());
		novoCliente.setCep(cliente.cep());
		novoCliente.setEmail(cliente.email());
		ClienteResponse c = clienteService.cadastrar(novoCliente);
		if (!c.isSucesso()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(c.getMensagem());
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body(c.getCliente());
		}

	}
	@GetMapping("/all")
	public List<Cliente> getAll() {
		logger.info(">>>>>> apicontroller consulta todos iniciado...");
		return clienteService.consultaTodos();
	}
}
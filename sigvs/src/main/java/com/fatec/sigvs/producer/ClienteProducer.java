package com.fatec.sigvs.producer;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fatec.sigvs.model.Cliente;
import com.fatec.sigvs.model.EmailDto;

@Component
public class ClienteProducer {
	Logger logger = LogManager.getLogger(this.getClass());
	final RabbitTemplate rabbitTemplate;
	public ClienteProducer (RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}
	@Value("${broker.queue.email.name}")
	private String routingKey;
	
	public void publishMessageEmail(Cliente cliente) {
		logger.info(">>>>> clienteProducer publishMessageEmail iniciado...");
		try {
		var emailDto = new EmailDto(cliente.getId(),cliente.getEmail(),"Confirmação de cadastro",cliente.getNome() + ", \n\n Agradecemos seu cadastro. \n\n SIGVS Administrador");
		rabbitTemplate.convertAndSend("", routingKey,emailDto);
		logger.info(">>>>> clienteProducer publish -> publish msg de novo usuario cadastrado");
		}catch(Exception e) {
			logger.info(">>>>> cliente producer erro no envio do email => " + e.getMessage());
			throw new IllegalArgumentException("Erro na conexao com o broker");
		}
	}

}
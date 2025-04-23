package com.fatec.sigvs.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRecordDTO(String cpf, @NotBlank String nome, String cep, @NotBlank @Email String email) {

}

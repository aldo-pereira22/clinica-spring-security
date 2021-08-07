package com.mballem.curso.securitys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.securitys.repository.MedicoRepository;

@Service
public class MedicoService {

	@Autowired
	private MedicoRepository repository;
	
	
	@Transactional
	public Medico buscarPorUsuarioId(Long id) {
		return repository.findById(id).orElse(new Medico());
		
				
	}
}

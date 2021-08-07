package com.mballem.curso.security.web.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.UsuarioService;
import com.mballem.curso.securitys.service.MedicoService;

@Controller
@RequestMapping("u")
public class UsuarioController {

	@Autowired
	private UsuarioService service;
	
	@Autowired
	private MedicoService medicoService;

//	Abrir cadastro de usuários (medico/admin/paciente)
	@GetMapping("/novo/cadastro/usuario")
	public String cadastroPorAdminParaAdminMedicoPaciente(Usuario usuario) {
		return "usuario/cadastro";
	}

//	Abrir lista de usuários de usuários
	@GetMapping("/lista")
	public String listarUsuarios() {

		return "usuario/lista";
	}

// Listar usuários na datatables
	@GetMapping("/datatables/server/usuarios")
	public ResponseEntity<?> listarUsuariosDataTables(HttpServletRequest request) {

		return ResponseEntity.ok(service.buscarTodos(request));
	}

	// Salvar cadastro de usuários por administrador
	@PostMapping("/cadastro/salvar")
	public String salvarUsuarios(Usuario usuario, RedirectAttributes attr) {
		List<Perfil> perfis = usuario.getPerfis();

		if (perfis.size() > 2 || perfis.containsAll(Arrays.asList(new Perfil(1L), new Perfil(3L)))
				|| perfis.containsAll(Arrays.asList(new Perfil(2L), new Perfil(3L)))) {

			attr.addFlashAttribute("falha", "Paciente não pode ser Admin e/ou Médico");
			attr.addFlashAttribute("usuario", usuario);

		} else {
			try {
				service.salvarUsuario(usuario);
				attr.addFlashAttribute("sucesso", "Operação realizada com sucesso");

			} catch (DataIntegrityViolationException ex) {
				attr.addFlashAttribute("falha", "Cadastro não realizado, email ja e");
			}
		}
		return "redirect:/u/novo/cadastro/usuario";
	}

	// Pre-edicção de credenciais de usuários
	@GetMapping("/editar/credenciais/usuario/{id}")
	public ModelAndView editarCredenciais(@PathVariable("id") Long id) {

		return new ModelAndView("usuario/cadastro", "usuario", service.buscarPorId(id));
	}

	@GetMapping("/editar/dados/usuario/{id}/perfis/{perfis}")
	public ModelAndView preEditarCadastroDadosPessoais(@PathVariable("id") Long usuarioId,
			@PathVariable("perfis") Long[] perfisId) {

		Usuario us = service.buscarPorIdEPerfis(usuarioId, perfisId);

		if (us.getPerfis().contains(new Perfil(PerfilTipo.ADMIN.getCod()))
				&& !us.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
			
		
			return new ModelAndView("usuario/cadastro", "usuario", us);
		
		} else if (us.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
			Medico medico = medicoService.buscarPorUsuarioId(usuarioId);
			
			return new ModelAndView("especialidade/especialidade");
		} else if (us.getPerfis().contains(new Perfil(PerfilTipo.PACIENTE.getCod()))) {

			ModelAndView model = new ModelAndView("error");
			model.addObject("status", 403);
			model.addObject("error", "Area Restrita");
			model.addObject("message", "Os dados do paciente são restritos a ele.");
			return model;

		}

		return new ModelAndView("redirect:/u/lista");

	}

}

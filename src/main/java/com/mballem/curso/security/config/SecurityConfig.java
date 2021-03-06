package com.mballem.curso.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.service.UsuarioService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
		
	private static final String ADMIN = PerfilTipo.ADMIN.getDesc();
	
	private static final String MEDICO = PerfilTipo.MEDICO.getDesc();
	
	private static final String PACIENTE = PerfilTipo.PACIENTE.getDesc();

	
	@Autowired
	private UsuarioService service;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
			// acessos públicos liberados
		
			.antMatchers("/webjars/**", "/css/**", "/image/**", "/js/**").permitAll()
			.antMatchers("/", "/home").permitAll()
			
			
//			acessos privados para admin
			.antMatchers("/u/**").hasAuthority(ADMIN)
			
//			acessos privados medicos
			.antMatchers("/medicos/dados", "/medicos/salvar", "/medicos/editar").hasAnyAuthority(MEDICO, ADMIN)
			.antMatchers("/medicos/**").hasAuthority(MEDICO)
			
//			acessos privados paciente
			.antMatchers("/pacientes/**").hasAuthority(PACIENTE)

			
			//			Acesso privado especialidades
			.antMatchers("/especialidades/datatables/server/medico/*").hasAnyAuthority( MEDICO, ADMIN)
			.antMatchers("/especialidades/titulo").hasAnyAuthority(ADMIN, MEDICO)
			.antMatchers("/especialidades/**").hasAnyAuthority(ADMIN)
			
			
			.anyRequest().authenticated()
			.and()
				.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/", true)
				.failureUrl("/login-error")
				.permitAll()
			.and()
				.logout()
				.logoutSuccessUrl("/")
			.and()
				.exceptionHandling()
				.accessDeniedPage("/acesso-negado")
				;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	

}

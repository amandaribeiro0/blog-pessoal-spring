package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
		usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", "-"));
	}

	@Test
	@DisplayName("🙌 Deve Cadastrar um novo usuaário")
	public void deveCriarUmUsuario() {
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Amanda", "amanda@email.com", "12345678", "-"));
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("🙌 Não Deve duplicar um novo usuaário")
	public void naoDeveCriarUmUsuario() {

		usuarioService.cadastrarUsuario(new Usuario(0L, "Maria", "maria@email.com", "rootroot", "-"));
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(
				new Usuario(0L, "Maria", "maria@email.com", "rootroot", "-"));
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}

	@Test
	@DisplayName("😎 Deve Atualizar os dados do Usuário")
	public void deveAtualizarUmUsuario() {

		Optional<Usuario> usuarioCadastrado = usuarioService
				.cadastrarUsuario(new Usuario(0L, "Juliana", "juliana@email.com.br", "12345678", "-"));
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(usuarioCadastrado.get().getId(),
				"Juliana Andrews", "juliana_andrews@email.com.br", "12345678", "-"));
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

	}

	@Test
	@DisplayName("🙌 Deve listar todos usuaário")
	public void deveMostrarTodosUsuarios() {

		usuarioService.cadastrarUsuario(new Usuario(0L, "Julia", "julia@email.com", "rootroot", "-"));
		usuarioService.cadastrarUsuario(new Usuario(0L, "Amanda", "amanda@email.com", "12345678", "-"));

		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}

	@Test
	@DisplayName("🙌 Deve listar apenas UM usuaário")
	public void deveListarUmUsuario() {
		Optional<Usuario> buscarUsuario = usuarioService
				.cadastrarUsuario(new Usuario(0L, "Amandicas", "amandicas@amandocas.com", "Amandica123", "-"));
		ResponseEntity<Usuario> resposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/" + buscarUsuario.get().getId(), HttpMethod.GET, null, Usuario.class);
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}

	@Test
	@DisplayName("🙌 Deve fazer Login do Usuário")
	public void deveFazerLogin() {
		usuarioService.cadastrarUsuario(new Usuario
				(0L, "Nicolle", "nicolle@email.com.br", "13465278", "-"));
		HttpEntity<UsuarioLogin> corpoRequisicao = new HttpEntity<UsuarioLogin>(
				new UsuarioLogin(0L, "", "nicolle@email.com.br", "13465278", "", ""));
		ResponseEntity<UsuarioLogin> corpoResposta = testRestTemplate.exchange("/usuarios/logar", HttpMethod.POST,
				corpoRequisicao, UsuarioLogin.class);
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

	}

}

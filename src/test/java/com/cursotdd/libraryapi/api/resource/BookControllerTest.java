package com.cursotdd.libraryapi.api.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cursotdd.libraryapi.api.resource.dto.BookDTO;
import com.cursotdd.libraryapi.api.service.BookService;
import com.cursotdd.libraryapi.exception.BusinessException;
import com.cursotdd.libraryapi.model.entity.Book;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

	private final static String BOOK_API = "/api/books";

	@MockBean
	BookService service;

	@Autowired
	private MockMvc mvc;

	@Test
	@DisplayName("Deve criar um livro com sucesso")
	public void createBookMockTest() throws Exception {

		BookDTO dto = createNewBook();
		Book saveBook = Book.builder().id(10L).author("Arthur").title("As Aventuras").isbn("001").build();

		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);

		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isCreated())
				.andExpect(jsonPath("id").value(10L))
				.andExpect(jsonPath("title").value(dto.getTitle()))
				.andExpect(jsonPath("author").value(dto.getAuthor()))
				.andExpect(jsonPath("isbn").value(dto.getIsbn()));
	}

	@Test
	@DisplayName("Deve lançar erro de validação com não houver dados suficientes para criar um livro")
	public void createInvalidBookMockTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(new BookDTO());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isBadRequest())
				.andExpect(jsonPath("errors", Matchers.hasSize(3)));
	}

	@Test
	@DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro")
	public void createBookWithDuplicatedIsbn() throws Exception {

		BookDTO dto = createNewBook();
		String json = new ObjectMapper().writeValueAsString(dto);
		String mensagemErro = "Isbn já cadastrado";
		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException("Isbn já cadastrado"));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isBadRequest())
				.andExpect(jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(jsonPath("errors[0]").value(mensagemErro));
	}

	@Test
	@DisplayName("Deve obter informacoes de um livro")
	public void getBookDetailsTest() throws Exception {
		
		// Cenario
		Long id = 1L;

		Book book = Book.builder().id(1L).title(createNewBook().getTitle()).author(createNewBook().getAuthor())
				.isbn(createNewBook().getIsbn()).build();

		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

		// Execucao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
				.accept(MediaType.APPLICATION_JSON);
		mvc.perform(request).andExpect(status().isOk())
				.andExpect(jsonPath("id").value(id))
				.andExpect(jsonPath("title").value(createNewBook().getTitle()))
				.andExpect(jsonPath("author").value(createNewBook().getAuthor()))
				.andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
	}

	@Test
	@DisplayName("Deve retornar resource not found quando o livro procurado não existir")
	public void bookNotFoundTest() throws Exception {
		
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + 1))
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isNotFound());
	}

	private BookDTO createNewBook() {
		return BookDTO.builder().author("Arthur").title("As Aventuras").isbn("001").build();
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() throws Exception {
		
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1));
		
		mvc.perform(request).andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Deve retornar not foun quando não encontrar o livro para deletar")
	public void deleteDoesntBookTest() throws Exception {
		
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1));
		
		mvc.perform(request).andExpect(status().isNotFound());
	}
}

package com.cursotdd.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cursotdd.libraryapi.api.service.BookService;
import com.cursotdd.libraryapi.api.service.imp.BookServiceImpl;
import com.cursotdd.libraryapi.exception.BusinessException;
import com.cursotdd.libraryapi.model.entity.Book;
import com.cursotdd.libraryapi.model.repository.BookRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService service;
	
	@MockBean
	BookRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(repository);
	}
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		// Cenario
		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(repository.save(book)).thenReturn(
				Book.builder()
					.id(1L)
					.isbn("123")
					.title("As aventuras")
					.author("Fulano").build());
		
		// Execucao
		Book saveBook = service.save(book);
		
		assertThat(saveBook.getId()).isNotNull();
		assertThat(saveBook.getIsbn()).isEqualTo("123");
		assertThat(saveBook.getTitle()).isEqualTo("As aventuras");
		assertThat(saveBook.getAuthor()).isEqualTo("Fulano");
	}

	@Test
	@DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
	public void shouldNotSaveABookWithDuplicatedISBN() {
		// Cenario
		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		//Execucao
		Throwable exception = Assertions.catchThrowable(() -> service.save(book));
		
		//Verificacao
		assertThat(exception)
			.isInstanceOf(BusinessException.class)
			.hasMessage("Isbn já cadastrado");
		
		Mockito.verify(repository, Mockito.never()).save(book);
	}
	
	private Book createValidBook() {
		return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
	}
}

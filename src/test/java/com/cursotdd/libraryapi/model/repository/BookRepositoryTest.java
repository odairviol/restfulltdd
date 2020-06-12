package com.cursotdd.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cursotdd.libraryapi.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository repository;
	
	@Test
	@DisplayName("Deve retornar verdade quando existir um livro na base com o isbn informado")
	public void returnTrueWhenIsbnExists() {
		// Cenario
		
		String isbn = "123";
		Book book = Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
		entityManager.persist(book);
		
		// Execucao
		boolean exists = repository.existsByIsbn(isbn);
		
		// Verificacao
		assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("Deve retornar false quando não existir um livro na base com o isbn informado")
	public void returnFalseWhenIsbnDoesntExists() {
		// Cenario
		
		String isbn = "123";
		
		// Execucao
		boolean exists = repository.existsByIsbn(isbn);
		
		// Verificacao
		assertThat(exists).isFalse();
	}
}

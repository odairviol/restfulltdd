package com.cursotdd.libraryapi.api.service.imp;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cursotdd.libraryapi.api.service.BookService;
import com.cursotdd.libraryapi.exception.BusinessException;
import com.cursotdd.libraryapi.model.entity.Book;
import com.cursotdd.libraryapi.model.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository respository;

	public BookServiceImpl(BookRepository respository) {
		this.respository = respository;
	}

	@Override
	public Book save(Book book) {
		if (respository.existsByIsbn(book.getIsbn())) {
			throw new  BusinessException("Isbn já cadastrado");
		}
		return respository.save(book);
	}

	@Override
	public Optional<Book> getById(Long id) {
		return Optional.empty();
	}
	
	@Override
	public void delete(Book book) {
		// TODO Auto-generated method stub
	}

}

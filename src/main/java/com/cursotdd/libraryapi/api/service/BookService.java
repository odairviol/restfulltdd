package com.cursotdd.libraryapi.api.service;

import java.util.Optional;

import com.cursotdd.libraryapi.model.entity.Book;

public interface BookService {

	Book save(Book any);

	Optional<Book> getById(Long id);

	void delete(Book book);

}

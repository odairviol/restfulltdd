package com.cursotdd.libraryapi.api.resource.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cursotdd.libraryapi.api.exception.ApiErrors;
import com.cursotdd.libraryapi.api.resource.dto.BookDTO;
import com.cursotdd.libraryapi.api.service.BookService;
import com.cursotdd.libraryapi.exception.BusinessException;
import com.cursotdd.libraryapi.model.entity.Book;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	private BookService service;
	
	private ModelMapper mapper;
	
	public BookController(BookService service, ModelMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public BookDTO createBook(@RequestBody @Valid BookDTO bookDTO) {

		Book entity = mapper.map(bookDTO, Book.class);
		entity = service.save(entity);
		
		return mapper.map(entity, BookDTO.class);
	}
	
	@GetMapping("{id}")
	public BookDTO get(@PathVariable("id") Long id) {
		return service.getById(id).map(book -> mapper.map(book, BookDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) {
		Book book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		service.delete(book);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		return new ApiErrors(bindingResult);
	}
	
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ApiErrors handleBusinessException(BusinessException ex) {
		return new ApiErrors(ex);
	}
}

package com.cursotdd.libraryapi.api.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

import com.cursotdd.libraryapi.exception.BusinessException;

public class ApiErrors {
	
	private List<String> errors;

	public ApiErrors(BindingResult bindingResults) {
		this.errors = new ArrayList<String>();
		bindingResults.getAllErrors().forEach(erro -> {
			this.errors.add(erro.getDefaultMessage());
		});
	}
	
	public ApiErrors(BusinessException ex) {
		this.errors = Arrays.asList(ex.getMessage());
	}

	public List<String> getErrors() {
		return errors;
	}
}

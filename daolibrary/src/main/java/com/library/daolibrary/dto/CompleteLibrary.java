package com.library.daolibrary.dto;

import java.util.List;

import org.jooq.codegen.maven.example.tables.pojos.Library;

public class CompleteLibrary extends Library {

	private List<CompleteBook> allBooks;

	public List<CompleteBook> getAllBooks() {
		return allBooks;
	}

	public void setAllBooks(List<CompleteBook> allBooks) {
		this.allBooks = allBooks;
	}

}

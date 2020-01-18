package com.library.daolibrary.dto;

import org.jooq.codegen.maven.example.tables.pojos.Author;
import org.jooq.codegen.maven.example.tables.pojos.Book;

public class CompleteBook extends Book {

	private Author author;

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

}

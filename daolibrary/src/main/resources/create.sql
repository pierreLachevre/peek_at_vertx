
-- Drop table

-- DROP TABLE public."Library";

CREATE TABLE public."Library" (
	"name" varchar NULL,
	id serial NOT NULL,
	CONSTRAINT library_pk PRIMARY KEY (id)
);

-- Drop table

-- DROP TABLE public.author;

CREATE TABLE public.author (
	id serial NOT NULL,
	"lastName" varchar NULL,
	firstname varchar NULL,
	age int4 NULL,
	CONSTRAINT author_pk PRIMARY KEY (id)
);

-- Drop table

-- DROP TABLE public.book;

CREATE TABLE public.book (
	id serial NOT NULL,
	"name" varchar NULL,
	"type" varchar NULL,
	idauthor int4 NOT NULL,
	CONSTRAINT book_pk PRIMARY KEY (id),
	CONSTRAINT book_fk FOREIGN KEY (idauthor) REFERENCES author(id)
);

-- Drop table

-- DROP TABLE public.librarybook;

CREATE TABLE public.librarybook (
	idbook int4 NOT NULL,
	idlibrary int4 NOT NULL,
	CONSTRAINT librarybook_pk PRIMARY KEY (idbook, idlibrary),
	CONSTRAINT librarybook_fk FOREIGN KEY (idlibrary) REFERENCES "Library"(id),
	CONSTRAINT librarybook_fk_1 FOREIGN KEY (idbook) REFERENCES book(id)
);


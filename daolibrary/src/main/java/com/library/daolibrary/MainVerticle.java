package com.library.daolibrary;

import static org.jooq.codegen.maven.example.tables.Author.AUTHOR;
import static org.jooq.codegen.maven.example.tables.Book.BOOK;
import static org.jooq.codegen.maven.example.tables.Library.LIBRARY;
import static org.jooq.codegen.maven.example.tables.Librarybook.LIBRARYBOOK;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.codegen.maven.example.tables.pojos.Author;
import org.jooq.codegen.maven.example.tables.pojos.Book;
import org.jooq.codegen.maven.example.tables.records.AuthorRecord;
import org.jooq.codegen.maven.example.tables.records.BookRecord;
import org.jooq.impl.DSL;

import com.library.daolibrary.dto.CompleteBook;
import com.library.daolibrary.dto.CompleteLibrary;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		final DSLContext dsl = DSL.using("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=myPassword");

		final Router router = Router.router(vertx);

		// This body handler will be called for all routes

		router.route().handler(BodyHandler.create());

		// ----------------------------------Library

		router.get("/library").produces("application/json").handler(context -> {
			LOGGER.info("libraririe");
			final List<CompleteLibrary> fetchInto = new ArrayList<>();

			final var fetch = dsl
					.select(LIBRARY.ID, LIBRARY.NAME, BOOK.ID, BOOK.NAME, BOOK.TYPE, BOOK.IDAUTHOR, AUTHOR.ID,
							AUTHOR.AGE, AUTHOR.FIRSTNAME, AUTHOR.LASTNAME)
					.from(LIBRARY).leftJoin(LIBRARYBOOK).on(LIBRARYBOOK.IDLIBRARY.eq(LIBRARY.ID)).leftJoin(BOOK)
					.on(BOOK.ID.eq(LIBRARYBOOK.IDBOOK)).leftJoin(AUTHOR).on(AUTHOR.ID.eq(BOOK.IDAUTHOR)).fetch();

			for (final Record r : fetch) {
				final int idLibrary = r.get(LIBRARY.ID);

				boolean toCreate = true;
				CompleteLibrary current = null;
				for (final CompleteLibrary library : fetchInto) {
					if (library.getId() == idLibrary) {
						toCreate = false;
						current = library;
						break;
					}

				}

				if (toCreate) {
					current = new CompleteLibrary();
					current.setId(idLibrary);
					current.setName(r.get(LIBRARY.NAME));
					current.setAllBooks(new ArrayList<>());
					fetchInto.add(current);
				}

				final CompleteBook book = new CompleteBook();
				final Author author = new Author();
				author.setFirstname(r.get(AUTHOR.FIRSTNAME));
				author.setLastname(r.get(AUTHOR.LASTNAME));
				author.setAge(r.get(AUTHOR.AGE));
				author.setId(r.get(AUTHOR.ID));

				book.setAuthor(author);

				book.setId(r.get(BOOK.ID));
				book.setIdauthor(r.get(BOOK.IDAUTHOR));
				book.setName(r.get(BOOK.NAME));
				book.setType(r.get(BOOK.TYPE));
				current.getAllBooks().add(book);
			}

			LOGGER.info("fetch: " + fetchInto.size());
			context.response().end(fetchInto.isEmpty() ? "" : Json.encode(fetchInto));
		});

		// ---------------------------------BOOK

		final Router subRouter = Router.router(vertx);

		subRouter.get("/:id").produces("application/json").handler(context -> {
			final String id = context.request().getParam("id");

			LOGGER.info("books get" + id);
			final Book book = dsl.selectFrom(BOOK).where(BOOK.ID.eq(Integer.parseInt(id))).fetchOneInto(Book.class);
			context.response().end(Json.encode(book)); // context.response().end();
		});

		subRouter.get().produces("application/json").handler(context -> {
			final List<org.jooq.codegen.maven.example.tables.pojos.Book> fetchInto = dsl
					.select(BOOK.ID, BOOK.IDAUTHOR, BOOK.NAME, BOOK.TYPE).from(BOOK)
					.fetchInto(org.jooq.codegen.maven.example.tables.pojos.Book.class);
			context.response().end(fetchInto.isEmpty() ? "" : Json.encode(fetchInto));

		});

		subRouter.post().produces("application/json").handler(context -> {
			final Book book = Json.decodeValue(context.getBody(), Book.class);
			LOGGER.info("books:" + book);

			// Load a jOOQ-generated BookRecord from your POJO
			final BookRecord record = dsl.newRecord(BOOK, book);

			record.store();

			context.response()
					.end(Buffer
							.buffer(Integer
									.toString(dsl
											.select(BOOK.ID).from(BOOK).where(
													BOOK.NAME.eq(book.getName())
															.and(BOOK.IDAUTHOR.eq(book.getIdauthor())
																	.and(BOOK.TYPE.eq(book.getType()))))
											.fetchOne().value1())));
		});

		// ---------------------------------------------AUTHOR

		final Router secondSubRouter = Router.router(vertx);

		secondSubRouter.get("/:id").produces("application/json").handler(context -> {
			final String id = context.request().getParam("id");
			LOGGER.info("author get " + id);

			final Author author = dsl.selectFrom(AUTHOR).where(AUTHOR.ID.eq(Integer.parseInt(id)))
					.fetchOneInto(Author.class);
			context.response().end(Json.encode(author));
		});

		secondSubRouter.get().produces("application/json").handler(context -> {
			final List<org.jooq.codegen.maven.example.tables.pojos.Author> fetchInto = dsl
					.select(AUTHOR.ID, AUTHOR.LASTNAME, AUTHOR.FIRSTNAME, AUTHOR.AGE).from(AUTHOR)
					.fetchInto(Author.class);
			context.response().end(fetchInto.isEmpty() ? "" : Json.encode(fetchInto));
		});

		secondSubRouter.post().consumes("application/json").handler(context -> {
			// --redeploy=**/*.java
			final Author author = Json.decodeValue(context.getBody(), Author.class);
			LOGGER.info("author:" + author);

			final AuthorRecord record = dsl.newRecord(AUTHOR, author);

			record.store();

			context.response()
					.end(Buffer.buffer(Integer.toString(dsl.select(AUTHOR.ID).from(AUTHOR)
							.where(AUTHOR.LASTNAME.eq(author.getLastname()).and(
									AUTHOR.FIRSTNAME.eq(author.getFirstname()).and(AUTHOR.AGE.eq(author.getAge()))))
							.fetchOne().value1())));
		});

		router.mountSubRouter("/books", subRouter);
		router.mountSubRouter("/authors", secondSubRouter);

		vertx.createHttpServer().requestHandler(router).listen(8081);
	}
}

package com.library.restlibrary;

import java.util.stream.Collectors;

import com.library.restlibrary.dto.RestException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.ParsedHeaderValues;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		// ------------------- Periodic

		vertx.setPeriodic(1000, id -> {
			// This handler will get called every second
			System.out.println("timer fired!");
		});

		final Router router = Router.router(vertx);

		// ------------------ Client web

		final WebClientOptions options = new WebClientOptions().setDefaultHost("localhost").setDefaultPort(8081);
		options.setKeepAlive(false);
		final WebClient client = WebClient.create(vertx, options);

		// Handler failure

		router.route().failureHandler(context -> {
			final Throwable failure = context.failure();

			context.response().setStatusCode(400)
					.end(Buffer.buffer(Json.encode(new RestException("Error message", failure))));

		});

		router.get("/exception").handler(context -> {
			throw new IllegalArgumentException();
		});

		// handlers

		router.route().handler(BodyHandler.create());

		router.route().handler(context -> {
			final ParsedHeaderValues parsedHeaders = context.parsedHeaders();
			LOGGER.info("headers: " + parsedHeaders.acceptCharset().stream().map(accept -> accept.rawValue())
					.collect(Collectors.joining(";")));

			LOGGER.info("encoding: " + parsedHeaders.acceptEncoding().stream().map(accept -> accept.rawValue())
					.collect(Collectors.joining(";")));

			LOGGER.info("language: " + parsedHeaders.acceptLanguage().stream().map(accept -> accept.rawValue())
					.collect(Collectors.joining(";")));

			LOGGER.info("User-Agent: " + context.request().getHeader("User-Agent"));
			context.next();

		});

		router.get("/library").produces("application/json").handler(context -> {
			LOGGER.info("libraririe");
			client.get("/library").send(result -> {
				context.response().end(result.result().body());
			});
		});

		final Router subRouter = Router.router(vertx);

		subRouter.get("/:id").produces("application/json").handler(context -> {
			final String id = context.request().getParam("id");

			LOGGER.info("books get" + id);
			client.get("/books/" + id).send(result -> {
				context.response().end(result.result().body());
			});
		});
		subRouter.get().produces("application/json").handler(context -> {
			client.get("/books").send(result -> {
				context.response().end(result.result().body());
			});
		});

		subRouter.post().produces("application/json").handler(context -> {
			LOGGER.info("books post");
			client.post("/books").sendJsonObject(context.getBodyAsJson(), result -> {
				final Buffer body = result.result().body();
				context.reroute(HttpMethod.GET, "/books/" + body.toString());
			});
		});

		final Router secondSubRouter = Router.router(vertx);

		secondSubRouter.get("/:id").produces("application/json").handler(context -> {
			final String id = context.request().getParam("id");
			LOGGER.info("author get " + id);
			client.get("/authors/" + id).send(result -> {
				context.response().end(result.result().body());
			});
		});

		secondSubRouter.get().produces("application/json").handler(context -> {
			client.get("/authors").send(result -> {
				context.response().end(result.result().body());
			});
		});

		secondSubRouter.post().produces("application/json").handler(context -> {
			LOGGER.info("author post");
			client.post("/authors").sendJsonObject(context.getBodyAsJson(), result -> {
				final Buffer body = result.result().body();
				context.reroute(HttpMethod.GET, "/authors/" + body.toString());
			});
		});

		router.mountSubRouter("/books", subRouter);
		router.mountSubRouter("/authors", secondSubRouter);

		vertx.createHttpServer().requestHandler(router).listen(8080);
	}
}

package com.vertx.hello.hello_world;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		vertx.createHttpServer().requestHandler(request -> {
			request.response().end("Hello with docker !");
		}).listen(8889);
	}
}

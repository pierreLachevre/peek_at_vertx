package com.vertx.cluster.mycluster;

import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.spi.cluster.ClusterManager;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		vertx.createHttpServer().requestHandler(req -> {

			final ClusterManager clusterManager = ((VertxInternal) vertx).getClusterManager();
			final Map<Object, Object> map = clusterManager.getSyncMap("mapName"); // shared distributed map

			if (!map.containsKey(1)) {
				map.put(1, 5);
			} else {
				map.put(1, (int) map.get(1) + 5);
			}

			req.response().putHeader("content-type", "text/plain").end("My first Verticle! " + map.get(1));
		}).listen(8890, http -> {
			if (http.succeeded()) {
				startPromise.complete();
				System.out.println("HTTP server started on port 8888");
			} else {
				startPromise.fail(http.cause());
			}
		});
	}
}

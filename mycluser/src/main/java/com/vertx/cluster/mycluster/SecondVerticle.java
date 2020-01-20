package com.vertx.cluster.mycluster;

import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.spi.cluster.ClusterManager;

public class SecondVerticle extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		vertx.createHttpServer().requestHandler(req -> {

			final ClusterManager clusterManager = ((VertxInternal) vertx).getClusterManager();
			final Map<Object, Object> map = clusterManager.getSyncMap("mapName"); // shared distributed map

			if (!map.containsKey(1)) {
				map.put(1, 10);
			} else {
				map.put(1, (int) map.get(1) + 10);
			}

			req.response().putHeader("content-type", "text/plain").end("My second Verticle! " + map.get(1));

		}).listen(8891);
	}
}
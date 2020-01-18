package com.vertx.cluster.mycluster;

import com.hazelcast.config.Config;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class ClusteredVerticle extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		final Config hazelcastConfig = new Config();

		// Now set some stuff on the config (omitted)

		final ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);

		final VertxOptions options = new VertxOptions().setClusterManager(mgr);

		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				final Vertx vertx = res.result();
				vertx.deployVerticle(MainVerticle.class, new DeploymentOptions());
				vertx.deployVerticle(SecondVerticle.class, new DeploymentOptions());
			} else {
				// failed!
			}
		});
	}
}
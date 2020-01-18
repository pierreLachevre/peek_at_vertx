###
# vert.x docker example using a Java verticle
# To build:
#  sudo docker build -t sample/vertx-java .
# To run:
# sudo docker run -t -i -p 8889:8889 sample/vertx-java
###

# Extend vert.x image
FROM vertx/vertx3

ENV VERTICLE_NAME com.vertx.hello.hello_world.MainVerticle
ENV VERTICLE_FILE target/hello-world-docker-0.0.1-SNAPSHOT.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8889

# Copy your verticle to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]
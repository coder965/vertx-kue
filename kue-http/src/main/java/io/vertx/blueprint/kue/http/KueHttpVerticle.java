package io.vertx.blueprint.kue.http;

import io.vertx.blueprint.kue.queue.Job;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Vert.x Blueprint - Job Queue
 * Kue REST API Verticle
 *
 * @author Eric Zhao
 */
public class KueHttpVerticle extends AbstractVerticle {

  private static final String HOST = "0.0.0.0";
  private static final int PORT = 3000;

  // Kue REST API
  public static final String KUE_API_JOB_SEARCH = "/job/search/:q";
  public static final String KUE_API_STATS = "/stats";
  public static final String KUE_API_GET_JOB = "/job/:id";
  public static final String KUE_API_CREATE_JOB = "/job";
  public static final String KUE_API_DELETE_JOB = "/job/:id";
  public static final String KUE_API_GET_JOB_LOG = "/job/:id/log";

  @Override
  public void start(Future<Void> future) throws Exception {

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    // routes
    router.get(KUE_API_JOB_SEARCH).handler(this::handleSearchJob);
    router.put(KUE_API_CREATE_JOB).handler(this::handleCreateJob);
    // create server
    vertx.createHttpServer()
      .requestHandler(router::accept)
      .listen(config().getInteger("http.port", PORT),
        config().getString("http.address", HOST), result -> {
          if (result.succeeded()) {
            System.out.println("Kue http server is running on " + PORT + " port...");
            future.complete();
          } else {
            future.fail(result.cause());
          }
        });

  }

  private void handleSearchJob(RoutingContext context) {

  }

  private void handleCreateJob(RoutingContext context) {
    try {
      Job job = new Job(new JsonObject(context.getBodyAsString()));
      job.save();
    } catch (DecodeException e) {
      sendError(400, context.response());
    }

  }

  private void sendError(int statusCode, HttpServerResponse response) {
    response.setStatusCode(statusCode).end();
  }
}

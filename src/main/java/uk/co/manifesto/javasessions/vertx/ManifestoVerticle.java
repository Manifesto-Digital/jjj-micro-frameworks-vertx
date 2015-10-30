package uk.co.manifesto.javasessions.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedHashMap;
import java.util.Map;

import uk.co.manifesto.javasessions.vertx.data.Gin;

public class ManifestoVerticle extends AbstractVerticle {

	private Map<Integer, Gin> products = new LinkedHashMap<>();
	
	@Override
	public void start(Future<Void> future) {
		  
		createSomeData();
		  
		Router router = Router.router(vertx);
	
		router.route("/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response
	        	.putHeader("content-type", "text/html")
	        	.end("<h1>Manifesto Vert.x 3 application</h1>");
		});
	  
	  router.get("/api/gin").handler(this::getAll);
	  router.route("/api/gin*").handler(BodyHandler.create());
	  router.post("/api/gin").handler(this::addOne);
	  router.get("/api/gin/:id").handler(this::getOne);
	  router.put("/api/gin/:id").handler(this::updateOne);
	  router.delete("/api/gin/:id").handler(this::deleteOne);
	  
	  // Serve static resources from the /assets directory
	  router.route("/assets/*").handler(StaticHandler.create("assets"));
	  
	  vertx
	  	.createHttpServer()
	  	.requestHandler(router::accept)
		.listen(config().getInteger("http.port", 8080), result -> {
				if (result.succeeded()) {
					future.complete();
				} else {
					future.fail(result.cause());
				}
		});
	}
	
	// Get all the Gin
	private void getAll(RoutingContext routingContext) {
	  routingContext.response()
	      .putHeader("content-type", "application/json; charset=utf-8")
	      .end(Json.encodePrettily(products.values()));
	}
	
	// Add some Gin
	private void addOne(RoutingContext routingContext) {
		  final Gin gin = Json.decodeValue(routingContext.getBodyAsString(), Gin.class);
		  products.put(gin.getId(), gin);
		  routingContext.response()
		      .setStatusCode(201)
		      .putHeader("content-type", "application/json; charset=utf-8")
		      .end(Json.encodePrettily(gin));
	}
	
	// Remove some Gin :(
	private void deleteOne(RoutingContext routingContext) {
		  String id = routingContext.request().getParam("id");
		  if (id == null) {
		    routingContext.response().setStatusCode(400).end();
		  } else {
		    Integer idAsInteger = Integer.valueOf(id);
		    products.remove(idAsInteger);
		  }
		  routingContext.response().setStatusCode(204).end();
	}	
	
	// Get some Gin :)
	private void getOne(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		if (id == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			final Integer idAsInteger = Integer.valueOf(id);
			Gin gin = products.get(idAsInteger);
			if (gin == null) {
				routingContext.response().setStatusCode(404).end();
			} else {
				routingContext
						.response()
						.putHeader("content-type",
								"application/json; charset=utf-8")
						.end(Json.encodePrettily(gin));
			}
		}
	}

	// Update the Gin
	private void updateOne(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id");
		JsonObject json = routingContext.getBodyAsJson();
		if (id == null || json == null) {
			routingContext.response().setStatusCode(400).end();
		} else {
			final Integer idAsInteger = Integer.valueOf(id);
			Gin gin = products.get(idAsInteger);
			if (gin == null) {
				routingContext.response().setStatusCode(404).end();
			} else {
				gin.setName(json.getString("name"));
				gin.setOrigin(json.getString("origin"));
				routingContext
						.response()
						.putHeader("content-type",
								"application/json; charset=utf-8")
						.end(Json.encodePrettily(gin));
			}
		}
	}
	
	// Create some product
	private void createSomeData() {
		Gin williamsChase = new Gin("Williams Chase", "Chase Distillery, Hereford");
		products.put(williamsChase.getId(), williamsChase);
		Gin caorunn = new Gin("Caorunn", "Balmenach Distillery, Speyside");
		products.put(caorunn.getId(), caorunn);
	}
}

package uk.co.manifesto.javasessions.vertx.data;

import java.util.concurrent.atomic.AtomicInteger;

public class Gin {

  private static final AtomicInteger COUNTER = new AtomicInteger();

  private final int id;

  private String name;

  private String origin;

  public Gin(String name, String origin) {
    this.id = COUNTER.getAndIncrement();
    this.name = name;
    this.origin = origin;
  }

  public Gin() {
    this.id = COUNTER.getAndIncrement();
  }

  public String getName() {
    return name;
  }

  public String getOrigin() {
    return origin;
  }

  public int getId() {
    return id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }
}
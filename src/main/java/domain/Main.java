package domain;

import Utils.BDUtils;
import domain.sugerenciasFusion.Sugerencia;
import domain.sugerenciasFusion.Sugeridor;
import handlers.GetSugerenciasHandler;
import handlers.PostAceptacionSugerencia;
import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiConfiguration;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
  public static void main(String[] args) {
    Javalin app = Javalin.create(config -> {
      config.plugins.register(new OpenApiPlugin(new OpenApiConfiguration()));
      config.plugins.register(new SwaggerPlugin(new SwaggerConfiguration()));
    }).start(7002);

    app.get("/", ctx -> ctx.result("Bienvenido a la API de Sugerencias de Fusiones de Comunidades" +
            "\n\n" + "Para ver las sugerencias de fusiones de comunidades, ingrese a /api/sugerencias" +
            "\n\n" + "Para aceptar una sugerencia de fusion de comunidades, ingrese a /api/sugerencias/{id}"));

    app.get("/api/sugerencias", new GetSugerenciasHandler());
    app.post("/api/sugerencias/{id}", new PostAceptacionSugerencia());

    //List<Sugerencia> sugerencias = Sugeridor.getInstance().sugerirFusiones();
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // Programa la tarea para que se ejecute cada 7 dias
    scheduler.scheduleAtFixedRate(new Sugerir(), 0, 7, TimeUnit.DAYS);

  }

  static class Sugerir implements Runnable {

    @Override
    public void run() {
      System.out.println("\n\nhola\n\n");
      List<Sugerencia> sugerencias = Sugeridor.getInstance().sugerirFusiones();
      EntityManager em = BDUtils.getEntityManager();
      BDUtils.comenzarTransaccion(em);
      sugerencias.forEach(s->em.persist(s));
      BDUtils.commit(em);
      em.close();
    }
  }

  }


package handlers;

import Utils.BDUtils;
import domain.comunidades.Comunidad;
import domain.comunidades.RepoComunidades;
import domain.sugerenciasFusion.Fusionador;
import domain.sugerenciasFusion.RepoSugerencias;
import domain.sugerenciasFusion.Sugerencia;
import handlers.dto.AceptacionSugerencia;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.openapi.*;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityManager;
import java.util.List;

public class PostAceptacionSugerencia implements Handler{
  @OpenApi(
      summary = "Aceptar sugerencia, crear nueva comunidad",
      operationId = "postAceptacionSugerencia",
      path = "/api/sugerencias/:sugerenciaId",
      methods = HttpMethod.POST,
      pathParams = {@OpenApiParam(name = "sugerenciaId", type = Integer.class, description = "Sugerencia ID")},
      tags = {"Sugerencia"},
      requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = AceptacionSugerencia.class)}),
      responses = {
          @OpenApiResponse(status = "201", content = {@OpenApiContent(from = Comunidad.class)}),
          @OpenApiResponse(status = "400", content = {@OpenApiContent(from = BadRequestResponse.class)}),
          @OpenApiResponse(status = "500", content = {@OpenApiContent(from = InternalServerErrorResponse.class)})
      }
  )
  @Override
  public void handle(@NotNull Context context) throws Exception {
    Integer idBuscado = context.pathParamAsClass("id", Integer.class).get();
    String bodyString = context.body();
    AceptacionSugerencia aceptacionSugerencia = context.bodyAsClass(AceptacionSugerencia.class);
    System.out.println("Creando comunidad: " + bodyString);
    System.out.println(aceptacionSugerencia);
    validarNuevaComunidad(aceptacionSugerencia);
    Fusionador fusionador = new Fusionador();
    Comunidad comunidad = fusionador.fusionarComunidades(RepoSugerencias.getInstance().getSugerenciaPorId(idBuscado),
        aceptacionSugerencia.getNombreNuevaComunidad());
    EntityManager em = BDUtils.getEntityManager();
    BDUtils.comenzarTransaccion(em);
    em.persist(comunidad);
    BDUtils.commit(em);
    em.close();
    context.status(201);
  }

  private void validarNuevaComunidad(AceptacionSugerencia aceptacionSugerencia) {
    List<Comunidad> comunidades = RepoComunidades.getInstance().getComunidades();
    boolean existeNombreComunidad = comunidades.stream().anyMatch(c ->
        c.getNombre().equalsIgnoreCase(
            aceptacionSugerencia.getNombreNuevaComunidad()));
    if (existeNombreComunidad) {
      throw new IllegalArgumentException("El nombre de la comunidad ya existe, seleccione otro");
    }
  }
}

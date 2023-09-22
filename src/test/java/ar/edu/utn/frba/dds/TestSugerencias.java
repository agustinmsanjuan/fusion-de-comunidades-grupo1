package ar.edu.utn.frba.dds;

import domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestSugerencias extends InstanciasIniciales{

    Sugeridor sugeridor = new Sugeridor();

    // Sugerencia 1: comunidad1 - comunidad2
    // Sugerencia 2: comunidad3 - comunidad4
    @Test
    void sugiereComunidades(){
        List<Sugerencia> sugerencias = sugeridor.sugerirFusiones();
        Assertions.assertEquals(comunidad1, sugerencias.get(0).getComunidad1());
        Assertions.assertEquals(comunidad2, sugerencias.get(0).getComunidad2());
        Assertions.assertEquals(comunidad3, sugerencias.get(1).getComunidad1());
        Assertions.assertEquals(comunidad4, sugerencias.get(1).getComunidad2());
    }



}

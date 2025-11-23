package proyectoagrocare;

import proyectoagrocare.Gestor.GestorCosecha;
import proyectoagrocare.Gestor.GestorCultivo;
import proyectoagrocare.Gestor.GestorFertilizacion;
import proyectoagrocare.Gestor.GestorReporte;
import proyectoagrocare.Gestor.GestorRiego;
import proyectoagrocare.Gestor.GestorUsuario;

public class SistemaAgroCare {

    private GestorCultivo gestorCultivo;
    private GestorFertilizacion gestorFertilizacion;
    private GestorRiego gestorRiego;
    private GestorCosecha gestorCosecha;
    private GestorReporte gestorReporte;
    private GestorUsuario gestorUsuario;

    public SistemaAgroCare() {
        // Inicializar gestores
        gestorCultivo = new GestorCultivo();
        gestorFertilizacion = new GestorFertilizacion();
        gestorRiego = new GestorRiego();
        gestorCosecha = new GestorCosecha();

        // Establecer referencias cruzadas (para validaciones)
        gestorCultivo.setGestores(gestorCosecha, gestorFertilizacion, gestorRiego);
        gestorFertilizacion.setGestorCultivo(gestorCultivo);
        gestorRiego.setGestorCultivo(gestorCultivo);
        gestorCosecha.setGestorCultivo(gestorCultivo);

        //estw no tiene porque no hace validaciones solo lectura
        gestorReporte = new GestorReporte(gestorCultivo, gestorRiego, gestorFertilizacion, gestorCosecha);

        gestorUsuario = new GestorUsuario();
    }

    /**
     * Inicializa el sistema y carga datos (preparado para Fase 2).
     */
    public void inicializar() {
        System.out.println("=================================");
        System.out.println("   AGROCARE COLOMBIA ");
        System.out.println("Registro de Cultivos para");
        System.out.println("   Pequenos Agricultores");
        System.out.println("=================================\n");

        // Fase 2: Cargar desde archivos
        gestorCultivo.cargarDesdeArchivo();
        gestorFertilizacion.cargarDesdeArchivo();
        gestorRiego.cargarDesdeArchivo();
        gestorCosecha.cargarDesdeArchivo();
        gestorReporte.cargarDesdeArchivo();
        gestorUsuario.cargarDesdeArchivo();

        System.out.println("Sistema AgroCare inicializado correctamente.\n");
    }

    /**
     * Finaliza el sistema y guarda datos (preparado para Fase 2).
     */
    public void finalizar() {
        // Fase 2: Guardar en archivos
        gestorCultivo.guardarEnArchivo();
        gestorFertilizacion.guardarEnArchivo();
        gestorRiego.guardarEnArchivo();
        gestorCosecha.guardarEnArchivo();
        gestorReporte.guardarEnArchivo();
        gestorUsuario.guardarEnArchivo();

        System.out.println("Datos guardados correctamente.");
    }

    // Getters para acceder a los gestores desde la UI
    public GestorCultivo getGestorCultivo() {
        return gestorCultivo;
    }

    public GestorFertilizacion getGestorFertilizacion() {
        return gestorFertilizacion;
    }

    public GestorRiego getGestorRiego() {
        return gestorRiego;
    }

    public GestorCosecha getGestorCosecha() {
        return gestorCosecha;
    }

    public GestorReporte getGestorReporte() {
        return gestorReporte;
    }

    public GestorUsuario getGestorUsuario() {
        return gestorUsuario;
    }

}

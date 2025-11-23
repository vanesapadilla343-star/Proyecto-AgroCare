package proyectoagrocare;

public enum UnidadFertilizante {
    KG("kg"),
    LITROS("L"),
    METROS_CUBICOS("m3");

    private final String etiqueta;

    UnidadFertilizante(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

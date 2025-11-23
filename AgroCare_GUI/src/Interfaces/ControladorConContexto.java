package Interfaces.vistas;

import proyectoagrocare.SistemaAgroCare;
import proyectoagrocare.Clases.Usuario;

public interface ControladorConContexto {

    void setSistema(SistemaAgroCare sistema);

    void setUsuario(Usuario usuario);
}

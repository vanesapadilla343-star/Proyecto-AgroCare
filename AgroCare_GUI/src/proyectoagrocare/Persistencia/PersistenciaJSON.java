package proyectoagrocare.Persistencia;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.time.LocalDate;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import proyectoagrocare.Clases.Administrador;
import proyectoagrocare.Clases.Agricultor;
import proyectoagrocare.Clases.Usuario;

/**
 * Clase de persistencia usando JSON. Maneja serialización y deserialización de
 * datos a archivos.
 */
public class PersistenciaJSON {

    private static final String DIRECTORIO_DATOS = "datos";
    //private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Gson gson = new GsonBuilder()
            // Para LocalDate
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context)
                    -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context)
                    -> LocalDate.parse(json.getAsString()))
            // Deserializer para Usuario
            .registerTypeAdapter(Usuario.class, (JsonDeserializer<Usuario>) (json, typeOfT, context) -> {
                JsonObject obj = json.getAsJsonObject();
                String tipo = obj.get("tipo").getAsString();

                switch (tipo) {
                    case "Administrador":
                        return context.deserialize(json, Administrador.class);
                    case "Agricultor":
                        return context.deserialize(json, Agricultor.class);
                    default:
                        throw new JsonParseException("Tipo de usuario desconocido: " + tipo);
                }
            })
            .setPrettyPrinting()
            .create();

    static {
        try {
            Files.createDirectories(Paths.get(DIRECTORIO_DATOS));
            System.out.println(Paths.get(DIRECTORIO_DATOS).toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error al crear directorio de datos: " + e.getMessage());
        }
    }

    /**
     * Guarda una lista de objetos en un archivo JSON.
     */
    public static <T> boolean guardar(String nombreArchivo, ArrayList<T> datos) {
        try {
            String ruta = DIRECTORIO_DATOS + File.separator + nombreArchivo + ".json";
            String json = gson.toJson(datos);
            Files.write(Paths.get(ruta), json.getBytes());
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar datos en " + nombreArchivo + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga una lista de objetos desde un archivo JSON.
     */
    public static <T> ArrayList<T> cargar(String nombreArchivo, Class<T> tipo) {
        try {
            String ruta = DIRECTORIO_DATOS + File.separator + nombreArchivo + ".json";
            File archivo = new File(ruta);

            if (!archivo.exists()) {
                return new ArrayList<>();
            }

            String json = new String(Files.readAllBytes(Paths.get(ruta)));
            
            Type tipoLista = TypeToken.getParameterized(ArrayList.class, tipo).getType();
            ArrayList<T> datos = gson.fromJson(json, tipoLista);
            
            return datos != null ? datos : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error al cargar datos de " + nombreArchivo + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Verifica si existe un archivo de datos.
     */
    public static boolean existeArchivo(String nombreArchivo) {
        String ruta = DIRECTORIO_DATOS + File.separator + nombreArchivo + ".json";
        return Files.exists(Paths.get(ruta));
    }

    /**
     * Elimina un archivo de datos.
     */
    public static boolean eliminarArchivo(String nombreArchivo) {
        try {
            String ruta = DIRECTORIO_DATOS + File.separator + nombreArchivo + ".json";
            return Files.deleteIfExists(Paths.get(ruta));
        } catch (IOException e) {
            System.out.println("Error al eliminar archivo " + nombreArchivo + ": " + e.getMessage());
            return false;
        }
    }
}

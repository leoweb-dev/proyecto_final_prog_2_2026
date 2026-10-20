package fachada;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import modelo.Registro;
import persistencia.PersistenciaRegistros;

public class FachadaRegistros {
    private final PersistenciaRegistros persistencia;
    private List<Registro> registros;
    private boolean cargaCorrecta;

    public FachadaRegistros(PersistenciaRegistros persistencia) {
        this.persistencia = persistencia;
        registros = new ArrayList<>();
    }

    public void cargarDatos() throws IOException {
        cargaCorrecta = false;
        registros = persistencia.leer();
        cargaCorrecta = true;
    }

    public List<Registro> listarRegistros() {
        return new ArrayList<>(registros);
    }

    public List<Registro> buscarRegistros(String textoBusqueda) {
        String consulta = textoBusqueda.trim().toLowerCase(Locale.ROOT);
        if (consulta.isEmpty()) {
            return listarRegistros();
        }

        List<Registro> resultados = new ArrayList<>();
        for (Registro registro : registros) {
            String identificador = String.valueOf(registro.getIdentificador());
            String nombre = registro.getNombre().toLowerCase(Locale.ROOT);
            String categoria = registro.getCategoria().toLowerCase(Locale.ROOT);
            String estado = registro.isActivo() ? "si activo" : "no inactivo";
            if (identificador.contains(consulta)
                    || nombre.contains(consulta)
                    || categoria.contains(consulta)
                    || estado.contains(consulta)) {
                resultados.add(registro);
            }
        }
        return resultados;
    }

    public void registrarRegistro(Registro registro) {
        for (Registro existente : registros) {
            if (existente.getIdentificador() == registro.getIdentificador()) {
                throw new IllegalArgumentException(
                        "Ya existe un registro con ese identificador.");
            }
        }
        registros.add(registro);
    }

    public boolean eliminarRegistro(int identificador) {
        return registros.removeIf(registro -> registro.getIdentificador() == identificador);
    }

    public boolean modificarRegistro(
            int identificador, String nombre, String categoria, boolean activo) {
        for (Registro registro : registros) {
            if (registro.getIdentificador() == identificador) {
                registro.setNombre(nombre);
                registro.setCategoria(categoria);
                registro.setActivo(activo);
                return true;
            }
        }
        return false;
    }

    public void guardarDatos() throws IOException {
        // No reemplazar un archivo que no se pudo recuperar.
        if (!cargaCorrecta) {
            throw new IOException("No se puede guardar sin haber cargado los datos correctamente.");
        }
        persistencia.escribir(registros);
    }
}

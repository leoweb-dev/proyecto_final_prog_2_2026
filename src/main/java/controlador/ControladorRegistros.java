package controlador;

import fachada.FachadaRegistros;
import java.io.IOException;
import modelo.Registro;
import vista.VentanaPrincipal;

public class ControladorRegistros {
    private final VentanaPrincipal vista;
    private final FachadaRegistros fachada;

    public ControladorRegistros(VentanaPrincipal vista, FachadaRegistros fachada) {
        this.vista = vista;
        this.fachada = fachada;
        vista.getBotonRegistrar().addActionListener(evento -> registrarRegistro());
        vista.getBotonModificar().addActionListener(evento -> modificarRegistro());
        vista.getBotonEliminar().addActionListener(evento -> eliminarRegistro());
        vista.getBotonBuscar().addActionListener(evento -> buscarRegistros());
        vista.getBotonLimpiar().addActionListener(evento -> limpiarFormulario());
        vista.getBotonGuardar().addActionListener(evento -> guardarRegistros());
    }

    public void iniciar() {
        vista.setVisible(true);
        try {
            fachada.cargarDatos();
            vista.mostrarRegistros(fachada.listarRegistros());
        } catch (IOException e) {
            vista.mostrarError("No se pudieron cargar los datos: " + e.getMessage()
                    + "\nEl archivo no fue modificado.");
        }
    }

    private void avisarPendiente(String accion) {
        vista.mostrarMensaje(accion + ": funcionalidad pendiente de implementar por el equipo.");
    }

    private void registrarRegistro() {
        int identificador;
        try {
            identificador = Integer.parseInt(vista.getCampoIdentificador().getText().trim());
        } catch (NumberFormatException e) {
            vista.mostrarError("El identificador debe ser un número entero.");
            return;
        }

        String nombre = vista.getCampoNombre().getText().trim();
        if (nombre.isEmpty()) {
            vista.mostrarError("El nombre es obligatorio.");
            return;
        }

        Object categoriaSeleccionada = vista.getComboCategoria().getSelectedItem();
        if (!(categoriaSeleccionada instanceof String categoria)) {
            vista.mostrarError("Debe seleccionar una categoría.");
            return;
        }

        try {
            fachada.registrarRegistro(new Registro(
                    identificador, nombre, categoria, vista.getCasillaActivo().isSelected()));
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
            return;
        }

        vista.mostrarRegistros(fachada.listarRegistros());
        vista.mostrarMensaje("Registro agregado correctamente.");
    }

    private void guardarRegistros() {
        try {
            fachada.guardarDatos();
            vista.mostrarMensaje("Registros guardados correctamente.");
        } catch (IOException e) {
            vista.mostrarError("No se pudieron guardar los datos: " + e.getMessage());
        }
    }

    private void eliminarRegistro() {
        int filaSeleccionada = vista.getTablaRegistros().getSelectedRow();
        if (filaSeleccionada < 0) {
            vista.mostrarError("Seleccione un registro de la tabla para eliminar.");
            return;
        }

        int filaModelo = vista.getTablaRegistros().convertRowIndexToModel(filaSeleccionada);
        Object identificadorSeleccionado =
                vista.getTablaRegistros().getModel().getValueAt(filaModelo, 0);
        if (!(identificadorSeleccionado instanceof Number identificador)) {
            vista.mostrarError("No se pudo obtener el identificador del registro seleccionado.");
            return;
        }

        if (!fachada.eliminarRegistro(identificador.intValue())) {
            vista.mostrarError("El registro seleccionado ya no existe.");
            return;
        }

        vista.mostrarRegistros(fachada.listarRegistros());
        vista.mostrarMensaje("Registro eliminado correctamente.");
    }

    private void modificarRegistro() {
        int filaSeleccionada = vista.getTablaRegistros().getSelectedRow();
        if (filaSeleccionada < 0) {
            vista.mostrarError("Seleccione un registro de la tabla para modificar.");
            return;
        }

        int filaModelo = vista.getTablaRegistros().convertRowIndexToModel(filaSeleccionada);
        Object identificadorSeleccionado =
                vista.getTablaRegistros().getModel().getValueAt(filaModelo, 0);
        if (!(identificadorSeleccionado instanceof Number identificador)) {
            vista.mostrarError("No se pudo obtener el identificador del registro seleccionado.");
            return;
        }

        String nombre = vista.getCampoNombre().getText().trim();
        if (nombre.isEmpty()) {
            vista.mostrarError("El nombre es obligatorio.");
            return;
        }

        Object categoriaSeleccionada = vista.getComboCategoria().getSelectedItem();
        if (!(categoriaSeleccionada instanceof String categoria)) {
            vista.mostrarError("Debe seleccionar una categoría.");
            return;
        }

        if (!fachada.modificarRegistro(
                identificador.intValue(), nombre, categoria, vista.getCasillaActivo().isSelected())) {
            vista.mostrarError("El registro seleccionado ya no existe.");
            return;
        }

        vista.mostrarRegistros(fachada.listarRegistros());
        vista.mostrarMensaje("Registro modificado correctamente.");
    }

    private void limpiarFormulario() {
        vista.getCampoIdentificador().setText("");
        vista.getCampoNombre().setText("");
        vista.getComboCategoria().setSelectedIndex(0);
        vista.getCasillaActivo().setSelected(true);
        vista.getCampoBusqueda().setText("");
        vista.getTablaRegistros().clearSelection();
    }

    private void buscarRegistros() {
        String textoBusqueda = vista.getCampoBusqueda().getText().trim();
        var resultados = fachada.buscarRegistros(textoBusqueda);
        vista.mostrarRegistros(resultados);
        vista.getTablaRegistros().clearSelection();

        if (!textoBusqueda.isEmpty() && resultados.isEmpty()) {
            vista.mostrarMensaje("No se encontraron registros que coincidan con la búsqueda.");
        }
    }
}

package Clases;

import java.util.Map;

public class CofreDeAlmacenamiento extends Cofre implements InterfaceSolicitador, InterfaceProveedor {
    public CofreDeAlmacenamiento(double x, double y, Map<String,Integer> items) {
        super(x, y, 4, items);
    }

    // Constructor para solicitudes
    public CofreDeAlmacenamiento(double x, double y, String item, int cantidad) {
        super(x, y, 4, item, cantidad);
    }

    public void agregarItem(String item, int cantidad) {
        if (this.cantidadActual + cantidad <= this.capacidad) {
            this.items.put(item, this.items.getOrDefault(item, 0) + cantidad);
            this.cantidadActual += cantidad;
        }
    }

    public void eliminarItem(String item, int cantidad) {
        if (this.items.containsKey(item)) {
            int cantidadActual = this.items.get(item);
            if (cantidadActual >= cantidad) {
                this.items.put(item, cantidadActual - cantidad);
                this.cantidadActual -= cantidad;
                if (this.items.get(item) == 0) {
                    this.items.remove(item);
                }
            } else {
                throw new IllegalArgumentException("No hay suficiente cantidad del item para eliminar.");
            }
        } else {
            throw new IllegalArgumentException("El item no existe en el cofre.");
        }
    }
}

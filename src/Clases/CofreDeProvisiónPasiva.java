package Clases;

import java.util.Map;

public class CofreDeProvisiónPasiva extends Cofre implements InterfaceProveedor {
    public CofreDeProvisiónPasiva(double x, double y, Map<String,Integer> items) {
        super(x, y, 3, items);
    }

    // Constructor para solicitudes
    public CofreDeProvisiónPasiva(double x, double y, String item, int cantidad) {
        super(x, y, 3, item, cantidad);
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

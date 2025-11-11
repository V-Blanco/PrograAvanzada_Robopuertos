package Clases;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class CofreIntermedioBufer extends Cofre implements InterfaceSolicitador, InterfaceProveedor {

	public CofreIntermedioBufer(double x, double y, Map<String,Integer> items) {
		super(x, y, 2, items);
	}

    // Constructor para solicitudes
    public CofreIntermedioBufer(double x, double y, String item, int cantidad) {
        super(x, y, 2, item, cantidad);
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

	public boolean solicitarItem(String item, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (item == null) {
            throw new IllegalArgumentException("El item no puede ser nulo.");
        }
        if((this.capacidad - this.cantidadActual) < cantidad) {
            return false;
        }

        SingletonMapa singleton = SingletonMapa.getInstancia();
        int cantidadRobotsNecesaria = (int) Math.ceil(cantidad / singleton.getCantMaxTransporte());

        Stack<UsuarioDeRedLogistica> pilaLlamadas = new Stack<>();
        List<Cofre> visitados = new LinkedList<>();
        
        if(this.robopuertoMasCercano.getContadorRobots() > 0) {
            pilaLlamadas.push(this.robopuertoMasCercano);
        } else {
            // Si no hay robots disponibles, se busca el robopuerto más cercano
            this.redLogistica.getRobopuertos().stream()
                .filter(r -> r.getContadorRobots() > 0)
                .min(Comparator.comparingDouble(r -> this.getDistancia(r.getX(), r.getY())))
                .ifPresent(pilaLlamadas::push);
        }

        if (pilaLlamadas.isEmpty()) {
            return false; // No se encontraron robopuertos disponibles
        }

        double bateria = 100 - this.getDistancia(((Robopuerto)pilaLlamadas.peek()).getX(), ((Robopuerto)pilaLlamadas.peek()).getY()) * SingletonMapa.getInstancia().getFactorConsumo();
        pilaLlamadas.push(this);
        boolean solicitud = this.calcularCaminoRecursivo(pilaLlamadas, item, cantidad, visitados, bateria);
        if (solicitud) {
            if(cantidadRobotsNecesaria == 1) {
                Robopuerto inicio = (Robopuerto) pilaLlamadas.pop();
                inicio.crearRobot(pilaLlamadas, item, cantidad);
            } else {
                for (int i = 0; i < cantidadRobotsNecesaria; i++) {
                    if(this.robopuertoMasCercano.getContadorRobots() > 0) {
                        pilaLlamadas.push(this.robopuertoMasCercano);
                    } else {
                        // Si no hay robots disponibles, se busca el robopuerto más cercano
                        this.redLogistica.getRobopuertos().stream()
                            .filter(r -> r.getContadorRobots() > 0)
                            .min(Comparator.comparingDouble(r -> this.getDistancia(r.getX(), r.getY())))
                            .ifPresent(pilaLlamadas::push);
                    }

                    if (pilaLlamadas.isEmpty()) {
                        return false; // No se encontraron robopuertos disponibles
                    }

                    bateria = 100 - this.getDistancia(((Robopuerto)pilaLlamadas.peek()).getX(), ((Robopuerto)pilaLlamadas.peek()).getY()) * SingletonMapa.getInstancia().getFactorConsumo();
                    pilaLlamadas.push(this);
                    this.calcularCaminoRecursivo(pilaLlamadas, item, Math.min(cantidad, singleton.getCantMaxTransporte()), visitados, bateria);
                    Robopuerto inicio = (Robopuerto) pilaLlamadas.pop();
                    inicio.crearRobot(pilaLlamadas, item, Math.min(cantidad, singleton.getCantMaxTransporte()));
                    cantidad -= Math.min(cantidad, singleton.getCantMaxTransporte());
                }
            }
            return true; // Retorna true si la solicitud fue exitosa
        }
        return false; // Retorna false si la solicitud no fue exitosa
    }
	
}

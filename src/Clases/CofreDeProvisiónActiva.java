package Clases;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class CofreDeProvisiónActiva extends Cofre implements InterfaceProveedor {

    public CofreDeProvisiónActiva(double x, double y, Map<String,Integer> items) {
        super(x, y, 1, items);
    }

    // Constructor para solicitudes
    public CofreDeProvisiónActiva(double x, double y, String item, int cantidad) {
        super(x, y, 1, item, cantidad);
    }

    public void eliminarItem(String item, int cantidad) {
        System.out.println(this);
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

    public boolean proveerItem(String item, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (item == null) {
            throw new IllegalArgumentException("El item no puede ser nulo.");
        }

        SingletonMapa singleton = SingletonMapa.getInstancia();
        int cantidadRobotsNecesaria = (int) Math.ceilDiv(cantidad, singleton.getCantMaxTransporte());

        Queue<UsuarioDeRedLogistica> colaLlamadas = new LinkedList<>();
        List<Cofre> visitados = new LinkedList<>();
        
        if(this.robopuertoMasCercano.getContadorRobots() > 0) {
            colaLlamadas.add(this.robopuertoMasCercano);
        } else {
            // Si no hay robots disponibles, se busca el robopuerto más cercano
            this.redLogistica.getRobopuertos().stream()
                .filter(r -> r.getContadorRobots() > 0)
                .min(Comparator.comparingDouble(r -> this.getDistancia(r.getX(), r.getY())))
                .ifPresent(colaLlamadas::add);
        }

        if (colaLlamadas.isEmpty()) {
            return false; // No se encontraron robopuertos disponibles
        }

        double bateria = 100 - this.getDistancia(((Robopuerto)colaLlamadas.peek()).getX(), ((Robopuerto)colaLlamadas.peek()).getY()) * SingletonMapa.getInstancia().getFactorConsumo();
        colaLlamadas.add(this);
        boolean solicitud = this.calcularCaminoRecursivo(colaLlamadas, item, cantidad, visitados, bateria);
        System.out.println("Cant Robots " + cantidadRobotsNecesaria + " Solicitud:" + solicitud);
        if (solicitud) {
            System.out.println("SOLICITUD APROBADA");
            if(cantidadRobotsNecesaria == 1) {
                System.out.println("UN SOLO ROBOT");
                Robopuerto inicio = (Robopuerto) colaLlamadas.poll();
                inicio.crearRobot(colaLlamadas, item, cantidad);
            } else {
                for (int i = 0; i < cantidadRobotsNecesaria; i++) {
                    colaLlamadas.clear();
                    if(this.robopuertoMasCercano.getContadorRobots() > 0) {
                        colaLlamadas.add(this.robopuertoMasCercano);
                    } else {
                        // Si no hay robots disponibles, se busca el robopuerto más cercano
                        this.redLogistica.getRobopuertos().stream()
                            .filter(r -> r.getContadorRobots() > 0)
                            .min(Comparator.comparingDouble(r -> this.getDistancia(r.getX(), r.getY())))
                            .ifPresent(colaLlamadas::add);
                    }

                    if (colaLlamadas.isEmpty()) {
                        return false; // No se encontraron robopuertos disponibles
                    }

                    bateria = 100 - this.getDistancia(((Robopuerto)colaLlamadas.peek()).getX(), ((Robopuerto)colaLlamadas.peek()).getY()) * SingletonMapa.getInstancia().getFactorConsumo();
                    colaLlamadas.add(this);
                    this.calcularCaminoRecursivo(colaLlamadas, item, Math.min(cantidad, singleton.getCantMaxTransporte()), visitados, bateria);
                    Robopuerto inicio = (Robopuerto) colaLlamadas.poll();
                    inicio.crearRobot(colaLlamadas, item, Math.min(cantidad, singleton.getCantMaxTransporte()));
                    cantidad -= Math.min(cantidad, singleton.getCantMaxTransporte());
                }
            }
            return true; // Retorna true si la solicitud fue exitosa
        }
        return false; // Retorna false si la solicitud no fue exitosa
    }
}

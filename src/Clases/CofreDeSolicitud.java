package Clases;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Comparator;
import java.util.LinkedList;
public class CofreDeSolicitud extends Cofre implements InterfaceSolicitador {
    
    public CofreDeSolicitud(double x, double y, Map<String,Integer> items) {
        super(x, y, 1, items);
        this.prioridad = 1;
    }

    // Constructor para solicitudes
    public CofreDeSolicitud(double x, double y, String item, int cantidad) {
        super(x, y, 1, item, cantidad);
    }

    public void agregarItem(String item, int cantidad) {
        if (this.cantidadActual + cantidad <= this.capacidad) {
            this.items.put(item, this.items.getOrDefault(item, 0) + cantidad);
            this.cantidadActual += cantidad;
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
        int cantidadRobotsNecesaria = (int) Math.ceilDiv(cantidad, singleton.getCantMaxTransporte());

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
        
        double bateria = singleton.getBateria() - this.getDistancia(((Robopuerto)pilaLlamadas.peek()).getX(), ((Robopuerto)pilaLlamadas.peek()).getY()) * SingletonMapa.getInstancia().getFactorConsumo();
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

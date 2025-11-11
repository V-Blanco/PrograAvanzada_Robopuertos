package Clases;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Stack;

public abstract class Cofre extends UsuarioDeRedLogistica {
    protected final int capacidad;
    protected int cantidadActual, prioridad;
    protected Map<String, Integer> items;
    protected List<Cofre> cofresSolicitadoresCercanos, cofresProveedoresCercanos;
    protected Robopuerto robopuertoMasCercano;

    public Cofre(double x, double y, int prioridad, Map<String,Integer> items) {
        SingletonMapa singleton = SingletonMapa.getInstancia();
        if(singleton.getCapacidad() < items.values().stream().mapToInt(Integer::intValue).sum())
            throw new IllegalArgumentException("La cantidad de items supera la capacidad del cofre.");
        this.x = x;
        this.y = y;
        this.cantidadActual = items.values().stream().mapToInt(Integer::intValue).sum();
        this.prioridad = prioridad;
        this.items = new HashMap<>(items);
        this.cofresSolicitadoresCercanos = new LinkedList<Cofre>();
        this.cofresProveedoresCercanos = new LinkedList<Cofre>();
        this.capacidad = singleton.getCapacidad();
        this.redLogistica = singleton.agregarCofre(this);
    }

    // Constructor para solicitudes
    public Cofre(double x, double y, int prioridad, String item, int cantidad) {
        SingletonMapa singleton = SingletonMapa.getInstancia();
        if(singleton.getCapacidad() < cantidad)
            throw new IllegalArgumentException("La cantidad de items solicitados supera la capacidad del cofre.");
        this.x = x;
        this.y = y;
        this.cantidadActual = 0;
        this.prioridad = prioridad;
        this.items = new HashMap<>();
        this.cofresSolicitadoresCercanos = new LinkedList<Cofre>();
        this.cofresProveedoresCercanos = new LinkedList<Cofre>();
        this.capacidad = 0;
        Solicitud solicitud = new Solicitud(this, item, cantidad);
        singleton.agregarSolicitud(solicitud);
    }

    public RedLogistica getRedLogistica() {
        return this.redLogistica;
    }

    public void setRedLogistica(RedLogistica redLogistica) {
        this.redLogistica = redLogistica;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int getPrioridad() {
        return this.prioridad;
    }

    public Map<String, Integer> getItems() {
        return this.items;
    }

    public double getDistancia(double x, double y) {
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
    }

    public List<Cofre> getCofresSolicitadoresCercanos() {
        return this.cofresSolicitadoresCercanos;
    }

    public List<Cofre> getCofresProveedoresCercanos() {
        return this.cofresProveedoresCercanos;
    }

    public Robopuerto getRobopuertoMasCercano() {
        return this.robopuertoMasCercano;
    }

    public int getCapacidad() {
        return this.capacidad;
    }

    public int getCantidadActual() {
        return this.cantidadActual;
    }

    public void setRobopuertoMasCercano(Robopuerto robopuerto) {
        this.robopuertoMasCercano = robopuerto;
    }

    public int getCantidadItem(String item) {
        return this.items.getOrDefault(item, 0);
    }

    public void insertarOrdenado(List<Cofre> lista, Cofre nuevo) {
        int i = 0;
        while (i < lista.size()) {
            Cofre actual = lista.get(i);
            if (nuevo.getPrioridad() < actual.getPrioridad()) {
                break;
            } 
            if (nuevo.getPrioridad() == actual.getPrioridad()) {
                if (nuevo.getDistancia(this.x, this.y) < actual.getDistancia(this.x, this.y)) {
                    break;
                }
            }
            i++;
        }
        lista.add(i, nuevo);  // Inserta en la posición correcta
    }

    public void insertarOrdenado(List<Cofre> lista, List<Cofre> nuevo) {
        for (Cofre cofre : nuevo) {
            int i = 0;
            while (i < lista.size()) {
                Cofre actual = lista.get(i);

                if (cofre.getPrioridad() < actual.getPrioridad()) {
                    break;
                } 
                i++;
            }
            lista.add(i, cofre);  // Inserta en la posición correcta
        }
    }

    public boolean calcularCaminoRecursivo(Stack<UsuarioDeRedLogistica> pila, String item, int cantidad, List<Cofre> visitado, double bateria) {
        visitado.add(this);
        
        for (Cofre c : this.cofresProveedoresCercanos) {
            if (!visitado.contains(c) && c.getItems().containsKey(item)) {
                if (bateria >= (c.getDistancia(this.x, this.y) + c.getDistancia(c.getRobopuertoMasCercano().getX(), c.getRobopuertoMasCercano().getY())) * SingletonMapa.getInstancia().getFactorConsumo()) {
                    pila.push(c);
                    int min = Math.min(c.getCantidadItem(item), cantidad);
                    cantidad -= min; // Reduce la cantidad solicitada por la cantidad disponible en el cofre
                    if (cantidad == 0) {
                        pila.push(c.getRobopuertoMasCercano());
                        return true; // Si la cantidad es cero, se considera que la solicitud es exitosa
                    } else {
                        bateria -= c.getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo();
                        return c.calcularCaminoRecursivo(pila, item, cantidad, visitado, bateria); // Llama recursivamente al siguiente cofre
                    } 
                } else {
                    pila.push(this.robopuertoMasCercano);
                    bateria -= this.getDistancia(this.robopuertoMasCercano.getX(), this.robopuertoMasCercano.getY()) * SingletonMapa.getInstancia().getFactorConsumo();
                    return robopuertoMasCercano.calcularCaminoRecursivo(pila, item, cantidad, visitado, bateria); // Llama recursivamente al siguiente cofre
                }
            }
        }
        return false; // No se pudo encontrar el item solicitado
    }

    public boolean calcularCaminoRecursivo(Queue<UsuarioDeRedLogistica> cola, String item, int cantidad, List<Cofre> visitado, double bateria) {
        visitado.add(this);
        System.out.println("Cofre en (" + this.x + ", " + this.y + ")");
        
        for (Cofre c : this.cofresSolicitadoresCercanos) {
            System.out.println("Entra al for");
            if (!visitado.contains(c) && c instanceof CofreDeAlmacenamiento && c.getCantidadActual() < c.getCapacidad()) {
                System.out.println("Entra al if el cofre" + c.getX() + "," + c.getY());
                if (bateria >= (c.getDistancia(this.x, this.y) + c.getDistancia(c.getRobopuertoMasCercano().getX(), c.getRobopuertoMasCercano().getY())) * SingletonMapa.getInstancia().getFactorConsumo()) {
                    cola.add(c);
                    int min = Math.min(c.getCapacidad() - c.getCantidadActual(), cantidad);
                    cantidad -= min; // Reduce la cantidad a entregar por la capacidad disponible en el cofre
                    if (cantidad == 0) {
                        System.out.println("Termina en Robopuerto (" + c.getRobopuertoMasCercano().getX() + ", " + c.getRobopuertoMasCercano().getY() + ")");
                        cola.add(c.getRobopuertoMasCercano());
                        return true; // Si la cantidad es cero, se considera que la solicitud es exitosa
                    } else {
                        bateria -= c.getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo();
                        return c.calcularCaminoRecursivo(cola, item, cantidad, visitado, bateria); // Llama recursivamente al siguiente cofre
                    }
                } else {
                    System.out.println("Va a robopuertoMasCercano en (" + this.robopuertoMasCercano.getX() + ", " + this.robopuertoMasCercano.getY() + ")");
                    cola.add(this.robopuertoMasCercano);
                    bateria -= this.getDistancia(this.robopuertoMasCercano.getX(), this.robopuertoMasCercano.getY()) * SingletonMapa.getInstancia().getFactorConsumo();
                    return robopuertoMasCercano.calcularCaminoRecursivo(cola, item, cantidad, visitado, bateria); // Llama recursivamente al siguiente cofre
                }
            }
        }
        return false; // No se pudieron encontrar cofres de almacenamiento con capacidad suficiente
    }

    public void mostrarCofres(List<Cofre> lista) {
        System.out.println("Contenido de la lista de cofres(desde("+this.x+", "+this.y+")):");
        for (Cofre cofre : lista) {
            System.out.println("Cofre en (" + cofre.getX() + ", " + cofre.getY() + ")"
                + " | Prioridad: " + cofre.getPrioridad()
                + " | Distancia desde (" + this.x + ", " + this.y + "): "
                + cofre.getDistancia(this.x, this.y));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cofre{\n");
        sb.append("  capacidad=").append(capacidad).append(",\n");
        sb.append("  cantidadActual=").append(cantidadActual).append(",\n");
        sb.append("  prioridad=").append(getPrioridad()).append(",\n");
        sb.append("  ubicación=(").append(x).append(", ").append(y).append("),\n");
        sb.append("  items={\n");

        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            sb.append("    ").append(entry.getKey()).append(": ").append(entry.getValue()).append(",\n");
        }

        sb.append("  }\n");
        sb.append("}");

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cofre cofre = (Cofre) obj;
        return Double.compare(cofre.x, x) == 0 &&
            Double.compare(cofre.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

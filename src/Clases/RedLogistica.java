package Clases;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class RedLogistica {
    private List<Robopuerto> robopuertos;
    private List<Cofre> solicitadores;
    private List<Cofre> proveedores;
    private PriorityQueue<Solicitud> solicitudesCofres;

    public RedLogistica() {
        this.robopuertos = new LinkedList<>();
        this.solicitadores = new LinkedList<>();
        this.proveedores = new LinkedList<>();
        this.solicitudesCofres = new PriorityQueue<>();
    }

    public List<Robopuerto> getRobopuertos() {
        return robopuertos;
    }

    public List<Cofre> getSolicitadores() {
        return solicitadores;
    }

    public List<Cofre> getProveedores() {
        return proveedores;
    }

    public PriorityQueue<Solicitud> getSolicitudesCofres() {
        return solicitudesCofres;
    }

    public void agregarRobopuertos(Robopuerto robopuertos) {
        this.robopuertos.add(robopuertos);
    }
    public void agregarRobopuertos(List<Robopuerto> robopuertos) {
        this.robopuertos.addAll(robopuertos);
    }

    public void agregarSolicitador(Cofre solicitador) {
        this.solicitadores.add(solicitador);
    }
    public void agregarSolicitador(List<Cofre> solicitadores) {
        this.solicitadores.addAll(solicitadores);
    }

    public void agregarProveedor(Cofre proveedor) {
        this.proveedores.add(proveedor);
    }
    
    public void agregarProveedor(List<Cofre> proveedores) {
        this.proveedores.addAll(proveedores);
    }

    public void insertarOrdenado(List<Cofre> lista, Cofre nuevo) {
        int i = 0;
        while (i < lista.size()) {
            Cofre actual = lista.get(i);

            if (nuevo.getPrioridad() < actual.getPrioridad()) {
                break;
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

    public void simular() {
    	while (!this.solicitudesCofres.isEmpty()) {
    	    Solicitud s = this.solicitudesCofres.poll(); 
    	    System.out.println("Simulando solicitud: x :" + s.getCofre().getX() + ", y: " + s.getCofre().getY());
    	    if (s.getCofre() instanceof CofreDeSolicitud) {
    	        CofreDeSolicitud cofre = (CofreDeSolicitud) s.getCofre();
    	        cofre.solicitarItem(s.getItem(), s.getCantidad());
    	    } else if (s.getCofre() instanceof CofreIntermedioBufer) {
    	        CofreIntermedioBufer cofre = (CofreIntermedioBufer) s.getCofre();
    	        cofre.solicitarItem(s.getItem(), s.getCantidad());
    	    }
    	}
        for(Cofre c : this.proveedores) {
            if(c instanceof CofreDeProvisiónActiva) {
                System.out.println("Simulando proveedor: x :" + c.getX() + ", y: " + c.getY());

                CofreDeProvisiónActiva cofre = (CofreDeProvisiónActiva) c;
                Map<String, Integer> copiaItems = new HashMap<>(cofre.getItems());
                for (Map.Entry<String, Integer> entry : copiaItems.entrySet()) {
                    String item = entry.getKey();
                    int cantidad = entry.getValue();
                    cofre.proveerItem(item, cantidad);
                }
            } else {
                break;
            }
        }
    }
}

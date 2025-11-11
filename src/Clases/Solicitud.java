package Clases;

public class Solicitud implements Comparable<Solicitud> {
    private Cofre cofre;
    private String item;
    private int cantidad;

    public Solicitud(Cofre cofre, String item, int cantidad) {
        if (cofre == null) {
            throw new IllegalArgumentException("El cofre no puede ser nulo.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (item == null) {
            throw new IllegalArgumentException("El item no puede ser nulo.");
        }
        if (!(cofre instanceof CofreDeSolicitud || cofre instanceof CofreIntermedioBufer)) {
            throw new IllegalArgumentException("El cofre debe ser de solicitud o intermedio bufer.");
        } 
        this.cofre = cofre;
        this.item = item;
        this.cantidad = cantidad;
    }

    public Cofre getCofre() {
        return cofre;
    }

    public String getItem() {
        return item;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCofre(Cofre cofre) {
        this.cofre = cofre;
    }
    
    public int compareTo(Solicitud otra) {
        return Integer.compare(this.cofre.getPrioridad(), otra.cofre.getPrioridad());
    }
    
}

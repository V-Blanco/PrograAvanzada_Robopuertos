package Clases;

import java.util.Queue;
import java.util.Stack;

public class Robot {
    private RedLogistica redLogistica;
    private double x, y, bateria;

    public Robot(double x, double y) {
        this.x = x;
        this.y = y;
        SingletonMapa singleton = SingletonMapa.getInstancia();
        this.bateria = singleton.getBateria();
    }

    public RedLogistica getRedLogistica() {
        return redLogistica;
    }

    public void setRedLogistica(RedLogistica redLogistica) {
        this.redLogistica = redLogistica;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getBateria() {
        return bateria;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setBateria(double bateria) {
        this.bateria = bateria;
    }

    public void recargarBateria(double cantidad) {
        this.bateria += cantidad;
        if (this.bateria > 100) {
            this.bateria = 100; // Limitar la bateria al maximo de 100
        }
    }

    public double getDistancia(double x, double y) {
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
    }

    public void recorrerCamino(Stack<UsuarioDeRedLogistica> pila, String item, int cantidad) {
        System.out.println("Robot " + this.x + "," + this.y + " salio ");
        UsuarioDeRedLogistica usuario;
        int contador = 0;
        while (!pila.isEmpty()) {
            contador++;
            System.out.println("Paso: " + contador);
            usuario = pila.pop();
            if(usuario instanceof InterfaceProveedor) {
                InterfaceProveedor proveedor = (InterfaceProveedor) usuario;
                Cofre cofre = (Cofre) usuario;
                System.out.println("X: " + cofre.getX() + " Y: " + cofre.getY());
                this.bateria -= this.getDistancia(cofre.getX(), cofre.getY()) * SingletonMapa.getInstancia().getFactorConsumo();
                this.x = cofre.getX();
                this.y = cofre.getY();
                int cantMin = Math.min(cofre.getCantidadItem(item), cantidad);
                proveedor.eliminarItem(item, cantMin);
                System.out.println("Robot " + this.x + "," + this.y + " saco " + cantMin + " " + item + " al cofre " + cofre.getX() + "," + cofre.getY());
            } else if(usuario instanceof InterfaceSolicitador) {
                InterfaceSolicitador solicitador = (InterfaceSolicitador) usuario;
                Cofre cofre = (Cofre) usuario;
                System.out.println("X: " + cofre.getX() + " Y: " + cofre.getY());
                this.bateria -= this.getDistancia(cofre.getX(), cofre.getY()) * SingletonMapa.getInstancia().getFactorConsumo();
                this.x = cofre.getX();
                this.y = cofre.getY();
                solicitador.agregarItem(item, cantidad);
                System.out.println("Robot " + this.x + "," + this.y + " entrego " + cantidad + " " + item + " al cofre " + cofre.getX() + "," + cofre.getY());
            } else if (usuario instanceof Robopuerto) {
                Robopuerto robopuerto = (Robopuerto) usuario;
                System.out.println("X: " + robopuerto.getX() + " Y: " + robopuerto.getY());
                this.bateria -= this.getDistancia(robopuerto.getX(), robopuerto.getY()) * SingletonMapa.getInstancia().getFactorConsumo();
                this.x = robopuerto.getX();
                this.y = robopuerto.getY();
                this.bateria = SingletonMapa.getInstancia().getBateria();
                if(pila.isEmpty()) {
                    robopuerto.setContadorRobots(robopuerto.getContadorRobots() + 1);
                    System.out.println("Robot " + this.x + "," + this.y + " termino en robopuerto " + robopuerto.getX() + "," + robopuerto.getY());
                } else {
                    System.out.println("Robot " + this.x + "," + this.y + " cargo en robopuerto " + robopuerto.getX() + "," + robopuerto.getY());
                }
            }
        }
    }

    public void recorrerCamino(Queue<UsuarioDeRedLogistica> cola, String item, int cantidad) {
        UsuarioDeRedLogistica usuario;
        int contador = 0;
        System.out.println("Robot " + this.x + "," + this.y + " salio ");
        while (!cola.isEmpty()) {
            contador++;
            System.out.println("Paso: " + contador);
            usuario = cola.poll();
            if(usuario instanceof CofreDeAlmacenamiento) {
                CofreDeAlmacenamiento cofre = (CofreDeAlmacenamiento) usuario;
                System.out.println("X: " + cofre.getX() + " Y: " + cofre.getY());
                this.bateria -= this.getDistancia(cofre.getX(), cofre.getY()) * SingletonMapa.getInstancia().getFactorConsumo();
                this.x = cofre.getX();
                this.y = cofre.getY();
                cofre.agregarItem(item, cantidad);
                System.out.println("Robot " + this.x + "," + this.y + " entrego " + cantidad + " " + item + " al cofre " + cofre.getX() + "," + cofre.getY());

            } else if(usuario instanceof InterfaceProveedor) {
                InterfaceProveedor proveedor = (InterfaceProveedor) usuario;
                Cofre cofre = (Cofre) usuario;
                System.out.println("X: " + cofre.getX() + " Y: " + cofre.getY());

                this.bateria -= this.getDistancia(cofre.getX(), cofre.getY()) * SingletonMapa.getInstancia().getFactorConsumo();
                this.x = cofre.getX();
                this.y = cofre.getY();
                int cantMin = Math.min(cofre.getCantidadItem(item), cantidad);
                proveedor.eliminarItem(item, cantMin);
                System.out.println("Robot " + this.x + "," + this.y + " saco " + cantMin + " " + item + " al cofre " + cofre.getX() + "," + cofre.getY());

            } else if(usuario instanceof InterfaceSolicitador) {
                InterfaceSolicitador solicitador = (InterfaceSolicitador) usuario;
                Cofre cofre = (Cofre) usuario;
                System.out.println("X: " + cofre.getX() + " Y: " + cofre.getY());

                this.bateria -= this.getDistancia(cofre.getX(), cofre.getY()) * SingletonMapa.getInstancia().getFactorConsumo();
                this.x = cofre.getX();
                this.y = cofre.getY();
                solicitador.agregarItem(item, cantidad);
                System.out.println("Robot " + this.x + "," + this.y + " entrego " + cantidad + " " + item + " al cofre " + cofre.getX() + "," + cofre.getY());

            } else if (usuario instanceof Robopuerto) {
                Robopuerto robopuerto = (Robopuerto) usuario;
                System.out.println("X: " + robopuerto.getX() + " Y: " + robopuerto.getY());

                this.bateria -= this.getDistancia(robopuerto.getX(), robopuerto.getY()) * SingletonMapa.getInstancia().getFactorConsumo();
                this.x = robopuerto.getX();
                this.y = robopuerto.getY();
                this.bateria = SingletonMapa.getInstancia().getBateria();;
                if(cola.isEmpty()) {
                    robopuerto.setContadorRobots(robopuerto.getContadorRobots() + 1);
                    System.out.println("Robot " + this.x + "," + this.y + " termino en robopuerto " + robopuerto.getX() + "," + robopuerto.getY());
                } else {
                    System.out.println("Robot " + this.x + "," + this.y + " cargo en robopuerto " + robopuerto.getX() + "," + robopuerto.getY());
                }
            }
        }
    }
}


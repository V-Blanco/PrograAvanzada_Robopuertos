package Clases;

import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class Robopuerto extends UsuarioDeRedLogistica {
    private double radioCobertura;
    private int contadorRobots;

    public Robopuerto(double x, double y, int contadorRobots) {
        this.x = x;
        this.y = y;
        this.contadorRobots = contadorRobots;
        SingletonMapa singleton = SingletonMapa.getInstancia();
        this.radioCobertura = singleton.getRadioCobertura();
        this.redLogistica = singleton.agregarRobopuerto(this);
    }

    public RedLogistica getRedLogistica() {
        return redLogistica;
    }

    public void setRedLogistica(RedLogistica redLogistica) {
        this.redLogistica = redLogistica;
    }

    public void setContadorRobots(int contadorRobots) {
        this.contadorRobots = contadorRobots;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadioCobertura() {
        return radioCobertura;
    }

    public int getContadorRobots() {
        return contadorRobots;
    }

    public double getDistancia(double x, double y) {
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
    }

    public void crearRobot(Stack<UsuarioDeRedLogistica> pila, String item, int cantidad) {
        this.contadorRobots--;
        Robot robot = new Robot(this.x, this.y);
        robot.setRedLogistica(this.redLogistica);
        robot.recorrerCamino(pila, item, cantidad);
    }

    public void crearRobot(Queue<UsuarioDeRedLogistica> cola, String item, int cantidad) {
        this.contadorRobots--;
        Robot robot = new Robot(this.x, this.y);
        robot.setRedLogistica(this.redLogistica);
        robot.recorrerCamino(cola, item, cantidad);
    }

    public boolean calcularCaminoRecursivo(Stack<UsuarioDeRedLogistica> pila, String item, int cantidad, List<Cofre> visitado, double bateria) {
        Cofre cofreMasCercano = this.redLogistica.getProveedores().get(0);
        for (Cofre c : this.redLogistica.getProveedores()) {
            if (!visitado.contains(c) && c.getItems().containsKey(item)) {
                if (!cofreMasCercano.getItems().containsKey(item)) 
                    cofreMasCercano = c;
                if(cofreMasCercano.getPrioridad() < c.getPrioridad())
                    break;
                if (c.getDistancia(this.x, this.y) < cofreMasCercano.getDistancia(this.x, this.y))
                    cofreMasCercano = c;
            }
        }
        if (!cofreMasCercano.getItems().containsKey(item)) 
            return false;

        // Bateria llena
        bateria = SingletonMapa.getInstancia().getBateria();

        // Llego al cofre + robopuerto más cercano al cofre
        if (bateria >= (cofreMasCercano.getDistancia(this.x, this.y) + cofreMasCercano.getDistancia(cofreMasCercano.getRobopuertoMasCercano().getX(), cofreMasCercano.getRobopuertoMasCercano().getY())) * SingletonMapa.getInstancia().getFactorConsumo()) {
            pila.push(cofreMasCercano);
            int min = Math.min(cofreMasCercano.getCantidadItem(item), cantidad);
            cantidad -= min; // Reduce la cantidad solicitada por la cantidad disponible en el cofre
            if (cantidad == 0) {
                pila.push(cofreMasCercano.getRobopuertoMasCercano());
                return true; // Si la cantidad es cero, se considera que la solicitud es exitosa
            } else {
                bateria -= cofreMasCercano.getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo();
                return cofreMasCercano.calcularCaminoRecursivo(pila, item, cantidad, visitado, bateria); // Llama recursivamente al siguiente cofre
            } 
        } else {
            // No llego al cofre + robopuerto más cercano al cofre, pregunto si llego solo al robopuerto
            if (bateria >= cofreMasCercano.getRobopuertoMasCercano().getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo()) {
                pila.push(cofreMasCercano.getRobopuertoMasCercano());
                bateria -= cofreMasCercano.getRobopuertoMasCercano().getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo();
                return cofreMasCercano.getRobopuertoMasCercano().calcularCaminoRecursivo(pila, item, cantidad, visitado, bateria); // Llama recursivamente al robopuerto del cofre más cercano
            } else {
                // No llego al robopuerto más cercano al cofre, busco otro robopuerto cercano al cofre al que llegue
                Robopuerto robopuertoMasCercano = this.redLogistica.getRobopuertos().get(0);
                for (Robopuerto r : this.redLogistica.getRobopuertos()) {
                    if (r.getDistancia(cofreMasCercano.getX(), cofreMasCercano.getY()) < robopuertoMasCercano.getDistancia(cofreMasCercano.getX(), cofreMasCercano.getY())
                        && bateria >= r.getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo()) {
                        robopuertoMasCercano = r;
                    }
                }
                if (bateria < robopuertoMasCercano.getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo())
                    return false; // No llego a ningún robopuerto
                pila.push(robopuertoMasCercano);
                bateria -= robopuertoMasCercano.getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo();
                return robopuertoMasCercano.calcularCaminoRecursivo(pila, item, cantidad, visitado, bateria);
            }
        }
    }

    public boolean calcularCaminoRecursivo(Queue<UsuarioDeRedLogistica> cola, String item, int cantidad, List<Cofre> visitado, double bateria) {
        Cofre cofreMasCercano = this.redLogistica.getProveedores().get(0);
        for (Cofre c : this.redLogistica.getSolicitadores()) {
            if (!visitado.contains(c) && c instanceof CofreDeAlmacenamiento && c.getCantidadActual() < c.getCapacidad()) {
                if (!(cofreMasCercano instanceof CofreDeAlmacenamiento))
                    cofreMasCercano = c;
                else if (c.getDistancia(this.x, this.y) < cofreMasCercano.getDistancia(this.x, this.y)) {
                    if(cofreMasCercano.getPrioridad() < c.getPrioridad())
                        break;    
                    cofreMasCercano = c;
                }
            }
        }
        if (!(cofreMasCercano instanceof CofreDeAlmacenamiento)) {
            return false;
        }

        // Bateria llena
        bateria = SingletonMapa.getInstancia().getBateria();

        // Llego al cofre + robopuerto más cercano al cofre
        if (bateria >= (cofreMasCercano.getDistancia(this.x, this.y) + cofreMasCercano.getDistancia(cofreMasCercano.getRobopuertoMasCercano().getX(), cofreMasCercano.getRobopuertoMasCercano().getY())) * SingletonMapa.getInstancia().getFactorConsumo()) {
            cola.add(cofreMasCercano);
            int min = Math.min(cofreMasCercano.getCapacidad() - cofreMasCercano.getCantidadActual(), cantidad);
            cantidad -= min; // Reduce la cantidad a entregar por la capacidad disponible en el cofre
            if (cantidad == 0) {
                cola.add(cofreMasCercano.getRobopuertoMasCercano());
                return true; // Si la cantidad es cero, se considera que la solicitud es exitosa
            } else {
                bateria -= cofreMasCercano.getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo();
                return cofreMasCercano.calcularCaminoRecursivo(cola, item, cantidad, visitado, bateria); // Llama recursivamente al siguiente cofre
            } 
        } else {
            // No llego al cofre + robopuerto más cercano al cofre, pregunto si llego solo al robopuerto
            if (bateria >= cofreMasCercano.getRobopuertoMasCercano().getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo()) {
                cola.add(cofreMasCercano.getRobopuertoMasCercano());
                bateria -= cofreMasCercano.getRobopuertoMasCercano().getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo();
                return cofreMasCercano.getRobopuertoMasCercano().calcularCaminoRecursivo(cola, item, cantidad, visitado, bateria); // Llama recursivamente al robopuerto del cofre más cercano
            } else {
                // No llego al robopuerto más cercano al cofre, busco otro robopuerto cercano al cofre al que llegue
                Robopuerto robopuertoMasCercano = this.redLogistica.getRobopuertos().get(0);
                for (Robopuerto r : this.redLogistica.getRobopuertos()) {
                    if (r.getDistancia(cofreMasCercano.getX(), cofreMasCercano.getY()) < robopuertoMasCercano.getDistancia(cofreMasCercano.getX(), cofreMasCercano.getY())
                        && bateria >= r.getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo()) {
                        robopuertoMasCercano = r;
                    }
                }
                if (bateria < robopuertoMasCercano.getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo())
                    return false; // No llego a ningún robopuerto
                cola.add(robopuertoMasCercano);
                bateria -= robopuertoMasCercano.getDistancia(this.x, this.y) * SingletonMapa.getInstancia().getFactorConsumo();
                return robopuertoMasCercano.calcularCaminoRecursivo(cola, item, cantidad, visitado, bateria);
            }
        }
    }
}

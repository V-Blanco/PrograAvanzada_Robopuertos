package Clases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

public class SingletonMapa {
	private static SingletonMapa instancia;
	private List<Robopuerto> robopuertos;
	private List<Cofre> cofresSueltos;
    private Set<RedLogistica> redesLogisticas;
	private int cantMaxTransporte = 10, capacidad = 100;
    private double bateria = 100.0, factorConsumo, radioCobertura = 50;

    // Constructor privado para evitar instanciación externa
    private SingletonMapa() {
		// Inicialización de la lista de Robopuertos
		this.robopuertos = new ArrayList<>();
		this.cofresSueltos = new ArrayList<>();
        this.redesLogisticas = new HashSet<>();
        this.factorConsumo = this.bateria / (this.radioCobertura * 2);
	}

    private SingletonMapa(int cantMaxTransporte, double bateria, double factorConsumo, double radioCobertura, int capacidad) {
		// Inicialización de la lista de Robopuertos
		this.robopuertos = new ArrayList<>();
		this.cofresSueltos = new ArrayList<>();
        this.redesLogisticas = new HashSet<>();
        this.cantMaxTransporte = cantMaxTransporte;
        this.bateria = bateria; 
        this.radioCobertura = radioCobertura;
        this.capacidad = capacidad;
        if (factorConsumo > (this.bateria / (this.radioCobertura * 2))) 
            this.factorConsumo = this.bateria / (this.radioCobertura * 2);
        else
            this.factorConsumo = factorConsumo;
	}

    // Método estático para obtener la instancia única
    public static SingletonMapa getInstancia() {
        if (instancia == null) {
            instancia = new SingletonMapa();
        }
		return instancia;
    }

    public static SingletonMapa getInstancia(int cantMaxTransporte, double bateria, double factorConsumo, double radioCobertura, int capacidad) {
        if (instancia == null) {
            instancia = new SingletonMapa(cantMaxTransporte, bateria, factorConsumo, radioCobertura, capacidad);
        }
		return instancia;
    }

	public int getCantMaxTransporte() {
		return cantMaxTransporte;
	}

    public int getCapacidad() {
        return capacidad;
    }

    public List<Robopuerto> getRobopuertos() {
        return robopuertos;
    }

	public double getBateria() {
		return bateria;
	}

	public double getFactorConsumo() {
		return factorConsumo;
	}

    public double getRadioCobertura() {
        return radioCobertura;
    }

	public RedLogistica agregarRobopuerto(Robopuerto robopuerto) {
        if (robopuerto == null) {
            throw new NullPointerException("El robopuerto no puede ser nulo");
        }

        RedLogistica redLogistica = null;
        Set<RedLogistica> uniqueRedLogisticas = new HashSet<>();

        for (Robopuerto r : this.robopuertos) {
            if (r == null) {
                throw new NullPointerException("Alguno de los robopuertos en la lista es nulo");
            }

            double distance = Math.sqrt(Math.pow(r.getX() - robopuerto.getX(), 2) + Math.pow(r.getY() - robopuerto.getY(), 2));
            if (distance < (r.getRadioCobertura() + robopuerto.getRadioCobertura())) {
                RedLogistica existingRedLogistica = r.getRedLogistica();
                if (existingRedLogistica != null) {
                    uniqueRedLogisticas.add(existingRedLogistica);
                }
            }
        }
        if (!this.robopuertos.contains(robopuerto)) {
            this.robopuertos.add(robopuerto);
        }

        if (uniqueRedLogisticas.isEmpty()) {
            redLogistica = new RedLogistica();
            this.redesLogisticas.add(redLogistica);
        } else if (uniqueRedLogisticas.size() == 1) {
            redLogistica = uniqueRedLogisticas.iterator().next();
        } else {
            redLogistica = new RedLogistica();
            for (RedLogistica r : uniqueRedLogisticas) {
                redLogistica.agregarRobopuertos(r.getRobopuertos());
                redLogistica.insertarOrdenado(redLogistica.getSolicitadores(), r.getSolicitadores());
                redLogistica.insertarOrdenado(redLogistica.getProveedores(), r.getProveedores());
                redLogistica.getSolicitudesCofres().addAll(r.getSolicitudesCofres());
                for (Robopuerto puerto : r.getRobopuertos()) {
                    puerto.setRedLogistica(redLogistica);
                }
                for (Cofre solicitador : r.getSolicitadores()) {
                    solicitador.setRedLogistica(redLogistica);
                }
                for (Cofre proveedor : r.getProveedores()) {
                    proveedor.setRedLogistica(redLogistica);
                }
                this.redesLogisticas.remove(r);
            }
            this.redesLogisticas.add(redLogistica);
        }

        if (!redLogistica.getRobopuertos().contains(robopuerto)) {
            redLogistica.agregarRobopuertos(robopuerto);
        }
        robopuerto.setRedLogistica(redLogistica);

        actualizarCofres(redLogistica.getSolicitadores(), robopuerto);
        actualizarCofres(redLogistica.getProveedores(), robopuerto);

        if (!this.cofresSueltos.isEmpty()) {
            Iterator<Cofre> iterator = this.cofresSueltos.iterator();
            while (iterator.hasNext()) {
                Cofre c = iterator.next();
                if (c != null && c.getDistancia(robopuerto.getX(), robopuerto.getY()) < robopuerto.getRadioCobertura()) {
                    c.setRedLogistica(redLogistica);
                    if (c instanceof InterfaceSolicitador) {
                        actualizarCofres(redLogistica.getSolicitadores(), c);
                        actualizarCofres(redLogistica.getProveedores(), c);
                        if (!redLogistica.getSolicitadores().contains(c))
                        redLogistica.insertarOrdenado(redLogistica.getSolicitadores(), c);
                    }
                    if (c instanceof InterfaceProveedor) {
                        actualizarCofres(redLogistica.getSolicitadores(), c);
                        actualizarCofres(redLogistica.getProveedores(), c);
                        if(!redLogistica.getProveedores().contains(c))
                        redLogistica.insertarOrdenado(redLogistica.getProveedores(), c);
                    }
                    iterator.remove();
                }
            }
        }

        return redLogistica;
    }

    private void actualizarCofres(List<Cofre> cofres, Robopuerto robopuerto) {
        for (Cofre c : cofres) {
            if (c != null && (c.getRobopuertoMasCercano() == null || c.getDistancia(robopuerto.getX(), robopuerto.getY()) < c.getDistancia(c.getRobopuertoMasCercano().getX(), c.getRobopuertoMasCercano().getY()))) {
                c.setRobopuertoMasCercano(robopuerto);
            }
        }
    }

    private void actualizarCofres(List<Cofre> cofres, Cofre nuevoCofre) {
        for (Cofre cof : cofres) {
            if (cof != null) {
                cof.insertarOrdenado(cof.getCofresSolicitadoresCercanos(), nuevoCofre);
                cof.insertarOrdenado(cof.getCofresProveedoresCercanos(), nuevoCofre);
            }
        }
    }

	public void setCofresSueltos(Cofre cofre) {
    if (cofre != null) {
        if (this.cofresSueltos == null) {
            this.cofresSueltos = new java.util.ArrayList<>();
        }
        this.cofresSueltos.add(cofre);
    }
}

	public RedLogistica agregarCofre(Cofre cofre) {
		RedLogistica redLogistica = null;
		if (cofre == null) {
			return redLogistica;
		}
		
		for (Robopuerto r : this.robopuertos) {
			// Verifica si el cofre se encuentra dentro del radio de cobertura de algún robopuerto (Red logística)
			if(cofre.getDistancia(r.getX(), r.getY()) < r.getRadioCobertura()) {
				redLogistica = r.getRedLogistica();

                // Se le agregan todos los cofres de la red logística
                cofre.insertarOrdenado(cofre.getCofresSolicitadoresCercanos(), redLogistica.getSolicitadores());
                cofre.insertarOrdenado(cofre.getCofresProveedoresCercanos(), redLogistica.getProveedores());

				// Si es Solicitador, se agrega a la lista de Solicitadores de la red logística y la lista de cofres solicitadores cercanos del cofre
				if (cofre instanceof InterfaceSolicitador) {
					for (Cofre c : redLogistica.getSolicitadores()) {
						c.insertarOrdenado(c.getCofresSolicitadoresCercanos(), cofre);
                        System.out.println("Se agrego el cofre Solicitador x" + c.getX() + " y" + c.getY());
					}
					for (Cofre c : redLogistica.getProveedores()) {
						c.insertarOrdenado(c.getCofresSolicitadoresCercanos(), cofre);
					}
					if(!redLogistica.getSolicitadores().contains(cofre))
					    redLogistica.insertarOrdenado(redLogistica.getSolicitadores(), cofre);
				} 
				// Si es Proveedor, se agrega a la lista de Proveedores de la red logística y la lista de cofres proveedores cercanos del cofre
				if (cofre instanceof InterfaceProveedor) {
					for (Cofre c : redLogistica.getSolicitadores()) {
						c.insertarOrdenado(c.getCofresProveedoresCercanos(), cofre);
					}
					for (Cofre c : redLogistica.getProveedores()) {
						c.insertarOrdenado(c.getCofresProveedoresCercanos(), cofre);
					}
                    if(!redLogistica.getProveedores().contains(cofre))
					    redLogistica.insertarOrdenado(redLogistica.getProveedores(), cofre);
				}
				Robopuerto minimo = r;
				for (Robopuerto puerto : redLogistica.getRobopuertos()) {
					if (cofre.getDistancia(puerto.getX(), puerto.getY()) < cofre.getDistancia(minimo.getX(), minimo.getY())) {
						minimo = puerto; // Encuentra el robopuerto más cercano al cofre
					}
				}
				cofre.setRobopuertoMasCercano(minimo);
				return redLogistica;
			}
		}
		this.cofresSueltos.add(cofre); // Agregamos solo los cofres que no se conectan a ninguna red logística
		return redLogistica;
	}

    public boolean agregarSolicitud(Solicitud solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula.");
        }

        for (RedLogistica r : this.redesLogisticas) {
            if (r.getSolicitadores().contains(solicitud.getCofre())) {
                Cofre cofre = r.getSolicitadores().get(r.getSolicitadores().indexOf(solicitud.getCofre())); // Busca el cofre correspondiente en la red logistica
                solicitud.setCofre(cofre); // Cambiar el cofre por el existente en la red logística
                r.getSolicitudesCofres().add(solicitud);
                return true;
            }
        }
        System.out.println("Cofre no encontrado en ninguna red logística. No se pudo atender la solicitud.");
        return false;
    }

    public void simular() {
        for (RedLogistica r : this.redesLogisticas) {
            r.simular();
        }
    }
    
}

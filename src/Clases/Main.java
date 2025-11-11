package Clases;

import com.google.gson.*;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) {
       // Crear Robopuertos
//        Robopuerto robopuerto1 = new Robopuerto(30, 30, 10);
//        Robopuerto robopuerto2 = new Robopuerto(70, 70, 10);
//        Robopuerto robopuerto3 = new Robopuerto(100, 130, 10);
//        Robopuerto robopuerto4 = new Robopuerto(90, 100, 10);
//
//        // Crear Items
//        Item cobre = new Item("cobre");
//        Item hierro = new Item("hierro");
//        Item madera = new Item("madera");
//
//        // Crear mapa de items
//        Map<Item, Integer> items = new HashMap<>();
//        items.put(cobre, 5);
//        items.put(hierro, 15);
//        
//        Map<Item, Integer> itemsss = new HashMap<>();
//        itemsss.put(cobre, 50);
//        itemsss.put(hierro, 45);
//
//        // Crear Cofres
//        CofreDeSolicitud cofre1 = new CofreDeSolicitud(0, 0, itemsss);
//        CofreDeProvisiónActiva cofre4 = new CofreDeProvisiónActiva(110, 65, items);
//        CofreDeProvisiónActiva cofre2 = new CofreDeProvisiónActiva(100, 55, items);
//        System.out.println(cofre2);
//        CofreDeProvisiónActiva cofre5 = new CofreDeProvisiónActiva(1000, 75, items);
//        CofreDeProvisiónPasiva cofre3 = new CofreDeProvisiónPasiva(100, 99, items);
//        CofreIntermedioBufer cofre6 = new CofreIntermedioBufer(100, 85, items);
//        CofreDeAlmacenamiento cofre7 = new CofreDeAlmacenamiento(149, 130, items);
//
//        //cofre1.mostrarCofres(cofre1.getCofresProveedoresCercanos());
//
//        cofre1.solicitarItem(cobre, 5);
//        System.out.println("Hola");
//        //cofre4.proveerItem(hierro, 5);
//        System.out.println(cofre1);
////        System.out.println(cofre4);
    	
    	try {
    		String jsonContent = Files.readString(Path.of("JSONPrueba2.json"));
    		
    		// Parsear el contenido como JSON
            JsonElement root = JsonParser.parseString(jsonContent);
    		JsonObject obj = root.getAsJsonObject();

            // Variables principales
            double bateria = obj.get("bateria").getAsDouble();
            int cantMaxTransporte = obj.get("cantMaxTransporte").getAsInt();
            double factorConsumo = obj.get("factorConsumo").getAsDouble();
            double radioCobertura = obj.get("radioCobertura").getAsDouble();
            int capacidad = obj.get("capacidad").getAsInt();
            
            SingletonMapa singleton = SingletonMapa.getInstancia(cantMaxTransporte, bateria, factorConsumo, radioCobertura, capacidad);

            // Cargo robopuertos
            JsonArray jsonRobopuertos = obj.getAsJsonArray("robopuertos");
            for (JsonElement elem : jsonRobopuertos) {
                JsonObject r = elem.getAsJsonObject();
                double x = r.get("x").getAsDouble();
                double y = r.get("y").getAsDouble();
                int contadorRobots = r.get("contadorRobots").getAsInt();
                Robopuerto robopuerto = new Robopuerto(x, y, contadorRobots);
            }

            // Cargo cofres
            JsonArray jsonCofres = obj.getAsJsonArray("cofres");
            for (JsonElement elem : jsonCofres) {
                JsonObject c = elem.getAsJsonObject();
                String tipo = c.get("tipo").getAsString();
                double x = c.get("x").getAsDouble();
                double y = c.get("y").getAsDouble();

                Map<String, Integer> items = new HashMap<>();
                JsonArray jsonItems = c.getAsJsonArray("items");
                for (JsonElement itemElem : jsonItems) {
                    JsonObject itemObj = itemElem.getAsJsonObject();
                    String nombre = itemObj.get("nombre").getAsString();
                    int cant = itemObj.get("cant").getAsInt();
                    items.put(nombre, cant);
                }

                Cofre cofre = null;
                switch (tipo) {
                    case "CofreDeSolicitud":
                        cofre = new CofreDeSolicitud(x, y, items);
                        break;
                    case "CofreDeAlmacenamiento":
                        cofre = new CofreDeAlmacenamiento(x, y, items);
                        break;
                    case "CofreDeProvisiónActiva":
                        cofre = new CofreDeProvisiónActiva(x, y, items);
                        break;
                    case "CofreDeProvisiónPasiva":
                        cofre = new CofreDeProvisiónPasiva(x, y, items);
                        break;
                    case "CofreIntermedioBufer":
                        cofre = new CofreIntermedioBufer(x, y, items);
                        break;
                    default:
                        System.out.println("Tipo de cofre desconocido: " + tipo);
                        break;
                }
            }

            // Cargo solicitudes
            JsonArray jsonSolicitudes = obj.getAsJsonArray("solicitudes");
            for (JsonElement elem : jsonSolicitudes) {
                JsonObject s = elem.getAsJsonObject();
                String tipo = s.get("tipo").getAsString();
                double x = s.get("x").getAsDouble();
                double y = s.get("y").getAsDouble();

                JsonArray jsonItems = s.getAsJsonArray("items");
                for (JsonElement itemElem : jsonItems) {
                    JsonObject itemObj = itemElem.getAsJsonObject();
                    String nombre = itemObj.get("nombre").getAsString();
                    int cant = itemObj.get("cant").getAsInt();
                    Cofre cofre = null;
                    switch (tipo) {
                        case "CofreDeSolicitud":
                            cofre = new CofreDeSolicitud(x, y, nombre, cant);
                            break;
                        case "CofreDeAlmacenamiento":
                            cofre = new CofreDeAlmacenamiento(x, y, nombre, cant);
                            break;
                        case "CofreDeProvisiónActiva":
                            cofre = new CofreDeProvisiónActiva(x, y, nombre, cant);
                            break;
                        case "CofreDeProvisiónPasiva":
                            cofre = new CofreDeProvisiónPasiva(x, y, nombre, cant);
                            break;
                        case "CofreIntermedioBufer":
                            cofre = new CofreIntermedioBufer(x, y, nombre, cant);
                            break;
                        default:
                            System.out.println("Tipo de cofre desconocido: " + tipo);
                            break;
                    }
                }
            }

            System.out.println("Se leyo el archivo");
            singleton.simular();
            System.out.println("Se leyo el archivo!!!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

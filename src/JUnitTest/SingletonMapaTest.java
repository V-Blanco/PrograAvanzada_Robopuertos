package JUnitTest;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Clases.Cofre;
import Clases.CofreDeProvisiónPasiva;
import Clases.CofreDeSolicitud;
import Clases.RedLogistica;
import Clases.Robopuerto;
import Clases.SingletonMapa;

class SingletonMapaTest {
	
	 private SingletonMapa mapa;

		@BeforeEach
		public void setup() {
			mapa = SingletonMapa.getInstancia();
			assertNotNull(mapa, "mapa should not be null");
		}

	    @Test
	    public void testAgregarRobopuertoNull() {
	        RedLogistica redLogistica = mapa.agregarRobopuerto(null);
	        assertNull(redLogistica);
	    }

	    @Test
		public void testAgregarRobopuertoNullPointer() {
			Robopuerto robopuerto = null;
			assertThrows(NullPointerException.class, () -> {
				mapa.agregarRobopuerto(robopuerto);
			}, "Should have thrown a NullPointerException");
		}

	    @Test
	    public void testAgregarRobopuertoExistingNetwork() {
	        Robopuerto robopuerto1 = new Robopuerto(0, 0, 0);
	        Robopuerto robopuerto2 = new Robopuerto(10, 10, 0);
	        mapa.agregarRobopuerto(robopuerto1);
	        RedLogistica redLogistica = mapa.agregarRobopuerto(robopuerto2);
	        assertNotNull(redLogistica, "RedLogistica should not be null");
	        assertNotNull(redLogistica.getRobopuertos(), "Robopuertos list should not be null");
			System.out.println(redLogistica.getRobopuertos());
	        assertEquals(2, redLogistica.getRobopuertos().size(), "Should have 2 Robopuertos");
			assertEquals(2, mapa.getRobopuertos().size(), "Should have 2 Robopuertos");
	    }

	    @Test
	    public void testAgregarRobopuertoMultipleExistingNetworks() {
	        Robopuerto robopuerto1 = new Robopuerto(0, 0, 0);
	        Robopuerto robopuerto2 = new Robopuerto(10, 10, 0);
	        Robopuerto robopuerto3 = new Robopuerto(20, 20, 0);
	        mapa.agregarRobopuerto(robopuerto1);
	        mapa.agregarRobopuerto(robopuerto2);
	        RedLogistica redLogistica = mapa.agregarRobopuerto(robopuerto3);
	        assertNotNull(redLogistica, "RedLogistica should not be null");
	        assertNotNull(redLogistica.getRobopuertos(), "Robopuertos list should not be null");
	        assertEquals(3, redLogistica.getRobopuertos().size(), "Should have 3 Robopuertos");
	    }

	    @Test
	    public void testAgregarRobopuertoNoExistingNetwork() {
	        Robopuerto robopuerto = new Robopuerto(100, 100, 0);
	        try {
	            RedLogistica redLogistica = mapa.agregarRobopuerto(robopuerto);
	            assertNotNull(redLogistica, "RedLogistica should not be null");
	            assertNotNull(redLogistica.getRobopuertos(), "Robopuertos list should not be null");
	            assertEquals(1, redLogistica.getRobopuertos().size(), "Should have 1 Robopuerto");
	        } catch (Exception e) {
	            fail("Unexpected exception: " + e.getMessage());
	        }
	    }

	    @Test
	    public void testAgregarRobopuertoNewNetwork() {
	        try {
	            Robopuerto robopuerto = new Robopuerto(100, 100, 0);
	            assertNotNull(robopuerto, "Robopuerto should not be null");

	            RedLogistica redLogistica = mapa.agregarRobopuerto(robopuerto);
	            assertNotNull(redLogistica, "RedLogistica should not be null");
	            assertNotNull(redLogistica.getRobopuertos(), "Robopuertos list should not be null");

	            assertEquals(1, redLogistica.getRobopuertos().size(), "Should have 1 Robopuerto");
	        } catch (Exception e) {
	            fail("Unexpected exception: " + e.getMessage());
	        }
	    }

	    @Test
	    public void testAgregarRobopuertoLooseCofres() {
	        Robopuerto robopuerto = new Robopuerto(100, 100, 0);
	        Cofre cofre = new CofreDeSolicitud(105, 105,null);
	        
	        mapa.setCofresSueltos(cofre);
	        
	        RedLogistica redLogistica = mapa.agregarRobopuerto(robopuerto);
	        
	        assertNotNull(redLogistica, "RedLogistica should not be null");
	        assertNotNull(redLogistica.getRobopuertos(), "Robopuertos list should not be null");
	        assertNotNull(redLogistica.getSolicitadores(), "Solicitadores list should not be null");
	        System.out.println(redLogistica.getSolicitadores());
	        assertEquals(1, redLogistica.getSolicitadores().size(), "Should have 1 Solicitador");
	        assertEquals(1, redLogistica.getRobopuertos().size(), "Should have 1 Robopuerto");
	    }

	    @Test
	    public void testAgregarRobopuertoSolicitadoresProveedores() {
	        Robopuerto robopuerto = new Robopuerto(100, 100, 0);
	        Cofre solicitador = new CofreDeSolicitud(105, 105,null);
	        Cofre proveedor = new CofreDeProvisiónPasiva(110, 110,null);
	        mapa.setCofresSueltos(proveedor);
	        mapa.setCofresSueltos(solicitador);
	        RedLogistica redLogistica = mapa.agregarRobopuerto(robopuerto);
	        assertAll(
	            () -> assertNotNull(redLogistica),
	            () -> assertEquals(1, redLogistica.getRobopuertos().size()),
	            () -> assertEquals(1, redLogistica.getSolicitadores().size()),
	            () -> assertEquals(1, redLogistica.getProveedores().size())
	        );
	    }

	
}

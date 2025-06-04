package v3;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.opencsv.CSVReader;

public class AjustePotenciaFV3 {

	static class Electrolizador {
		String modelo;
		double potenciaMaxKW;
		double consumoKWhPorKgH2;
		double precioEly;
		double precioWTP;
		double precioPurificacion;
		double mejorCosto;

		public Electrolizador(String modelo, double potenciaMaxKW, double consumoKWhPorKgH2, double precioEly,
				double precioWTP, double precioPurificacion) {
			this.modelo = modelo;
			this.potenciaMaxKW = potenciaMaxKW;
			this.consumoKWhPorKgH2 = consumoKWhPorKgH2;
			this.precioEly = precioEly;
			this.precioWTP = precioWTP;
			this.precioPurificacion = precioPurificacion;
			this.mejorCosto = Double.MAX_VALUE;
		}

		public double getMejorCosto() {
			return this.mejorCosto;
		}

		public void setMejorCosto(double mejorCosto) {
			this.mejorCosto = mejorCosto;
		}
	}
	
	
	/**
	 * Maxima capacidad de la bateria
	 */
	static float capacidadMax = 10000;
	/**
	 * rentabilidad
	 */
	static float rentabilidad =.5f;
	
	/**
	 * velocidad de carga en MW/h
	 */
	static double velocidadDeCarga = .4;
	/**
	 * precio que supone un kg de H2
	 */
	static double precioH2 = 3;
	/**
	 *  Superficie que cubre un kWp en m2
	 */
	static double superficiekWp = 5;
	// ## Listas para guardar las variables
	/**
	 * Lista donde se guarda la produccion fotovoltaica en kW
	 */
	static ArrayList<Double> listaE;
	/**
	 * Lista donde se guarda el precio de la red €/MW
	 */
	static ArrayList<Double> listaD;

	public static void main(String[] args) throws IOException {
		double K4 = 54.0; // kWh/kg H2{
		String archivoCSV = "valores_D_E.csv";
		listaD = new ArrayList<>();
		listaE = new ArrayList<>();

		try (CSVReader reader = new CSVReader(new FileReader(archivoCSV))) {
			String[] nextLine;
			boolean esPrimeraLinea = true;
			while ((nextLine = reader.readNext()) != null) {
				if (esPrimeraLinea) {
					esPrimeraLinea = false;
					continue;
				}
				// System.out.println(nextLine[1]);
				listaD.add(Double.parseDouble(nextLine[0]));
				// System.out.println(nextLine[0]);
				listaE.add(Double.parseDouble(nextLine[1]));
			}
		}

		// Lista de electrolizadores
		List<Electrolizador> electrolizadores = Arrays.asList(
				new Electrolizador("EL200N", 1340, K4, 2038921, 42664, 194217),
				new Electrolizador("EL400N", 2700, K4, 3162107, 126579, 311443),
				new Electrolizador("EL600N", 4000, K4, 4404196, 123207, 270502),
				new Electrolizador("EL800N", 5400, K4, 5558532, 164277, 392032),
				new Electrolizador("EL1000N", 6700, K4, 6643089, 205346, 470438),
				new Electrolizador("EL2000N", 14000, K4, 12047029, 382152, 613976));

		Scanner sc = new Scanner(System.in);
		// Pedir la superfice maxima aprovechable		
		System.out.print("Introduce la superficie maxima(m2):");
		double superficieMax = Double.MAX_VALUE;
		try {
			superficieMax = Double.parseDouble(sc.nextLine());
		} catch (Exception e) {

		}
		// Pedir la cantidad deseada de H2
		System.out.print("Introduce la cantidad deseada de H2 en kg: ");
		double kgH2Objetivo = sc.nextDouble();

		long inicio = System.currentTimeMillis();

		System.out.println("Con todo");
		HashMap<String, Object> conTodo = ConTodo3.calcularConTodo(electrolizadores, kgH2Objetivo, superficieMax);
		escribir(conTodo);

//		System.out.println("sin Bateria");
//		HashMap<String, Object> sinBateria = SinBateria.calcularSinBateria(electrolizadores, kgH2Objetivo,
//				superficieMax);
//		escribir(sinBateria);
//
//		System.out.println("Sin red");
//		HashMap<String, Object> sinRed = SinRed.calcularSinRed(electrolizadores, kgH2Objetivo, superficieMax);
//		escribir(sinRed);
//
//		System.out.println("Sin baterias y sin red");
//		HashMap<String, Object> sinBateriasSinRed = SinBateriasSinRed.calcularSinBateriaSinRed(electrolizadores,
//				kgH2Objetivo, superficieMax);
//		escribir(sinBateriasSinRed);

//		System.out.println("Sin Fotovoltaica");

		long fin = System.currentTimeMillis();
		System.out.println("Tiempo de ejecución: " + (fin - inicio) + " milisegundos");

		sc.close();
	}
	/**
	 * Calula el precio de la intalacion fotovoltaica unicamente
	 * @param potenciaInstalada
	 * @return 
	 */
	static float precioInstalacion(double potenciaInstalada) {
		String archivoCSV = "precios_instalacion_fotovoltaica.csv";
		ArrayList<Integer> listaCapacidad = new ArrayList<>();
		HashMap<Integer, Integer> listaCosteMW = new HashMap<Integer, Integer>();

		try (CSVReader reader = new CSVReader(new FileReader(archivoCSV))) {
			String[] nextLine;
			boolean esPrimeraLinea = true;
			while ((nextLine = reader.readNext()) != null) {
				if (esPrimeraLinea) {
					esPrimeraLinea = false;
					continue;
				}
				// System.out.println(nextLine[1]);
				listaCapacidad.add(Integer.parseInt(nextLine[0]));
				// System.out.println(nextLine[0]);
				listaCosteMW.put(Integer.parseInt(nextLine[0]), Integer.parseInt(nextLine[1]));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		float res = listaCosteMW.get(1);
		for (int i : listaCosteMW.keySet()) {
			res = i < potenciaInstalada / 1000 ? listaCosteMW.get(i) * (float) (potenciaInstalada / 1000) : res;
		}

		return res;
	}

	static void escribir(HashMap<String, Object> hs) {
		if (hs.get("mejorElectrolizador") != null) {
			int mejorHoras = (int) hs.get("mejorHoras");
			double mejorBateria = (double) hs.get("mejorBateria");
			double mejorProduccion = (double) hs.get("mejorProduccion");
			double mejorCosteTotal = (double) hs.get("mejorCosteTotal");
			try {
				System.out.println("\n- Electrolizador más rentable :");
				System.out.println("- Modelo: " + hs.get("mejorElectrolizador"));
				System.out.printf("- Superficie total necesaria:  %.2f m2 %n", hs.get("superficie"));
				System.out.println("- Horas necesarias: " + mejorHoras);
				System.out.printf("- H2 producido: %.2f kg%n", mejorProduccion);

				System.out.printf("- Maximo almacenaje de bateria: %.2f  %n", BigDecimal.valueOf(mejorBateria));
			} catch (Exception e) {
			}
			try {
				System.out.printf("- Potencia instalada necesaria:  %.2f kW %n", hs.get("mejorPotenciaInstalada"));
				System.out.printf("- Coste total (equipo + potencia instalada + coste red 20 años) : %.2f Euros %n", mejorCosteTotal);

			} catch (Exception e) {
			}
		} else {
			System.err.println("X No se pudo alcanzar la producción deseada en menos de un año con todo.");
		}
	}

}

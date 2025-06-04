package v3;

import java.util.HashMap;
import java.util.List;

public class ConTodo3 extends AjustePotenciaFV3 {

	static HashMap<String, Object> calcularConTodo(List<Electrolizador> electrolizadores, double kgH2Objetivo,
			double superficieMax) {
		// Variables para guardar el mejor resultado final
		Electrolizador mejorElectrolizador = null;
		double mejorCosteTotal = Double.MAX_VALUE;
		double bateriausada = 0;
		double mejorPotenciaInstalada = 0;
		int mejorHoras = 0;
		double mejorProduccion = 0;
		double mejorBateria = 0;

		// Recorremos TODOS los electrolizadores
		for (Electrolizador e : electrolizadores) {
			double potenciaInstalada = 100; // reinicia para cada electrolizador kW
			double incremento = Math.max(Math.min(kgH2Objetivo / 5000, 1000d), 10d); // nunca menos de 10 kW 100000
																						// 4.336.046

			boolean encontrado = false;
			while (!encontrado) {
				// Precio electricidad red
				double gastoRed = 0;

				double sumaK = 0.0;
				int horas = 0;
				double bateria = 0;
				double bateriaMax = 0;

				for (int i = 0; i < listaE.size(); i++) {
//					if (sumaK >= kgH2Objetivo)
//						break;

					double F = listaE.get(i) * potenciaInstalada;
					double potenciaDisponible = Math.min(F, e.potenciaMaxKW);
					double bateriaCargada = 0;
					if (F >= e.potenciaMaxKW) {
						bateriaCargada = Math.min(bateriaCargada + (-F + e.potenciaMaxKW), capacidadMax);
						bateriaMax = Math.max(bateriaMax, bateriaCargada);
					}

					if (listaD.get(i) * e.consumoKWhPorKgH2 < rentabilidad * precioH2) {

						bateriaCargada += velocidadDeCarga * 1000;
						potenciaDisponible = e.potenciaMaxKW;
						gastoRed += (e.potenciaMaxKW - F) * listaD.get(i) + velocidadDeCarga * 1000;

					} else {
						potenciaDisponible = Math.min(e.potenciaMaxKW, bateria + potenciaDisponible);
						bateriaCargada -= (e.potenciaMaxKW - potenciaDisponible);
						bateriausada -= bateriaCargada < 0 ? bateriaCargada : 0;// 6152133
					}
					bateria = bateria + bateriaCargada;
					if (bateria < 0)
						bateria = 0;
					bateriaMax = Math.max(bateria, bateriaMax);
					horas++;
					double kgH2Hora = potenciaDisponible / e.consumoKWhPorKgH2;
					sumaK += kgH2Hora;

				}

				if (sumaK >= kgH2Objetivo) {
					double costeEquipo = e.precioEly + e.precioWTP + e.precioPurificacion;

					double costePotenciaInstalada = precioInstalacion(potenciaInstalada);

					double costeTotal = costeEquipo + costePotenciaInstalada + gastoRed * 25;
					if (e.mejorCosto > costeTotal) {
						System.out.println("Se ha llegado con " + e.modelo);
						e.setMejorCosto(costeTotal);
						// System.out.println("gafas");
						// Si este es el mejor coste encontrado, lo guardamos
						if (costeTotal < mejorCosteTotal) {
							mejorCosteTotal = costeTotal;
							mejorElectrolizador = e;
							mejorPotenciaInstalada = potenciaInstalada;
							mejorHoras = horas;
							mejorProduccion = sumaK;
							mejorBateria = bateriaMax;
						}
					} else {
						// Ya encontró para este electrolizador
						encontrado = true;
						// System.out.println("lunettes");
					}
				}
				potenciaInstalada += incremento;

				if (potenciaInstalada > Math.min(e.potenciaMaxKW * 10, superficieMax / superficiekWp)) {
					System.out.println("Electrolizador: " + e.modelo + " no es valido");
					break;
				}

			}

		}
		HashMap<String, Object> hs = new HashMap<String, Object>();
		if (mejorElectrolizador != null) {
			System.out.println("cantidad de energia de la bateria empleada : " + bateriausada);
			hs.put("mejorElectrolizador", mejorElectrolizador.modelo);
			hs.put("mejorPotenciaInstalada", mejorPotenciaInstalada);
			hs.put("superficie", (mejorPotenciaInstalada * superficiekWp));
			hs.put("mejorHoras", mejorHoras);
			hs.put("mejorBateria", mejorBateria);
			hs.put("mejorProduccion", mejorProduccion);
			hs.put("mejorCosteTotal", mejorCosteTotal);
		}
		return hs;
	}

}

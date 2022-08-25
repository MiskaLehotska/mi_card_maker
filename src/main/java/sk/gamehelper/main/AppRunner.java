package sk.gamehelper.main;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sk.gamehelper.config.AppConfig;

public class AppRunner {

	public static void main(String[] args) {

		/**
		 * Tuto to cele zacina, vytvorenim springoveho kontextu pre aplikaciu.
		 * Trieda sa vola AnnotationConfigApplicationContext preto, pretoze chceme pouzivat anotacie namiesto xml suborov
		 * a spring teda bude hladat pomocou reflexie na nasich triedach specificke anotacie, ktore mu umoznia vsetko
		 * nastavit a nainicializovat objekty, ktore potrebujeme v nasom kontexte drzat.
		 * 
		 * Kontext si tu predstav ze je nejaky ramec alebo kontajner v ktorom sa drzia vsetky objkekty a nastavenia, ktore
		 * spring inicializoval a pokym mame k dispozicii tento kontext, tak si z neho mozeme vyberat tieto objekty a vyuzit
		 * benefity, ktore tento kontext ponuka ako napriklad resource loader (automaticky zhromazduje nastavenia z properties
		 * suborov a premennych prostredia na jedno miesto), manazuje zivotny cyklus bean objektov a pod.
		 */

		// do konstruktora mozeme zadat konfiguracnu triedu, ktorou ma spring zacat a od kial bude pokracovat s inicializaciou
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {

		}
	}
}

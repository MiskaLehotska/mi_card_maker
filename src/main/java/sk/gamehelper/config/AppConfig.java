package sk.gamehelper.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.TransactionManager;

/**
 * Tato trieda je teda konfiguracna a spring zacina od tialto.
 * 
 * @ComponentScan anotacia hovori o tom, ze kde a ako ma hladat kanditatov na takzvanu injection,
 * co je v preklade vytvorenie objektu na miesto kde ten objekt potrebujeme.
 * (vysledkom dependency injection je to, ze nemusis pisat pri vytvarani globalnych objektov ziadne
 *  "new" a tym padom sa nestane, ze vytvoris vela zbytocnych objektov, ked ti staci napr. pre celu
 *  aplikaciu iba jeden)
 * Argument basePackages hovori springu, ze kde chceme hladat komponenty/beany/kanditatov na injection
 * a on potom inicializuje vsetky objekty a nastavenia ktore maju springove anotacie na sebe v balicku
 * sk.gamehelper a jeho vsetkych podbalickoch.
 * 
 * @PropertySource annotacia urcuje kde sa nachadzaju konfiguracne subory, ktore by sme chceli nacitat
 * do aplikacie.. toto mozu byt vseliake properties subory na connection k databaze alebo ine nastavenia.
 * Spring ich za nas automaticky nacita do jedneho zdielaneho uloziska, z ktoreho si potom tuto property
 * mozeme vypytat podla jeho kluca v ramci anotacie @Value - ktora sluzi presne na toto, no ak sa hodnota
 * nedokaze ziskat, tak anotacia @Value vrati nazov kluca, ktory sme tam zadali..
 * 
 * Priklad:
 * 	
 * @Value("${playerName}") // ak spring nenajde pod klucom playerName ziadnu hodnotu, ktoru by injectol
 * private String name;    // tak do tejto premennej sa injectne kluc a teda hodnota name bude "playerName"
 * 
 */
@ComponentScan(basePackages = "sk.gamehelper")
@PropertySource("classpath:/db/connection.properties")
public class AppConfig {

	/**
	 * Tato metoda funguje nasledovne:
	 * 
	 * @Bean oznacuje navratovy typ tejto metody ako kandidata na injectnutie..co znamena, ze
	 * ked si z kontextu niekedy vypytam na priamo alebo pomocou anotacie @Autowire objekt tohto
	 * typu, tak sa spring pozrie, ci ho ma v kontexte a ak ano, tak mi ho na to miesto, kde to 
	 * potrebujem poskytne.
	 * 
	 * Toto nacitanie beanov nastava hned na zaciatku vramci skenovania. Takze anotacie @Bean a 
	 * @Component ked uvidis, znamena to to, ze ked bude aplikacia bezat, tak tieto objekty uz
	 * budu existovat a v ramci celej aplikacie si ich mozes z kontextu vypytat pretoze spring
	 * ich inicializuje. Defaultne vytvara z tychto bean a komponentov singletony, co znamena, 
	 * ze objekt takej triedy existuje v springovom kontexte a teda celej aplikacie iba jeden.
	 * 
	 * Takze tato metoda getTransactionManager() vrati TransactionManager objekt ked si to spring
	 * vypyta a to ze od kial si to ma pytat vie podla navratoveho typu a @Bean anotacie.
	 * 
	 * Vsimni si, ze metoda berie ako argument DataSource objekt.. co znamena ze tento datasource
	 * objekt tam musi do tej metody vojst, aby spring spravne nainicializoval ten TransactionManager.
	 * Lenze od kial tento datasource pride?
	 * Posledna metoda vracia DataSource a je oznacena anotaciou @Bean, co vravi springu, ze ked bude
	 * potrebovat datasource objekt, tak tu si ho moze vyzdvihnut.
	 * Avsak, ako spring vie, ze ten datasource od neho potrebujeme?
	 * @Autowired - v tomto pripade je na metode aj tato anotacia, ktora sa vztahuje na vsetky arguemnty
	 * metody.. takze sa stane nasledovne:
	 * 
	 * 1. spring vojde do tejto triedy a ide inicializovat beany rad za radom
	 * 2. natrafi na prvu metodu s @Bean
	 * 3. natrafi na @Autowired a vsimne si, ze tato beana ma dependenciu na DataSource
	 * 4. hlada komponent/beanu, ktora mu da takyto objekt typu DataSource
	 * 5. najde ju v poslednej metode.. ta ma este nejake @Value anotacie na svojich argumentoch, ktore dosadi
	 * 6. ziska DataSource objekt a vrati sa na miesto, kde vsade ten objekt niekto potreboval a da im ho
	 * 7. prva metoda uspesne vrati objekt typu TransactionManager a spring si ho ako singleton drzi v kontexte
	 * 8. takto pokracuje dalej aj s nizsou metodou, no kedze uz datasource objekt ma k dispozicii ihned ho vie predat.
	 */
	@Bean
	@Autowired
	public TransactionManager getTransactionManager(DataSource dataSource) {
		// o tomto sme sa bavili.. toto nam umoznuje dat na nejaku public metodu komponentu anotaciu @Transactional
		// co je riadene TransactionManagerom, ktory ma spojenie s nasou databazou...transakcia znamena, ze chceme
		// nieco vykonat ako celok a ak sa to z nejakeho dovodu nepodari..niekde na ceste vykonavania dojde k chybe
		// tak cokolvek co by ovplyvnilo stav databazy sa rollbackne, cize sa na databazu ne-commitne..
		// avsak, ak vsetko v metode ktora je oznacena @Transactional prebehne uspesne, tak na konci transakcie
		// sa vsetky zmeny na databaze commitnu.
		
		// takze bud sa metoda vykona uspesne cela na to aby sa daco ulozilo alebo sa neulozi nic.
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	@Autowired
	public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
		// jdbcTemplate objekt sluzi na lepsie dotazovanie databazy a ponuka viac moznosti.. + lepsie vynimky
		// inak ide o to iste ako cez connection -> statement -> resultSet ...insert, update, delete, procedury a tak..
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public DataSource getDatabaseDataSource(
			@Value("${url}") String url, 
			@Value("${username}") String username, 
			@Value("${password}") String password) {

		// DataSource je objekt ktory drzi info o databazovom spojeni a dokaze s tymto spojenim manipulovat/poskytovat ho
		// v nasom pripade tato trieda SingleConnectionDataSource inicializuje data o spojeni z properties suboru a vzdy ked
		// si vypytame spojenie, tak datasource nam ho poskytne ale nikdy ho neuzavrie, aby sme ho mohli vzdy znovu pouzit.
		// ak by sme vzdy uzatvarali spojenie vytvarali ho nanovo tak by to zralo vela casu..takto ostane socket na databazu
		// stale pripojeny (lepsie vysvetlim osobne)
		SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		return dataSource;
	}
}

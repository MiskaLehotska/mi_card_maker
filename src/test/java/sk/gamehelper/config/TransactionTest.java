package sk.gamehelper.config;

import org.springframework.stereotype.Component;

/**
 * @Component je vlastne akoby @Bean ktoru ma spring v ramci @ComponentScanu nainicializovat.
 * @Component sa pouziva na nase triedy, ktore si my vytvorime sami a chceme aby boli springom
 * manazovane..
 * 
 * @Bean sa pouziva na metodach v konfiguracnych triedach, a vacsinou tam musime objekt vytvorit/vratit
 * my, lebo ide o nieco, na co nemame dosah oznacit anotaciou @Component... niekto nam dal balicky a triedy
 * a chceme aby to spring nacital ako komponent, tak mu poskytneme @Bean metodu s tym navratovym typom a
 * on nam ho zahrnie do kontextu.
 *
 */

// TODO: use this class with some global variable to test the @Transactional
@Component
public class TransactionTest {

}

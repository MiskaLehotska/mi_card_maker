package sk.gamehelper.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * This class enables spring managed beans to be used from within classes that
 * are not managed by spring.
 * 
 * @author martin
 */
@Component
public class AccessibleContext implements ApplicationContextAware {

	private static ApplicationContext context;

	/**
	 * Returns the Spring managed bean instance of the given 
	 * class type if it exists. Returns null otherwise.
	 * 
	 * @param beanClass
	 * @return
	 */
	public static <T extends Object> T getBean(Class<T> beanClass) {
		return context.getBean(beanClass);
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// store ApplicationContext reference to access required beans later on
		AccessibleContext.context = context;
	}
}

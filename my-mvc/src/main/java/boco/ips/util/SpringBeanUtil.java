package boco.ips.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBeanUtil implements ApplicationContextAware {
	
	private static ApplicationContext applicationContext;
	 
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }
 
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return clazz.cast(getBean(beanName));
    }
 
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	SpringBeanUtil.applicationContext = applicationContext;
    }
}

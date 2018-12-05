package ir.co.sadad.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Logging producer for injectable log4j logger
 *
 */
public class LoggerProducer {

    /**
     * @param injectionPoint
     * @return logger
     */
    @Produces
    public Logger producer(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

}

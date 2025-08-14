package jpaoletti.jpm2.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

public class MySqlCleanupContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // No necesitamos hacer nada al inicio
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Apagar el hilo interno de MySQL Connector/J
        com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();

        // Desregistrar drivers JDBC cargados por este classloader
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        ClassLoader cl = this.getClass().getClassLoader();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == cl) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (Exception e) {
                    // Log si es necesario
                }
            }
        }
    }
}

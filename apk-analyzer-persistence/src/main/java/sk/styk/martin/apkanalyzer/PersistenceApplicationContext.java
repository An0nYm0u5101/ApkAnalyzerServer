package sk.styk.martin.apkanalyzer;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Configuration class used to setup persistence application context
 *
 * @author Martin Styk
 * @version 20.11.2017
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
@ComponentScan(basePackages = "sk.styk.martin.apkanalyzer.dao")
public class PersistenceApplicationContext {

    public final String DATABASE_URL = System.getProperty("DATABASE_URL");

    @Bean
    public JpaTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean jpaFactoryBean = new LocalContainerEntityManagerFactoryBean();
        jpaFactoryBean.setDataSource(db());
        jpaFactoryBean.setLoadTimeWeaver(instrumentationLoadTimeWeaver());
        jpaFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        return jpaFactoryBean;
    }

    @Bean
    public LoadTimeWeaver instrumentationLoadTimeWeaver() {
        return new InstrumentationLoadTimeWeaver();
    }

//    @Bean
//    public DataSource db() {
//        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
//        EmbeddedDatabase db = builder.setType(EmbeddedDatabaseType.DERBY).build();
//        return db;
//    }

    @Bean
    @Primary
    public DataSource db() {

        if (DATABASE_URL == null) {

            return DataSourceBuilder
                    .create()
                    .username("postgres")
                    .password("admin")
                    .url("jdbc:postgresql://localhost:5432")
                    .driverClassName("org.postgresql.Driver")
                    .build();
        } else {
            URL url = null;
            try {
                url = new URL(DATABASE_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return DataSourceBuilder
                    .create()
                    .url("jdbc:postgresql://" + url.getHost() + ':' + url.getPort() + url.getPath())
                    .username(url.getUserInfo().split(":")[0])
                    .password(url.getUserInfo().split(":")[1])
                    .driverClassName("org.postgresql.Driver")
                    .build();
        }

    }


    @Bean
    public PersistenceExceptionTranslationPostProcessor postProcessor() {
        PersistenceExceptionTranslationPostProcessor postProcessor = new PersistenceExceptionTranslationPostProcessor();
        return postProcessor;
    }
}

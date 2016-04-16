package net.kear.recipeorganizer.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:database.oracle.properties")
//@PropertySource("classpath:database.postgres.properties")
@ComponentScan("net.kear.recipeorganizer.persistence")
public class RepositoryConfig {

	@Autowired
    private Environment env;	

	@Bean
	public DataSource dataSource() {
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
	    dataSource.setUrl(env.getProperty("jdbc.url"));
	    dataSource.setUsername(env.getProperty("jdbc.username"));
	    dataSource.setPassword(env.getProperty("jdbc.password"));
	    dataSource.setAccessToUnderlyingConnectionAllowed(true);
	    dataSource.addConnectionProperty("useUnicode", "yes");
	    dataSource.addConnectionProperty("characterEncoding", "UTF-8");
	    return dataSource;
	}
	
	@Bean
	public SessionFactory sessionFactory(DataSource dataSource) {
	    LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
	    sessionBuilder.scanPackages("net.kear.recipeorganizer.*");
	    sessionBuilder.addProperties(getHibernateProperties());
	    return sessionBuilder.buildSessionFactory();
	}
	
	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
	    HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
	    return transactionManager;
	}
	
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
	private Properties getHibernateProperties() {
	    Properties properties = new Properties();
	    properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
	    properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
	    properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
	    properties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
	    properties.put("hibernate.use_sql_comments", env.getProperty("hibernate.use_sql_comments"));
	    properties.put("hibernate.connection.pool_size", "1");
	    properties.put("hibernate.c3p0.acquire_increment", env.getProperty("hibernate.c3p0.acquire_increment"));
	    properties.put("hibernate.c3p0.idle_test_period", env.getProperty("hibernate.c3p0.idle_test_period"));
	    properties.put("hibernate.c3p0.max_size", env.getProperty("hibernate.c3p0.max_size"));
	    properties.put("hibernate.c3p0.max_statements", env.getProperty("hibernate.c3p0.max_statements"));
	    properties.put("hibernate.c3p0.min_size", env.getProperty("hibernate.c3p0.min_size"));
	    properties.put("hibernate.c3p0.timeout", env.getProperty("hibernate.c3p0.timeout"));
	    properties.put("hibernate.c3p0.testOnBorrow", env.getProperty("hibernate.c3p0.testOnBorrow"));
	    properties.put("hibernate.c3p0.validationQuery", env.getProperty("hibernate.c3p0.validationQuery"));
	    return properties;
	}
}

package net.crizin.learning.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.crizin.learning.support.SnakeCasePhysicalNamingStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@Configuration
@EnableJpaRepositories("net.crizin.learning.repository")
@EnableTransactionManagement
public class PersistenceConfig {
	@Value("#{config['dataSource.dataSourceClassName']}")
	private String dataSourceClassName;
	@Value("#{config['dataSource.poolSize']}")
	private int dataSourcePoolSize;
	@Value("#{config['dataSource.idleTimeout']}")
	private long dataSourceIdleTimeout;
	@Value("#{config['dataSource.connectionTimeout']}")
	private long dataSourceConnectionTimeout;
	@Value("#{config['dataSource.jdbc.url']}")
	private String dataSourceJdbcUrl;
	@Value("#{config['dataSource.jdbc.userName']}")
	private String dataSourceJdbcUserName;
	@Value("#{config['dataSource.jdbc.password']}")
	private String dataSourceJdbcPassword;
	@Value("#{config['hibernate.dialect']}")
	private String hibernateDialect;
	@Value("#{config['hibernate.showSql']}")
	private boolean hibernateShowSql;
	@Value("#{config['hibernate.formatSql']}")
	private boolean hibernateFormatSql;
	@Value("#{config['hibernate.hbm2ddl.auto']}")
	private String hibernateHbm2DdlAuto;

	@Bean
	public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return jpaTransactionManager;
	}

	@Bean(destroyMethod = "close")
	public HikariDataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setPoolName("springHikariCP");
		hikariConfig.setDataSourceClassName(dataSourceClassName);
		hikariConfig.setMaximumPoolSize(dataSourcePoolSize);
		hikariConfig.setIdleTimeout(dataSourceIdleTimeout);
		hikariConfig.setConnectionTimeout(dataSourceConnectionTimeout);
		hikariConfig.addDataSourceProperty("url", dataSourceJdbcUrl);
		hikariConfig.addDataSourceProperty("user", dataSourceJdbcUserName);
		hikariConfig.addDataSourceProperty("password", dataSourceJdbcPassword);

		return new HikariDataSource(hikariConfig);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", String.valueOf(hibernateDialect));
		jpaProperties.setProperty("hibernate.show_sql", String.valueOf(hibernateShowSql));
		jpaProperties.setProperty("hibernate.format_sql", String.valueOf(hibernateFormatSql));
		jpaProperties.setProperty("hibernate.use_sql_comments", String.valueOf(true));
		jpaProperties.setProperty("hibernate.id.new_generator_mappings", String.valueOf(true));
		jpaProperties.setProperty("hibernate.hbm2ddl.auto", String.valueOf(hibernateHbm2DdlAuto));
		jpaProperties.setProperty("hibernate.physical_naming_strategy", String.valueOf(SnakeCasePhysicalNamingStrategy.class.getName()));

		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setDataSource(dataSource());
		localContainerEntityManagerFactoryBean.setPackagesToScan("net.crizin.learning.entity");
		localContainerEntityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		localContainerEntityManagerFactoryBean.setJpaProperties(jpaProperties);
		return localContainerEntityManagerFactoryBean;
	}
}
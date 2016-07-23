package net.crizin.learning.config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ComponentScan("net.crizin.learning.service")
@Import({PersistenceConfig.class, SecurityConfig.class})
public class RootConfig {
	@Bean
	public PropertiesFactoryBean config() {
		YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
		yamlPropertiesFactoryBean.setResources(new ClassPathResource("config.yml"));

		PropertiesFactoryBean bean = new PropertiesFactoryBean();
		bean.setProperties(yamlPropertiesFactoryBean.getObject());
		return bean;
	}
}
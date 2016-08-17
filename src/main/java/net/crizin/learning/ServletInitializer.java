package net.crizin.learning;

import net.crizin.learning.config.MvcConfig;
import net.crizin.learning.config.RootConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.nio.charset.StandardCharsets;

public class ServletInitializer implements WebApplicationInitializer {
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext annotationConfigWebApplicationContext = new AnnotationConfigWebApplicationContext();
		annotationConfigWebApplicationContext.register(RootConfig.class);
		servletContext.addListener(new ContextLoaderListener(annotationConfigWebApplicationContext));

		addDispatcherServlet(servletContext);
		addCharacterEncodingFilter(servletContext);
		addEtagEncodingFilter(servletContext);
		addSpringSecurityFilterChainFilter(servletContext);
	}

	private void addDispatcherServlet(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext annotationConfigWebApplicationContext = new AnnotationConfigWebApplicationContext();
		annotationConfigWebApplicationContext.register(MvcConfig.class);

		DispatcherServlet dispatcherServlet = new DispatcherServlet(annotationConfigWebApplicationContext);
		dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", dispatcherServlet);
		dispatcher.setAsyncSupported(true);
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
		dispatcher.setMultipartConfig(new MultipartConfigElement(null, 10485760, 10485760, 0));
	}

	private void addCharacterEncodingFilter(ServletContext servletContext) {
		FilterRegistration.Dynamic filter = servletContext.addFilter("characterEncodingFilter", CharacterEncodingFilter.class);
		filter.setInitParameter("encoding", StandardCharsets.UTF_8.displayName());
		filter.addMappingForUrlPatterns(null, false, "/*");
	}

	private void addEtagEncodingFilter(ServletContext servletContext) {
		FilterRegistration.Dynamic filter = servletContext.addFilter("etagFilter", ShallowEtagHeaderFilter.class);
		filter.addMappingForUrlPatterns(null, false, "/static/*");
	}

	private void addSpringSecurityFilterChainFilter(ServletContext servletContext) {
		FilterRegistration.Dynamic filter = servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
		filter.addMappingForUrlPatterns(null, false, "/*");
	}
}

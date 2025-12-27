package at.qe.skeleton;

import at.qe.skeleton.configs.CustomServletContextInitializer;
import at.qe.skeleton.configs.WebSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import javax.faces.webapp.FacesServlet;

/**
 * Spring boot application. Execute maven with <code>mvn spring-boot:run</code>
 * to start this web application.
 * <p>
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" offered by the University of Innsbruck.
 */
@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)

public class Main extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Main.class, CustomServletContextInitializer.class, WebSecurityConfig.class);
    }

    @Bean
    public ServletRegistrationBean<FacesServlet> servletRegistrationBean() {
        FacesServlet servlet = new FacesServlet();
        ServletRegistrationBean<FacesServlet> servletRegistrationBean = new ServletRegistrationBean<>(servlet,
                "*.xhtml");
        servletRegistrationBean.setName("Faces Servlet");
        servletRegistrationBean.setAsyncSupported(true);
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }
}
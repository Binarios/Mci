package com.aegean.icsd.mciwebapp;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ServletContextInit implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext container) {
    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    context.register(WebAppConfig.class);

    container.addListener(new ContextLoaderListener(context));
    DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
    ServletRegistration.Dynamic dispatcher = container.addServlet("mci", dispatcherServlet);
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/*");

  }
}
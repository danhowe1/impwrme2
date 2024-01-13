package com.impwrme2.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
@ComponentScan(basePackages = "com.impwrme2.configuration")
public class AppConfig implements WebMvcConfigurer {

	//----------------------------
	// Internationalisation START.
	//----------------------------
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(localeChangeInterceptor());
	}
	
	@Bean
	protected LocaleResolver localeResolver() {
	    SessionLocaleResolver slr = new SessionLocaleResolver();
//	    slr.setDefaultLocale(Locale.ENGLISH);
	    return slr;
	}
		
	@Bean
	protected LocaleChangeInterceptor localeChangeInterceptor() {
	    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
	    lci.setParamName("lang");
	    return lci;
	}

	@Bean("messageSource")
	protected MessageSource messageSource() {
	    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	    messageSource.setBasenames("lang/messages");
	    messageSource.setDefaultEncoding("UTF-8");
	    return messageSource;
	}
	
	//--------------------------
	// Internationalisation END.
	//--------------------------
}

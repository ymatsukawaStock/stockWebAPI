package jp.ymatsukawa.stockapi;

import jp.ymatsukawa.stockapi.controller.interceptor.AuthnInterceptorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
  // set bean because interceptor handler needs autowired annotation
  // https://stackoverflow.com/a/18218439
  @Bean
  public AuthnInterceptorHandler authnInterceptorHandler() {
    return new AuthnInterceptorHandler();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(authnInterceptorHandler())
            .addPathPatterns("/information*")          // information - subject
            .addPathPatterns("/information/*")         // information - detail
            .addPathPatterns("/information/create")    // information - delete
            .addPathPatterns("/information/edit/*")    // information - edit
            .addPathPatterns("/information/delete/*"); // information - delete
  }
}

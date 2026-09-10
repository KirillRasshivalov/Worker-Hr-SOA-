package itmo.soa.hr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder(MappingJackson2XmlHttpMessageConverter xmlConverter) {
        return RestClient.builder()
                .messageConverters(converters -> {
                    converters.removeIf(c -> c instanceof MappingJackson2XmlHttpMessageConverter);
                    converters.add(xmlConverter);
                });
    }

    @Bean
    public MappingJackson2XmlHttpMessageConverter xmlConverter() {
        return new MappingJackson2XmlHttpMessageConverter();
    }
}

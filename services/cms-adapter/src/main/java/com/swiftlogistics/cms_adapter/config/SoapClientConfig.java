package com.swiftlogistics.cms_adapter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.swiftlogistics.cms_adapter.soap.CmsSoapClient;

@Configuration
public class SoapClientConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.swiftlogistics.cms_adapter.wsdl");
        return marshaller;
    }

    @Bean
    public CmsSoapClient cmsSoapClient(Jaxb2Marshaller marshaller) {
        CmsSoapClient client = new CmsSoapClient();
        client.setDefaultUri("http://cms-mock-service:5001/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
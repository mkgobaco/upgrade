package com.upgrade.campsite;

import com.upgrade.campsite.dto.InitializeRequest;
import com.upgrade.campsite.services.CampsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@SpringBootApplication
@EnableJpaAuditing
public class CampsiteApplication {

    private CampsiteService campsiteService;

    @Autowired
    public CampsiteApplication(CampsiteService campsiteService) {
        this.campsiteService = campsiteService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CampsiteApplication.class, args);
    }

    @PostConstruct
    public void postConstruct() {
        // This initialize the microserve to have available dates for the next 60 days
        LocalDate availableStartDate = LocalDate.now().plusDays(1L);
        LocalDate availableEndDate = availableStartDate.plusMonths(2L);

        InitializeRequest initializeRequest = InitializeRequest.builder().availableStartDate(availableStartDate)
                .availableEndDate(availableEndDate).build();
        campsiteService.initialize(initializeRequest);
    }
}

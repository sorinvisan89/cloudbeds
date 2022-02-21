package com.cloudbeds.demo.config;

import com.cloudbeds.demo.service.UserGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class GrpcServerConfig {

    @Bean
    public Server grpcServer(final UserGrpcService userGrpcService) throws IOException {
        final Server server = ServerBuilder.forPort(8081)
                .addService(userGrpcService).build();

        log.info("Starting GRPC server...");
        server.start();
        log.info("GRPC Server started!");
        return server;
    }
}

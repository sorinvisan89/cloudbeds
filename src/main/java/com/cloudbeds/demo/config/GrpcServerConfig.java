package com.cloudbeds.demo.config;

import com.cloudbeds.demo.service.UserGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class GrpcServerConfig {

    @Value("${grpc.server.port:8081}")
    private int grpcServerPort;

    @Bean
    public Server grpcServer(final UserGrpcService userGrpcService) throws IOException {
        final Server server = ServerBuilder.forPort(grpcServerPort)
                .addService(userGrpcService).build();

        log.info(String.format("Starting GRPC server on port %d...", grpcServerPort));
        server.start();
        log.info("GRPC Server started!");
        return server;
    }
}

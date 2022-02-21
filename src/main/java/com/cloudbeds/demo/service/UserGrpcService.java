package com.cloudbeds.demo.service;

import com.cloudbeds.demo.grpc.UserGrpcServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class UserGrpcService {

    private final Server server;

    @Autowired
    public UserGrpcService(final UserGrpcServiceImpl userGrpcService) throws IOException, InterruptedException {

        this.server = ServerBuilder.forPort(8081)
                .addService(userGrpcService).build();

        log.info("Starting GRPC server...");
        server.start();
        log.info("GRPC Server started!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
            log.info("Successfully stopped the GRPC server");
        }));

        server.awaitTermination();
    }

}

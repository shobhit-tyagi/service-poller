package com.example.servicepoller.mock.server;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.mockserver.integration.ClientAndServer;

import java.net.ServerSocket;

@Getter
public class ServiceMock {

    private final MockService service;
    private final ClientAndServer mockServer;

    public ServiceMock(final MockService service) {
        this.service = service;
        mockServer = ClientAndServer.startClientAndServer(getFreePort());
    }

    @SneakyThrows
    private static int getFreePort() {
        try (val socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    public void reset() {
        mockServer.reset();
    }
}

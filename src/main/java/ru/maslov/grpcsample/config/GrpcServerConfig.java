package ru.maslov.grpcsample.config;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GrpcServerConfig implements BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {
    @Value("${grpc.port}")
    private int port;

    private final List<BindableService> grpcServices = new ArrayList<>();
    private Server server;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        GrpcService annotation = targetClass.getAnnotation(GrpcService.class);
        if (annotation != null && BindableService.class.isAssignableFrom(targetClass)) {
            grpcServices.add((BindableService) bean);
        }
        return bean;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
        grpcServices.forEach(serverBuilder::addService);
        server = serverBuilder.build();
        try {
            server.start();
        } catch (IOException e) {
            throw new IllegalStateException("Grpc server not start", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                if (server != null) {
                    server.shutdown();
                }
                System.err.println("*** server shut down");
            }
        });
    }
}

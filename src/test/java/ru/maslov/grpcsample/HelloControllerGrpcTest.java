package ru.maslov.grpcsample;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.maslov.grpc.GreeterGrpc;
import ru.maslov.grpc.HelloReply;
import ru.maslov.grpc.HelloRequest;


@SpringBootTest
@RunWith(SpringRunner.class)
public class HelloControllerGrpcTest {
    @Value("${grpc.port}")
    private int port;

    private ManagedChannel channel;
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @Before
    public void init() {
        channel = ManagedChannelBuilder.forAddress("localhost", port)
                .usePlaintext(true)
                .build();
        greeterBlockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    @After
    public void shutdown() {
        channel.shutdown();
    }

    @Test
    public void helloTest() {
        // given:
        String name = "Dezmond";

        // when:
        HelloReply reply = greeterBlockingStub.sayHello(HelloRequest.newBuilder()
                .setName(name)
                .build());

        // then:
        Assert.assertEquals("Hello " + name, reply.getMessage());
    }
}

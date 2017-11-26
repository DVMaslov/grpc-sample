package ru.maslov.grpcsample.controller.grpc;

import io.grpc.stub.StreamObserver;
import ru.maslov.grpc.GreeterGrpc;
import ru.maslov.grpc.HelloReply;
import ru.maslov.grpc.HelloRequest;
import ru.maslov.grpcsample.config.GrpcService;

@GrpcService
public class HelloGrpcController extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}

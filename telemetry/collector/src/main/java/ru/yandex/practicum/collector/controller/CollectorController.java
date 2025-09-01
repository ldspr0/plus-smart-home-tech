package ru.yandex.practicum.collector.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.collector.producers.hub.HubEventProducer;
import ru.yandex.practicum.collector.producers.sensor.SensorEventProducer;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.CollectorResponse;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, SensorEventProducer> sensorEventProducers;
    private final Map<HubEventProto.PayloadCase, HubEventProducer> hubEventProducers;

    public CollectorController(Set<SensorEventProducer> sensorEventBuilders, Set<HubEventProducer> hubEventBuilders) {
        this.sensorEventProducers = sensorEventBuilders.stream()
                .collect(Collectors.toMap(
                        SensorEventProducer::getMessageType,
                        Function.identity()
                ));
        this.hubEventProducers = hubEventBuilders.stream()
                .collect(Collectors.toMap(
                        HubEventProducer::getMessageType,
                        Function.identity()
                ));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<CollectorResponse> responseObserver) {
        try {
            if (sensorEventProducers.containsKey(request.getPayloadCase())) {
                sensorEventProducers.get(request.getPayloadCase()).builder(request);
            } else {
                throw new IllegalArgumentException("Не могу найти обработчик для события " + request.getPayloadCase());
            }
            responseObserver.onNext(CollectorResponse.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.fromThrowable(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<CollectorResponse> responseObserver) {
        try {
            if (hubEventProducers.containsKey(request.getPayloadCase())) {
                hubEventProducers.get(request.getPayloadCase()).builder(request);
            } else {
                throw new IllegalArgumentException("Не могу найти обработчик для события " + request.getPayloadCase());
            }
            responseObserver.onNext(CollectorResponse.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.fromThrowable(e)
            ));
        }
    }
}

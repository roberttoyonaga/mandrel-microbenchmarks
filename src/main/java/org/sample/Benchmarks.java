package org.sample;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.http.DefaultHttpHeadersFactory;
import io.vertx.core.http.impl.HttpUtils;

import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.core.http.HttpHeaders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.ByteBuf;



@State(Scope.Thread)
public class Benchmarks {
    private CharSequence[] headerNames;
    private CharSequence[] headerValues;
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyyy HH:mm:ss z");


    @Setup
    public void setup() {
        headerNames = new CharSequence[4];
        headerValues = new CharSequence[4];
        headerNames[0] = HttpHeaders.CONTENT_TYPE;
        headerValues[0] = HttpHeaders.createOptimized("text/plain");
        headerNames[1] = HttpHeaders.CONTENT_LENGTH;
        headerValues[1] = HttpHeaders.createOptimized("20");
        headerNames[2] = HttpHeaders.SERVER;
        headerValues[2] = HttpHeaders.createOptimized("vert.x");
        headerNames[3] = HttpHeaders.DATE;
        headerValues[3] = HttpHeaders.createOptimized(DATE_FORMAT.format(new java.util.Date(0)));
        }


    @Benchmark
    @Fork(value = 1, warmups = 1)
    @BenchmarkMode(Mode.Throughput)
    public void headersMultiMapAdd(Blackhole blackhole) {
        HeadersMultiMap hmm = HeadersMultiMap.httpHeaders();
        for (int i=0; i < headerNames.length; i++) {
            blackhole.consume(hmm.add(headerNames[i], headerValues[i]));
        }
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    @BenchmarkMode(Mode.Throughput)
    public ByteBuf pooledByteBufAllocatorNewDirectBuffer(Blackhole blackhole) {
        ByteBuf buf =  PooledByteBufAllocator.DEFAULT.directBuffer(); // calls newDirectBuffer
        return buf;
    }

    /* TODO: Compare with Java metrics to see if this is actually interesting.
    This seems like a special case since it only has many inlined callees when in the  io.netty.channel.AbstractChannelHandlerContext::invokeWrite0 stack
     */
    @Benchmark
    @Fork(value = 1, warmups = 1)
    @BenchmarkMode(Mode.Throughput)
    public ByteBuf headersMultiMapEncoderHeader(Blackhole blackhole) {
        // Buffer will be written to
        ByteBuf buf =  PooledByteBufAllocator.DEFAULT.directBuffer();
        HeadersMultiMap hmm = HeadersMultiMap.httpHeaders();
        for (int i=0; i < headerNames.length; i++) {
            blackhole.consume(hmm.add(headerNames[i], headerValues[i]));
        }
        // calls encoderHeader
        hmm.encode(buf);
        return buf;
    }
}
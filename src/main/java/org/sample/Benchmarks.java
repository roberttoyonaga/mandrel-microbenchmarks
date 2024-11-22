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

import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.core.http.HttpHeaders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.ByteBuf;



@State(Scope.Thread)
public class Benchmarks {
    private CharSequence[] headerNames;
    private CharSequence[] headerValues;
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyyy HH:mm:ss z");

    private ByteBuf buf;
    private HeadersMultiMap hmm;

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

        buf =  PooledByteBufAllocator.DEFAULT.directBuffer();
        hmm = HeadersMultiMap.httpHeaders();
        for (int i=0; i < headerNames.length; i++) {
            hmm.add(headerNames[i], headerValues[i]);
        }
    }


    @Benchmark
//    @Fork(value = 1, warmups = 0)
//    @Measurement(iterations = 20)
//    @Warmup(iterations = 1000)
    @BenchmarkMode(Mode.Throughput)
    public HeadersMultiMap headersMultiMapAdd() {
        HeadersMultiMap hmm = HeadersMultiMap.httpHeaders();
        int loopSize = headerNames.length;
        /* We don't want loop unrolling. It allows the optimizations to be too specific to each header due to easier branch prediction.
        And it means that each version of the method inlined has it's branches hit with a different probability.
         So we won't get performance representative to what happens in a real Quarkus app with ambiguous headers.
          -XX:LoopUnrollLimit=1
         */
        for (int i=0; i < loopSize; i++) {
            hmm.clear();// Franz said we may need to clear to avoid the size of the object becoming a relevant factor. Profile to check if clear() becomes relevant.
           hmm.add(headerNames[i], headerValues[i]);
        }
        return hmm;
    }

    @Benchmark
//    @Fork(value = 1, warmups = 0)
//    @Measurement(iterations = 20)
//    @Warmup(iterations = 1000)
    @BenchmarkMode(Mode.Throughput)
    public HeadersMultiMap headersMultiMapSet() {
        int loopSize = headerNames.length;
        for (int i=0; i < loopSize; i++) {
            this.hmm.set(headerNames[i], headerValues[i]);
        }
        return this.hmm;
    }

    @Benchmark
//    @Fork(value = 1, warmups = 0)
//    @Measurement(iterations = 20)
//    @Warmup(iterations = 1000)
    @BenchmarkMode(Mode.Throughput)
    public ByteBuf pooledByteBufAllocatorNewDirectBuffer() {
        ByteBuf buf =  PooledByteBufAllocator.DEFAULT.directBuffer(8,8); // calls newDirectBuffer
        return buf;
    }

    /* TODO: Compare with Java metrics to see if this is actually interesting.
    This seems like a special case since it only has many inlined callees when in the  io.netty.channel.AbstractChannelHandlerContext::invokeWrite0 stack
     */
    @Benchmark
//    @Fork(value = 1, warmups = 0)
//    @Measurement(iterations = 20)
//    @Warmup(iterations = 1000)
    @BenchmarkMode(Mode.Throughput)
    public ByteBuf headersMultiMapEncoderHeader() {
        // calls encoderHeader
        buf.clear();
        this.hmm.encode(buf);
        return buf;
    }
}

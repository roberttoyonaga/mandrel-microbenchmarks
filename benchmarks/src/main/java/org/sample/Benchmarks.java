package org.sample;


import io.netty.handler.codec.http.DefaultHttpHeaders;
import org.openjdk.jmh.annotations.Benchmark;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.core.http.HttpHeaders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.ByteBuf;



@State(Scope.Thread)
public class Benchmarks {
    private CharSequence[] headerNames;
    private CharSequence[] headerValues;
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyyy HH:mm:ss z");

    private ByteBuf buf;
    private HeadersMultiMap hmm;


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public HeadersMultiMap headersMultiMapAdd() {
        HeadersMultiMap hh = HeadersMultiMap.httpHeaders();
//        HeadersMultiMap hh = HeadersMultiMap.headers(); // Using this avoids the lambda which validates headers.
        int loopSize = headerNames.length;
        /* We don't want loop unrolling. It allows the optimizations to be too specific to each header due to easier branch prediction.
        And it means that each version of the method inlined has it's branches hit with a different probability.
         So we won't get performance representative to what happens in a real Quarkus app with ambiguous headers.
          -XX:LoopUnrollLimit=1
         */
        for (int i=0; i < loopSize; i++) {
            hh.clear();// Franz said we may need to clear to avoid the size of the object becoming a relevant factor. Profile to check if clear() becomes relevant.
            hh.add(headerNames[i], headerValues[i]);
        }
        return hh;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public HeadersMultiMap headersMultiMapAddMonomorphic() {
        int loopSize = headerNames.length;
        HeadersMultiMap hh = null;
        for (int i=0; i < loopSize; i++) {
            hh = getHttpHeadersMonomorphic(i);
            hh.clear();
            hh.add(headerNames[i], headerValues[i]);
        }
        return hh;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public io.netty.handler.codec.http.HttpHeaders polymorphicHeadersMultiMapAdd() {
        int loopSize = headerNames.length;
        io.netty.handler.codec.http.HttpHeaders hh = null;

        for (int i=0; i < loopSize; i++) {
            hh = getHttpHeadersPolymorphic(i);
            hh.clear();
            hh.add(headerNames[i], headerValues[i]);
        }
        return hh;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public io.netty.handler.codec.http.HttpHeaders polymorphicHeadersMultiMapAdd2() {
        int loopSize = headerNames.length;
        io.netty.handler.codec.http.HttpHeaders hh = null;

        for (int i=0; i < loopSize; i++) {
            hh = getHttpHeadersPolymorphic2(i);
            hh.clear();
            hh.add(headerNames[i], headerValues[i]);
        }
        return hh;
    }


    io.netty.handler.codec.http.HttpHeaders hmm1 = HeadersMultiMap.httpHeaders();
    io.netty.handler.codec.http.HttpHeaders hmm2 = HeadersMultiMap.httpHeaders();
    io.netty.handler.codec.http.HttpHeaders dhh = new DefaultHttpHeaders();

    private io.netty.handler.codec.http.HttpHeaders getHttpHeadersPolymorphic(int i){
        if(i%42==0){
            return dhh;
        } else {
            return hmm1;
        }
    }

    private io.netty.handler.codec.http.HttpHeaders getHttpHeadersPolymorphic2(int i){
        if(i%2==0){
            return hmm1;
        } else {
            return hmm2;
        }
    }

    private HeadersMultiMap getHttpHeadersMonomorphic(int i){
        if(i%2==0){
            return (HeadersMultiMap) hmm1;
        } else {
            return (HeadersMultiMap) hmm2;
        }
    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public HeadersMultiMap headersMultiMapSet() {
        int loopSize = headerNames.length;
        for (int i=0; i < loopSize; i++) {
            this.hmm.set(headerNames[i], headerValues[i]);
        }
        return this.hmm;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public ByteBuf pooledByteBufAllocatorNewDirectBuffer() {
        ByteBuf buf =  PooledByteBufAllocator.DEFAULT.directBuffer(8,8); // calls newDirectBuffer
        buf.release(); // Don't forget to free
        return buf;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public ByteBuf headersMultiMapEncoderHeader() {
        // calls encoderHeader
        this.buf.clear();
        this.hmm.encode(this.buf);
        return this.buf;
    }
}

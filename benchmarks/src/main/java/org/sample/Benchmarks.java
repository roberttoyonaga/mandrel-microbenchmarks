package org.sample;


import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.vertx.core.http.impl.HttpUtils;
import org.openjdk.jmh.annotations.Benchmark;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.core.http.HttpHeaders;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.ByteBuf;



@State(Scope.Thread)
public class Benchmarks {
    private CharSequence[] headerNames;
    private CharSequence[] headerValues;
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyyy HH:mm:ss z");

    private ByteBuf buf;
    private HeadersMultiMap hmm;

    private String[] charAtValues;
    private int charAtIndex;
    private Random rand = new Random();

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

        charAtValues = new String[2];
        charAtValues[0] = "Latin1 string";
        charAtValues[1] = "UTF-\uFF11\uFF16 string";
        charAtIndex = 3;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public HeadersMultiMap headersMultiMapAdd() {
        HeadersMultiMap hh = HeadersMultiMap.httpHeaders();
        int loopSize = headerNames.length;
        /* We don't want loop unrolling. It allows the optimizations to be too specific to each header due to easier branch prediction.
        And it means that each version of the method inlined has it's branches hit with a different probability.
         So we won't get performance representative to what happens in a real Quarkus app with ambiguous headers.
          -XX:LoopUnrollLimit=1
         */
        for (int i=0; i < loopSize; i++) {
            //hh.clear();// Franz said we may need to clear to avoid the size of the object becoming a relevant factor. Profile to check if clear() becomes relevant.
            hh.add(headerNames[i], headerValues[i]);
        }
        return hh;
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
        buf.release();
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

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public char charAtLatin1() {
        final String strLatin1 = charAtValues[0];
        return strLatin1.charAt(charAtIndex);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public char charAtUtf16() {
        final String strUtf16 = charAtValues[1];
        return strUtf16.charAt(charAtIndex);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void validateHeader() {
        try {
            for (int i=0; i < headerNames.length; i++) {
                HttpUtils.validateHeader(headerNames[i], headerValues[i]);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    // ---------- Negative single callsite inlining tests ----------

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Integer sciShouldNotInline() {
        Integer i2 = sciShouldNotInline0(rand.nextInt(), rand.nextInt());
        Integer i1 = sciShouldNotInline0(rand.nextInt(), rand.nextInt());
        return i1.intValue() + i2.intValue();
    }

    // This should be inlined vai single callsite inlining
    private Integer sciShouldNotInline0(int a, int b) {
        Integer integerA = Integer.valueOf(a);
        Integer integerB = Integer.valueOf(b);
        return integerA.intValue() + integerB.intValue() + rand.nextInt();
    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Integer sciShouldNotNestedInline() {
        Integer i = sciShouldNotNestedInline0(rand.nextInt(), rand.nextInt());
        return i + rand.nextInt();
    }

    // This will be inlined
    private Integer sciShouldNotNestedInline0(int a, int b) {
        Integer integerA = Integer.valueOf(a) +  sciShouldNotNestedInline1(a,b);
        Integer integerB = Integer.valueOf(b) +  sciShouldNotNestedInline1(a,b);
        return integerA.intValue() + integerB.intValue();
    }

    // This should NOT be inlined since it has 2 callsites
    private Integer sciShouldNotNestedInline1(int a, int b) {
        Integer integerA = Integer.valueOf(a);
        Integer integerB = Integer.valueOf(b);
        return integerA.intValue() + integerB.intValue() + rand.nextInt();
    }

    // ---------- Positive single callsite inlining tests ----------

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Integer sciShouldInline() {
        Integer i2 = sciShouldInline0(rand.nextInt(), rand.nextInt());
        Integer i1 = sciShouldInline1(rand.nextInt(), rand.nextInt());
        return i1.intValue() + i2.intValue();
    }

    // This should be inlined via single callsite inlining
    private Integer sciShouldInline0(int a, int b) {
        Integer integerA = Integer.valueOf(a);
        Integer integerB = Integer.valueOf(b);
        return integerA.intValue() + integerB.intValue() + rand.nextInt();
    }

    // This should be inlined via single callsite inlining
    private Integer sciShouldInline1(int a, int b) {
        Integer integerA = Integer.valueOf(a);
        Integer integerB = Integer.valueOf(b);
        return integerA.intValue() + integerB.intValue() + rand.nextInt();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Integer sciShouldNestedInline() {
        Integer i = sciShouldNestedInline0(rand.nextInt(), rand.nextInt());
        return i + rand.nextInt();
    }

    private Integer sciShouldNestedInline0(int a, int b) {
        Integer integerA = Integer.valueOf(a);
        Integer integerB = Integer.valueOf(b);
        return integerA.intValue() + integerB.intValue() + sciShouldNestedInline1(rand.nextInt(), rand.nextInt());
    }


    private Integer sciShouldNestedInline1(int a, int b) {
        Integer integerA = Integer.valueOf(a);
        Integer integerB = Integer.valueOf(b);
        return integerA.intValue() + integerB.intValue() + rand.nextInt();
    }
}

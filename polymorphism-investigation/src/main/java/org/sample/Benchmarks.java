package org.sample;


import io.netty.util.AsciiString;
import org.openjdk.jmh.annotations.Benchmark;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import io.vertx.core.http.impl.headers.HeadersMultiMap;

import java.util.Objects;


@State(Scope.Thread)
public class Benchmarks {
    private static final CharSequence cs1 = "sequence1";
    private static final CharSequence cs2 = "sequence2";
    private static final HeadersMultiMap hh = HeadersMultiMap.httpHeaders();

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public HeadersMultiMap headersMultiMapAddMonomorphic1() {
        hh.add(cs1, cs2);
        return hh;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public int monomorphic1() {
        return Helper1.foo(true);
    }

    static final class Helper1 {
        public static int foo(boolean b1) {
            return foo0(b1);
        }

        private static int foo0(boolean b1){
            int mult = 0;
            if (b1) {
                mult = 1;
            } else {
                mult = 2;
            }
            return mult * 3;
        }

    }

    private final Helper2 helper2 = new Helper2(3);

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public int monomorphic2() {
        return helper2.foo(true);
    }

    static final class Helper2 {
        final int i;
        public Helper2(int value){
            i = value;
        }
        public int foo(boolean b1) {
            return foo0(b1);
        }

        private  int foo0(boolean b1){
            int mult = 0;
            if (b1) {
                mult = 1;
            } else {
                mult = 2;
            }
            return mult * i;
        }

    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public int monomorphic3() {
        return helper2.foo(System.currentTimeMillis() % 2 == 0);
    }

    private static final Helper4 helper4 = Helper4.httpHeaders();

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Helper4 monomorphic4 () {
        helper4.add(cs1, cs2);
        return helper4;
    }

    static final class Helper4 {
        public static Helper4 httpHeaders() {
            return new Helper4();
        }

        public Helper4 add(CharSequence name, CharSequence value) {
            Objects.requireNonNull(value);
            int h = AsciiString.hashCode(name);
            int i = h & 15;
            this.add0(h, i, name, value);
            return this;
        }

        private void add0(int h, int i, CharSequence name, CharSequence value) {
            if (name.equals(value) && h == i) {
                System.out.println("test.");
            }
        }
    }
    private static final String staticFinalString1 = "string1";
    private static final String staticFinalString2 = "string2";

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Helper4 monomorphic5 () {
        helper4.add(staticFinalString1, staticFinalString2);
        return helper4;
    }

    private static final Helper6 helper6 = Helper6.httpHeaders();

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Helper6 monomorphic6 () { // Not inlined
        helper6.add(String.valueOf(System.currentTimeMillis()), staticFinalString2);
        return helper6;
    }

    static final class Helper6 {
        public static Helper6 httpHeaders() {
            return new Helper6();
        }

        public Helper6 add(String name, String value) { // This is inlined
            Objects.requireNonNull(value);
            int h = 13;
            int i = h & 15;
            this.add0(h, i, name, value);
            return this;
        }

        private void add0(int h, int i, String name, String value) { // Not inlined
            if (name.equals(value) && h == i) {
                System.out.println("test.");
            }
        }
    }



    private static final Helper7 helper7 = Helper7.httpHeaders();

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Helper7 monomorphic7() { // inlined
        helper7.add(staticFinalString2);
        return helper7;
    }

    static final class Helper7 {
        public static Helper7 httpHeaders() {
            return new Helper7();
        }

        // Moving currentTimeMillis into this method will allow monomorphic7() to be inlined.
        public Helper7 add(String value){ // not inlined
            return add0(String.valueOf(System.currentTimeMillis()), value);
        }

        public Helper7 add0(String name, String value) { // inlined
            Objects.requireNonNull(value);
            int h = 13;
            int i = h & 15;
            this.add1(h, i, name, value);
            return this;
        }

        private void add1(int h, int i, String name, String value) { // not inlined
            if (name.equals(value) && h == i) {
                System.out.println("test.");
            }
        }
    }

    private final String finalString = "string";
    private Helper8 helper8 = Helper8.httpHeaders();

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Helper8 monomorphic8() {
        helper8.add(finalString);
        return helper8;
    }

    static final class Helper8 {

        public static Helper8 httpHeaders() {
            return new Helper8();
        }

        public Helper8 add(String value){ // not inlined
            return add0(String.valueOf(value.length()), value);
        }

        public Helper8 add0(String name, String value) { // inlined
            Objects.requireNonNull(value);
            int h = 13;
            int i = h & 15;
            this.add1(h, i, name, value);
            return this;
        }

        private void add1(int h, int i, String name, String value) { // not inlined
            if (name.equals(value) && h == i) {
                System.out.println("test.");
            }
        }
    }
}

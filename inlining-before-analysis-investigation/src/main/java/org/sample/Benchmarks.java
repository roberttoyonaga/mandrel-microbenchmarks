package org.sample;

import org.openjdk.jmh.annotations.Benchmark;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;


/** This test is only for debugging the InliningBeforeAnalysis phase of the Native Image Builder. */
@State(Scope.Thread)
public class Benchmarks {
    /*
    !!! commitCalleeScope: org.sample.Benchmarks$IBAHelper.f1(long)
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.entry(long)] inline call target org.sample.Benchmarks$IBAHelper.f1(long)
    !!! commitCalleeScope: org.sample.Benchmarks$IBAHelper.f1(long)
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.f2(long)] inline call target org.sample.Benchmarks$IBAHelper.f1(long)
    !!! commitCalleeScope: org.sample.Benchmarks$IBAHelper.f1(long)
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.f2(long)] inline call target org.sample.Benchmarks$IBAHelper.f1(long)
    !!! commitCalleeScope: org.sample.Benchmarks$IBAHelper.f2(long)
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.entry(long)] inline call target org.sample.Benchmarks$IBAHelper.f2(long)
    !!! abortInlining: org.sample.Benchmarks$IBAHelper.f3(long)
    !!! finishInlining inliningAborted: org.sample.Benchmarks$IBAHelper.f3(long)
    !!! abortInlining: org.sample.Benchmarks$IBAHelper.f4(long)
    !!! finishInlining inliningAborted: org.sample.Benchmarks$IBAHelper.f4(long)
    !!! abortInlining: org.sample.Benchmarks$IBAHelper.entry(long)  // ABORT. Since f3 and f4 cant be inlined, entry(long) itself cannot be inlined since it has 2 invocations.
    !!! finishInlining inliningAborted: org.sample.Benchmarks$IBAHelper.entry(long)
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.entry(long)] inline call target org.sample.Benchmarks$IBAHelper.f1(long)
    !!! commitCalleeScope: org.sample.Benchmarks$IBAHelper.f1(long)
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.f2(long)] inline call target org.sample.Benchmarks$IBAHelper.f1(long)
    !!! abortInlining: org.sample.Benchmarks$IBAHelper.f1(long)                 // TODO. Why are we aborting here? I think this just exposes an implementation detail of the DFS...
    !!! finishInlining inliningAborted: org.sample.Benchmarks$IBAHelper.f1(long)
    !!! abortInlining: org.sample.Benchmarks$IBAHelper.f2(long)
    !!! finishInlining inliningAborted: org.sample.Benchmarks$IBAHelper.f2(long)
    !!! abortInlining: org.sample.Benchmarks$IBAHelper.f3(long)
    !!! finishInlining inliningAborted: org.sample.Benchmarks$IBAHelper.f3(long)
    !!! abortInlining: org.sample.Benchmarks$IBAHelper.f4(long)
    !!! finishInlining inliningAborted: org.sample.Benchmarks$IBAHelper.f4(long)
    !!! abortInlining: org.sample.Benchmarks$IBAHelper.f5(long)
    !!! finishInlining inliningAborted: org.sample.Benchmarks$IBAHelper.f5(long)
    !!! abortInlining: org.sample.Benchmarks$IBAHelper.f6(long)
    !!! finishInlining inliningAborted: org.sample.Benchmarks$IBAHelper.f6(long)
    !!! abortInlining: org.sample.Benchmarks$IBAHelper.f3(long)
    !!! finishInlining inliningAborted: org.sample.Benchmarks$IBAHelper.f3(long)                            // f3 is not inlined into f7
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.entry(long)] inline call target org.sample.Benchmarks$IBAHelper.f7(long) // Lines below are inlined as expected, but why do it again?
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.f2(long)] inline call target org.sample.Benchmarks$IBAHelper.f1(long)
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.f2(long)] inline call target org.sample.Benchmarks$IBAHelper.f1(long)
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.entry(long)] inline call target org.sample.Benchmarks$IBAHelper.f2(long)
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.entry(long)] inline call target org.sample.Benchmarks$IBAHelper.f4(long)
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.entry(long)] inline call target org.sample.Benchmarks$IBAHelper.f5(long)
    [PEGraphDecoder.finishInlining] [org.sample.Benchmarks$IBAHelper.entry(long)] inline call target org.sample.Benchmarks$IBAHelper.f6(long)
    */

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public long inliningBeforeAnalysisTest() {
        return IBAHelper.entry(12);
    }

    static final class IBAHelper {
        public static long entry(long value) {
            long result1 = f1(value);
            long result2 = f2(value);
            long result3 = f3(value);
            long result4 = f4(value);
            long result5 = f5(value);
            long result6 = f6(value);
            long result7 = f7(value);
            return result1 + result2 + result3 + result3 + result4 + result5 + result6 + result7;
        }

        // Inlined.
        private static long f1(long value) {
            return 42 * value;
        }

        // Inlined. When f1 is inlined, then this method makes no further invokes.
        private static long f2(long value) {
            long result1 = f1(value);
            long result2 = f1(value);
            return result1 + result2;
        }


        // Not inlined. Multiple invokes.
        private static long f3(long value) {
            return System.currentTimeMillis() + value + System.currentTimeMillis();
        }

        // Inlined. Loops are ok.
        private static long f4(long value) {
            long count = 0;
            for (int i = 0; i < value; i++) {
                count += value;
            }
            return count;
        }

        // Inlined. Only a single invoke.
        private static long f5(long value) {
            long count = 2 * value;
            return System.currentTimeMillis() + count;
        }

        // Inlined. No invokes even though there are lots of computations.
        private static long f6(long value) {
            long c1 = value * value;
            long c2 = value + 42;
            long c3 = c1 + c2;
            return c3 * 2;
        }

        // Inlined
        private static long f7(long value) {
            return f3(value);
        }
    }
}

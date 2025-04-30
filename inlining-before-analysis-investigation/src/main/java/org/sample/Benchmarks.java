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
    --- Committing callee scope in InlineBeforeAnalysisPolicyUtils.commitCalleeScope: org.sample.Benchmarks$IBAHelper.f1(long)
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.entry(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f1(long)
    --- Committing callee scope in InlineBeforeAnalysisPolicyUtils.commitCalleeScope: org.sample.Benchmarks$IBAHelper.f1(long)
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.f2(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f1(long)
    --- Committing callee scope in InlineBeforeAnalysisPolicyUtils.commitCalleeScope: org.sample.Benchmarks$IBAHelper.f1(long)
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.f2(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f1(long)
    --- Committing callee scope in InlineBeforeAnalysisPolicyUtils.commitCalleeScope: org.sample.Benchmarks$IBAHelper.f2(long)
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.entry(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f2(long)
    !!! Aborting inlining in InlineBeforeAnalysisGraphDecoder.abortInlining: org.sample.Benchmarks$IBAHelper.f3(long)
    ### Inining Aborted in InlineBeforeAnalysisGraphDecoder.finishInlining: org.sample.Benchmarks$IBAHelper.f3(long)
    !!! Aborting inlining in InlineBeforeAnalysisGraphDecoder.abortInlining: org.sample.Benchmarks$IBAHelper.f4(long)
    ### Inining Aborted in InlineBeforeAnalysisGraphDecoder.finishInlining: org.sample.Benchmarks$IBAHelper.f4(long)
    !!! Aborting inlining in InlineBeforeAnalysisGraphDecoder.abortInlining: org.sample.Benchmarks$IBAHelper.entry(long) // ABORT. Since f3 and f4 cant be inlined, entry(long) itself cannot be inlined since it has 2 invocations.
    ### Inining Aborted in InlineBeforeAnalysisGraphDecoder.finishInlining: org.sample.Benchmarks$IBAHelper.entry(long)
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.entry(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f1(long)
    --- Committing callee scope in InlineBeforeAnalysisPolicyUtils.commitCalleeScope: org.sample.Benchmarks$IBAHelper.f1(long)
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.f2(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f1(long)
    !!! Aborting inlining in InlineBeforeAnalysisGraphDecoder.abortInlining: org.sample.Benchmarks$IBAHelper.f1(long) // TODO. Why are we aborting here? I think this just exposes an implementation detail of the DFS...
    ### Inining Aborted in InlineBeforeAnalysisGraphDecoder.finishInlining: org.sample.Benchmarks$IBAHelper.f1(long)
    !!! Aborting inlining in InlineBeforeAnalysisGraphDecoder.abortInlining: org.sample.Benchmarks$IBAHelper.f2(long)
    ### Inining Aborted in InlineBeforeAnalysisGraphDecoder.finishInlining: org.sample.Benchmarks$IBAHelper.f2(long)
    !!! Aborting inlining in InlineBeforeAnalysisGraphDecoder.abortInlining: org.sample.Benchmarks$IBAHelper.f3(long)
    ### Inining Aborted in InlineBeforeAnalysisGraphDecoder.finishInlining: org.sample.Benchmarks$IBAHelper.f3(long)
    !!! Aborting inlining in InlineBeforeAnalysisGraphDecoder.abortInlining: org.sample.Benchmarks$IBAHelper.f4(long)
    ### Inining Aborted in InlineBeforeAnalysisGraphDecoder.finishInlining: org.sample.Benchmarks$IBAHelper.f4(long)
    !!! Aborting inlining in InlineBeforeAnalysisGraphDecoder.abortInlining: org.sample.Benchmarks$IBAHelper.f5(long)
    ### Inining Aborted in InlineBeforeAnalysisGraphDecoder.finishInlining: org.sample.Benchmarks$IBAHelper.f5(long)
    !!! Aborting inlining in InlineBeforeAnalysisGraphDecoder.abortInlining: org.sample.Benchmarks$IBAHelper.f6(long)
    ### Inining Aborted in InlineBeforeAnalysisGraphDecoder.finishInlining: org.sample.Benchmarks$IBAHelper.f6(long)
    !!! Aborting inlining in InlineBeforeAnalysisGraphDecoder.abortInlining: org.sample.Benchmarks$IBAHelper.f3(long)
    ### Inining Aborted in InlineBeforeAnalysisGraphDecoder.finishInlining: org.sample.Benchmarks$IBAHelper.f3(long) // f3 is not inlined into f7
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.entry(long)] ||| Callee: org.sample.Benchmarks$IBAHelper.f7(long) // Lines below are inlined as expected, but why do it again?
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.f2(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f1(long)
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.f2(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f1(long)
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.entry(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f2(long)
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.entry(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f4(long)
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.entry(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f5(long)
    $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.entry(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f6(long)
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
            long result8 = f8_inlining_phase(value);
            long result9 = f9_propagation(value);
            return result1 + result2 + result3 + result3 + result4 + result5 + result6 + result7 + result8 + result9;
        }

        // Inlined before analysis.
        private static long f1(long value) {
            return 42 * value;
        }

        // Inlined before analysis. When f1 is inlined, then this method makes no further invokes.
        private static long f2(long value) {
            long result1 = f1(value);
            long result2 = f1(value);
            return result1 + result2;
        }


        /*
            Not inlined before analysis. Multiple invocatons that cannot be inlined.
            Not inlined after analysis either.
            [CompileQueue.makeInlineDecision][Benchmarks$IBAHelper.entry(long)->org.sample.Benchmarks$IBAHelper.f3(long)] negative decision as fallback
         */
        private static long f3(long value) {
            return System.currentTimeMillis() + value + System.currentTimeMillis();
        }

        // Inlined before analysis. Loops are ok.
        private static long f4(long value) {
            long count = 0;
            for (int i = 0; i < value; i++) {
                count += value;
            }
            return count;
        }

        // Inlined before analysis. Only a single invoke.
        private static long f5(long value) {
            long count = 2 * value;
            return System.currentTimeMillis() + count;
        }

        // Inlined before analysis. No invokes even though there are lots of computations.
        private static long f6(long value) {
            long c1 = value * value;
            long c2 = value + 42;
            long c3 = c1 + c2;
            return c3 * 2;
        }

        // Inlined before analysis
        private static long f7(long value) {
            return f3(value);
        }

        /*
            [InliningUtilities.shouldBeTrivial][()->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(0) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][()->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(1) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][()->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(2) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][()->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(3) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][()->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(4) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][()->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(5) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][()->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(6) <= MaxNodesInTrivialMethod(20)? true
            !!! Aborting inlining in InlineBeforeAnalysisGraphDecoder.abortInlining: org.sample.Benchmarks$IBAHelper.f8_inlining_phase(long)
            ### Inining Aborted in InlineBeforeAnalysisGraphDecoder.finishInlining: org.sample.Benchmarks$IBAHelper.f8_inlining_phase(long) // Initially aborted

            [2/8] Performing analysis...  [******]
            [3/8] Building universe...
            [4/8] Parsing methods...      [*]
            [5/8] Inlining methods...     [***]

            [InliningUtilities.shouldBeTrivial][Direct call from long Benchmarks$IBAHelper.entry(long)->Benchmarks$IBAHelper.f8_inlining_phase(long)] is leaf method, trivial check: number of non-invoke nodes 1 <= 40? true
            [InliningUtilities.shouldBeTrivial][Direct call from long Benchmarks$IBAHelper.entry(long)->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(1) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][Direct call from long Benchmarks$IBAHelper.entry(long)->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(2) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][Direct call from long Benchmarks$IBAHelper.entry(long)->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(3) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][Direct call from long Benchmarks$IBAHelper.entry(long)->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(4) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][Direct call from long Benchmarks$IBAHelper.entry(long)->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(5) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][Direct call from long Benchmarks$IBAHelper.entry(long)->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(6) <= MaxNodesInTrivialMethod(20)? true
            [InliningUtilities.shouldBeTrivial][Direct call from long Benchmarks$IBAHelper.entry(long)->Benchmarks$IBAHelper.f8_inlining_phase(long)] is not leaf method && numInvokes(1) <= MaxInvokesInTrivialMethod(1) && numOthers(7) <= MaxNodesInTrivialMethod(20)? true
            [ForkJoinPool.commonPool-worker-15][CompileQueue.checkNewlyTrivial][Direct call from long Benchmarks$IBAHelper.entry(long)->Benchmarks$IBAHelper.f8_inlining_phase(long)] canNewlyTrivial? true, canBeInlined? true, isTrivialMethod? true
            [CompileQueue.makeInlineDecision][Benchmarks$IBAHelper.entry(long)->org.sample.Benchmarks$IBAHelper.f8_inlining_phase(long)] positive decision because callee is considered trivial method  // After analysis the conditional block is gone, and f8_inlining_phase is the same as f5 with a single invocation.
            [CompileQueue.makeInlineDecision][Benchmarks$IBAHelper.f8_inlining_phase(long)->java.lang.System.currentTimeMillis()] negative decision as fallback     // Just like the other test cases, we cannot inline System.currentTimeMillis() into f8_inlining_phase
            [CompileQueue.makeInlineDecision][Benchmarks$IBAHelper.f8_inlining_phase(long)->java.lang.System.currentTimeMillis()] negative decision as fallback
            [CompileQueue.makeInlineDecision][Benchmarks$IBAHelper.f8_inlining_phase(long)->java.lang.System.currentTimeMillis()] negative decision as fallback
            [CompileQueue.makeInlineDecision][Benchmarks$IBAHelper.f8_inlining_phase(long)->java.lang.System.currentTimeMillis()] negative decision as fallback
         */
        private static long f8_inlining_phase(long value) {
            if (false) {
                value += System.currentTimeMillis() + value + System.currentTimeMillis();
            }
            return value + System.currentTimeMillis();
        }

        /*
        *[1/8] Initializing...                                                                                    (4.8s @ 0.30GB)
         Java version: 25+3, vendor version: GraalVM CE 25-dev+3.1
         Graal compiler: optimization level: 2, target machine: x86-64-v3
         C compiler: gcc (redhat, x86_64, 14.2.1)
         Garbage collector: Serial GC (max heap size: 80% of RAM)
         1 user-specific feature(s):
         - com.oracle.svm.thirdparty.gson.GsonFeature
        ------------------------------------------------------------------------------------------------------------------------
        Build resources:
         - 26.49GB of memory (42.4% of 62.49GB system memory, determined at start)
         - 16 thread(s) (100.0% of 16 available processor(s), determined at start)
        PEGraphDecoder.trySimplifyInvoke: org.sample.Benchmarks$IBAHelper.f9_propagation(long)
        [InliningUtilities.isTrivialMethod][Benchmarks$IBAHelper.f9_propagation(long)]
        PEGraphDecoder.trySimplifyInvoke: org.sample.Benchmarks$IBAHelper.f9_propagation_helper(boolean)
        [InliningUtilities.isTrivialMethod][Benchmarks$IBAHelper.f9_propagation_helper(boolean)]
        PEGraphDecoder.trySimplifyInvoke: org.sample.Benchmarks$IBAHelper.f9_propagation_helper0(boolean)
        [InliningUtilities.isTrivialMethod][Benchmarks$IBAHelper.f9_propagation_helper0(boolean)]
        --- Committing callee scope in InlineBeforeAnalysisPolicyUtils.commitCalleeScope: org.sample.Benchmarks$IBAHelper.f9_propagation_helper0(boolean)
        $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.f9_propagation_helper(boolean) ||| Callee: org.sample.Benchmarks$IBAHelper.f9_propagation_helper0(boolean)
        --- Committing callee scope in InlineBeforeAnalysisPolicyUtils.commitCalleeScope: org.sample.Benchmarks$IBAHelper.f9_propagation_helper(boolean)
        $$$ Completed inlining in PEGraphDecoder.finishInlining. Caller: org.sample.Benchmarks$IBAHelper.f9_propagation(long) ||| Callee: org.sample.Benchmarks$IBAHelper.f9_propagation_helper(boolean)
        [2/8] Performing analysis...  [*****]                                                                    (7.4s @ 0.66GB)
            4,745 reachable types   (70.1% of    6,765 total)
            7,462 reachable fields  (47.0% of   15,880 total)
           22,838 reachable methods (48.6% of   46,977 total)
            1,686 types,   125 fields, and   419 methods registered for reflection
               63 types,    70 fields, and    59 methods registered for JNI access
                4 native libraries: dl, pthread, rt, z
        [3/8] Building universe...                                                                               (1.5s @ 0.62GB)
        [4/8] Parsing methods...      [*]                                                                        (1.0s @ 0.68GB)
        [main] ==== Trivial Inlining  round round 1
        [main] ==== Trivial Inlining  round round 2
        [main] ==== Trivial Inlining  round round 3
        [5/8] Inlining methods...     [***]                                                                      (0.6s @ 0.73GB)
        [6/8] Compiling methods...    [***]                                                                      (7.8s @ 0.83GB)
        [7/8] Laying out methods...   [*]                                                                        (1.7s @ 0.90GB)
        [8/8] Creating image...       [*]     */
        private static long f9_propagation (long value) {
            final boolean maybePropogate = false;
            return f9_propagation_helper(maybePropogate);
        }

        private static long f9_propagation_helper(boolean maybePropogate) {
            if (!maybePropogate){
                return f9_propagation_helper0(maybePropogate);
            }
           return -1;
        }

        // this method is inlined in the before analysis phase. The value of maybePropogate must somehow get propogated.
        private static long f9_propagation_helper0(boolean maybePropogate) {
            long ret = 0;
            if (maybePropogate) {
                ret += System.currentTimeMillis() + System.currentTimeMillis();
            }
            return ret + System.currentTimeMillis();
        }


        private static long f10_propagation (long value) {
            final boolean maybePropogate = false;
            return f10_propagation_helper(maybePropogate);
        }

        // Never inlined
        private static long f10_propagation_helper(boolean maybePropogate) {
            if (!maybePropogate){
                long temp1 = f10_propagation_helper0(maybePropogate);
                long temp2  = f10_propagation_helper1(maybePropogate);
                return temp1 + temp2;
            }
            return -1;
        }

        // Only inlined after analysis
        private static long f10_propagation_helper0(boolean maybePropogate) {
            long ret = 0;
            if (maybePropogate) {
                ret += System.currentTimeMillis() + System.currentTimeMillis();
            }
            return ret + System.currentTimeMillis();
        }

        // never inlined
        private static long f10_propagation_helper1(boolean maybePropogate) {
            long ret = 0;
            if (!maybePropogate) {
                ret += System.currentTimeMillis() + System.currentTimeMillis();
            }
            return ret + System.currentTimeMillis();
        }
    }
}

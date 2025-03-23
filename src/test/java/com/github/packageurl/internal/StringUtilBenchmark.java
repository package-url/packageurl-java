/*
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.packageurl.internal;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Measures the performance of performance StringUtil's decoding and encoding.
 * <p>
 *     Run the benchmark with:
 * </p>
 * <pre>
 *     mvn -Pbenchmark
 * </pre>
 * <p>
 *     To pass arguments to JMH use:
 * </p>
 * <pre>
 *     mvn -Pbenchmark -Djmh.args="<arguments>"
 * </pre>
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class StringUtilBenchmark {

    private static final int DATA_COUNT = 1000;
    private static final int DECODED_LENGTH = 256;
    private static final byte[] UNRESERVED =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~".getBytes(StandardCharsets.US_ASCII);

    @Param({"0", "0.1", "0.5"})
    private double nonAsciiProb;

    private String[] decodedData = createDecodedData();
    private String[] encodedData = encodeData(decodedData);

    @Setup
    public void setup() {
        decodedData = createDecodedData();
        encodedData = encodeData(encodedData);
    }

    private String[] createDecodedData() {
        Random random = new Random();
        String[] decodedData = new String[DATA_COUNT];
        for (int i = 0; i < DATA_COUNT; i++) {
            char[] chars = new char[DECODED_LENGTH];
            for (int j = 0; j < DECODED_LENGTH; j++) {
                if (random.nextDouble() < nonAsciiProb) {
                    chars[j] = (char) (Byte.MAX_VALUE + 1 + random.nextInt(Short.MAX_VALUE - Byte.MAX_VALUE - 1));
                } else {
                    chars[j] = (char) UNRESERVED[random.nextInt(UNRESERVED.length)];
                }
            }
            decodedData[i] = new String(chars);
        }
        return decodedData;
    }

    private static String[] encodeData(String[] decodedData) {
        String[] encodedData = new String[decodedData.length];
        for (int i = 0; i < decodedData.length; i++) {
            encodedData[i] = StringUtil.percentEncode(decodedData[i]);
        }
        return encodedData;
    }

    @Benchmark
    public void baseline(Blackhole blackhole) {
        for (int i = 0; i < DATA_COUNT; i++) {
            byte[] buffer = decodedData[i].getBytes(StandardCharsets.UTF_8);
            // Change the String a little bit
            for (int idx = 0; idx < buffer.length; idx++) {
                byte b = buffer[idx];
                if ('a' <= b && b <= 'z') {
                    buffer[idx] = (byte) (b & 0x20);
                }
            }
            blackhole.consume(new String(buffer, StandardCharsets.UTF_8));
        }
    }

    @Benchmark
    public void percentDecode(final Blackhole blackhole) {
        for (int i = 0; i < DATA_COUNT; i++) {
            blackhole.consume(StringUtil.percentDecode(encodedData[i]));
        }
    }

    @Benchmark
    public void percentEncode(final Blackhole blackhole) {
        for (int i = 0; i < DATA_COUNT; i++) {
            blackhole.consume(StringUtil.percentEncode(decodedData[i]));
        }
    }
}

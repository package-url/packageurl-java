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
 * 
 * SPDX-License-Identifier: MIT
 * Copyright (c) AboutCode, and contributors. All Rights Reserved.
 */

package com.github.packageurl;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.DeserializationContext;

import java.util.Map;

public class PurlSpecRefTest {

    public static class TestSuite {
        @JsonProperty("$schema")
        public String schema;
        public List<TestCase> tests;
    }

    public static class TestCase {
        public String description;
        public String test_group;
        public String test_type;

        public PurlOrComponent input;
        public PurlOrComponent expected_output;

        public boolean expected_failure;
        public String expected_failure_reason;

        public TestCase() {
        }
    }

    @JsonDeserialize(using = PurlOrComponentDeserializer.class)
    public static class PurlOrComponent {
        public String purl;
        public PurlComponents components;
    }

    public static class PurlComponents {
        public String type;
        public String namespace;
        public String name;
        public String version;
        public Map<String, String> qualifiers;
        public String subpath;
    }

    public static class PurlOrComponentDeserializer extends JsonDeserializer<PurlOrComponent> {
        @Override
        public PurlOrComponent deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            ObjectCodec codec = p.getCodec();
            JsonNode node = codec.readTree(p);

            PurlOrComponent value = new PurlOrComponent();

            if (node.isTextual()) {
                value.purl = node.asText();
            } else if (node.isObject()) {
                value.components = codec.treeToValue(node, PurlComponents.class);
            }

            return value;
        }
    }

    static Stream<TestCase> collectTestCases() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        URL dirURL = PurlSpecRefTest.class.getClassLoader().getResource("purl-spec/tests/types/");
        if (dirURL == null) {
            throw new RuntimeException("Resource directory 'purl-spec/tests/types/' not found.");
        }

        Path testDataPath = Paths.get(dirURL.toURI());
        List<Path> jsonFiles = Files.list(testDataPath)
                .filter(p -> p.toString().endsWith(".json"))
                .collect(Collectors.toList());

        Stream.Builder<TestCase> builder = Stream.builder();

        for (Path jsonFile : jsonFiles) {
            try (InputStream is = Files.newInputStream(jsonFile)) {
                TestSuite suite = mapper.readValue(is, TestSuite.class);
                suite.tests.forEach(builder::add);
            }
        }

        return builder.build();
    }

    void runRoundtripTest(TestCase testCase) throws Exception {
        String result;
        try {
            result = new PackageURL(testCase.input.purl).canonicalize().toString();
        } catch (Exception e) {
            assertTrue(testCase.expected_failure, "Unexpected failure: " + e.getMessage());
            return;
        }
        assertFalse(testCase.expected_failure, "Expected failure but parsing succeeded");

        assertEquals(result, testCase.expected_output.purl);

    }

    void runBuildTest(TestCase testCase) throws Exception {
        PurlComponents input = testCase.input.components;
        String result;
        try {
            result = new PackageURL(input.type, input.namespace, input.name, input.version, input.qualifiers,
                    input.subpath).canonicalize().toString();
        } catch (Exception e) {
            assertTrue(testCase.expected_failure, "Unexpected failure: " + e.getMessage());
            return;
        }

        assertFalse(testCase.expected_failure, "Expected failure but build succeeded");
        assertEquals(result, testCase.expected_output.purl);
    }

    void runParseTest(TestCase testCase) throws Exception {
        PackageURL result;
        try {
            result = new PackageURL(testCase.input.purl);
        } catch (Exception e) {
            assertTrue(testCase.expected_failure, "Unexpected failure: " + e.getMessage());
            return;
        }
        assertFalse(testCase.expected_failure, "Expected failure but parsing succeeded");

        PurlComponents expected = testCase.expected_output.components;
        result.canonicalize();

        assertEquals(expected.type, result.getType(), "Type mismatch");
        assertEquals(expected.namespace, result.getNamespace(), "Namespace mismatch");
        assertEquals(expected.name, result.getName(), "Name mismatch");
        assertEquals(expected.version, result.getVersion(), "Version mismatch");
        assertEquals(expected.subpath, result.getSubpath(), "Subpath mismatch");

        assertEquals(
                expected.qualifiers != null ? expected.qualifiers : Collections.emptyMap(),
                result.getQualifiers(),
                "Qualifiers mismatch");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("collectTestCases")
    void runTest(TestCase testCase) throws Exception {
        switch (testCase.test_type) {
            case "roundtrip":
                runRoundtripTest(testCase);
                break;
            case "build":
                runBuildTest(testCase);
                break;
            case "parse":
                runParseTest(testCase);
                break;
            default:
                throw new IllegalArgumentException("Unknown test_type: " + testCase.test_type);
        }

    }

}

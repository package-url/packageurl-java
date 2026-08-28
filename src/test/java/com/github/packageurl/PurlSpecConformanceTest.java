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
package com.github.packageurl;

import static com.github.packageurl.PackageURLBuilder.aPackageURL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

/**
 * Runs the conformance suite of the PURL specification.
 * <p>
 * Only the {@code required} test group is enforced.
 * {@code recommended} covers remediation of non-canonical input,
 * which this implementation does not offer as a distinct mode.
 * <p>
 * {@code required} tests that do not pass yet are listed in {@code purl-conformance-known-gaps.json}.
 * To regenerate the file, run:
 * <pre>mvn test -Dtest=PurlSpecConformanceTest -Dpurl.conformance.recordGaps=true</pre>
 *
 * @see <a href="https://github.com/package-url/purl-spec/blob/main/docs/tests/test-suite.md">Test suite</a>
 */
class PurlSpecConformanceTest {

    private static final Path SPEC_DIR = Paths.get(System.getProperty("purl.spec.dir", "purl-spec"));
    private static final String KNOWN_GAPS_RESOURCE = "/purl-conformance-known-gaps.json";
    private static final Path KNOWN_GAPS_FILE =
            Paths.get("src", "test", "resources").resolve(KNOWN_GAPS_RESOURCE.substring(1));
    private static final String TEST_GROUP_REQUIRED = "required";
    private static final String[] PURL_COMPONENTS = {"type", "namespace", "name", "version", "subpath", "qualifiers"};
    private static final boolean RECORD_GAPS = Boolean.getBoolean("purl.conformance.recordGaps");
    private static final Set<String> KNOWN_GAP_KEYS = loadKnownGaps();
    private static final Map<String, JSONObject> RECORDED_GAPS = Collections.synchronizedMap(new TreeMap<>());

    @TestFactory
    Stream<DynamicNode> conformance() throws IOException {
        if (!Files.isDirectory(SPEC_DIR)) {
            throw new IllegalStateException(
                    "purl-spec not found at " + SPEC_DIR.toAbsolutePath() + "; run: git submodule update --init");
        }

        final List<DynamicNode> containers;
        try (Stream<Path> paths = Files.walk(SPEC_DIR.resolve("tests"))) {
            containers = paths.filter(path -> path.toString().endsWith(".json"))
                    .sorted()
                    .map(PurlSpecConformanceTest::toContainer)
                    .collect(Collectors.toList());
        }
        if (containers.isEmpty()) {
            throw new IllegalStateException("No fixtures below " + SPEC_DIR.resolve("tests"));
        }

        return containers.stream();
    }

    @AfterAll
    static void writeRecordedGaps() throws IOException {
        if (!RECORD_GAPS) {
            return;
        }

        final JSONArray gaps = new JSONArray();
        for (JSONObject recorded : RECORDED_GAPS.values()) {
            gaps.put(new JSONObject()
                    .put("test_type", recorded.optString("test_type"))
                    .put("description", recorded.optString("description"))
                    .put("input", normalizedInput(recorded)));
        }

        if (!Files.isDirectory(KNOWN_GAPS_FILE.toAbsolutePath().getParent())) {
            throw new IOException("Expected test resources at "
                    + KNOWN_GAPS_FILE.toAbsolutePath().getParent() + "; run from the module directory");
        }
        Files.write(
                KNOWN_GAPS_FILE,
                (new JSONObject().put("gaps", gaps).toString(2) + "\n").getBytes(StandardCharsets.UTF_8));
    }

    private static DynamicNode toContainer(Path file) {
        final JSONArray tests = readJson(file).optJSONArray("tests");
        final Map<String, List<DynamicNode>> byGroup = new TreeMap<>();

        for (int i = 0; tests != null && i < tests.length(); i++) {
            final JSONObject test = tests.getJSONObject(i);
            final String group = test.optString("test_group", TEST_GROUP_REQUIRED);
            byGroup.computeIfAbsent(group, key -> new ArrayList<>())
                    .add(dynamicTest(
                            "[" + test.optString("test_type") + "] " + test.optString("description"),
                            () -> execute(test, group)));
        }

        final List<DynamicNode> groups = new ArrayList<>();
        for (Map.Entry<String, List<DynamicNode>> group : byGroup.entrySet()) {
            groups.add(dynamicContainer(group.getKey() + " (" + group.getValue().size() + ")", group.getValue()));
        }

        return dynamicContainer(file.getFileName().toString(), groups);
    }

    private static void execute(JSONObject test, String group) {
        assumeTrue(TEST_GROUP_REQUIRED.equals(group), "recommended group: no lenient parsing mode");

        AssertionError failure = null;
        try {
            run(test);
        } catch (AssertionError e) {
            failure = e;
        } catch (RuntimeException e) {
            failure = new AssertionError(e);
        }

        final String key = keyOf(test);
        if (RECORD_GAPS) {
            if (failure != null) {
                RECORDED_GAPS.put(key, test);
            }
        } else if (KNOWN_GAP_KEYS.contains(key)) {
            if (failure == null) {
                fail("Known gap now passes; remove it from " + KNOWN_GAPS_RESOURCE + ": " + key);
            }
        } else if (failure != null) {
            throw failure;
        }
    }

    private static void run(JSONObject test) {
        final String type = test.optString("test_type");
        final boolean expectedFailure = test.optBoolean("expected_failure", false);

        if ("build".equals(type)) {
            executeBuild(test, expectedFailure);
        } else if ("parse".equals(type) || "validate".equals(type)) {
            executeParse(test, expectedFailure, "validate".equals(type));
        } else {
            throw new IllegalStateException("Unsupported test type: " + type);
        }
    }

    private static void executeParse(JSONObject test, boolean expectedFailure, boolean roundTrip) {
        final String input = test.getString("input");
        if (expectedFailure) {
            assertThrows(
                    MalformedPackageURLException.class, () -> new PackageURL(input), test.optString("description"));
            return;
        }

        final PackageURL purl;
        try {
            purl = new PackageURL(input);
        } catch (MalformedPackageURLException e) {
            throw new AssertionError("Failed to parse " + input, e);
        }

        if (roundTrip) {
            assertEquals(test.getString("expected_output"), purl.canonicalize(), "canonical form");
            return;
        }

        final JSONObject expected = test.getJSONObject("expected_output");
        assertEquals(optString(expected, "type"), purl.getType(), "type");
        assertEquals(optString(expected, "namespace"), purl.getNamespace(), "namespace");
        assertEquals(optString(expected, "name"), purl.getName(), "name");
        assertEquals(optString(expected, "version"), purl.getVersion(), "version");
        assertEquals(optString(expected, "subpath"), purl.getSubpath(), "subpath");
        assertEquals(qualifiersOf(expected), purl.getQualifiers(), "qualifiers");
    }

    private static void executeBuild(JSONObject test, boolean expectedFailure) {
        JSONObject input = test.getJSONObject("input");
        if (expectedFailure) {
            assertThrows(MalformedPackageURLException.class, () -> build(input), test.optString("description"));
            return;
        }

        try {
            assertEquals(test.getString("expected_output"), build(input).canonicalize(), "canonical form");
        } catch (MalformedPackageURLException e) {
            throw new AssertionError("Failed to build from " + input, e);
        }
    }

    private static PackageURL build(JSONObject components) throws MalformedPackageURLException {
        PackageURLBuilder builder = aPackageURL()
                .withType(optString(components, "type"))
                .withNamespace(optString(components, "namespace"))
                .withName(optString(components, "name"))
                .withVersion(optString(components, "version"))
                .withSubpath(optString(components, "subpath"));

        final JSONObject qualifiers = components.optJSONObject("qualifiers");
        if (qualifiers != null) {
            for (String key : sortedKeys(qualifiers)) {
                builder.withQualifier(key, optString(qualifiers, key));
            }
        }

        return builder.build();
    }

    private static Map<String, String> qualifiersOf(JSONObject components) {
        final JSONObject qualifiers = components.optJSONObject("qualifiers");
        if (qualifiers == null) {
            return Collections.emptyMap();
        }

        final Map<String, String> result = new LinkedHashMap<>();
        for (String key : sortedKeys(qualifiers)) {
            String value = optString(qualifiers, key);
            if (value != null && !value.isEmpty()) {
                result.put(key, value);
            }
        }

        return result;
    }

    private static String keyOf(JSONObject test) {
        return test.optString("test_type") + " " + normalizedInput(test);
    }

    private static Object normalizedInput(JSONObject test) {
        final Object input = test.get("input");
        if (!(input instanceof JSONObject)) {
            return input;
        }

        final JSONObject components = (JSONObject) input;
        final JSONObject normalized = new JSONObject();
        for (String field : PURL_COMPONENTS) {
            normalized.put(field, normalizedComponent(components, field));
        }

        return normalized;
    }

    private static Object normalizedComponent(JSONObject components, String field) {
        if (!"qualifiers".equals(field)) {
            final String value = optString(components, field);
            return value != null ? value : JSONObject.NULL;
        }

        final JSONObject qualifiers = components.optJSONObject(field);
        if (qualifiers == null) {
            return JSONObject.NULL;
        }

        final JSONObject sorted = new JSONObject();
        for (String key : sortedKeys(qualifiers)) {
            sorted.put(key, optString(qualifiers, key));
        }

        return sorted;
    }

    private static List<String> sortedKeys(JSONObject object) {
        final List<String> keys = new ArrayList<>(object.keySet());
        Collections.sort(keys);
        return keys;
    }

    private static String optString(JSONObject object, String field) {
        return object.isNull(field) ? null : object.optString(field, null);
    }

    private static Set<String> loadKnownGaps() {
        final Set<String> keys = new HashSet<>();
        try (InputStream in = PurlSpecConformanceTest.class.getResourceAsStream(KNOWN_GAPS_RESOURCE)) {
            if (in == null) {
                return keys;
            }

            final JSONArray gaps = new JSONObject(new JSONTokener(in)).optJSONArray("gaps");
            for (int i = 0; gaps != null && i < gaps.length(); i++) {
                keys.add(keyOf(gaps.getJSONObject(i)));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return keys;
    }

    private static JSONObject readJson(Path file) {
        try (InputStream in = Files.newInputStream(file)) {
            return new JSONObject(new JSONTokener(in));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + file, e);
        }
    }
}

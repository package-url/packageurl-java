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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.params.provider.Arguments;

class PurlParameters {
    static Stream<Arguments> getTestDataFromFiles(String... names) throws IOException {
        Stream<Arguments> result = Stream.empty();
        for (String name : names) {
            try (InputStream is = PackageURLTest.class.getResourceAsStream("/" + name)) {
                assertNotNull(is);
                JSONArray jsonArray = new JSONArray(new JSONTokener(is));
                result = Stream.concat(
                        result,
                        IntStream.range(0, jsonArray.length())
                                .mapToObj(jsonArray::getJSONObject)
                                .map(PurlParameters::createTestDefinition));
            }
        }
        return result;
    }

    /**
     * Returns test arguments:
     * <ol>
     *     <li>Serialized PURL</li>
     *     <li>PURL split into components</li>
     *     <li>Canonical serialized PURL</li>
     *     <li>{@code true} if the PURL is not valid</li>
     * </ol>
     */
    private static Arguments createTestDefinition(JSONObject testDefinition) {
        return Arguments.of(
                testDefinition.getString("description"),
                testDefinition.optString("purl"),
                new PurlParameters(
                        testDefinition.optString("type", null),
                        testDefinition.optString("namespace", null),
                        testDefinition.optString("name", null),
                        testDefinition.optString("version", null),
                        testDefinition.optJSONObject("qualifiers"),
                        testDefinition.optString("subpath", null)),
                testDefinition.optString("canonical_purl"),
                testDefinition.getBoolean("is_invalid"));
    }

    private final @Nullable String type;
    private final @Nullable String namespace;
    private final @Nullable String name;
    private final @Nullable String version;
    private final Map<String, String> qualifiers;
    private final @Nullable String subpath;

    private PurlParameters(
            @Nullable String type,
            @Nullable String namespace,
            @Nullable String name,
            @Nullable String version,
            @Nullable JSONObject qualifiers,
            @Nullable String subpath) {
        this.type = type;
        this.namespace = namespace;
        this.name = name;
        this.version = version;
        if (qualifiers != null) {
            this.qualifiers = qualifiers.toMap().entrySet().stream()
                    .collect(
                            HashMap::new,
                            (m, e) -> m.put(e.getKey(), Objects.toString(e.getValue(), null)),
                            HashMap::putAll);
        } else {
            this.qualifiers = Collections.emptyMap();
        }
        this.subpath = subpath;
    }

    public @Nullable String getType() {
        return type;
    }

    public @Nullable String getNamespace() {
        return namespace;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getVersion() {
        return version;
    }

    public Map<String, @Nullable String> getQualifiers() {
        return Collections.unmodifiableMap(qualifiers);
    }

    public @Nullable String getSubpath() {
        return subpath;
    }

    @Override
    public String toString() {
        return "(" + type + ", " + namespace + ", " + name + ", " + version + ", " + qualifiers + ", " + subpath + ")";
    }
}

/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author Eduardo P. Garcia del Valle
 */
public class GenericSemiStructuredTextParser {

    private final String recordDelimiter;
    private final String propertyDelimiter;
    private final String idKey;
    private boolean isTerm = false;
    private Record currentRecord;
    private List<Record> result = new ArrayList();

    public GenericSemiStructuredTextParser(
            String recordDelimiter, String propertyDelimiter, String idKey) {

        this.recordDelimiter = recordDelimiter;
        this.propertyDelimiter = propertyDelimiter;
        this.idKey = idKey;
    }

    public final List<Record> parse(Path filePath, String encoding) throws IOException {
        try (Scanner scanner = new Scanner(filePath, encoding)) {
            while (scanner.hasNextLine()) {
                processLine(scanner.nextLine());
            }
        }

        return result;
    }

    protected void processLine(String aLine) {
        aLine = aLine.trim();

        if (aLine.isEmpty()) {
            isTerm = false;

            return;
        }

        if (aLine.startsWith(recordDelimiter)) {
            isTerm = true;

            if (currentRecord != null) {
                result.add(currentRecord);
            }

            currentRecord = new Record();

            return;
        }

        if (!isTerm) {
            return;
        }

        Scanner scanner = new Scanner(aLine);

        scanner.useDelimiter(propertyDelimiter);

        if (!scanner.hasNext()) {
            return;
        }

        String key = scanner.next();
        String value = scanner.next();

        if (key.equals(idKey)) {
            currentRecord.setId(value);

            return;
        }

        currentRecord.addProperty(key, value);
    }

    public class Record {
        private String id;
        private Map<String, List<String>> properties = new HashMap();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, List<String>> getProperties() {
            return properties;
        }

        public void addProperty(String key, String value) {
            if (properties.containsKey(key)) {
                List<String> values = properties.get(key);

                values.add(value);

                properties.put(key, values);
            } else {
                properties.put(key, new ArrayList(Arrays.asList(new String[]{value})));
            }
        }

        public String getPropertyValue(String key) {
            if (hasProperty(key)) {
                return properties.get(key).stream().findFirst().get();
            }

            return null;
        }

        public String getPropertyValue(String key, String valuePrefix) {
            if (hasPropertyValue(key, valuePrefix)) {
                return getPropertyValues(key, valuePrefix).stream().findFirst().get();
            }

            return null;
        }

        public List<String> getPropertyValues(String key) {
            return properties.get(key);
        }

        public List<String> getPropertyValues(String key, String valuePrefix) {
            if (hasPropertyValue(key, valuePrefix)) {
                return properties.get(key).stream().filter(o -> o.startsWith(valuePrefix)).map(o -> {
                    return o.replace(valuePrefix, "");
                }).collect(toList());
            }

            return Collections.emptyList();
        }

        public boolean hasProperty(String key) {
            return properties.containsKey(key);
        }

        public boolean hasPropertyValue(String key, String valuePrefix) {
            if (hasProperty(key)) {
                return properties.get(key).stream().filter(o -> o.startsWith(valuePrefix)).findFirst().isPresent();
            }

            return false;
        }
    }

}
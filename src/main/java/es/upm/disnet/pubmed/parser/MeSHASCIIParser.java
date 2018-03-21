/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.parser;

import es.upm.disnet.pubmed.parser.GenericSemiStructuredTextParser.Record;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Eduardo P. Garcia del Valle
 */
public class MeSHASCIIParser {

    public final static String RECORD_DELIMETER = "*NEWRECORD";
    public final static String PROPERTY_DELIMETER = " = ";
    public final static String ID_KEY = "UI";
    private final GenericSemiStructuredTextParser parser = new GenericSemiStructuredTextParser(
            RECORD_DELIMETER, PROPERTY_DELIMETER, ID_KEY
    );

    public final List<Record> parse(String filePath, String encoding) throws IOException {
        return parser.parse(filePath, encoding);
    }
}
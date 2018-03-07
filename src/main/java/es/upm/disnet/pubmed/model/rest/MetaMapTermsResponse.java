/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * @author Eduardo P. Garcia del Valle
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaMapTermsResponse {
    private Text[] textList;

    private String validationMesssage;

    private Configuration configuration;

    public Text[] getTextList() {
        return textList;
    }

    public void setTextList(Text[] textList) {
        this.textList = textList;
    }

    public String getValidationMesssage() {
        return validationMesssage;
    }

    public void setValidationMesssage(String validationMesssage) {
        this.validationMesssage = validationMesssage;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @JsonIgnore
    public Concept[] getConcepts(int index) {
        if ((textList != null) && (textList.length > index)) {
            return textList[index].getConcepts();
        }

        return null;
    }

    @Override
    public String toString() {
        return "ClassPojo [textList = " + textList + ", validationMesssage = " + validationMesssage + ", configuration = " + configuration + "]";
    }

    public static class Text {
        private String id;

        private Concept[] concepts;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Concept[] getConcepts() {
            return concepts;
        }

        public void setConcepts(Concept[] concepts) {
            this.concepts = concepts;
        }

        @Override
        public String toString() {
            return "ClassPojo [id = " + id + ", concepts = " + concepts + "]";
        }
    }

    public static class Concept {
        private String positionalInfo;

        private String[] matchedWords;

        private String name;

        private String cui;

        private String[] semanticTypes;

        public String getPositionalInfo() {
            return positionalInfo;
        }

        public void setPositionalInfo(String positionalInfo) {
            this.positionalInfo = positionalInfo;
        }

        public String[] getMatchedWords() {
            return matchedWords;
        }

        public void setMatchedWords(String[] matchedWords) {
            this.matchedWords = matchedWords;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCui() {
            return cui;
        }

        public void setCui(String cui) {
            this.cui = cui;
        }

        public String[] getSemanticTypes() {
            return semanticTypes;
        }

        public void setSemanticTypes(String[] semanticTypes) {
            this.semanticTypes = semanticTypes;
        }

        @Override
        public String toString() {
            return "ClassPojo [positionalInfo = " + positionalInfo + ", matchedWords = " + matchedWords + ", name = " + name + ", cui = " + cui + ", semanticTypes = " + semanticTypes + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof Concept)) {
                return false;
            }

            Concept c = (Concept) o;

            return c.getCui().equals(cui);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cui);
        }
    }

    public static class Configuration {
        private String[] semanticTypes;

        private String[] sources;

        private String options;

        public String[] getSemanticTypes() {
            return semanticTypes;
        }

        public void setSemanticTypes(String[] semanticTypes) {
            this.semanticTypes = semanticTypes;
        }

        public String[] getSources() {
            return sources;
        }

        public void setSources(String[] sources) {
            this.sources = sources;
        }

        public String getOptions() {
            return options;
        }

        public void setOptions(String options) {
            this.options = options;
        }

        @Override
        public String toString() {
            return "ClassPojo [semanticTypes = " + semanticTypes + ", sources = " + sources + ", options = " + options + "]";
        }
    }
}

/*
{
    "configuration": {
        "semanticTypes": [
            "sosy",
            "diap",
            "dsyn",
            "fndg",
            "lbpr",
            "lbtr"
        ],
        "sources": [
            "SNOMEDCT_US",
            "DSM-5"
        ],
        "options": "-y -R"
    },
    "validationMesssage": "OK",
    "textList": [
        {
            "id": "1",
            "concepts": [
                {
                    "cui": "C0012634",
                    "name": "Disease",
                    "semanticTypes": [
                        "dsyn"
                    ],
                    "matchedWords": [
                        "disease"
                    ],
                    "positionalInfo": "[(0, 7)]"
                },
                {
                    "cui": "C0030193",
                    "name": "Pain",
                    "semanticTypes": [
                        "sosy"
                    ],
                    "matchedWords": [
                        "pain"
                    ],
                    "positionalInfo": "[(17, 4)]"
                },
                {
                    "cui": "C1262477",
                    "name": "Weight decreased",
                    "semanticTypes": [
                        "fndg"
                    ],
                    "matchedWords": [
                        "weightloss"
                    ],
                    "positionalInfo": "[(23, 11)]"
                },
                {
                    "cui": "C0030193",
                    "name": "Pain",
                    "semanticTypes": [
                        "sosy"
                    ],
                    "matchedWords": [
                        "pain"
                    ],
                    "positionalInfo": "[(50, 4)]"
                },
                {
                    "cui": "C1262477",
                    "name": "Weight decreased",
                    "semanticTypes": [
                        "fndg"
                    ],
                    "matchedWords": [
                        "weightloss"
                    ],
                    "positionalInfo": "[(59, 11)]"
                }
            ]
        }
    ]
}
 */


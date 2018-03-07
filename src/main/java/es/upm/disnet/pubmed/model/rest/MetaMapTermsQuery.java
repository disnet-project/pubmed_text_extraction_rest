/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Eduardo P. Garcia del Valle
 */
public class MetaMapTermsQuery {
    private Text[] textList;

    private Configuration configuration;

    public Text[] getTextList() {
        return textList;
    }

    public void setTextList(Text[] textList) {
        this.textList = textList;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return "ClassPojo [textList = " + textList + ", configuration = " + configuration + "]";
    }

    public static class Text {
        private String id;

        private String text;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "ClassPojo [id = " + id + ", text = " + text + "]";
        }
    }

    public static class Configuration {
        private String[] semanticTypes;

        private String[] sources;

        private String options;

        private boolean conceptLocation;

        @JsonProperty("concept_location")
        public boolean getConceptLocation() {
            return conceptLocation;
        }

        @JsonProperty("concept_location")
        public void setConceptLocation(boolean conceptLocation) {
            this.conceptLocation = conceptLocation;
        }

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
			"sosy", "diap", "dsyn", "fndg", "lbpr", "lbtr"
		],
		"sources": [
			"SNOMEDCT_US", "DSM-5"
		],
		"options": "-y -R"
	},
	"text": [
		{
			"id": "1",
			"text": "Disease produces pain, weight loss. Also produces pain and weight loss"
		}
	]
}
 */

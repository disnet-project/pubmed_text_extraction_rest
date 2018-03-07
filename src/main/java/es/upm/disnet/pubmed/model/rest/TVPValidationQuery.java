/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.model.rest;

/**
 * @author Eduardo P. Garcia del Valle
 */
public class TVPValidationQuery {
    private Concept[] concepts;

    public Concept[] getConcepts() {
        return concepts;
    }

    public void setConcepts(Concept[] concepts) {
        this.concepts = concepts;
    }

    @Override
    public String toString() {
        return "ClassPojo [concepts = " + concepts + "]";
    }

    public static class Concept {
        private String name;

        private String cui;

        public Concept(String name, String cui) {
            this.name = name;
            this.cui = cui;
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

        @Override
        public String toString() {
            return "ClassPojo [name = " + name + ", cui = " + cui + "]";
        }
    }
}

/*
{
	"concepts" : [
		{
		  "cui" : "C0002627",
		  "name" : "Amniocentesis"
		},
		{
	    	"cui": "C0015967",
	    	"name": "Fever"
		},
        {
          "cui": "C0262655",
          "name": "Recurrent urinary tract infection"
        },
        {
          "cui": "C0429826",
          "name": "Dysfunctional voiding of urine"
        },
        {
          "cui": "C0009450",
          "name": "Communicable disease"
        },
        {
		  "cui" : "C0002871",
		  "name" : "Anemia"
		}
		]
}
 */
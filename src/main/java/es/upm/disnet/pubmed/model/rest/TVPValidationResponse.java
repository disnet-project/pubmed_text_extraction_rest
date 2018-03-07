/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Eduardo P. Garcia del Valle
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TVPValidationResponse {
    private ValidatedConcept[] validatedConcepts;

    private String authorized;

    private String authorizationMessage;

    private String token;

    public ValidatedConcept[] getValidatedConcepts() {
        return validatedConcepts;
    }

    public void setValidatedConcepts(ValidatedConcept[] validatedConcepts) {
        this.validatedConcepts = validatedConcepts;
    }

    public String getAuthorized() {
        return authorized;
    }

    public void setAuthorized(String authorized) {
        this.authorized = authorized;
    }

    public String getAuthorizationMessage() {
        return authorizationMessage;
    }

    public void setAuthorizationMessage(String authorizationMessage) {
        this.authorizationMessage = authorizationMessage;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "ClassPojo [validatedConcepts = " + validatedConcepts + ", authorized = " + authorized + ", authorizationMessage = " + authorizationMessage + ", token = " + token + "]";
    }

    public static class Matches {
        private String validationMethod;

        private ValidationFinding validationFinding;

        private String validationFindingString;

        public String getValidationMethod() {
            return validationMethod;
        }

        public void setValidationMethod(String validationMethod) {
            this.validationMethod = validationMethod;
        }

        public ValidationFinding getValidationFinding() {
            return validationFinding;
        }

        public void setValidationFinding(ValidationFinding validationFinding) {
            this.validationFinding = validationFinding;
        }

        public String getValidationFindingString() {
            return validationFindingString;
        }

        public void setValidationFindingString(String validationFindingString) {
            this.validationFindingString = validationFindingString;
        }

        @Override
        public String toString() {
            return "ClassPojo [validationMethod = " + validationMethod + ", validationFinding = " + validationFinding + ", validationFindingString = " + validationFindingString + "]";
        }
    }

    public static class Concept {
        private String name;

        private String cui;

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

    public static class ValidatedConcept {
        private Matches[] matches;

        private Concept concept;

        private boolean hasMatches;

        public Matches[] getMatches() {
            return matches;
        }

        public void setMatches(Matches[] matches) {
            this.matches = matches;
        }

        public Concept getConcept() {
            return concept;
        }

        public void setConcept(Concept concept) {
            this.concept = concept;
        }

        public boolean getHasMatches() {
            return hasMatches;
        }

        public void setHasMatches(boolean hasMatches) {
            this.hasMatches = hasMatches;
        }

        @Override
        public String toString() {
            return "ClassPojo [matches = " + matches + ", concept = " + concept + ", hasMatches = " + hasMatches + "]";
        }
    }

    public static class ValidationFinding {
        private String[] cuis;

        private String source;

        private String name;

        private String[] codes;

        private String[] synonyms;

        private String code;

        private String uri;

        public String[] getCuis() {
            return cuis;
        }

        public void setCuis(String[] cuis) {
            this.cuis = cuis;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String[] getCodes() {
            return codes;
        }

        public void setCodes(String[] codes) {
            this.codes = codes;
        }

        public String[] getSynonyms() {
            return synonyms;
        }

        public void setSynonyms(String[] synonyms) {
            this.synonyms = synonyms;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        @Override
        public String toString() {
            return "ClassPojo [cuis = " + cuis + ", source = " + source + ", name = " + name + ", codes = " + codes + ", synonyms = " + synonyms + ", code = " + code + ", uri = " + uri + "]";
        }
    }
}

/*
{
    "token": null,
    "authorized": true,
    "authorizationMessage": "Authorization out of use",
    "validatedConcepts": [
        {
            "concept": {
                "cui": "C0002627",
                "name": "Amniocentesis"
            },
            "hasMatches": true,
            "matches": [
                {
                    "validationFinding": {
                        "name": "Amniocentesis",
                        "code": "/en/amniocentesis",
                        "uri": "http://www.freebase.com//en/amniocentesis",
                        "source": "Freebase",
                        "cuis": [
                            "n/a"
                        ],
                        "synonyms": [
                            "n/a"
                        ],
                        "codes": [
                            "n/a"
                        ]
                    },
                    "validationFindingString": "Amniocentesis {AMNIOCENTESIS}",
                    "validationMethod": "Equals"
                },
                {
                    "validationFinding": {
                        "name": "Amniocentesis",
                        "code": "n/a",
                        "uri": "n/a",
                        "source": "MediciNet",
                        "cuis": [
                            "n/a"
                        ],
                        "synonyms": [
                            "n/a"
                        ],
                        "codes": [
                            "n/a"
                        ]
                    },
                    "validationFindingString": "Amniocentesis {AMNIOCENTESIS}",
                    "validationMethod": "Equals"
                },
                {
                    "validationFinding": {
                        "name": "Amniotic Fluid",
                        "code": "n/a",
                        "uri": "n/a",
                        "source": "MediciNet",
                        "cuis": [
                            "n/a"
                        ],
                        "synonyms": [
                            "Amniocentesis"
                        ],
                        "codes": [
                            "n/a"
                        ]
                    },
                    "validationFindingString": "Amniocentesis {AMNIOCENTESIS}",
                    "validationMethod": "Equals"
                },
                {
                    "validationFinding": {
                        "name": "Prenatal Diagnosis",
                        "code": "n/a",
                        "uri": "n/a",
                        "source": "MediciNet",
                        "cuis": [
                            "n/a"
                        ],
                        "synonyms": [
                            "Amniocentesis"
                        ],
                        "codes": [
                            "n/a"
                        ]
                    },
                    "validationFindingString": "Amniocentesis {AMNIOCENTESIS}",
                    "validationMethod": "Equals"
                }
            ]
        },
        {
            "concept": {
                "cui": "C0015967",
                "name": "Fever"
            },
            "hasMatches": true,
            "matches": [
                {
                    "validationFinding": {
                        "name": "Fever",
                        "code": "/en/fever",
                        "uri": "http://www.freebase.com//en/fever",
                        "source": "Freebase",
                        "cuis": [
                            "C0015967",
                            "C0743973"
                        ],
                        "synonyms": [
                            "Controlled hyperthermia",
                            "Fever in children",
                            "Fever temperature",
                            "High Fever",
                            "Infant fever",
                            "Night fever",
                            "Pyrexia"
                        ],
                        "codes": [
                            "D005334"
                        ]
                    },
                    "validationFindingString": "Fever",
                    "validationMethod": "CUI"
                },
                {
                    "validationFinding": {
                        "name": "Fever, unspecified",
                        "code": "R50.9",
                        "uri": "http://purl.bioontology.org/ontology/ICD10CM/R50.9",
                        "source": "ICD10CM",
                        "cuis": [
                            "C0015967"
                        ],
                        "synonyms": [
                            "n/a"
                        ],
                        "codes": [
                            "n/a"
                        ]
                    },
                    "validationFindingString": "Fever, unspecified",
                    "validationMethod": "CUI"
                },
                {
                    "validationFinding": {
                        "name": "Fever, unspecified",
                        "code": "780.60",
                        "uri": "http://purl.bioontology.org/ontology/ICD9CM/780.60",
                        "source": "ICD9CM",
                        "cuis": [
                            "C0015967"
                        ],
                        "synonyms": [
                            "n/a"
                        ],
                        "codes": [
                            "n/a"
                        ]
                    },
                    "validationFindingString": "Fever, unspecified",
                    "validationMethod": "CUI"
                },
                {
                    "validationFinding": {
                        "name": "fever",
                        "code": "SYMP:0000613",
                        "uri": "http://purl.obolibrary.org/obo/SYMP_0000613",
                        "source": "Symptoms Ontology",
                        "cuis": [
                            "n/a"
                        ],
                        "synonyms": [
                            "n/a"
                        ],
                        "codes": [
                            "n/a"
                        ]
                    },
                    "validationFindingString": "fever {FEVER}",
                    "validationMethod": "Equals"
                },
                {
                    "validationFinding": {
                        "name": "Fever",
                        "code": "2.3.3 and 22.1",
                        "uri": "http://who.int/ictm/signsAndSymptoms#TM529451",
                        "source": "TM Signs and Symptoms Ontology",
                        "cuis": [
                            "n/a"
                        ],
                        "synonyms": [
                            "n/a"
                        ],
                        "codes": [
                            "n/a"
                        ]
                    },
                    "validationFindingString": "Fever {FEVER}",
                    "validationMethod": "Equals"
                }
            ]
        },
        {
            "concept": {
                "cui": "C0262655",
                "name": "Recurrent urinary tract infection"
            },
            "hasMatches": false,
            "matches": []
        },
        {
            "concept": {
                "cui": "C0429826",
                "name": "Dysfunctional voiding of urine"
            },
            "hasMatches": false,
            "matches": []
        },
        {
            "concept": {
                "cui": "C0009450",
                "name": "Communicable disease"
            },
            "hasMatches": false,
            "matches": []
        },
        {
            "concept": {
                "cui": "C0002871",
                "name": "Anemia"
            },
            "hasMatches": true,
            "matches": [
                {
                    "validationFinding": {
                        "name": "Anemia",
                        "code": "/en/anemia",
                        "uri": "http://www.freebase.com//en/anemia",
                        "source": "Freebase",
                        "cuis": [
                            "C0002871"
                        ],
                        "synonyms": [
                            "Anaemia",
                            "Anæmia",
                            "Lack of blood"
                        ],
                        "codes": [
                            "D000740"
                        ]
                    },
                    "validationFindingString": "Anemia",
                    "validationMethod": "CUI"
                },
                {
                    "validationFinding": {
                        "name": "anemia",
                        "code": "SYMP:0000208",
                        "uri": "http://purl.obolibrary.org/obo/SYMP_0000208",
                        "source": "Symptoms Ontology",
                        "cuis": [
                            "n/a"
                        ],
                        "synonyms": [
                            "n/a"
                        ],
                        "codes": [
                            "n/a"
                        ]
                    },
                    "validationFindingString": "anemia {ANEMIA}",
                    "validationMethod": "Equals"
                },
                {
                    "validationFinding": {
                        "name": "anaemia",
                        "code": "SYMP:0000208",
                        "uri": "http://purl.obolibrary.org/obo/SYMP_0000208",
                        "source": "Symptoms Ontology",
                        "cuis": [
                            "n/a"
                        ],
                        "synonyms": [
                            "n/a"
                        ],
                        "codes": [
                            "n/a"
                        ]
                    },
                    "validationFindingString": "anaemia {ANAEMIA}",
                    "validationMethod": "Similarity"
                }
            ]
        }
    ]
}
 */
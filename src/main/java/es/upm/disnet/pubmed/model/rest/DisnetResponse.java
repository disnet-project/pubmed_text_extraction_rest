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

import java.util.*;

/**
 * @author Eduardo P. Garcia del Valle
 */
public class DisnetResponse {

    private String responseMessage;

    private String responseCode;

    private String authorized;

    private String[] errorsFound;

    private String authorizationMessage;

    private String token;

    private Disease[] diseaseList;

    private int diseaseCount;

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getAuthorized() {
        return authorized;
    }

    public void setAuthorized(String authorized) {
        this.authorized = authorized;
    }

    public String[] getErrorsFound() {
        return errorsFound;
    }

    public void setErrorsFound(String[] errorsFound) {
        this.errorsFound = errorsFound;
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

    public Disease[] getDiseaseList() {
        return diseaseList;
    }

    public void setDiseaseList(Disease[] diseaseList) {
        this.diseaseList = diseaseList;
    }

    public int getDiseaseCount() {
        return diseaseCount;
    }

    public void setDiseaseCount(int diseaseCount) {
        this.diseaseCount = diseaseCount;
    }

    @Override
    public String toString() {
        return "ClassPojo [responseMessage = " + responseMessage + ", responseCode = " + responseCode + ", authorized = " + authorized + ", errorsFound = " + errorsFound + ", authorizationMessage = " + authorizationMessage + ", token = " + token + ", diseaseList = " + diseaseList + ", diseaseCount = " + diseaseCount + "]";
    }

    @JsonIgnore
    public Concept[] getConceptList() {
        List<Concept> concepts = new ArrayList();

        for (Disease disease : diseaseList) {
            concepts.addAll(Arrays.asList(disease.getDisnetConceptList()));
        }

        return concepts.stream().toArray(size -> new Concept[concepts.size()]);
    }

    public static class Disease {
        private String name;

        private Concept[] disnetConceptList;

        private String url;

        private int disnetConceptsCount;
        private Code[] codes;
        private int codesCount;

        public Code[] getCodes() {
            return codes;
        }

        public void setCodes(Code[] codes) {
            this.codes = codes;
        }

        public int getCodesCount() {
            return codesCount;
        }

        public void setCodesCount(int codesCount) {
            this.codesCount = codesCount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Concept[] getDisnetConceptList() {
            return disnetConceptList;
        }

        public void setDisnetConceptList(Concept[] disnetConceptList) {
            this.disnetConceptList = disnetConceptList;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getDisnetConceptsCount() {
            return disnetConceptsCount;
        }

        public void setDisnetConceptsCount(int disnetConceptsCount) {
            this.disnetConceptsCount = disnetConceptsCount;
        }

        @Override
        public String toString() {
            return "ClassPojo [name = " + name + ", disnetConceptList = " + disnetConceptList + ", url = " + url + ", disnetConceptsCount = " + disnetConceptsCount + "]";
        }

        @JsonIgnore
        public boolean containsCode(String typeCode, String code) {
            Optional<Code> optional = Arrays.stream(codes)
                    .filter(c -> code.equals(c.code) && typeCode.equals(c.typeCode))
                    .findFirst();

            if (optional.isPresent()) {//Check whether optional has element you are looking for
                return true;
            }

            return false;
        }
    }

    public static class Concept {
        private String name;

        private String cui;

        private String[] semanticTypes;

        private DetectionInformation detectionInformation;

        public DetectionInformation getDetectionInformation() {
            return detectionInformation;
        }

        public void setDetectionInformation(DetectionInformation detectionInformation) {
            this.detectionInformation = detectionInformation;
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
            return "ClassPojo [name = " + name + ", cui = " + cui + ", semanticTypes = " + semanticTypes + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof DisnetResponse.Concept)) {
                return false;
            }

            DisnetResponse.Concept c = (DisnetResponse.Concept) o;

            return c.getCui().equals(cui);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cui);
        }
    }

    public static class DetectionInformation {
        private int timesFoundInTexts;

        public int getTimesFoundInTexts() {
            return timesFoundInTexts;
        }

        public void setTimesFoundInTexts(int timesFoundInTexts) {
            this.timesFoundInTexts = timesFoundInTexts;
        }
    }

    public static class Code {
        private String typeCode;

        private String code;

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "ClassPojo [typeCode = " + typeCode + ", code = " + code + "]";
        }
    }


}

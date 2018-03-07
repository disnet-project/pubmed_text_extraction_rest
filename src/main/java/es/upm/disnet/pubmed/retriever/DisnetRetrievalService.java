/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.retriever;

import es.upm.disnet.pubmed.model.rest.DisnetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Service
public class DisnetRetrievalService {
    public static final String DISEASE_CODE_MESH = "MeSH";
    public static final String DISEASE_CODE_OMIM = "OMIM";
    public static final String DISEASE_CODE_ICD10 = "ICD-10";
    private static final Logger log = LoggerFactory.getLogger(DisnetRetrievalService.class);
    private static final String DISNET_CONCEPTS_SERVICE_URL =
            "http://disnet.ctb.upm.es/api/disnet/query/disnetConceptList";
    private static final String DISNET_DISEASE_SERVICE_URL =
            "http://disnet.ctb.upm.es/api/disnet/query/diseaseList";
    private static final String VERSION_PARAM_KEY = "version";
    private static final String SOURCE_PARAM_KEY = "source";
    private static final String DETECTION_INFORMATION_KEY = "detectionInformation";
    private static final String DETECTION_INFORMATION_VALUE = "true";
    private static final String SOURCE_PARAM_VALUE = "wikipedia";
    private static final String DISEASE_CODE_KEY = "diseaseCode";
    private static final String DISEASE_CODE_TYPE_KEY = "typeCode";
    private static final String TOKEN_KEY = "token";
    private static final String TOKEN_VALUE = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlcC5nYXJjaWFAYWx1bW5vcy51cG0uZXMiLCJhdWQiOiJ3ZWIiLCJuYW1lIjoiRWR1YXJkbyBHYXJjaWEgZGVsIFZhbGxlIiwidXNlciI6dHJ1ZSwiaWF0IjoxNTEzNzc3NDM1fQ.FbZLLyJW8IoRYuKHCaOEAD_51ItWs549GStZvNi-7sLWP5ZWsqXdHeNh3KY5lrXPHQwV2JD_6aJclUA5CmwYEA";

    /**
     * http://disnet.ctb.upm.es/api/disnet/query/disnetConceptList?
     * source=wikipedia&version=2018-02-01&diseaseCode=D000562&typeCode=MeSH&
     * token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlcC5nYXJjaWFAYWx1bW5vcy51cG0uZXMiLCJhdWQiOiJ3ZWIiLCJuYW1lIjoiRWR1YXJkbyBHYXJjaWEgZGVsIFZhbGxlIiwidXNlciI6dHJ1ZSwiaWF0IjoxNTEzNzc3NDM1fQ.FbZLLyJW8IoRYuKHCaOEAD_51ItWs549GStZvNi-7sLWP5ZWsqXdHeNh3KY5lrXPHQwV2JD_6aJclUA5CmwYEA
     *
     * @param diseaseCodeType
     * @param diseaseCode
     * @param snapshot
     * @return
     * @throws Exception
     */
    public DisnetResponse.Concept[] getDisnetTerms(String diseaseCodeType, String diseaseCode, String snapshot) throws Exception {
        URI targetUrl = UriComponentsBuilder.fromUriString(DISNET_CONCEPTS_SERVICE_URL)
                .queryParam(SOURCE_PARAM_KEY, SOURCE_PARAM_VALUE)
                .queryParam(DETECTION_INFORMATION_KEY, DETECTION_INFORMATION_VALUE)
                .queryParam(VERSION_PARAM_KEY, snapshot)
                .queryParam(DISEASE_CODE_TYPE_KEY, diseaseCodeType)
                .queryParam(DISEASE_CODE_KEY, diseaseCode)
                .queryParam(TOKEN_KEY, TOKEN_VALUE)
                .build()
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        try {
            DisnetResponse disnetResponse = restTemplate.getForObject(targetUrl, DisnetResponse.class);

            String responseCode = disnetResponse.getResponseCode();

            if ((responseCode == null) || !responseCode.equals(String.valueOf(HttpStatus.OK))) {
                return null;
            }

            log.debug("Retrieved DisNet Terms {}", disnetResponse.toString());

            return disnetResponse.getConceptList();
        } catch (HttpMessageNotReadableException hmnre) {
            log.error("Unexpected response");

            return null;
        }
    }

    /**
     * http://disnet.ctb.upm.es:80/api/disnet/query/diseaseList?
     * source=wikipedia&version=2018-02-01&
     * token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlcC5nYXJjaWFAYWx1bW5vcy51cG0uZXMiLCJhdWQiOiJ3ZWIiLCJuYW1lIjoiRWR1YXJkbyBHYXJjaWEgZGVsIFZhbGxlIiwidXNlciI6dHJ1ZSwiaWF0IjoxNTEzNzc3NDM1fQ.FbZLLyJW8IoRYuKHCaOEAD_51ItWs549GStZvNi-7sLWP5ZWsqXdHeNh3KY5lrXPHQwV2JD_6aJclUA5CmwYEA
     *
     * @param snapshot
     * @return
     * @throws Exception
     */
    public DisnetResponse.Disease[] getDisnetDiseases(String snapshot) throws Exception {
        URI targetUrl = UriComponentsBuilder.fromUriString(DISNET_DISEASE_SERVICE_URL)
                .queryParam(SOURCE_PARAM_KEY, SOURCE_PARAM_VALUE)
                .queryParam(VERSION_PARAM_KEY, snapshot)
                .queryParam(TOKEN_KEY, TOKEN_VALUE)
                .build()
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        try {
            DisnetResponse disnetResponse = restTemplate.getForObject(targetUrl, DisnetResponse.class);

            String responseCode = disnetResponse.getResponseCode();

            if ((responseCode == null) || !responseCode.equals(String.valueOf(HttpStatus.OK))) {
                return null;
            }

            log.debug("Retrieved DisNet Diseases {}", disnetResponse.toString());

            return disnetResponse.getDiseaseList();
        } catch (HttpMessageNotReadableException hmnre) {
            log.error("Unexpected response");

            return null;
        }
    }

}


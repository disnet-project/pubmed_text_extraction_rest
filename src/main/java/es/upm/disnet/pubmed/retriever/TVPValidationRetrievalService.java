/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.retriever;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.disnet.pubmed.model.rest.MetaMapTermsResponse;
import es.upm.disnet.pubmed.model.rest.TVPValidationQuery;
import es.upm.disnet.pubmed.model.rest.TVPValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.stream.Stream;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Service
public class TVPValidationRetrievalService {
    private static final Logger log = LoggerFactory.getLogger(TVPValidationRetrievalService.class);
    private static final String TVP_SERVICE_URL = "http://138.4.130.6:11062/api/tvp/validate";

    public TVPValidationResponse getTVPValidation(MetaMapTermsResponse.Concept[] concepts) throws Exception {
        TVPValidationQuery tvpValidationQuery = new TVPValidationQuery();

        TVPValidationQuery.Concept[] queryConcepts = Stream.of(concepts)
                .map(c -> new TVPValidationQuery.Concept(c.getName(), c.getCui()))
                .toArray(TVPValidationQuery.Concept[]::new);

        tvpValidationQuery.setConcepts(queryConcepts);

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();

        String tvpValidationQueryJson = objectMapper.writeValueAsString(tvpValidationQuery);

        HttpEntity<String> httpEntity = new HttpEntity<>(tvpValidationQueryJson, httpHeaders);

        TVPValidationResponse tvpValidationResponse =
                restTemplate.postForObject(TVP_SERVICE_URL, httpEntity, TVPValidationResponse.class);

        log.debug("Retrieved Validation {}", tvpValidationResponse.toString());

        return tvpValidationResponse;
    }
}



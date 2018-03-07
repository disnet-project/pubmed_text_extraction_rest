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
import es.upm.disnet.pubmed.model.rest.MetaMapTermsQuery;
import es.upm.disnet.pubmed.model.rest.MetaMapTermsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Service
public class MetaMapTermsRetrievalService {
    private static final Logger log = LoggerFactory.getLogger(MetaMapTermsRetrievalService.class);
    private final static String META_MAP_SERVICE_URL = "http://138.4.130.6:11063/api/metamap/filter";

    public MetaMapTermsResponse getMetaMapTerms(String text) throws Exception {
        MetaMapTermsQuery metaMapTermsQuery = new MetaMapTermsQuery();

        MetaMapTermsQuery.Configuration configuration = new MetaMapTermsQuery.Configuration();

        configuration.setOptions("-y -R");
        configuration.setSemanticTypes(new String[]{"sosy", "diap", "dsyn", "fndg", "lbpr", "lbtr"});
        configuration.setSources(new String[]{"SNOMEDCT_US", "DSM-5"});
        configuration.setConceptLocation(false);

        metaMapTermsQuery.setConfiguration(configuration);

        MetaMapTermsQuery.Text textList = new MetaMapTermsQuery.Text();

        textList.setId("1");
        textList.setText(text);

        metaMapTermsQuery.setTextList(new MetaMapTermsQuery.Text[]{textList});

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();

        String metaMapTermsQueryJson = objectMapper.writeValueAsString(metaMapTermsQuery);

        HttpEntity<String> httpEntity = new HttpEntity<>(metaMapTermsQueryJson, httpHeaders);

        log.info("Query to Metamap {}" + metaMapTermsQueryJson);

        MetaMapTermsResponse metaMapTermsResponse =
                restTemplate.postForObject(META_MAP_SERVICE_URL, httpEntity, MetaMapTermsResponse.class);

        log.debug("Retrieved MetaMap Terms {}", metaMapTermsResponse.toString());

        return metaMapTermsResponse;
    }
}



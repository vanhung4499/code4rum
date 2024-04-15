package com.hnv99.forum.service.config.es;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Elasticsearch configuration class.
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchConfig {

    // Whether to enable Elasticsearch
    private Boolean open;

    // Elasticsearch host IP address (cluster)
    private String hosts;

    // Elasticsearch username
    private String userName;

    // Elasticsearch password
    private String password;

    // Elasticsearch request scheme
    private String scheme;

    // Elasticsearch cluster name
    private String clusterName;

    // Elasticsearch connection timeout
    private int connectTimeOut;

    // Elasticsearch socket connection timeout
    private int socketTimeOut;

    // Elasticsearch request timeout
    private int connectionRequestTimeOut;

    // Elasticsearch maximum number of connections
    private int maxConnectNum;

    // Maximum number of connections per route for Elasticsearch
    private int maxConnectNumPerRoute;

    /**
     * If @Bean does not specify the bean name, then the bean name is the method name.
     */
    @Bean(name = "restHighLevelClient")
    public RestHighLevelClient restHighLevelClient() {

        // For single-node Elasticsearch
        String host = hosts.split(":")[0];
        String port = hosts.split(":")[1];
        HttpHost httpHost = new HttpHost(host, Integer.parseInt(port));

        // Build connection object
        RestClientBuilder builder = RestClient.builder(httpHost);

        // Set username and password
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

        // Connection delay configuration
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeOut);
            requestConfigBuilder.setSocketTimeout(socketTimeOut);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
            return requestConfigBuilder;
        });
        // Connection count configuration
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(maxConnectNum);
            httpClientBuilder.setMaxConnPerRoute(maxConnectNumPerRoute);
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            return httpClientBuilder;
        });

        return new RestHighLevelClient(builder);
    }
}


package com.topaiebiz.elasticsearch.configuration;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author tangx.w
 * @Description: 将restClient bean 放入到spring容器
 * @Date: Create in 17:00 2018/6/21
 * @Modified by:
 */
@Configuration
public class ESConfiguration {


	@Value("${elasticSearch.restClient_user}")
	private String user;

	@Value("${elasticSearch.restClient_password}")
	private String password;

	@Value("${elasticSearch.restClient_ip}")
	private String restClientIp;

	@Value("${elasticSearch.restClient_port}")
	private Integer port;

	@Bean
	public RestClient restClient(){
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials("user", "password"));
		return RestClient.builder(new HttpHost("restClientIp", port))
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					}
				})
				.build();
	}
}

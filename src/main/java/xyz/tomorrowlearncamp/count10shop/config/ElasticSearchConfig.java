package xyz.tomorrowlearncamp.count10shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;



@Configuration
@EnableElasticsearchRepositories(basePackages = "xyz.tomorrowlearncamp.count10shop.domain.item.repository")//elastic 레포지토리 지원 활성화
public class ElasticSearchConfig extends ElasticsearchConfiguration {
	@Value("${spring.elasticsearch.uris}")
	private String host;

	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder()
			.connectedTo(host)
			.build();
	}
}

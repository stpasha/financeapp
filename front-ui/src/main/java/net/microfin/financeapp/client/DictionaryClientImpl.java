package net.microfin.financeapp.client;

import net.microfin.financeapp.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "${client.service.dictionaries}",
        configuration = FeignConfig.class,
        fallback = DictionaryClientFallback.class
)
public interface DictionaryClientImpl extends DictionaryClient {
}

package com.og.templateback.configuration.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author ogbozoyan
 * @since 03.12.2023
 */
@FeignClient(name = "FClient", url = "${services.custom-svc}", configuration = FeignClientConfiguration.class)
public interface FClient {

}

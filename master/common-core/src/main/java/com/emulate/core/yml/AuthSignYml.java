package com.emulate.core.yml;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties("emulate.auth-sign")
public class AuthSignYml {

    private Boolean enableAuth;

    private List<String> noAuthList;

    private Boolean enableSign;

    private List<String> noSignList;
}

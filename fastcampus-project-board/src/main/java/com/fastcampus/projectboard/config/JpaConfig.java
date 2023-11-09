package com.fastcampus.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.swing.text.html.Option;
import java.util.Optional;

@EnableJpaAuditing
@Configuration // Configuration bean이 된다는 어노테이션
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("wlsgur073"); // TODO: 스프링 시큐리티로 인증 기능을 붙이게 될 때, 수정 필요
    }
}

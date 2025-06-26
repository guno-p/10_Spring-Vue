package org.scoula.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Configuration        // Spring 설정 클래스임을 명시
@EnableSwagger2      // Swagger 2.0 활성화
public class SwaggerConfig {

  // API 문서 메타 정보 상수
  private final String API_NAME = "Board API";
  private final String API_VERSION = "1.0";
  private final String API_DESCRIPTION = "Board API 명세서";

  /**
   * API 문서 기본 정보 설정
   */
  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
            .title(API_NAME)                    // API 문서 제목
            .description(API_DESCRIPTION)       // API 문서 설명
            .version(API_VERSION)               // API 버전
            .build();
  }

  /**
   * Swagger 문서 생성 설정
   */
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)    // Swagger 2.0 사용
            // JWT 인증
            // .securityContexts(List.of(this.securityContext())) // SecurityContext 설정
            // .securitySchemes(List.of(this.apiKey())) // ApiKey 설정

            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))  // @RestController가 붙은 클래스만 문서화
            .paths(PathSelectors.any())  // 모든 경로 포함
            .build()
            .apiInfo(apiInfo());         // 위에서 설정한 API 정보 적용
  }

  // JWT SecurityContext 구성
  private SecurityContext securityContext() {
    return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .build();

  }
  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return List.of(new SecurityReference("Authorization", authorizationScopes));
  }
  // ApiKey 정의
  private ApiKey apiKey() {
    return new ApiKey("Authorization", "Authorization", "header");
  }
}
package org.scoula.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.scoula.security.filter.AuthenticationErrorFilter;
import org.scoula.security.filter.JwtAuthenticationFilter;
import org.scoula.security.filter.JwtUsernamePasswordAuthenticationFilter;
import org.scoula.security.handler.CustomAccessDeniedHandler;
import org.scoula.security.handler.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CharacterEncodingFilter;

// 필수 import 구문들
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity  // Spring Security 활성화
@Slf4j
@MapperScan(basePackages = {"org.scoula.security.account.mapper"})  // 매퍼 스캔 설정

@ComponentScan(basePackages = {"org.scoula.security"})    // 서비스 클래스 스캔
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  /* 필드  추가 */
  private final UserDetailsService userDetailsService;   // CustomUserDetailsService 주입

  // 인증 예외 처리 필터
  private final AuthenticationErrorFilter authenticationErrorFilter;

  // JWT 인증 필터
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  // 401/403 에러 처리 핸들러
  private final CustomAccessDeniedHandler accessDeniedHandler;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;

  // 커스텀 인증 필터 추가
  @Autowired
  private JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter;

  // PasswordEncoder(BCryptPasswordEncoder) Bean 등록 설정
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // BCrypt 해시 함수 사용
  }

  // 문자셋 필터 메서드
//  public CharacterEncodingFilter encodingFilter() {
//    CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
//    encodingFilter.setEncoding("UTF-8");           // UTF-8 인코딩 설정
//    encodingFilter.setForceEncoding(true);         // 강제 인코딩 적용
//    return encodingFilter;
//  }

  // AuthenticationManager 빈 등록 - JWT 토큰 인증에서 필요
  @Bean
  public AuthenticationManager authenticationManager() throws Exception {
    return super.authenticationManager();
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // CSRF 필터보다 앞에 인코딩 필터 추가
    // - CSRF 필터는 Spring Security 환경에서 기본적으로 활성화 되어있음!
    http
        // 문자 인코딩
        // .addFilterBefore(encodingFilter(), CsrfFilter.class)
        // 인증 에러 필터
        .addFilterBefore(authenticationErrorFilter, UsernamePasswordAuthenticationFilter.class)
        // JWT 인증필터
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        // API 로그인 인증 필터 추가 (기존 UsernamePasswordAuthenticationFilter 앞에 배치)
        .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        // 예외 처리 설정
        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)  // 401 에러 처리
        .accessDeniedHandler(accessDeniedHandler);           // 403 에러 처리

    //  HTTP 보안 설정
    http.httpBasic().disable()      // 기본 HTTP 인증 비활성화
            .csrf().disable()           // CSRF 보호 비활성화 (REST API에서는 불필요)
            .formLogin().disable()      // 폼 로그인 비활성화 (JSON 기반 API 사용)
            .sessionManagement()        // 세션 관리 설정
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // 무상태 모드

    http
      .authorizeRequests() // 경로별 접근 권한 설정
      .antMatchers(HttpMethod.OPTIONS).permitAll()  //  org.springframework.http.HttpMethod

      // 🌐 회원 관련 공개 API (인증 불필요)
      .antMatchers(HttpMethod.GET, "/api/member/checkusername/**").permitAll()     // ID 중복 체크
      .antMatchers(HttpMethod.POST, "/api/member").permitAll()                    // 회원가입
      .antMatchers(HttpMethod.GET, "/api/member/*/avatar").permitAll()            // 아바타 이미지

      // 🔒 회원 관련 인증 필요 API
      .antMatchers(HttpMethod.PUT, "/api/member/**").authenticated() // 회원 정보 수정, 비밀번호 변경

      .antMatchers(HttpMethod.POST, "/api/board/**").authenticated()
      .antMatchers(HttpMethod.PUT, "/api/board/**").authenticated()
      .antMatchers(HttpMethod.DELETE, "/api/board/**").authenticated()
      .anyRequest().permitAll();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    log.info("configure .........................................");

    // UserDetailsService와 PasswordEncoder 설정
    auth.userDetailsService(userDetailsService)  // 커스텀 서비스 사용
            .passwordEncoder(passwordEncoder()); // BCrypt 암호화 사용
  }

  // 브라우저의 CORS 정책을 우회하여 다른 도메인에서의 API 접근 허용
  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    config.setAllowCredentials(true);           // 인증 정보 포함 허용
    config.addAllowedOriginPattern("*");        // 모든 도메인 허용
    config.addAllowedHeader("*");               // 모든 헤더 허용
    config.addAllowedMethod("*");               // 모든 HTTP 메서드 허용

    source.registerCorsConfiguration("/**", config);  // 모든 경로에 적용
    return new CorsFilter(source);
  }

  // Spring Security 검사를 우회할 경로 설정
  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(
            "/assets/**",      // 정적 리소스
            "/*",              // 루트 경로의 파일들

            // "/api/member/**",   // 회원 관련 공개 API
            // 기능 개발 후에는 수정이 필요함
            // 인증 요구하는게 많은지? 인증 요구하지 않는게 많은지?
            // 인증 요구는
            // POST :: /api/member - 가입
            // PUT :: /api/member - 수정
            // PUT :: /api/member/*/changepassword - 수정

            // Swagger 관련
            "/swagger-ui.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs"
    );
  }
}
package com.seven.marketclip.security;

import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.account.oauth.OauthHandler;
import com.seven.marketclip.account.oauth.PrincipalOauth2UserService;
import com.seven.marketclip.security.filter.FormLoginFilter;
import com.seven.marketclip.security.filter.JwtAuthFilter;
import com.seven.marketclip.security.jwt.HeaderTokenExtractor;
import com.seven.marketclip.security.jwt.JwtDecoder;
import com.seven.marketclip.security.provider.FormLoginAuthProvider;
import com.seven.marketclip.security.provider.JWTAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    private final PrincipalOauth2UserService principalOauth2UserService;
    private final AccountRepository accountRepository;
    private final JWTAuthProvider jwtAuthProvider;
    private final HeaderTokenExtractor headerTokenExtractor;
    private final JwtDecoder jwtDecoder;
    private final OauthHandler oauthHandler;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }





    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(formLoginAuthProvider())
                .authenticationProvider(jwtAuthProvider);
    }

    @Override
    public void configure(WebSecurity web) {
// h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**");
        web.ignoring().antMatchers(PERMIT_URL_ARRAY);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "OPTIONS", "PUT","DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.addExposedHeader("X-ACCESSR-TOKEN");
        configuration.addExposedHeader("Y-REFRESH-TOKEN");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static final String[] PERMIT_URL_ARRAY = {
            /* swagger v2 */
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource());

        http.csrf().disable();

        // 서버에서 인증은 JWT로 인증하기 때문에 Session의 생성을 막습니다.
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        /*
         * 1.
         * UsernamePasswordAuthenticationFilter 이전에 FormLoginFilter, JwtFilter 를 등록합니다.
         * FormLoginFilter : 로그인 인증을 실시합니다.
         * JwtFilter       : 서버에 접근시 JWT 확인 후 인증을 실시합니다.
         */
//        http.addFilter(corsFilter);
//        http.addFilter(new JwtAuthenticationFilter(authenticationManager()));
//        http.addFilter(new JwtAuthorizationFilter(authenticationManager(),accountRepository));
        http
                .addFilterBefore(formLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);


        //TODO mvcMatchers 하고 authorizatino 차이
        http.authorizeHttpRequests()
//                .mvcMatchers(HttpMethod.GET,"/h2-console/**").permitAll()
                .antMatchers("/","/api/sign-up","/api/refresh-re").permitAll()
                .antMatchers("/login/oauth2/code/google","/login/oauth2/code/naver","/login/oauth2/code/kakao").permitAll()
                .antMatchers("/api/manager").hasRole("USER")
                .anyRequest().authenticated();


        http.oauth2Login().loginPage("/login").successHandler(oauthHandler).userInfoEndpoint().userService(principalOauth2UserService());
        }
        
        //TODO 여기서 빈을 뺴고 컴포턴트로 만든 후 주입해서 가져오기
        @Bean
        public PrincipalOauth2UserService principalOauth2UserService() {
            return new PrincipalOauth2UserService(accountRepository,bCryptPasswordEncoder);
        }

//        http.authorizeRequests()
//                .anyRequest()
//                .permitAll()
//                .and()
//    // [로그아웃 기능]
//                .logout()
//    // 로그아웃 요청 처리 URL
//                .logoutUrl("/user/logout")
//                .permitAll()
//                .and()
//                .exceptionHandling()
//    // "접근 불가" 페이지 URL 설정
//                .accessDeniedPage("/forbidden.html");
        @Bean
        public FormLoginFilter formLoginFilter() throws Exception {
            FormLoginFilter formLoginFilter = new FormLoginFilter(authenticationManager());
            formLoginFilter.setFilterProcessesUrl("/api/login");
            formLoginFilter.setAuthenticationSuccessHandler(formLoginSuccessHandler());
            formLoginFilter.afterPropertiesSet(); //TODO 찾아보기 -> formLoginFilter.afterPropertiesSet
            return formLoginFilter;
        }
        @Bean
        public FormLoginSuccessHandler formLoginSuccessHandler() {
            return new FormLoginSuccessHandler(accountRepository);
        }
        @Bean
        public FormLoginAuthProvider formLoginAuthProvider() {
            return new FormLoginAuthProvider(bCryptPasswordEncoder);//TODO 이걸 왜 넣지?
        }

        //글쓰기 요청 할 때만 뚫려야 함.with 수정 삭제
        private JwtAuthFilter jwtFilter() throws Exception {
            List<String> skipPathList = new ArrayList<>();

            // Static 정보 접근 허용
            skipPathList.add("GET,/images/**");
            skipPathList.add("GET,/css/**");

            // h2-console 허용
            skipPathList.add("GET,/h2-console/**");
            skipPathList.add("POST,/h2-console/**");

            // 회원 관리 API 허용
            //TODO 여기 왜 /를 필터에서 제외 시켰는데 왜 들어가지?
            //한번 지금 index랑 연결 -> 홈을 따로 만들어서 연결 해보기
            skipPathList.add("GET,/");
            skipPathList.add("GET,/api/refresh-re");
            skipPathList.add("POST,/api/refresh-re");
            skipPathList.add("POST,/api/sign-up");

            //소셜 콜백 주소
            //KAKAO
            skipPathList.add("GET,/api/kakao/callback");
            skipPathList.add("GET,/login/oauth2/code/google");
            skipPathList.add("POST,/login/oauth2/code/kakao");
            skipPathList.add("GET,/login/oauth2/code/naver");

            //보드게시판 API 허용/swagger-resources/**
            skipPathList.add("GET,/api/boards");
            skipPathList.add("GET,/swagger-resources/**");
//            skipPathList.add("GET,/");
//            skipPathList.add("GET,/basic.js");
//
//            skipPathList.add("GET,/favicon.ico");

            FilterSkipMatcher matcher = new FilterSkipMatcher(
                    skipPathList,
                    "/**"
            );

            JwtAuthFilter filter = new JwtAuthFilter(
                    matcher,
                    headerTokenExtractor
                    ,jwtDecoder
                    ,accountRepository
            );
            filter.setAuthenticationManager(super.authenticationManagerBean());

            return filter;
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

}

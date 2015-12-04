/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hello;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import hello.data.OAuthURIAccess;
import hello.data.URIAccessRepository;

@Configuration
public class OAuth2ServerConfiguration {

	private static final String RESOURCE_ID = "restservice";

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Autowired
		private DataSource dataSource;

		@Autowired
		URIAccessRepository repository;

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			// @formatter:off
			resources.resourceId(RESOURCE_ID);
			// @formatter:on
		}

		@Bean
		public RequestAccessFilter requestAccessFilter(){
			RequestAccessFilter filter = new RequestAccessFilter();
			return filter;
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/static/**", "/signup").permitAll().anyRequest().authenticated().and().logout().permitAll();
			http.addFilterAfter(requestAccessFilter(), FilterSecurityInterceptor.class);
			/*
			 * List<OAuthURIAccess> findAll = repository.findAll();
			 * // @formatter:off //
			 * http.authorizeRequests().antMatchers("/users").hasRole("ADMIN").
			 * antMatchers("/greeting").authenticated();
			 * System.out.println(findAll); for (OAuthURIAccess access :
			 * findAll) { System.out.println(access);
			 * http.authorizeRequests().antMatchers(access.getUri()).hasRole(
			 * access.getRole()); if
			 * (BooleanUtils.toBoolean(access.getSecured())) {
			 * 
			 * } else {
			 * 
			 * } }
			 */
			// @formatter:on
		}

	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		@Qualifier("dataSource")
		DataSource dataSource;

		@Bean
		public TokenStore tokenStore() {
			return new JdbcTokenStore(dataSource);
		}

		// private TokenStore tokenStore = new InMemoryTokenStore();
		// private TokenStore tokenStore = new JdbcTokenStore(dataSource);

		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;

		@Autowired
		private CustomUserDetailsService userDetailsService;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			// @formatter:off
			endpoints.tokenStore(tokenStore()).authenticationManager(this.authenticationManager).userDetailsService(userDetailsService);
			// @formatter:on
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @formatter:off
			// clients.inMemory().withClient("clientapp").authorizedGrantTypes("password",
			// "refresh_token")
			// .authorities("USER").scopes("read",
			// "write").resourceIds(RESOURCE_ID).secret("123456");
			// @formatter:on
			clients.jdbc(dataSource);
		}

		/*
		 * @Bean
		 * 
		 * @Primary public DefaultTokenServices tokenServices() {
		 * DefaultTokenServices tokenServices = new DefaultTokenServices();
		 * tokenServices.setSupportRefreshToken(true);
		 * tokenServices.setTokenStore(this.tokenStore); return tokenServices; }
		 */

	}

}

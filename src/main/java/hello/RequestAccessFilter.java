package hello;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import hello.data.OAuthURIAccess;
import hello.data.Role;
import hello.data.URIAccessRepository;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class RequestAccessFilter extends GenericFilterBean {

	@Autowired
	URIAccessRepository repository;

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		if (request.getRequestURI().contains("/oauth/token")) {
			chain.doFilter(arg0, arg1);
		} else {
			System.out.println("Accessing other things");
			String uri = request.getRequestURI().substring(request.getContextPath().length());
			System.out.println(uri);
			List<OAuthURIAccess> uriConfiguration = repository.getURIConfiguration(uri);
			System.out.println(uriConfiguration);
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			List<Role> roles = (List<Role>) authentication.getAuthorities();
			System.out.println(roles);
			for (OAuthURIAccess access : uriConfiguration) {
				boolean userInRole = request.isUserInRole(access.getRole());
				System.out.println("User Role is >> " + userInRole);
				if (BooleanUtils.toBoolean(access.getSecured()) && StringUtils.contains(access.getUri(), request.getRequestURI()) && userInRole) {
					chain.doFilter(arg0, arg1);
				}
				HttpServletResponse response = (HttpServletResponse) arg1;
				response.setContentType(MediaType.APPLICATION_JSON);
				response.setStatus(401);
				response.getWriter().write("{\"error\":\"Unauthorized access\"");
			}
		}

	}

}

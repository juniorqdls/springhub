package com.mjeanroy.springhub.filters;

import com.mjeanroy.springhub.commons.web.utils.Browser;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

public class InternetExplorerFilter implements Filter {

	private Pattern staticPattern;

	private Pattern restPattern;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String pattern = filterConfig.getInitParameter("restPattern");
		if (StringUtils.isNotBlank(pattern)) {
			this.restPattern = Pattern.compile(pattern);
		}

		staticPattern = Pattern.compile(
				"\\.js|\\.html|\\.jsp|\\.css|\\.jpg|\\.jpeg|\\.png|\\.gif|\\.svg|\\.mp3|\\.flv|\\.swf|\\.ogg",
				Pattern.CASE_INSENSITIVE
		);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String path = req.getRequestURI().substring(req.getContextPath().length());
		if ((!isRestPath(path)) && (!isStaticPath(path))) {
			Browser browser = new Browser(req);
			if (browser.ltIE9()) {
				// Redirect to hash bang URL
				HttpServletResponse res = (HttpServletResponse) response;
				res.sendRedirect("/#" + path);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

	private boolean isRestPath(String path) {
		if (this.restPattern == null) {
			return false;
		}
		return restPattern.matcher(path).find();
	}

	private boolean isStaticPath(String path) {
		return staticPattern.matcher(path).find();
	}
}

package com.cas.controller.interceptor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.cas.framework.utils.StringUtil;
/**
 * @Creat 2017年04月8日
 * @Author:kingson·liu
 * 支持跨域用
 */
public class DomainCorsFilter extends CorsFilter {
	
	private static UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	private CorsProcessor processor = new DefaultCorsProcessor();
	/**
	 * @param configSource
	 */
	public DomainCorsFilter() {
		
		super(source);
	}

	/**
	 * 支持跨域的域名
	 */
	private static Set<String> accessAllowedFrom;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		
		accessAllowedFrom=parseStringToSet(getFilterConfig().getInitParameter("accessAllowedFrom"));
		if (CorsUtils.isCorsRequest(request)) {
			
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowCredentials(true);
			
			String referer = request.getHeader("Origin");
			if(StringUtil.isEmpty(referer)){
				referer = request.getHeader("Referer");
			}
			if(StringUtil.isNotEmpty(referer)){
				for (String str : accessAllowedFrom) {
					Matcher matcher = Pattern.compile(str).matcher(referer);
					if(matcher.find()){
				        String host=matcher.group();
				        config.addAllowedOrigin(host);
						break;
				    }
				}
			    
			}
			config.addAllowedHeader("*");
			config.addAllowedMethod("*");
			
			source.registerCorsConfiguration("/**", config);
			
			CorsConfiguration corsConfiguration = source.getCorsConfiguration(request);
			if (corsConfiguration != null) {
				boolean isValid = processor.processRequest(corsConfiguration, request, response);
				if (!isValid || CorsUtils.isPreFlightRequest(request)) {
					return;
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private Set<String> parseStringToSet(final String data) {
        String[] splits;

        if (data != null && data.length() > 0) {
            splits = data.split(",");
        } else {
            splits = new String[] {};
        }

        Set<String> set = new HashSet<String>();
        if (splits.length > 0) {
            for (String split : splits) {
                set.add(split.trim());
            }
        }

        return set;
    }

}

/*
 * Copyright 2015 the original author or authors.
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
package leap.web.cors;

import leap.lang.Strings;
import leap.lang.http.HTTP;
import leap.lang.intercepting.State;
import leap.web.Request;
import leap.web.Response;

/**
 * @see <a href="http://www.w3.org/TR/cors/">CORS specification</a>
 */
public class DefaultCorsHandler implements CorsHandler {
	
	public State handle(Request request, Response response, CorsConfig conf) throws Throwable {
		if(!request.hasHeader(REQUEST_HEADER_ORIGIN)) {
			return State.CONTINUE;
		}
		
		//6.1 Simple Cross-Origin Request, Actual Request, and Redirects
		//1. If the Origin header is not present terminate this set of steps. 
		//   The request is outside the scope of this specification.

		//6.2 Preflight Request
		//1. If the Origin header is not present terminate this set of steps. 
		//   The request is outside the scope of this specification.
		
		return doProcess(request, response, conf, request.getHeader(REQUEST_HEADER_ORIGIN));
	}
	
	protected State doProcess(Request request, Response response, CorsConfig conf, String origin) throws Throwable {
		//HTTP method
		String method = request.getRawMethod();
		
		//Preflight request
		if("OPTIONS".equals(method)) {
			String accessControlRequestMethod = request.getHeader(REQUEST_HEADER_ACCESS_CONTROL_REQUEST_METHOD);
			if(null != accessControlRequestMethod) {
				return doProcessPreflight(request, response, conf, origin, method, accessControlRequestMethod);
			}
		}
		
		//Simple or Actual request.
		return doProcessSimpleOrActual(request, response, conf, origin, method);
	}
	
	protected State doProcessSimpleOrActual(Request request, Response response, CorsConfig conf, 
										    String origin, String method) throws Throwable {
		
		//6.1.1 6.1.2
		//Check the origin is allowed
		if(!checkAndProcessInvalidOrigin(request, response, conf, origin)) {
			return State.INTERCEPTED;
		}
		
		//Check the method is allowed
		if(!conf.isMethodAllowed(method)) {
			responseInvalid(request, response, conf, origin);
			return State.INTERCEPTED;
		}
		
		//Section 6.1.3
		if(conf.isSupportsCredentials()) {
			response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN,      origin);
			response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		}else{
			if(conf.isAllowAnyOrigin()) {
				response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, "*");
			}else{
				response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, origin);
			}
		}
		
		//Section 6.1.4
		/*
		 	If the list of exposed headers is not empty add one or more Access-Control-Expose-Headers headers, 
		 	with as values the header field names given in the list of exposed headers.
		 */
		if(conf.hasExposedHeaders()) {
			response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS, conf.getExposedHeadersValue());
		}
		
		return State.CONTINUE;
	}
	
	protected State doProcessPreflight(Request request, Response response, CorsConfig conf, String origin, 
									  String method, String accessControlRequestMethod) throws Throwable {

		//6.2.1 6.2.2
		//Check the origin is allowed
		if(!checkAndProcessInvalidOrigin(request, response, conf, origin)) {
			return State.INTERCEPTED;
		}
		
		//6.2.3
		//Check the access-control-request-method is allowed.
		if(accessControlRequestMethod.isEmpty() || !conf.isMethodAllowed(accessControlRequestMethod)) {
			responseInvalid(request, response, conf, origin);
			return State.INTERCEPTED;
		}
		
		//Section 6.2.6
		//Check the access-control-request-headers is allowed.
		String accessControlRequestHeaders = request.getHeader(REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS);
		if(!conf.isAllowAnyHeader()) {
			if(null != accessControlRequestHeaders && accessControlRequestHeaders.length() > 0) {
				for(String h : Strings.split(accessControlRequestHeaders, ',')){
					if(!conf.isHeaderAllowedIgnoreCase(h)) {
						responseInvalid(request, response, conf, origin);
						return State.INTERCEPTED;
					}
				}
			}
		}
		
		//6.2.7
		if(conf.isSupportsCredentials()) {
			response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN,      origin);
			response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		}else{
			if(conf.isAllowAnyOrigin()) {
				response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, "*");
			}else{
				response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, origin);
			}
		}	
		
		//6.2.8
		if(conf.getPreflightMaxAge() > 0) {
			response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_MAX_AGE, String.valueOf(conf.getPreflightMaxAge()));
		}
		
		//6.2.9
		response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_METHODS, accessControlRequestMethod);
		
		//6.2.10
		if(accessControlRequestHeaders != null && accessControlRequestHeaders.length() > 0) {
			response.addHeader(RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_HEADERS, accessControlRequestHeaders);
		}
		
		return State.INTERCEPTED;
	}
	
	protected boolean checkAndProcessInvalidOrigin(Request request, Response response, CorsConfig conf, String origin) throws Throwable {
		if(null == origin || origin.isEmpty()) {
			responseInvalid(request, response, conf, origin);
			return false;
		}
		
		if(!conf.isOriginAllowed(origin)) {
			responseInvalid(request, response, conf, origin);
			return false;
		}
		
		return true;
	}
	
	protected void responseInvalid(Request request, Response response, CorsConfig conf, String origin) throws Throwable {
		response.setContentType("text/plain");
		response.setStatus(HTTP.SC_FORBIDDEN);
	}
}


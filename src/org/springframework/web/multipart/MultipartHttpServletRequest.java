package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public interface MultipartHttpServletRequest extends HttpServletRequest, MultipartRequest {

	/**
	 * Return this request's method as a convenient HttpMethod instance.
	 */
	HttpMethod getRequestMethod();

	/**
	 * Return this request's headers as a convenient HttpHeaders instance.
	 */
	HttpHeaders getRequestHeaders();

	/**
	 * Return the headers associated with the specified part of the multipart request.
	 * <p>If the underlying implementation supports access to headers, then all headers are returned.
	 * Otherwise, the returned headers will include a 'Content-Type' header at the very least.
	 */
	HttpHeaders getMultipartHeaders(String paramOrFileName);

}


package com.mjeanroy.springhub.controllers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mjeanroy.springhub.commons.web.utils.Browser;
import com.mjeanroy.springhub.exceptions.DisconnectedException;
import com.mjeanroy.springhub.exceptions.EmailUniqueException;
import com.mjeanroy.springhub.exceptions.EntityNotFoundException;
import com.mjeanroy.springhub.exceptions.NotImplementedException;
import com.mjeanroy.springhub.exceptions.RequestParameterException;
import com.mjeanroy.springhub.exceptions.ResourceNotFoundException;
import com.mjeanroy.springhub.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public abstract class AbstractController {

	/**
	 * Class logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

	@Autowired
	protected HttpServletRequest request;

	/**
	 * Get browser information for current request.
	 *
	 * @return Browser details.
	 */
	protected Browser browser() {
		return new Browser(request);
	}

	/**
	 * Check if client browser is Internet Explorer less or equals to version 9.
	 *
	 * @return True if client browser is Internet Explorer less or equals to version 9.
	 */
	protected boolean ltIE9() {
		return new Browser(request).ltIE9();
	}

	/**
	 * Check if client browser is Internet Explorer less or equals to version 8.
	 *
	 * @return True if client browser is Internet Explorer less or equals to version 8.
	 */
	protected boolean ltIE8() {
		return new Browser(request).ltIE8();
	}

	/**
	 * Check if client browser is Internet Explorer less or equals to version 7.
	 *
	 * @return True if client browser is Internet Explorer less or equals to version 7.
	 */
	protected boolean ltIE7() {
		return new Browser(request).ltIE7();
	}

	/**
	 * Check if client browser is Internet Explorer less or equals to version 6.
	 *
	 * @return True if client browser is Internet Explorer less or equals to version 6.
	 */
	protected boolean ltIE6() {
		return new Browser(request).ltIE6();
	}

	@ExceptionHandler(DisconnectedException.class)
	public void disconnectedException(DisconnectedException ex, HttpServletResponse response) throws IOException {
		LOG.error(ex.getMessage());
		setResponse(response, 401, ex.getMessage());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public void unauthorizedException(UnauthorizedException ex, HttpServletResponse response) throws IOException {
		LOG.error(ex.getMessage());
		setResponse(response, 403, ex.getMessage());
	}

	@ExceptionHandler(RequestParameterException.class)
	public void requestParameterException(RequestParameterException ex, HttpServletResponse response) {
		LOG.error(ex.getMessage(), ex);
		setResponse(response, 400, ex.getMessage());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public void entityNotFoundException(EntityNotFoundException ex, HttpServletResponse response) {
		LOG.error(ex.getMessage());
		setResponse(response, 500, ex.getMessage());
	}

	@ExceptionHandler(NotImplementedException.class)
	public void notImplementedException(NotImplementedException ex, HttpServletResponse response) {
		LOG.error(ex.getMessage(), ex);
		setResponse(response, 501, ex.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public void resourceNotFoundException(ResourceNotFoundException ex, HttpServletResponse response) {
		LOG.error(ex.getMessage());
		setResponse(response, 404, ex.getMessage());
	}

	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public void methodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletResponse response) throws IOException {
		LOG.error(ex.getMessage());
		setResponse(response, 400, ex.getMessage());
	}

	@ExceptionHandler(value = {BindException.class})
	public void bindException(BindException ex, HttpServletResponse response) throws IOException {
		LOG.error(ex.getMessage());
		setResponse(response, 400, ex.getMessage());
	}

	@ExceptionHandler(value = {InvalidFormatException.class})
	public void invalidFormatException(InvalidFormatException ex, HttpServletResponse response) throws IOException {
		LOG.error(ex.getMessage());
		setResponse(response, 400, ex.getMessage());
	}

	@ExceptionHandler(value = {EmailUniqueException.class})
	public void emailUniqueException(EmailUniqueException ex, HttpServletResponse response) throws IOException {
		LOG.error(ex.getMessage());
		setResponse(response, 400, ex.getMessage());
	}

	protected void setResponse(HttpServletResponse response, int status, String message) {
		response.setStatus(status);

		try {
			response.getWriter().print(message);
		}
		catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}
}

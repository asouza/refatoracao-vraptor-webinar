/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.interceptor.multipart;

import static com.google.common.base.Strings.nullToEmpty;
import static org.slf4j.LoggerFactory.getLogger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import br.com.caelum.vraptor.events.ControllerMethodDiscovered;

/**
 * A null implementation of {@link MultipartInterceptor}. This interceptor will
 * be activated when no commons-fileupload was found in classpath. If application
 * try to upload any files, this interceptor will warn a message in console.
 *
 * @author Otávio Scherer Garcia
 * @author Rodrigo Turini
 * @since 3.1.3
 */
@RequestScoped
public class NullMultipartObserver {

	private static final Logger logger = getLogger(NullMultipartObserver.class);

	private final HttpServletRequest request;

	/**
	 * @deprecated CDI eyes only
	 */
	protected NullMultipartObserver() {
		this(null);
	}

	@Inject
	public NullMultipartObserver(HttpServletRequest request) {
		this.request = request;
	}

	public void nullUpload(@Observes ControllerMethodDiscovered event) {
		if (request.getMethod().toUpperCase().equals("POST")
				&& nullToEmpty(request.getContentType()).startsWith("multipart/form-data")) {
			logger.warn("There is no file upload handlers registered. If you are willing to "
					+ "upload a file, please add the commons-fileupload in your classpath");
		}
	}
}
/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.http.route;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.proxy.Proxifier;

//25 pontos no total

/*
 * 25 pontos no começo
 * 
 * 20 pontos depois da primeira rodada movendo o criador de defaultroutebuilder
 * 
 * 16 pontos depois da primeira extracao do routes
 * 
 * 14 pontos
 */
/**
 * The default implementation of controller localization rules. It also uses a Path annotation to discover
 * path-&gt;method mappings using the supplied ControllerLookupInterceptor.
 * 
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class DefaultRouter implements Router {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultRouter.class);

	//12 pontos de acoplamento contextual
	//agora caiu para 7 pontos aqui
	//agora caiu para 6
	//essa colecao de routes pode virar uma classe
	private final Routes routes = new Routes();
	private final Proxifier proxifier;
	private final CacheStore<Invocation, Route> cache;
	private final CriaDefaultRouteBuilder criaDefaultRouteBuilder;


	/**
	 * @deprecated CDI eyes only
	 */
	protected DefaultRouter() {
		this(null, null, null);
	}

	@Inject
	public DefaultRouter(Proxifier proxifier,
			CacheStore<Invocation, Route> cache,CriaDefaultRouteBuilder criaDefaultRouteBuilder) {
		this.proxifier = proxifier;
		this.cache = cache;
		this.criaDefaultRouteBuilder = criaDefaultRouteBuilder;
	}

	@Override
	public RouteBuilder builderFor(String uri) {		
		//Aqui daria para criar o Builder do DefaultRouteBuilder. A classe tem tudo que ele precisa e espera a uri para 
		//construir o objeto. É meio o que foi feito, mas aí estourou por uns 5 ponyos...
		return criaDefaultRouteBuilder.cria(uri);		
	}

	/**
	 * You can override this method to get notified by all added routes.
	 */
	@Override
	public void add(Route r) {
		routes.add(r);
	}

	//3 pontos
	@Override
	public ControllerMethod parse(String uri, HttpMethod method, MutableRequest request) throws MethodNotAllowedException {
		Collection<Route> routesMatchingUriAndMethod = routesMatchingUriAndMethod(uri, method);

		Iterator<Route> iterator = routesMatchingUriAndMethod.iterator();

		Route route = iterator.next();
		checkIfThereIsAnotherRoute(uri, method, iterator, route);

		return route.controllerMethod(request, uri);
	}

	//2 pontos if + httpmethod
	private void checkIfThereIsAnotherRoute(String uri, HttpMethod method, Iterator<Route> iterator, Route route) {
		if (iterator.hasNext()) {
			Route otherRoute = iterator.next();
			checkState(route.getPriority() != otherRoute.getPriority(),
					"There are two rules that matches the uri '%s' with method %s: %s, %s with same priority."
						+ " Consider using @Path priority attribute.", uri, method, route, otherRoute);
		}
	}

	private Collection<Route> routesMatchingUriAndMethod(String uri, HttpMethod method) {
		return routes.routesMatchingUriAndMethod(uri, method);
	}

	//1 ponto do for
	@Override
	public EnumSet<HttpMethod> allowedMethodsFor(String uri) {
		EnumSet<HttpMethod> allowed = EnumSet.noneOf(HttpMethod.class);
		for (Route route : routes.routesMatchingUri(uri)) {
			allowed.addAll(route.allowedMethods());
		}
		return allowed;
	}

	//2 pontos do ternario 
	@Override
	public <T> String urlFor(final Class<T> type, final Method method, Object... params) {
		final Class<?> rawtype = proxifier.isProxyType(type) ? type.getSuperclass() : type;
		final Invocation invocation = new Invocation(rawtype, method);

		Route route = cache.fetch(invocation, routes.lazyBla(rawtype, method));

		logger.debug("Selected route for {} is {}", method, route);
		String url = route.urlFor(type, method, params);
		logger.debug("Returning URL {} for {}", url, route);

		return url;
	}

	@Override
	public List<Route> allRoutes() {
		return routes.allRoutes();
	}




}

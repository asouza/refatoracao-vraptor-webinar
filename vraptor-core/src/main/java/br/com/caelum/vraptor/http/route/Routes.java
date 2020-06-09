package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

import br.com.caelum.vraptor.controller.HttpMethod;

/*
 * 6 pontos
 * 
 * depois de trazer mais métodos para operar aqui
 * 
 * 9 pontos
 */
public class Routes {
	//2
	private Collection<Route> routes = new PriorityRoutesList();
	private static final Route NULL = new NoStrategy() {
		@Override
		public String urlFor(Class<?> type, Method m, Object... params) {
			throw new RouteNotFoundException("The selected route is invalid for redirection: " + type + "." + m.getName());
		}
	};	
	
	public List<Route> allRoutes() {
		return Collections.unmodifiableList(new ArrayList<>(routes));
	}

	public void add(Route r) {
		routes.add(r);
	}
	
	//1 do if
	public Collection<Route> routesMatchingUri(String uri) {
		Collection<Route> routesMatchingURI = FluentIterable.from(routes)
				.filter(canHandle(uri)).toSet();

		if (routesMatchingURI.isEmpty()) {
			throw new ControllerNotFoundException();
		}
		return routesMatchingURI;
	}
	
	//1 do supplier
	public Supplier<Route> lazyBla(final Class<?> rawtype, final Method method) {
		return new Supplier<Route>() {
			@Override
			public Route get() {
				return FluentIterable.from(routes).filter(canHandle(rawtype, method))
				.first().or(NULL);
			}
		};		
	}
	
	//1 ponto do predicate
	private Predicate<Route> canHandle(final String uri) {
		return new Predicate<Route>() {
			@Override
			public boolean apply(Route route) {
				return route.canHandle(uri);
			}
		};
	}	
	
	//1 ponto do predicate
	private Predicate<Route> canHandle(final Class<?> type, final Method method) {
		return new Predicate<Route>() {
			@Override
			public boolean apply(Route route) {
				return route.canHandle(type, method);
			}
		};
	}	
	
	// 1 ponto do if
	public Collection<Route> routesMatchingUriAndMethod(String uri, HttpMethod method) {
		Collection<Route> routesMatchingMethod = FluentIterable.from(routesMatchingUri(uri))
				.filter(allow(method)).toSet();

		if (routesMatchingMethod.isEmpty()) {
			EnumSet<HttpMethod> allowed = allowedMethodsFor(uri);
			throw new MethodNotAllowedException(allowed, method.toString());
		}
		return routesMatchingMethod;
	}

	//1 ponto do for
	private EnumSet<HttpMethod> allowedMethodsFor(String uri) {
		EnumSet<HttpMethod> allowed = EnumSet.noneOf(HttpMethod.class);
		for (Route route : routesMatchingUri(uri)) {
			allowed.addAll(route.allowedMethods());
		}
		return allowed;
	}
	
	//1 ponto do predicate
	private Predicate<Route> allow(final HttpMethod method) {
		return new Predicate<Route>() {
			@Override
			public boolean apply(Route route) {
				return route.allowedMethods().contains(method);
			}
		};
	}	
	
	
}

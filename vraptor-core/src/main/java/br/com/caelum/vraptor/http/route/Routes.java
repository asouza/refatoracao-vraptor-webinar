package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

public class Routes {

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
	
	public Collection<Route> routesMatchingUri(String uri) {
		Collection<Route> routesMatchingURI = FluentIterable.from(routes)
				.filter(canHandle(uri)).toSet();

		if (routesMatchingURI.isEmpty()) {
			throw new ControllerNotFoundException();
		}
		return routesMatchingURI;
	}
	
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
	
	
}

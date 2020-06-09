package br.com.caelum.vraptor.http.route;

import com.google.common.base.Supplier;

import br.com.caelum.vraptor.cache.CacheStore;

public class RoutesCache {

	private CacheStore<Invocation, Route> cache;

	public RoutesCache(CacheStore<Invocation, Route> cache) {
		super();
		this.cache = cache;
	}

	public Route fetch(Invocation invocation, Supplier<Route> lazyBla) {
		return cache.fetch(invocation, lazyBla);
	}

}

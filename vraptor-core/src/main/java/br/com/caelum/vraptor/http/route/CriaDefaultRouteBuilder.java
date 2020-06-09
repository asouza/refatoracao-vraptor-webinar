package br.com.caelum.vraptor.http.route;

import javax.enterprise.context.ApplicationScoped;

import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.proxy.Proxifier;

@ApplicationScoped
public class CriaDefaultRouteBuilder {

	// sai
	private final TypeFinder finder;
	// sai
	private final Converters converters;
	// sai
	private final ParameterNameProvider nameProvider;
	// sai
	private final Evaluator evaluator;
	// sai
	private final EncodingHandler encodingHandler;

	private final Proxifier proxifier;

	public CriaDefaultRouteBuilder(TypeFinder finder, Converters converters,
			ParameterNameProvider nameProvider, Evaluator evaluator,
			EncodingHandler encodingHandler, Proxifier proxifier) {
		super();
		this.finder = finder;
		this.converters = converters;
		this.nameProvider = nameProvider;
		this.evaluator = evaluator;
		this.encodingHandler = encodingHandler;
		this.proxifier = proxifier;
	}

	public RouteBuilder cria(String uri) {
		return new DefaultRouteBuilder(proxifier, finder, converters,
				nameProvider, evaluator, uri, encodingHandler);
	}

}

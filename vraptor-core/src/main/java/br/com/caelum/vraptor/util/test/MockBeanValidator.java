package br.com.caelum.vraptor.util.test;

import javax.enterprise.inject.Alternative;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import br.com.caelum.vraptor.validator.beanvalidation.DefaultBeanValidator;
import br.com.caelum.vraptor.validator.beanvalidation.MessageInterpolatorFactory;

/**
 * Mock Bean Validators - JSR 303
 *
 * @author fagnermoura@gmail.com
 * @since 3.4.0
 */
@Alternative
public class MockBeanValidator extends MockValidator {

	private final DefaultBeanValidator that;

	private static final ValidatorFactory validatorFactory;

	static {
		validatorFactory = Validation.buildDefaultValidatorFactory();
	}

	public MockBeanValidator() {
		Validator validator = validatorFactory.getValidator();
		MessageInterpolatorFactory factoryMessageInterpolator = new MessageInterpolatorFactory(validatorFactory);

		// @PostConstruct not works out of container.
		factoryMessageInterpolator.createInterpolator();
		MessageInterpolator interpolator = factoryMessageInterpolator.getInstance();

		that = new DefaultBeanValidator(new MockLocalization(), validator, interpolator);
	}

	@Override
	public void validate(Object bean, Class<?>... groups) {
		addAll(that.validate(bean, groups));
	}

	@Override
	public void validateProperties(Object bean, String... properties) {
		addAll(that.validateProperties(bean, properties));
	}

}

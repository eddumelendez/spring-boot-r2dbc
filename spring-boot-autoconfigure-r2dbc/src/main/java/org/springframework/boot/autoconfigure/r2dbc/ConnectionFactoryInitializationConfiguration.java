/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ConnectionFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.r2dbc.connectionfactory.init.DatabasePopulator;

/**
 * Configures {@link ConnectionFactory} initialization.
 *
 * @author Mark Paluch
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DatabasePopulator.class)
@Import({ ConnectionFactoryInitializerInvoker.class, ConnectionFactoryInitializationConfiguration.Registrar.class })
class ConnectionFactoryInitializationConfiguration {

	/**
	 * {@link ImportBeanDefinitionRegistrar} to register the
	 * {@link ConnectionFactoryInitializerPostProcessor} without causing early bean
	 * instantiation issues.
	 */
	static class Registrar implements ImportBeanDefinitionRegistrar {

		private static final String BEAN_NAME = "connectionFactoryInitializerPostProcessor";

		@Override
		public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
				BeanDefinitionRegistry registry) {
			if (!registry.containsBeanDefinition(BEAN_NAME)) {
				GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
				beanDefinition.setBeanClass(ConnectionFactoryInitializerPostProcessor.class);
				beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				// We don't need this one to be post processed otherwise it can cause a
				// cascade of bean instantiation that we would rather avoid.
				beanDefinition.setSynthetic(true);
				registry.registerBeanDefinition(BEAN_NAME, beanDefinition);
			}
		}

	}

}

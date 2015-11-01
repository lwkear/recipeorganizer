package net.kear.recipeorganizer.config;

import java.util.Arrays;

//import net.kear.recipeorganizer.webflow.AddRecipeFlowBuilder;



import net.kear.recipeorganizer.webflow.AddRecipeFlowBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.webflow.config.AbstractFlowConfiguration;
import org.springframework.webflow.config.FlowDefinitionRegistryBuilder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;
import org.springframework.webflow.security.SecurityFlowExecutionListener;

@Configuration
public class WebFlowConfig extends AbstractFlowConfiguration {

	@Autowired
	private WebMvcConfig webMvcConfig;
	
	@Bean
	public FlowExecutor flowExecutor() {
		return getFlowExecutorBuilder(flowRegistry())
				.addFlowExecutionListener(new SecurityFlowExecutionListener(), "*")
				.build();
	}

	@Bean
	public FlowDefinitionRegistry flowRegistry() {
		FlowDefinitionRegistryBuilder builder = new FlowDefinitionRegistryBuilder(getApplicationContext(), flowBuilderServices());
		builder.addFlowBuilder(new AddRecipeFlowBuilder());
		return builder.build();
		
		//return getFlowDefinitionRegistryBuilder(flowBuilderServices())
				//.setBasePath("/WEB-INF/views")
				//.addFlowLocationPattern("/**/*-flow.xml")
				//.build();				
	}
	
	/*@Bean
	public FlowBuilder flowBuilder() {
		AddRecipeFlowBuilder addRecipeFlow = new AddRecipeFlowBuilder();
		return addRecipeFlow;
	}*/

	@Bean
	public FlowBuilderServices flowBuilderServices() {
		return getFlowBuilderServicesBuilder()
				.setViewFactoryCreator(mvcViewFactoryCreator())
				.setValidator(this.webMvcConfig.getValidator())
				.setDevelopmentMode(true)	//TODO: turn this off for production
				.build();
	}

	@Bean
	public MvcViewFactoryCreator mvcViewFactoryCreator() {
		MvcViewFactoryCreator factoryCreator = new MvcViewFactoryCreator();
		factoryCreator.setViewResolvers(Arrays.<ViewResolver>asList(this.webMvcConfig.viewResolver()));
		factoryCreator.setUseSpringBeanBinding(true);
		return factoryCreator;
	}
}

package com.project.fitness.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
		info=@Info(
				title="Fitness Monolith App REST API Document",
				version="V1.0",
				description="REST endpoints to perform different operations on Fitness App.",
				license=@License(
						name="ABC Company Ltd",
						url="https://www.google.com"
						)
				),
			servers=@Server(description="localhost",url="http://localhost:8080")
		)

public class ApiConfig {

}

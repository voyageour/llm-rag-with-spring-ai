package dev.amitwani.mcp_spring_java;

import dev.amitwani.mcp_spring_java.service.UserService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpSpringJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpSpringJavaApplication.class, args);
	}

	@Bean
	ToolCallbackProvider userTools(UserService userService) {
		return MethodToolCallbackProvider
				.builder()
				.toolObjects(userService)
				.build();
	}

	//    @Bean
//    public List<ToolCallback> danTools() {
//        return List.of(ToolCallbacks.from(userService));
//    }

}

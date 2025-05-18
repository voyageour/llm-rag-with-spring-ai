package bootiful.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "bootiful.service"})
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

//    @Bean
//    ApplicationRunner applicationRunner(
//            Chatbot chatbot,
//            VectorStore vectorStore,
//            JdbcTemplate jdbcTemplate,
//            @Value("file://${HOME}/Desktop/pdfs/Carina.pdf") Resource resource) {
//        return args -> {
//            init(vectorStore, jdbcTemplate, resource);
//            var response = chatbot.chat("Who’s Sandeep Mishra?");
//            System.out.println(Map.of("response", response));
//        };
//    }
}














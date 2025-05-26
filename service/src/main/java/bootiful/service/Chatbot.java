package bootiful.service;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Chatbot {


    private final String template1 = """
                        
            You're assisting with questions about services offered by Carina.
            Carina is a two-sided healthcare marketplace focusing on home care aides (caregivers)
            and their Medicaid in-home care clients (adults and children with developmental disabilities and low income elderly population).
            Carina's mission is to build online tools to bring good jobs to care workers, so care workers can provide the
            best possible care for those who need it.
                    
            Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
            If unsure, simply state that you don't know.
                    
            DOCUMENTS:
            {documents}
                        
            """;

    private final String template2 = """
                        
            You're assisting with questions about news of Shri Ram Temple inauguration in Ayodhya , India.
                    
            Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
            If unsure, simply state that you don't know.
                    
            DOCUMENTS:
            {documents}
                        
            """;

    @Value("classpath:/prompts/qa-prompt.st")
    private Resource qaPromptResource;

    @Value("classpath:/News.MD")
    Resource docsToStuffResource;
    private final OllamaChatModel aiClient;
//    private final VectorStoreDocumentRetriever vsr;

    Chatbot(OllamaChatModel aiClient/*, VectorStoreDocumentRetriever vsr*/) {
        this.aiClient = aiClient;
//        this.vsr = vsr;
    }

//    public String chatUsingRAG(String message) {
//       var listOfSimilarDocuments = this.vsr.retrieve(new Query(message));
//        var documents = listOfSimilarDocuments
//                .stream()
//                .map(Document::getContent)
//                .collect(Collectors.joining(System.lineSeparator()));
//        var systemMessage = new SystemPromptTemplate(this.template2)
//                .createMessage(Map.of("documents", documents));
//        var userMessage = new UserMessage(message);
//        var prompt = new Prompt(List.of(systemMessage , userMessage));
//        var aiResponse = aiClient.call(prompt)
//                                .getResult()
//                                .getOutput()
//                                .getContent();
//        return aiResponse;
//    }

    public String chatWithFewShotLearning(String message) {
        var system = new SystemMessage("You are an assistant that classifies tweet sentiment as Positive, Negative, or Neutral.");
        var tweets = new UserMessage("""
        Tweet: "I love sunny days!"
        Sentiment: Positive
        
        Tweet: "I lost my wallet today."
        Sentiment: Negative
        
        Tweet: "Just had lunch."
        Sentiment: Neutral
        
        Tweet: "Excited for my trip tomorrow!"
        Sentiment:
        
        Tweet: """+message.toString()+"""
        Sentiment:
        """);
        var client = OllamaChatModel.builder().defaultOptions(OllamaOptions.builder().temperature(0.0d).model("llama3").build()).ollamaApi(new OllamaApi()).build();

        return client.call(new Prompt(List.of(system, tweets))).getResult().getOutput().getText();
    }

    public String chatWithStuffing(String message) {
        PromptTemplate promptTemplate = new PromptTemplate(qaPromptResource);
        Map<String, Object> map = new HashMap<>();
        map.put("question", message);
        map.put("context", docsToStuffResource);

        Prompt prompt = promptTemplate.create(map);
//        chatClient.prompt(userInput).call().content();
        return aiClient.call(prompt)
                .getResult()
                .getOutput()
                .getText();
    }

    public String chat(String message) {
        return aiClient.call(message);
    }
}

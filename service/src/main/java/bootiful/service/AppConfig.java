package bootiful.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.ToolCallbackProvider;

@Component
public class AppConfig {

    @Bean
    VectorStore vectorStore(EmbeddingModel ec,
                            JdbcTemplate t) {
        return PgVectorStore.builder(t, ec).build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools) {
        return chatClientBuilder.defaultTools(tools).build();
    }

//    @Bean
//    VectorStoreDocumentRetriever vectorStoreRetriever(VectorStore vs) {
//        return new VectorStoreDocumentRetriever(vs, 0.75, 4,null);
//    }

    @Bean
    TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }
}

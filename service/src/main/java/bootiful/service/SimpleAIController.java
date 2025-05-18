package bootiful.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SimpleAIController {


    @Autowired
    Chatbot chatbot;
    @Autowired
    VectorStore vectorStore;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Value("classpath:/News.MD")
    Resource resource;

    @Value("classpath:/CT25.pdf")
    Resource pdfResource;

    List<Document> init(VectorStore vectorStore, JdbcTemplate template, Resource pdfResource)
            throws Exception {

        template.update("delete from vector_store");

        var config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().withNumberOfBottomTextLinesToDelete(3)
                        .withNumberOfTopPagesToSkipBeforeDelete(1)
                        .build())
                .withPagesPerDocument(1)
                .build();

        var pdfReader = new PagePdfDocumentReader(pdfResource, config);

//        var pdfReader = new TextReader(pdfResource);

        var textSplitter = new TokenTextSplitter();
        List<Document> documents = textSplitter.apply(pdfReader.get());
        vectorStore.accept(documents);
        return documents;
    }


    @GetMapping("/ai/simpleRAG")
    public ResponseEntity<Map<String, String>> chatRAG(@RequestParam(value = "message") String message) throws Exception {
        var documents = init(vectorStore, jdbcTemplate, pdfResource);
        var response = chatbot.chatUsingRAG(message);
        Map<String, String> response1 = Map.of("response", response);
        System.out.println(response1);
        return ResponseEntity.ok(response1);
    }

    @GetMapping("/ai/simpleStuff")
    public ResponseEntity<Map<String, String>> chatStuff(@RequestParam(value = "message") String message) throws Exception {
        var response = chatbot.chatWithStuffing(message);
        Map<String, String> response1 = Map.of("response", response);
        System.out.println(response1);
        return ResponseEntity.ok(response1);
    }

    @GetMapping("/ai/simpleChat")
    public ResponseEntity<String> chat(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) throws Exception {
        return ResponseEntity.ok(chatbot.chat(message));

    }

    @GetMapping("/ai/fewshot")
    public ResponseEntity<String> chatFewShot(@RequestParam(value = "message") String message) throws Exception {
        return ResponseEntity.ok(chatbot.chatWithFewShotLearning(message));

    }
}

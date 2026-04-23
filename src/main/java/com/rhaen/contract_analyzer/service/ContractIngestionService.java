package com.rhaen.contract_analyzer.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ContractIngestionService {

    private final VectorStore vectorStore;

    public ContractIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void processAndStoreContract(String extractedText, String contractId) {
        Document contractDoc = new Document(extractedText, Map.of("contractId", contractId));

        List<Character> punctuationMarks = List.of('.', '!', '?', ',', ';', ':', '-', ' ', '\n', '\t');
        TokenTextSplitter splitter = new TokenTextSplitter(1200, 350, 5, 10000, true, punctuationMarks);

        List<Document> chunkedDocs = splitter.apply(List.of(contractDoc));

        chunkedDocs.forEach(doc -> doc.getMetadata().put("contractId", contractId));

        vectorStore.add(chunkedDocs);
    }
}

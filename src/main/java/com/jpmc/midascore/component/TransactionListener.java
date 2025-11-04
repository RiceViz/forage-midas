package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);
    private final TransactionService transactionService;
    
    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    @KafkaListener(topics = "${general.kafka-topic}")
    public void handleTransaction(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);
        boolean success = transactionService.processTransaction(transaction);
        if (success) {
            logger.info("Transaction processed successfully");
        } else {
            logger.warn("Transaction was rejected");
        }
    }
}

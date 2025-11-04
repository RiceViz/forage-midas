package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    
    public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }
    
    @Transactional
    public boolean processTransaction(Transaction transaction) {
        try {
            // Validate sender
            UserRecord sender = userRepository.findById(transaction.getSenderId());
            if (sender == null) {
                logger.warn("Transaction rejected: Invalid senderId {}", transaction.getSenderId());
                return false;
            }
            
            // Validate recipient
            UserRecord recipient = userRepository.findById(transaction.getRecipientId());
            if (recipient == null) {
                logger.warn("Transaction rejected: Invalid recipientId {}", transaction.getRecipientId());
                return false;
            }
            
            // Validate sender balance
            if (sender.getBalance() < transaction.getAmount()) {
                logger.warn("Transaction rejected: Insufficient balance for sender {}. Required: {}, Available: {}", 
                           sender.getName(), transaction.getAmount(), sender.getBalance());
                return false;
            }
            
            // Process the transaction
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount());
            
            // Save updated balances
            userRepository.save(sender);
            userRepository.save(recipient);
            
            // Record the transaction
            TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
            transactionRepository.save(transactionRecord);
            
            logger.info("Transaction processed successfully: {} -> {} amount: {}", 
                       sender.getName(), recipient.getName(), transaction.getAmount());
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error processing transaction: {}", transaction, e);
            return false;
        }
    }
}

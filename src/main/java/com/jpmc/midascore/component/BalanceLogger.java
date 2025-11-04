package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BalanceLogger implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(BalanceLogger.class);
    private final DatabaseConduit databaseConduit;
    
    public BalanceLogger(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // This will run after the Spring context is fully loaded
        // We'll add a small delay to ensure transactions are processed
        Thread.sleep(3000);
        
        UserRecord waldorf = databaseConduit.findByName("waldorf");
        if (waldorf != null) {
            logger.info("WALDORF'S FINAL BALANCE: {}", waldorf.getBalance());
            logger.info("WALDORF'S FINAL BALANCE (rounded down): {}", (int) waldorf.getBalance());
        }
    }
}

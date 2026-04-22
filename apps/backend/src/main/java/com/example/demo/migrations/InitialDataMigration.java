package com.example.demo.migrations;

import com.example.demo.model.Document;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ChangeUnit(id = "initial-data-generation", order = "001", author = "tobiasVoeg")
public class InitialDataMigration {

    @Execution
    public void execution(MongoTemplate mongoTemplate) {
        List<Document> documents = new ArrayList<>();

        // 1. Cars (1-333)
        for (int i = 1; i <= 333; i++) {
            String name = "Auto #" + i;
            String content = String.format("""
                    # Auto: %s

                    Dies ist eine DOD-konforme Beschreibung für ein Fahrzeug.

                    - **Marke:** BMW
                    - **Modell:** M%d
                    - **Leistung:** %d PS
                    - **Baujahr:** %d
                    """, name, i % 10, 200 + (i % 300), 2010 + (i % 15));
            documents.add(new Document(i, name, content, LocalDateTime.now()));
        }

        // 2. Food (334-666)
        for (int i = 334; i <= 666; i++) {
            String name = "Lebensmittel #" + i;
            String content = String.format("""
                    # Lebensmittel: %s

                    DOD-Style Produktbeschreibung für Lebensmittel.

                    - **Marke:** Bio-Nature
                    - **Gewicht:** %dg
                    - **Kalorien:** %dkcal
                    - **MHD:** 2026-12-%02d
                    """, name, 100 + (i % 900), 50 + (i % 500), 1 + (i % 28));
            documents.add(new Document(i, name, content, LocalDateTime.now()));
        }

        // 3. Electronics (667-1000)
        for (int i = 667; i <= 1000; i++) {
            String name = "Elektronik #" + i;
            String content = String.format("""
                    # Elektronik: %s

                    Automatisierte Beschreibung für ein elektronisches Gerät.

                    - **Typ:** Laptop
                    - **Marke:** TechMaster
                    - **Leistung:** %dW
                    - **Garantie:** %d Jahre
                    """, name, 45 + (i % 200), 1 + (i % 5));
            documents.add(new Document(i, name, content, LocalDateTime.now()));
        }

        // Batch insert for performance (DOD style)
        mongoTemplate.insertAll(documents);
    }

    @RollbackExecution
    public void rollbackExecution(MongoTemplate mongoTemplate) {
        // Rollback strategy: remove generated documents in the ID range
        for (int i = 1; i <= 1000; i++) {
            mongoTemplate.remove(new Document(i, null, null, null));
        }
    }
}

package com.phonebook;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Service;

@Service
public class MetricService {
    private final Counter addCounter, searchCounter, deleteCounter, editCounter, listCounter, failedCounter, emptyCounter;

    public MetricService(MeterRegistry registry) {
        this.addCounter = registry.counter("phonebook.requests", "type", "add");
        this.searchCounter = registry.counter("phonebook.requests", "type", "search");
        this.deleteCounter = registry.counter("phonebook.requests", "type", "delete");
        this.editCounter = registry.counter("phonebook.requests", "type", "edit");
        this.listCounter = registry.counter("phonebook.requests", "type", "list");
        this.failedCounter = registry.counter("phonebook.requests.failed");
        this.emptyCounter = registry.counter("phonebook.requests.empty");
    }

    public void incrementAdd() { addCounter.increment(); }
    public void incrementSearch() { searchCounter.increment(); }
    public void incrementDelete() { deleteCounter.increment(); }
    public void incrementEdit() { editCounter.increment(); }
    public void incrementList() { listCounter.increment(); }
    public void incrementFailed() { failedCounter.increment(); }
    public void incrementEmpty() { emptyCounter.increment(); }
} 
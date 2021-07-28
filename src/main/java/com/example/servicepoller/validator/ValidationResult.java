package com.example.servicepoller.validator;

import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Value
public class ValidationResult {

    Map<String, List<String>> warnings = new HashMap<>();
    Map<String, List<String>> errors = new HashMap<>();

    public void addError(final String field, final String message) {
        val messageList = errors.getOrDefault(field, new ArrayList<>());
        messageList.add(message);
        errors.put(field, messageList);
    }

    public void addWarning(final String field, final String message) {
        val messageList = warnings.getOrDefault(field, new ArrayList<>());
        messageList.add(message);
        warnings.put(field, messageList);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !errors.isEmpty();
    }
}

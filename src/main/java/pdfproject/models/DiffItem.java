package pdfproject.models;

import pdfproject.constants.Operation;

import java.util.Objects;

public class DiffItem {
    private final Operation operation;
    private final String from;
    private final String to;

    public DiffItem(Operation operation, String from, String to) {
        this.operation = operation;
        this.from = from;
        this.to = to;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiffItem)) return false;
        DiffItem that = (DiffItem) o;
        return operation == that.operation &&
               Objects.equals(from, that.from) &&
               Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, from, to);
    }
}

package pdfproject.models;

import pdfproject.constants.Operation;

import java.util.Objects;

public record DiffItem(Operation operation, String from, String to) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiffItem that)) return false;
        return operation == that.operation &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

}

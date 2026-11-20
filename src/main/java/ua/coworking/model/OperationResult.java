package ua.coworking.model;
/** Доменний результат. Коди HTTP сюди не належать. */
public record OperationResult(ResultKind kind, String message) {}

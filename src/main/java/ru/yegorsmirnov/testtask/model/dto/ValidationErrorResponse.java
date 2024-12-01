package ru.yegorsmirnov.testtask.model.dto;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) { }



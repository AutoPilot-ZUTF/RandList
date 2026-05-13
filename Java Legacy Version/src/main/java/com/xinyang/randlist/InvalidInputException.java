package com.xinyang.randlist;

public class InvalidInputException extends RuntimeException {

    public static final String inputBoundError = "Lower bound must < upper bound.";
    public static final String lowerBoundRangeError = "Lower bound must ≥ 0.";
    public static final String upperBoundRangeError = "Upper bound must > 0.";
    public static final String amountRangeError = "Amount must > 0.";
    public static final String amountError = "Amount must not exceed range.";
    public static final String nullInputFieldError = "One or more input fields are empty.";
    public static final String inputSyntaxError = "Invalid list. Example: 1,2,3,4,5...";
    public static final String duplicateElementError = "Input has one or more duplicated elements.";
    public static final String lowerListElementError = "Minimum element in list out of bound.";
    public static final String upperListElementError = "Maximum element in list out of bound.";
    public static final String includeAmountError = "Amount should ≥ included elements.";
    public static final String specifyAmountError = "Amount should ≤ specified elements.";
    public static final String excludeAmountError = "Remaining elements < amount.";
    public static final String allElementsExcludedError = "All elements excluded from given range.";
    public static final String smallFieldsNotIntegerError = "Lower/Upper bound or amount not integer.";
    public static final String stringListSyntaxError = "Invalid list. Example: 1,ab,3.2,-4...";

    public InvalidInputException(String message) {
        super(message);
    }
}

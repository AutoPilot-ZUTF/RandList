package com.xinyang.randlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InputHandler {

    // 根据当前模式统一处理输入
    public List<?> handleGenerate(String mode,
                                  String lowerText,
                                  String upperText,
                                  String amountText,
                                  String listText) {
        return switch (mode) {
            case "Simple" -> handleSimpleMode(lowerText, upperText, amountText);
            case "Include" -> handleIncludeMode(lowerText, upperText, amountText, listText);
            case "Exclude" -> handleExcludeMode(lowerText, upperText, amountText, listText);
            case "Specify" -> handleSpecifyMode(listText, amountText);
            default -> throw new InvalidInputException("Invalid mode.");
        };
    }

    // 把单个输入框文本解析成 int
    private int parseIntField(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (RuntimeException exception) {
            throw new InvalidInputException(InvalidInputException.smallFieldsNotIntegerError);
        }
    }

    // 把逗号分隔的字符串解析成整数列表
    private List<Integer> parseIntegerList(String text) {
        List<Integer> parsedList = new ArrayList<>();
        if (!text.isEmpty()) {
            String[] parts = text.split("[,，]");
            for (String part : parts) {
                String trimmed = part.trim();
                //可以删除
                if (trimmed.isEmpty()) {
                    throw new InvalidInputException(InvalidInputException.inputSyntaxError);
                }
                int value = Integer.parseInt(trimmed);
                parsedList.add(value);
            }
        }
        return parsedList.stream().sorted().collect(Collectors.toList());
    }

    private List<String> parseStringList(String text) {
        List<String> parsedList = new ArrayList<>();
        if (!text.isEmpty()) {
            String[] parts = text.split("[,，]");
            for (String part : parts) {
                String trimmed = part.trim();
                //可以删除
                if (trimmed.isEmpty()) {
                    throw new InvalidInputException(InvalidInputException.inputSyntaxError);
                }
                parsedList.add(trimmed);
            }
        }
        return parsedList.stream().sorted().collect(Collectors.toList());
    }

    // 处理 Simple 模式：只需要区间和数量
    private List<Integer> handleSimpleMode(String lowerText,
                                           String upperText,
                                           String amountText) {
        if (lowerText.isEmpty() || upperText.isEmpty() || amountText.isEmpty()) {
            throw new InvalidInputException(InvalidInputException.nullInputFieldError);
        }
        int lower = parseIntField(lowerText);
        int upper = parseIntField(upperText);
        int amount = parseIntField(amountText);
        // 1. 创建 RandList 对象
        RandList thisRandList = new RandList(lower, upper, amount);
        // 2. 调用 generateList()
        return thisRandList.generateList();
    }

    // 处理 Include 模式：区间、数量、包含列表
    private List<Integer> handleIncludeMode(String lowerText,
                                            String upperText,
                                            String amountText,
                                            String listText) {
        List<String> errors = new ArrayList<>();
        if (lowerText.isEmpty() || upperText.isEmpty() || amountText.isEmpty()) {
            errors.add(InvalidInputException.nullInputFieldError);
        }
        if (listText.isEmpty()) {
            errors.add(InvalidInputException.nullInputFieldError);
        } else {
            try {
                parseIntegerList(listText);
            } catch (RuntimeException exception) {
                errors.add(InvalidInputException.inputSyntaxError);
            }
        }
        if (!errors.isEmpty()) {
            errors = errors.stream().distinct().collect(Collectors.toList());
            throw new InvalidInputException(String.join("\n", errors));
        }
        int lower = parseIntField(lowerText);
        int upper = parseIntField(upperText);
        int amount = parseIntField(amountText);
        List<Integer> includeList = parseIntegerList(listText);

        RandListInclude thisRandListInclude = new RandListInclude(lower, upper, amount, includeList);
        return thisRandListInclude.generateIncludeList();
    }

    // 处理 Exclude 模式：区间、数量、排除列表
    private List<Integer> handleExcludeMode(String lowerText,
                                            String upperText,
                                            String amountText,
                                            String listText) {
        List<String> errors = new ArrayList<>();
        if (lowerText.isEmpty() || upperText.isEmpty() || amountText.isEmpty()) {
            errors.add(InvalidInputException.nullInputFieldError);
        }
        if (listText.isEmpty()) {
            errors.add(InvalidInputException.nullInputFieldError);
        } else {
            try {
                parseIntegerList(listText);
            } catch (RuntimeException exception) {
                errors.add(InvalidInputException.inputSyntaxError);
            }
        }
        if (!errors.isEmpty()) {
            errors = errors.stream().distinct().collect(Collectors.toList());
            throw new InvalidInputException(String.join("\n", errors));
        }
        int lower = parseIntField(lowerText);
        int upper = parseIntField(upperText);
        int amount = parseIntField(amountText);
        List<Integer> excludeList = parseIntegerList(listText);

        RandListExclude thisRandListExclude = new RandListExclude(lower, upper, amount, excludeList);
        return thisRandListExclude.generateExcludeList();
    }

    // 处理 Specify 模式：指定列表和数量
    private List<String> handleSpecifyMode(String listText, String amountText) {
        List<String> errors = new ArrayList<>();
        if (amountText.isEmpty()) {
            errors.add(InvalidInputException.nullInputFieldError);
        }
        if (listText.isEmpty()) {
            errors.add(InvalidInputException.nullInputFieldError);
        } else {
            try {
                parseStringList(listText);
            } catch (RuntimeException exception) {
                errors.add(InvalidInputException.stringListSyntaxError);
            }
        }
        if (!errors.isEmpty()) {
            errors = errors.stream().distinct().collect(Collectors.toList());
            throw new InvalidInputException(String.join("\n", errors));
        }
        List<String> specifiedList = parseStringList(listText);
        int amount = parseIntField(amountText);

        RandListSpecified thisRandListSpecified = new RandListSpecified(amount, specifiedList);
        return thisRandListSpecified.generateSpecifiedList();
    }

}

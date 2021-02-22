package com.cevelop.ctylechecker.domain.types.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;


public class RenameTransformer {

    public String transform(String name, BiFunction<Integer, String[], String> action) {
        String[] nameParts = splitName(name);
        StringBuilder adjustedName = new StringBuilder();
        if (nameParts.length > 0) {
            for (int i = 0; i < nameParts.length; i++) {
                adjustedName.append(action.apply(i, nameParts));
            }
        } else {
            adjustedName.append(name);
        }
        return adjustedName.toString();
    }

    public String transformToSnakeCase(String name) {
        String transformed = transform(name, (index, nameParts) -> {
            if (index == nameParts.length - 1) {
                return nameParts[index];
            } else {
                return nameParts[index] + "_";
            }
        });
        return transformed.toLowerCase();
    }

    public String transformToConstCase(String name) {
        String transformed = transform(name, (index, nameParts) -> {
            if (index == nameParts.length - 1) {
                return nameParts[index];
            } else {
                return nameParts[index] + "_";
            }
        });
        return transformed.toUpperCase();
    }

    public String transformToCamelCase(String name) {
        return transform(name, (index, nameParts) -> {
            if (index == 0) {
                return nameParts[index].toLowerCase();
            } else {
                String part = nameParts[index].toLowerCase();
                if (part.length() > 1) {
                    part = Character.valueOf(part.charAt(0)).toString().toUpperCase() + part.substring(1);
                } else {
                    part = part.toUpperCase();
                }
                return part;
            }
        });
    }

    public String transformToPascalCase(String name) {
        return transform(name, (index, nameParts) -> {
            String part = nameParts[index].toLowerCase();
            if (part.length() > 1) {
                part = Character.valueOf(part.charAt(0)).toString().toUpperCase() + part.substring(1);
            } else {
                part = part.toUpperCase();
            }
            return part;
        });
    }

    public String transformToAllSmallCase(String name) {
        return transform(name, (index, nameParts) -> {
            return nameParts[index].toLowerCase();
        });
    }

    public String transformToAllBigCase(String name) {
        return transform(name, (index, nameParts) -> {
            return nameParts[index].toUpperCase();
        });
    }

    private List<String> balanceNames(List<String> pSplitNames) {
        List<String> balancedNames = new ArrayList<>();
        for (int i = 0; i < pSplitNames.size(); i++) {
            if (pSplitNames.get(i).length() < 2) {
                String current = pSplitNames.get(i);
                if ((i + 1) < pSplitNames.size()) {
                    String next = pSplitNames.get(i + 1);
                    current += next;
                    i++;
                }
                balancedNames.add(current);
            } else {
                balancedNames.add(pSplitNames.get(i));
            }
        }
        return balancedNames;
    }

    private String[] splitName(String name) {
        List<String> splittedNames = splitByPoint(Arrays.asList(name));
        splittedNames = splitByDash(splittedNames);
        splittedNames = splitPascalCase(splittedNames);
        splittedNames = splitCamelCase(splittedNames);
        splittedNames = splitSnakeCase(splittedNames);
        splittedNames = balanceNames(splittedNames);
        return splittedNames.toArray(new String[0]);
    }

    private List<String> splitByPoint(List<String> splittedNames) {
        List<String> tempNames = new ArrayList<>();
        for (String splitName : splittedNames) {
            tempNames.addAll(Arrays.asList(splitByPoint(splitName)));
        }
        return tempNames;
    }

    private List<String> splitByDash(List<String> splittedNames) {
        List<String> tempNames = new ArrayList<>();
        for (String splitName : splittedNames) {
            tempNames.addAll(Arrays.asList(splitByDash(splitName)));
        }
        return tempNames;
    }

    private List<String> splitPascalCase(List<String> splittedNames) {
        List<String> tempNames = new ArrayList<>();
        for (String splitName : splittedNames) {
            tempNames.addAll(Arrays.asList(splitPascalCase(splitName)));
        }
        return tempNames;
    }

    private List<String> splitCamelCase(List<String> splittedNames) {
        List<String> tempNames = new ArrayList<>();
        for (String splitName : splittedNames) {
            tempNames.addAll(Arrays.asList(splitCamelCase(splitName)));
        }
        return tempNames;
    }

    private List<String> splitSnakeCase(List<String> splittedNames) {
        List<String> tempNames = new ArrayList<>();
        for (String splitName : splittedNames) {
            tempNames.addAll(Arrays.asList(splitSnakeCase(splitName)));
        }
        return tempNames;
    }

    private String[] splitPascalCase(String name) {
        return name.split(Expressions.PASCAL_CAMEL_CASE_SPLIT.getExpression());
    }

    private String[] splitByPoint(String name) {
        return name.split(Expressions.POINT_SPLIT.getExpression());
    }

    private String[] splitCamelCase(String name) {
        return name.split(Expressions.PASCAL_CAMEL_CASE_SPLIT.getExpression());
    }

    private String[] splitSnakeCase(String name) {
        return name.split(Expressions.SNAKE_CASE_SPLIT.getExpression());
    }

    private String[] splitByDash(String name) {
        return name.split(Expressions.DASH_SPLIT.getExpression());
    }
}

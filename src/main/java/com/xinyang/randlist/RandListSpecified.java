package com.xinyang.randlist;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RandListSpecified extends RandList {

    private List<String> specifiedList;

    public RandListSpecified(int amount, List<String> specifiedList) {
        super(0, specifiedList.stream().distinct().collect(Collectors.toList()).size() - 1, amount);
        this.specifiedList = specifiedList;
    }

    public void validateInput() {
        List<String> errors = new ArrayList<>();
        if (specifiedList.stream().distinct().count() < specifiedList.size()) {
            errors.add(InvalidInputException.duplicateElementError);
        } else  {
            if (specifiedList.size() < getAmount()) {
                errors.add(InvalidInputException.specifyAmountError);
            }
        }
        if (!errors.isEmpty()) {
            errors = errors.stream().distinct().collect(Collectors.toList());
            throw new InvalidInputException(String.join("\n", errors));
        }
    }

    public List<String> generateSpecifiedList() {
        List<String> finalResult = new ArrayList<>();
        List<Integer> resultIndex = super.generateList();
        for (int i = 0; i < resultIndex.size(); i++) {
            finalResult.add(specifiedList.get(resultIndex.get(i)));
        }
        return finalResult.stream().sorted().collect(Collectors.toList());
    }

    public List<String> getSpecifiedList() {
        return specifiedList;
    }

    public void setSpecifiedList(List<String> specifiedList) {
        this.specifiedList = specifiedList;
    }
}

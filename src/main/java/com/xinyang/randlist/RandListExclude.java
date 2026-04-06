package com.xinyang.randlist;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RandListExclude extends RandList {

    private List<Integer> excludeList;

    public RandListExclude(int lowerBound, int upperBound, int amount, List<Integer> excludeList) {
        super(lowerBound, upperBound, amount);
        this.excludeList = excludeList;
    }


    @Override
    public List<String> commonErrorList(){
        List<String> errors = new ArrayList<>(super.commonErrorList());
        if (excludeList.stream().distinct().count() < excludeList.size()) {
            errors.add(InvalidInputException.duplicateElementError);
        }
        if (excludeList.size() == getUpperBound() - getLowerBound() + 1) {
            errors.add(InvalidInputException.allElementsExcludedError);
        }
        if (excludeList.get(0) < getLowerBound()) {
            errors.add(InvalidInputException.lowerListElementError);
        }
        if (excludeList.get(excludeList.size() - 1) > getUpperBound()) {
            errors.add(InvalidInputException.upperListElementError);
        }
        if (getAmount() > getUpperBound() - getLowerBound() + 1 - excludeList.size()) {
            errors.add(InvalidInputException.excludeAmountError);
        }
        if (errors.contains(InvalidInputException.lowerListElementError)
                || errors.contains(InvalidInputException.upperListElementError)
                || errors.contains(InvalidInputException.duplicateElementError)) {
            errors.remove(InvalidInputException.excludeAmountError);
            errors.remove(InvalidInputException.allElementsExcludedError);
        }
        return errors;
    }

    private void validateInput() {
        List<String> errors = commonErrorList();
        if (!errors.isEmpty()) {
            errors = errors.stream().distinct().collect(Collectors.toList());
            throw new InvalidInputException(String.join("\n", errors));
        }
    }

    public List<Integer> generateExcludeList() {
        validateInput();
        return pickDistinctNumbers(buildCandidateList(), getAmount());
    }

    @Override
    public List<Integer> buildCandidateList() {
        List<Integer> candidateList = super.buildCandidateList();
        candidateList.removeAll(excludeList);
        return candidateList;
    }

    @Override
    public List<Integer> pickDistinctNumbers(List<Integer> candidateList, int amount) {
        List<Integer> resultList = super.pickDistinctNumbers(candidateList, getAmount());
        return resultList.stream().sorted().collect(Collectors.toList());
    }


    public List<Integer> getExcludeList() {
        return excludeList;
    }

    public void setExcludeList(List<Integer> excludeList) {
        this.excludeList = excludeList;
    }
}

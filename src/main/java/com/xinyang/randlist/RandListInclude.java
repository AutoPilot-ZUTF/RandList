package com.xinyang.randlist;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RandListInclude extends RandList{

    private List<Integer> includeList;

    public RandListInclude(int lowerBound, int upperBound, int amount, List<Integer> includeList) {
        super(lowerBound, upperBound, amount);
        this.includeList = includeList;
    }

    @Override
    public List<String> commonErrorList(){
        List<String> errors = new ArrayList<>(super.commonErrorList());
        if (includeList.stream().distinct().count() < includeList.size()) {
            errors.add(InvalidInputException.duplicateElementError);
        }
        if (includeList.get(0) < getLowerBound()) {
            errors.add(InvalidInputException.lowerListElementError);
        }
        if (includeList.get(includeList.size() - 1) > getUpperBound()) {
            errors.add(InvalidInputException.upperListElementError);
        }
        if (getAmount() < includeList.size()) {
            errors.add(InvalidInputException.includeAmountError);
        }
        if (errors.contains(InvalidInputException.lowerListElementError) || errors.contains(InvalidInputException.upperListElementError)) {
            errors.remove(InvalidInputException.includeAmountError);
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

    public List<Integer> generateIncludeList() {
        validateInput();
        return pickDistinctNumbers(buildCandidateList(), getAmount() - includeList.size());
    }

    @Override
    public List<Integer> buildCandidateList() {
        List<Integer> candidateList = super.buildCandidateList();
        candidateList.removeAll(includeList);
        return candidateList;
    }

    @Override
    public List<Integer> pickDistinctNumbers(List<Integer> candidateList, int amount) {
        List<Integer> resultList = super.pickDistinctNumbers(candidateList, getAmount() - includeList.size());
        resultList.addAll(includeList);
        return resultList.stream().sorted().collect(Collectors.toList());
    }

    public List<Integer> getIncludeList() {
        return includeList;
    }

    public void setIncludeList(List<Integer> includeList) {
        this.includeList = includeList;
    }
}

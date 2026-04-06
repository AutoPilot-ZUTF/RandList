package com.xinyang.randlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandList {

    private int lowerBound;
    private int upperBound;
    private int amount;

    public RandList(int lowerBound, int upperBound, int amount) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.amount = amount;
    }

    public List<String> commonErrorList() {
        List<String> errors = new ArrayList<>();
        if (lowerBound >= upperBound) {
            errors.add(InvalidInputException.inputBoundError);
        }
        if (lowerBound < 0) {
            errors.add(InvalidInputException.lowerBoundRangeError);
        }
        if (upperBound <= 0) {
            errors.add(InvalidInputException.upperBoundRangeError);
        }
        if (amount <= 0) {
            errors.add(InvalidInputException.amountRangeError);
        }
        if (amount > (upperBound - lowerBound + 1)) {
            errors.add(InvalidInputException.amountError);
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

    public List<Integer> generateList() {
        validateInput();
        return pickDistinctNumbers(buildCandidateList(), amount);
    }

    public List<Integer> buildCandidateList() {
        List<Integer> candidateList = new ArrayList<>();
        for (int i = lowerBound; i <= upperBound; i++) {
            candidateList.add(i);
        }
        return candidateList;
    }

    public List<Integer> pickDistinctNumbers(List<Integer> candidateList, int amount) {
        List<Integer> pickedList = new ArrayList<>();
        int randomIntBound = candidateList.size();
        Random random = new Random();
        while (pickedList.size() < amount) {
            int index = random.nextInt(randomIntBound);
            if (!pickedList.contains(candidateList.get(index))) {
                pickedList.add(candidateList.get(index));
            }
        }
        return pickedList.stream().sorted().collect(Collectors.toList());
    }


    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

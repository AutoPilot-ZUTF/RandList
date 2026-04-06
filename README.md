

# RandList

RandList is a JavaFX desktop application for generating random lists under several modes, including simple generation, inclusion, exclusion, and specified-list selection.

## Features

- **Simple mode**: generate distinct random integers within a lower and upper bound.
- **Include mode**: generate random integers while forcing a given set of numbers to be included.
- **Exclude mode**: generate random integers while removing a given set of numbers from the candidate range.
- **Specify mode**: generate results from a user-provided list of strings instead of an integer range.
- **Input validation**: centralized validation with custom exception handling.
- **Input memory**: remembers user inputs for different modes in the GUI.

## Project Structure

The project is organized into UI, input handling, domain logic, and validation/error handling.

### UI Layer

#### `Launcher`
Application entry point.

- `main(String[])`: launches the application.

#### `RandListApp`
JavaFX GUI application.

Main responsibilities:
- start the primary stage
- build and update mode-specific input areas
- append results to the output area
- clear results
- save and restore current mode inputs

Key methods:
- `start(Stage)`
- `appendResult(String)`
- `updateDynamicInputBox(VBox)`
- `clearResults()`
- `saveCurrentModeInputs()`
- `restoreCurrentModeInputs()`
- `createInputField(String, double)`
- `withLabel(String, TextField)`

### Input Layer

#### `InputHandler`
Converts raw text input from the UI into typed values and delegates to the appropriate generation mode.

Main responsibilities:
- parse integer fields
- parse comma-separated integer lists
- parse string lists
- dispatch requests by mode

Key methods:
- `parseIntField(String)`
- `parseIntegerList(String)`
- `parseStringList(String)`
- `handleGenerate(String, String, String, String, String)`
- `handleSimpleMode(String, String, String)`
- `handleIncludeMode(String, String, String, String)`
- `handleExcludeMode(String, String, String, String)`
- `handleSpecifyMode(String, String)`

#### `InputMemory`
Stores the latest inputs for each mode so the UI can restore them when the user switches modes.

Stored fields include:
- simple mode: lower bound, upper bound, amount
- include mode: lower bound, upper bound, amount, include list
- exclude mode: lower bound, upper bound, amount, exclude list
- specify mode: amount, specify list

Key method:
- `clear()`

### Core Domain Layer

#### `RandList`
Base class for integer-based random list generation.

Core responsibilities:
- store the numeric generation range and target amount
- validate shared input rules
- build a candidate list
- randomly select distinct numbers
- generate the final result list
- provide shared error messages

Key methods:
- `RandList(int, int, int)`
- `validateInput()`
- `buildCandidateList()`
- `pickDistinctNumbers(List<Integer>, int)`
- `generateList()`
- `commonErrorList()`

Core fields:
- `lowerBound`
- `upperBound`
- `amount`

#### `RandListInclude`
Subclass of `RandList` for include mode.

Purpose:
- guarantees that a given integer list is included in the final result

Key methods:
- `RandListInclude(int, int, int, List<Integer>)`
- `validateInput()`
- `buildCandidateList()`
- `pickDistinctNumbers(List<Integer>, int)`
- `generateIncludeList()`
- `commonErrorList()`

#### `RandListExclude`
Subclass of `RandList` for exclude mode.

Purpose:
- removes a given integer list from the candidate pool before generation

Key methods:
- `RandListExclude(int, int, int, List<Integer>)`
- `validateInput()`
- `buildCandidateList()`
- `validateInput()`
- `generateExcludeList()`
- `commonErrorList()`

#### `RandListSpecified`
Independent generator for specified-list mode.

Purpose:
- randomly select items from a provided `List<String>`

Key methods:
- `RandListSpecified(int, List<String>)`
- `validateInput()`
- `generateSpecifiedList()`

### Validation and Exception Handling

#### `InvalidInputException`
Custom exception used when user input violates validation rules.

Key constructor:
- `InvalidInputException(String)`

## Relationships Between Classes

- `Launcher` starts `RandListApp`.
- `RandListApp` uses `InputHandler` to process user input.
- `RandListApp` owns an `InputMemory` instance to preserve form state.
- `InputHandler` creates and uses:
  - `RandList` for simple mode
  - `RandListInclude` for include mode
  - `RandListExclude` for exclude mode
  - `RandListSpecified` for specify mode
- `RandListInclude` and `RandListExclude` extend `RandList`.
- Validation failures are reported with `InvalidInputException`.

## Supported Modes

### 1. Simple
Input:
- lower bound
- upper bound
- amount

Output:
- a list of distinct random integers inside the range

### 2. Include
Input:
- lower bound
- upper bound
- amount
- include list

Output:
- a list of distinct random integers that must contain the specified integers

### 3. Exclude
Input:
- lower bound
- upper bound
- amount
- exclude list

Output:
- a list of distinct random integers that never contain the excluded integers

### 4. Specify
Input:
- amount
- specify list

Output:
- a random selection of items from the given string list

## Typical Flow

1. The user selects a mode in the GUI.
2. `RandListApp` collects the current text-field values.
3. `InputHandler` parses the input strings into integers, integer lists, or string lists.
4. The corresponding generator object is created.
5. Input is validated.
6. The result list is generated.
7. `RandListApp` appends the formatted result to the output area.
8. `InputMemory` stores the current input state for later restoration.

## Tech Stack

- Java
- JavaFX
- Maven

## Notes

This README is based on the project UML design. If implementation details differ slightly from the diagram, the source code should be treated as the final reference.
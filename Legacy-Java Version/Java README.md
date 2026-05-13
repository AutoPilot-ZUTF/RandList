# RandList

RandList is a JavaFX desktop application for generating **distinct random results** through four modes: **Simple**, **Include**, **Exclude**, and **Specify**.

It is designed for quick use with a minimal UI, clear validation feedback, clickable result copying, and per-mode input memory.

## Features

- **Four generation modes**
  - **Simple**: generate distinct random integers within a numeric range.
  - **Include**: force specific integers to appear in the final result.
  - **Exclude**: remove specific integers from the candidate pool before generation.
  - **Specify**: generate results from a user-provided list of strings instead of a numeric range.
- **Distinct output**: results never contain duplicate elements.
- **Sorted output**: generated results are displayed in sorted order.
- **Clickable result prefixes**: click a prefix such as `01:` to copy the full corresponding line.
- **Hover feedback**: hovering over a clickable prefix shows a copy hint in the results header.
- **Auto-growing result area**: the window grows with content until a maximum height is reached, then enables scrolling.
- **Per-mode input memory**: each mode remembers the last values entered when switching between modes.
- **Centralized validation**: invalid input is handled through a custom runtime exception with readable error messages.
- **Keyboard-friendly flow**: pressing **Enter** triggers generation.

## UI Preview

<img width="532" height="832" alt="Screenshot 2026-04-15 at 23 12 45" src="https://github.com/user-attachments/assets/46b06309-7ca9-4a5c-ae85-a6f6bca3425c" />


## Current UI / App Behavior

Based on the current implementation:

- Window title: `RandList v1.1`
- Window is **not resizable**
- Results are numbered as `01:`, `02:`, ...
- Error messages are shown as `Error:` entries in the same result area
- Repeating the **same consecutive error message** will not append a duplicate error entry
- The **Clear All** button uses a confirmation step: `Clear All` → `Confirm`

## Supported Modes

### 1. Simple

**Inputs**
- `Lower bound`
- `Upper bound`
- `Amount`

**Behavior**
- Builds a candidate list from `lowerBound` to `upperBound` (inclusive)
- Randomly selects distinct integers
- Sorts the final result before display

**Example output**
```text
[2, 5, 9, 11]
```

### 2. Include

**Inputs**
- `Lower bound`
- `Upper bound`
- `Amount`
- `Elements to include`

**Behavior**
- Validates that all included integers are inside the range
- Removes the included integers from the candidate pool
- Randomly fills the remaining slots
- Adds the required integers back and sorts the final result

**Example**
Range `1-10`, amount `5`, include list `2,7`

Possible result:
```text
[2, 4, 6, 7, 9]
```

### 3. Exclude

**Inputs**
- `Lower bound`
- `Upper bound`
- `Amount`
- `Elements to exclude`

**Behavior**
- Validates that all excluded integers are inside the range
- Removes the excluded integers from the candidate pool
- Randomly selects distinct integers from the remaining pool
- Sorts the final result

**Example**
Range `1-10`, amount `4`, exclude list `3,5`

Possible result:
```text
[1, 4, 8, 10]
```

### 4. Specify

**Inputs**
- `Specified list`
- `Amount`

**Behavior**
- Parses a comma-separated string list
- Selects distinct items by randomly picking indices
- Returns the chosen items in sorted order

**Example**
List `apple, banana, cherry, mango`, amount `2`

Possible result:
```text
[apple, mango]
```

## Input Rules

### Numeric modes (`Simple`, `Include`, `Exclude`)

The current validation rules are:

- `Lower bound` must be `< Upper bound`
- `Lower bound` must be `>= 0`
- `Upper bound` must be `> 0`
- `Amount` must be `> 0`
- `Amount` must not exceed the available number of elements in the mode

### Include mode

Additional rules:

- Included elements must not contain duplicates
- Minimum included element must not be below the lower bound
- Maximum included element must not exceed the upper bound
- `Amount` must be **greater than or equal to** the number of included elements

### Exclude mode

Additional rules:

- Excluded elements must not contain duplicates
- Minimum excluded element must not be below the lower bound
- Maximum excluded element must not exceed the upper bound
- Excluding all elements in the range is invalid
- The remaining available elements must still be enough to satisfy `Amount`

### Specify mode

Additional rules:

- The specified list must not contain duplicates
- `Amount` must be **less than or equal to** the number of specified elements

## Input Syntax

### Integer list syntax

Used by **Include** and **Exclude** modes:

```text
1,2,3,4,5
```

Notes:
- Both English comma `,` and Chinese comma `，` are accepted
- Empty items such as `1,,3` are treated as invalid input

### String list syntax

Used by **Specify** mode:

```text
apple,banana,cherry
```

Notes:
- Both English comma `,` and Chinese comma `，` are accepted
- Empty items such as `a,,b` are treated as invalid input
- Parsed strings are trimmed before use

## Result Interaction

Each result line has two parts:

- a fixed-width prefix such as `01:`
- the generated content

Interaction details:

- Clicking the prefix copies the **full line**, including the prefix
- Hovering over the prefix makes it bold and shows `(Click to copy)` in the header
- Long content wraps automatically in the result area

## Error Messages

Validation failures are reported through `InvalidInputException`.

Current messages in the code include:

- `Lower bound must < upper bound.`
- `Lower bound must ≥ 0.`
- `Upper bound must > 0.`
- `Amount must > 0.`
- `Amount must not exceed range.`
- `One or more input fields are empty.`
- `Invalid list. Example: 1,2,3,4,5...`
- `Input has one or more duplicated elements.`
- `Minimum element in list out of bound.`
- `Maximum element in list out of bound.`
- `Amount should ≥ included elements.`
- `Amount should ≤ specified elements.`
- `Remaining elements < amount.`
- `All elements excluded from given range.`
- `Lower/Upper bound or amount not integer.`
- `Invalid list. Example: 1,ab,3.2,-4...`

## Project Structure

### UI Layer

#### `Launcher`
Small launcher class that delegates startup to `RandListApp`.

#### `RandListApp`
Main JavaFX application.

Responsibilities:
- build the window and mode selector
- switch mode-specific input areas dynamically
- handle result rendering and copy interaction
- manage result scrolling and dynamic window height
- save and restore mode-specific inputs
- trigger generation on button click or `Enter`

### Input Layer

#### `InputHandler`
Parses raw text input and dispatches generation requests to the correct mode.

Responsibilities:
- parse single integer fields
- parse comma-separated integer lists
- parse comma-separated string lists
- validate mode-level empty/syntax cases before domain generation
- route generation by selected mode

#### `InputMemory`
Stores the latest input values for each mode so switching modes does not immediately lose user input.

Stored state:
- simple mode: lower bound, upper bound, amount
- include mode: lower bound, upper bound, amount, include list
- exclude mode: lower bound, upper bound, amount, exclude list
- specify mode: specified list, amount

### Domain Layer

#### `RandList`
Base generator for integer-based random selection.

Responsibilities:
- hold `lowerBound`, `upperBound`, and `amount`
- validate shared numeric constraints
- build the candidate integer range
- randomly pick distinct integers
- return sorted results

#### `RandListInclude`
Extends `RandList`.

Responsibilities:
- validate include-specific constraints
- remove included values from the candidate pool
- generate the remaining required values
- merge included values back into the final result

#### `RandListExclude`
Extends `RandList`.

Responsibilities:
- validate exclude-specific constraints
- remove excluded values from the candidate pool
- generate the final result from the remaining values

#### `RandListSpecified`
String-list generator built on top of index selection through `RandList`.

Responsibilities:
- validate the specified list
- generate random distinct indices
- map indices back to strings
- return sorted string results

### Exception Layer

#### `InvalidInputException`
Custom runtime exception used for user-facing validation errors.

## Generation Flow

1. The user selects a mode.
2. `RandListApp` builds the matching input area.
3. The user enters values and clicks **Generate** (or presses **Enter**).
4. `InputHandler` parses raw text into typed values.
5. A mode-specific generator is created.
6. Validation runs.
7. A random distinct result is generated.
8. The result is appended to the UI.
9. The current mode input values remain available through `InputMemory`.

## Tech Stack

- Java
- JavaFX
- Maven

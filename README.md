# RandList

RandList is a random list generator for quickly picking random results from a number range or a custom list. It is useful for drawings, random grouping, choosing candidates, and generating random numbers.

> This project is not open source. This GitHub page is only used for project presentation and usage information.
Compatible with iPhone and iPad (iOS 17.0+), as well as Apple Silicon Macs (macOS 14.0+). The Liquid Glass effect requires iOS 26.0+ or macOS 26.0+.
The original Java version of RandList is open source. If you need an open-source version, you can clone the Java repository.
<br>
<p align="center">
  <a href="https://apps.apple.com/app/randlist/id6766366716">
    <img width="1083" height="677" alt="GitHub README" src="https://github.com/user-attachments/assets/f595ae8b-e547-451a-8866-b6678a86423e" />
  </a>
</p>

## Features

- Pick unique random numbers from a specified range.
- Force specific numbers to be included in the result.
- Exclude specific numbers from the result.
- Pick random items from a custom text list.
- Display generated results in a clean numbered list.
- Keep generated values unique within each result.
- Show results in sorted order for easier reading.
- Highlight included values in Include mode.
- Show clear validation messages when the input is invalid.
- Automatically remember inputs and generated results.
- Keep a history of generated results for review and reuse.
- Copy results by tapping them.
- Customize light/dark appearance, accent color, result font, and result size.
- Support multiple interface languages.

## How To Use

### Simple

Use this mode to generate random numbers from a range.

Inputs:

- Lower Bound
- Upper Bound
- Amount

Example:

```text
Lower Bound: 1
Upper Bound: 100
Amount: 5
```

RandList will pick 5 unique random numbers from 1 to 100.

Useful for:

- Drawing random numbers.
- Picking winners from a numbered list.
- Creating random IDs or sample groups.

### Include

Use this mode when the result must include certain numbers.

Example:

```text
Lower Bound: 1
Upper Bound: 20
Amount: 5
Included Values: 3, 8
```

The result will always include 3 and 8. The remaining numbers will be randomly filled.

Useful for cases where some values must appear in the final result while the rest should still be random.

### Exclude

Use this mode when the result must avoid certain numbers.

Example:

```text
Lower Bound: 1
Upper Bound: 20
Amount: 5
Excluded Values: 4, 9, 13
```

The result will not contain 4, 9, or 13.

Useful for avoiding unavailable, already-used, or disallowed values.

### Specify

Use this mode to pick random items from a custom list.

Example:

```text
Specified Items: Alpha, Beta, Gamma, Delta
Amount: 2
```

RandList will randomly pick 2 items from the list.

Useful for:

- Picking names.
- Choosing tasks.
- Selecting teams, topics, meals, or options from a custom list.

## Input Format

Number lists support commas, Chinese commas, enumeration commas, spaces, and line breaks:

```text
1, 2, 3
1，2，3
1、2、3
1 2 3
```

Text lists support commas, Chinese commas, enumeration commas, and line breaks:

```text
Apple, Banana, Orange
苹果、香蕉、橙子
Red
Blue
Green
```

## Result Behavior

Each generated result is shown as a numbered line, such as:

```text
01: [3, 8, 12, 17, 19]
```

Result behavior:

- Each result contains no duplicate items.
- Numeric results are displayed in ascending order.
- Custom text results are displayed in sorted order.
- Multiple generations stay visible in the current mode.
- Tapping a result copies it when copy interaction is enabled.
- Invalid input is shown as an error message instead of being saved to history.

## Input Memory

RandList remembers what you typed in each mode. You can switch between Simple, Include, Exclude, Specify, and History without losing the current inputs.

The app also restores previous inputs and visible generated results when reopened.

## Validation

RandList checks the input before generating results.

For number range modes:

- Lower Bound must be smaller than Upper Bound.
- Lower Bound must be 0 or greater.
- Upper Bound must be greater than 0.
- Amount must be greater than 0.
- Amount cannot be larger than the available number of values.

For Include mode:

- Included values cannot contain duplicates.
- Included values must be inside the selected range.
- The number of included values cannot be greater than Amount.

For Exclude mode:

- Excluded values cannot contain duplicates.
- Excluded values must be inside the selected range.
- There must be enough remaining values to generate the requested Amount.

For Specify mode:

- The custom list cannot be empty.
- Custom items cannot contain duplicates.
- Amount cannot be greater than the number of custom items.

## History

Successful results are automatically saved in the History page. You can:

- Review previous results.
- Browse history by date.
- Copy history results.
- Tap a history summary to apply the previous input settings back to the matching mode.
- Delete unwanted history records.

History only stores successful generations. Error results are not saved.

## Settings

The Settings page lets you change:

- Appearance mode: Light, Dark, or System.
- Accent color.
- Default start mode.
- Result font and size.
- Tap result to copy.
- Tap history summary to apply template.
- Haptic feedback.
- Generation animation.
- History cleanup.

Settings are saved automatically and applied the next time you open RandList.

## Privacy

RandList performs random generation, input memory, result history, and settings storage locally. The project does not use third-party SDKs.

## Notice

This repository is only for presenting RandList and explaining how to use it. It does not grant open-source rights, redistribution rights, or commercial usage rights.

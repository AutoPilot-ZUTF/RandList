//
//  RandListTests.swift
//  RandListTests
//
//  Created by Xinyang He on 4/21/26.
//

import Foundation
import Testing
@testable import RandList

@MainActor
struct RandListTests {
    @Test func generateListReturnsDistinctSortedValuesInsideBounds() throws {
        let randList = RandList(lowerBound: 1, upperBound: 10, amount: 4)
        let generated = try randList.generateList()

        #expect(generated.count == 4)
        #expect(generated == generated.sorted())
        #expect(Set(generated).count == 4)
        #expect(generated.allSatisfy { (1...10).contains($0) })
    }

    @Test func commonErrorListMatchesInvalidInputRules() {
        let randList = RandList(lowerBound: -1, upperBound: 0, amount: 0)

        #expect(
            randList.commonErrorList() == [
                InvalidInputException.inputBoundError,
                InvalidInputException.lowerBoundRangeError,
                InvalidInputException.upperBoundRangeError,
                InvalidInputException.amountRangeError,
                InvalidInputException.amountError
            ]
        )
    }

    @Test func generateListThrowsJoinedValidationMessage() {
        let randList = RandList(lowerBound: 5, upperBound: 3, amount: 10)

        do {
            _ = try randList.generateList()
            Issue.record("Expected InvalidInputException to be thrown.")
        } catch let error as InvalidInputException {
            #expect(
                error.message == [
                    InvalidInputException.inputBoundError,
                    InvalidInputException.amountError
                ].joined(separator: "\n")
            )
        } catch {
            Issue.record("Unexpected error: \(error)")
        }
    }

    @Test func includeModeAlwaysContainsIncludedValues() throws {
        let generated = try RandListInclude(
            lowerBound: 1,
            upperBound: 10,
            amount: 4,
            includedValues: [2, 9]
        ).generateList()

        #expect(generated.count == 4)
        #expect(generated.contains(2))
        #expect(generated.contains(9))
        #expect(Set(generated).count == 4)
    }

    @Test func excludeModeNeverContainsExcludedValues() throws {
        let generated = try RandListExclude(
            lowerBound: 1,
            upperBound: 8,
            amount: 3,
            excludedValues: [2, 5, 7]
        ).generateList()

        #expect(generated.count == 3)
        #expect(generated.allSatisfy { ![2, 5, 7].contains($0) })
    }

    @Test func largeRangeGenerationStaysInsideBoundsWithoutMaterializingWholeRange() throws {
        let generated = try RandList(lowerBound: 1, upperBound: 15_555_555, amount: 5).generateList()

        #expect(generated.count == 5)
        #expect(generated == generated.sorted())
        #expect(Set(generated).count == 5)
        #expect(generated.allSatisfy { (1...15_555_555).contains($0) })
    }

    @Test func largeRangeExcludeModeSkipsExcludedValues() throws {
        let excludedValues = [2, 3, 5, 8, 13]
        let generated = try RandListExclude(
            lowerBound: 1,
            upperBound: 15_555_555,
            amount: 5,
            excludedValues: excludedValues
        ).generateList()

        #expect(generated.count == 5)
        #expect(generated == generated.sorted())
        #expect(generated.allSatisfy { !excludedValues.contains($0) })
    }

    @Test func specifiedModeReturnsDistinctRequestedAmount() throws {
        let generated = try RandListSpecified(
            specifiedValues: ["Gamma", "Alpha", "Beta"],
            amount: 2
        ).generateList()

        #expect(generated.count == 2)
        #expect(Set(generated).count == 2)
        #expect(generated == generated.sorted { $0.localizedStandardCompare($1) == .orderedAscending })
    }

    @Test func inputHandlerParsesChineseCommaSeparatedValues() {
        var memory = InputMemory()
        memory.include.lowerBound = "1"
        memory.include.upperBound = "10"
        memory.include.amount = "4"
        memory.include.includedElements = "2，5"

        let result = InputHandler().generate(mode: .include, from: memory, sequence: 1)

        #expect(result.isError == false)
        #expect(result.fullLine.contains("["))
        #expect(result.fullLine.contains("2"))
        #expect(result.fullLine.contains("5"))
    }

    @Test func integerListParsingAcceptsWhitespaceAndChineseSeparators() throws {
        let values = try ValidationService().parseIntegerList(
            "2 5\n8、10，13",
            fieldName: String(localized: "field_included_values")
        )

        #expect(values == [2, 5, 8, 10, 13])
    }

    @Test func specifiedValueParsingAcceptsChineseEnumerationComma() {
        let values = ValidationService().parseSpecifiedValues("苹果、香蕉，red apple")

        #expect(values == ["苹果", "香蕉", "red apple"])
    }

    @Test func excludeModeInputAcceptsWhitespaceSeparatedIntegers() {
        var memory = InputMemory()
        memory.exclude.lowerBound = "1"
        memory.exclude.upperBound = "10"
        memory.exclude.amount = "3"
        memory.exclude.excludedElements = "2 5 7"

        let result = InputHandler().generate(mode: .exclude, from: memory, sequence: 1)

        #expect(result.isError == false)
    }

    @Test func historyStoreSkipsErrorResults() {
        var historyStore = HistoryStore()
        let result = GenerateResult.error(message: "Amount must be greater than 0.")
        let inputMemory = InputMemory()

        historyStore.append(mode: .simple, result: result, inputMemory: inputMemory)
        historyStore.append(mode: .simple, result: result, inputMemory: inputMemory)

        #expect(historyStore.records.isEmpty)
    }

    @Test func specifiedHistorySummaryShowsSpecifiedItemsOnce() {
        var memory = InputMemory()
        memory.specify.amount = "2"
        memory.specify.specifiedElements = "Alpha, Beta, Gamma"

        let request = HistoryRequestSnapshot(mode: .specify, inputMemory: memory)
        let specifiedSummary = "\(String(localized: "field_specified_items")) [Alpha, Beta, Gamma]"
        let amountSummary = "\(String(localized: "field_amount")) 2"

        #expect(request.summaryLine == "\(specifiedSummary) • \(amountSummary)")
        #expect(request.summaryLine.components(separatedBy: specifiedSummary).count == 2)
    }

    @Test func historyRequestAppliesTemplateWithoutListSpaces() {
        var source = InputMemory()
        source.include.lowerBound = "1"
        source.include.upperBound = "10"
        source.include.amount = "3"
        source.include.includedElements = "2, 5 7、9"
        let request = HistoryRequestSnapshot(mode: .include, inputMemory: source)
        var target = InputMemory()

        request.applyTemplate(to: &target)

        #expect(target.include.lowerBound == "1")
        #expect(target.include.upperBound == "10")
        #expect(target.include.amount == "3")
        #expect(target.include.includedElements == "2,5,7,9")
    }

    @Test func specifiedHistoryRequestAppliesTemplateWithoutListSpaces() {
        var source = InputMemory()
        source.specify.amount = "2"
        source.specify.specifiedElements = "Alpha, Beta\nred apple"
        let request = HistoryRequestSnapshot(mode: .specify, inputMemory: source)
        var target = InputMemory()

        request.applyTemplate(to: &target)

        #expect(target.specify.amount == "2")
        #expect(target.specify.specifiedElements == "Alpha,Beta,red apple")
    }

    @Test func legacyHistoryRequestSummaryRelocalizesAfterDecode() throws {
        let legacyRecordJSON = """
        {
            "id": "00000000-0000-0000-0000-000000000001",
            "mode": "include",
            "result": {
                "id": "00000000-0000-0000-0000-000000000002",
                "prefix": "01",
                "content": "[2, 5, 7, 9]",
                "isError": false,
                "highlightedIntegers": [2, 5],
                "showsSeparatorBefore": false
            },
            "createdAt": 0,
            "request": {
                "boundSummary": "Bereich 1 - 10",
                "amountSummary": "Anzahl 4",
                "extraSummary": "Enthaltene Werte [2, 5]"
            }
        }
        """

        let record = try JSONDecoder().decode(HistoryRecord.self, from: Data(legacyRecordJSON.utf8))

        #expect(
            record.request?.summaryLine == [
                "\(String(localized: "history_boundary")) 1 - 10",
                "\(String(localized: "field_amount")) 4",
                "\(String(localized: "field_included_values")) [2, 5]"
            ].joined(separator: " • ")
        )
    }
}

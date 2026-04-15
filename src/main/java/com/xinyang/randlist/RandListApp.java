package com.xinyang.randlist;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class RandListApp extends Application {

    // ===== 布局与样式常量 =====
    private final double boxSpacing = 8;
    private final double uiFontSize = 14;
    private final double windowWidth = 420;
    private final double resultWidth = 362;
    private final double smallFieldWidth = 115;
    private final double specifiedListWidth = 238;
    private double resultPrefixWidth = 30;
    private final double maximumHeight = 720;
    private double windowDecorationHeight = -1;

    // ===== UI组件 =====
    private Button generateButton;
    private Button clearButton;
    private Label sceneLabel;
    private Label selectLabel;
    private String prefix;

    private ToggleGroup modeSelector;
    private RadioButton simpleButton;
    private RadioButton includeButton;
    private RadioButton excludeButton;
    private RadioButton specifyButton;

    private TextField lowerBound;
    private TextField upperBound;
    private TextField includeList;
    private TextField excludeList;
    private TextField specifyList;
    private TextField desiredAmount;
    private TextField specifyAmount;

    private final InputMemory inputMemory = new InputMemory();

    private VBox resultBox;
    private ScrollPane resultScrollPane;
    private Label resultsTitleLabel;
    private Label resultsTitleLabelSub;
    private Label contentLabel;
    private HBox resultsTitleLabelBox;
    private int resultCount;
    private Separator resultSeparator;
    private Label prefixLabel;
    private int errorCount;
    private String lastExceptionMessage;

    // ===== 应用入口 =====
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Font.loadFont(
                getClass().getResource("/fonts/SF-Mono-Regular.otf").toExternalForm(),
                14
        );
        Font.loadFont(
                getClass().getResource("/fonts/SF-Mono-Bold.otf").toExternalForm(),
                14
        );

        // 标题
        sceneLabel = new Label("Random List Generator");
        sceneLabel.setFont(Font.font("SF Mono", FontWeight.BOLD, 25));

        // 分隔线 1
        Separator line1 = new Separator();
        line1.setPadding(new Insets(0, boxSpacing, 0, boxSpacing));

        // 模式选择标题
        selectLabel = new Label("Select mode:");
        selectLabel.setFont(Font.font("SF Mono", FontWeight.BOLD, 15));

        // 模式选择按钮
        modeSelector = new ToggleGroup();
        simpleButton = new RadioButton("Simple");
        simpleButton.setFont(Font.font("SF Mono", uiFontSize));
        simpleButton.setToggleGroup(modeSelector);
        includeButton = new RadioButton("Include");
        includeButton.setFont(Font.font("SF Mono", uiFontSize));
        includeButton.setToggleGroup(modeSelector);
        excludeButton = new RadioButton("Exclude");
        excludeButton.setFont(Font.font("SF Mono", uiFontSize));
        excludeButton.setToggleGroup(modeSelector);
        specifyButton = new RadioButton("Specify");
        specifyButton.setFont(Font.font("SF Mono", uiFontSize));
        specifyButton.setToggleGroup(modeSelector);
        simpleButton.setSelected(true);

        // 模式选择按钮容器
        HBox modeBox = new HBox(boxSpacing);
        modeBox.setAlignment(Pos.CENTER);
        modeBox.getChildren().addAll(simpleButton, includeButton, excludeButton, specifyButton);

        // 分隔线 2
        Separator line2 = new Separator();
        line2.setPadding(new Insets(0, boxSpacing, 0, boxSpacing));

        // 动态输入区域：根据当前模式切换显示内容
        VBox dynamicInputBox = new VBox(boxSpacing);
        dynamicInputBox.setAlignment(Pos.TOP_CENTER);
        updateDynamicInputBox(dynamicInputBox);

        // 切换模式时：更新输入区域并清空结果
        modeSelector.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            saveCurrentModeInputs();
            updateDynamicInputBox(dynamicInputBox);
            clearButton.setText("Clear All");
            updateResultScrollState(primaryStage);
        });

        // 分隔线 3
        Separator line3 = new Separator();
        line3.setPadding(new Insets(0, boxSpacing, 0, boxSpacing));

        // 生成按钮
        generateButton = new Button("Generate");
        generateButton.setPadding(new Insets(boxSpacing, 0, boxSpacing, 0));
        generateButton.setPrefWidth(smallFieldWidth);
        generateButton.setFont(Font.font("SF Mono", FontWeight.BOLD, 15));
        generateButton.setOnAction(event -> {
            try {
                if (resultCount < 100) {
                    resultPrefixWidth = 30;
                } else {
                    resultPrefixWidth = 38;
                }
                prefix = String.format("%02d: ", resultCount);
                InputHandler inputHandler = new InputHandler();
                String mode = ((RadioButton) modeSelector.getSelectedToggle()).getText();
                String listText = "";
                if (includeButton.isSelected()) {
                    listText = includeList.getText();
                } else if (excludeButton.isSelected()) {
                    listText = excludeList.getText();
                } else if (specifyButton.isSelected()) {
                    listText = specifyList.getText();
                }
                String amountText = specifyButton.isSelected() ? specifyAmount.getText() : desiredAmount.getText();
                List<?> result = inputHandler.handleGenerate(
                        mode,
                        lowerBound.getText(),
                        upperBound.getText(),
                        amountText,
                        listText
                );
                appendResult(result.toString());
                clearButton.setText("Clear All");
                updateResultScrollState(primaryStage);
            } catch (RuntimeException exception) {
                String currentExceptionMessage = exception.getMessage();
                if (!java.util.Objects.equals(currentExceptionMessage, lastExceptionMessage)) {
                    errorCount++;
                    prefix = "Error:";
                    resultPrefixWidth = 53;
                    appendResult(currentExceptionMessage);
                    lastExceptionMessage = currentExceptionMessage;
                    updateResultScrollState(primaryStage);
                }
            }
        });

        clearButton = new Button("Clear All");
        clearButton.setPadding(new Insets(boxSpacing, 0, boxSpacing, 0));
        clearButton.setPrefWidth(smallFieldWidth);
        clearButton.setFont(Font.font("SF Mono", 14));
        clearButton.setOnAction(event -> {
            if (resultCount == 0 && errorCount == 0) {
                return;
            }
            if (clearButton.getText().equals("Clear All")) {
                clearButton.setText("Confirm");
            } else {
                clearResults();
                clearButton.setText("Clear All");
                updateResultScrollState(primaryStage);
            }
        });

        HBox buttonBox = new HBox(boxSpacing);
        buttonBox.setAlignment(Pos.TOP_CENTER);
        buttonBox.getChildren().addAll(generateButton, clearButton);

        // 结果区分隔线
        resultSeparator = new Separator();
        resultSeparator.setPadding(new Insets(0, -13, 0, -13));
        resultSeparator.setMinWidth(resultWidth);
        resultSeparator.setPrefWidth(resultWidth);
        resultSeparator.setMaxWidth(resultWidth);

        // 结果标题
        resultsTitleLabel = new Label("Results:");
        resultsTitleLabel.setFont(Font.font("SF Mono", FontWeight.BOLD, 15));
        resultsTitleLabel.setAlignment(Pos.TOP_LEFT);

        resultsTitleLabelSub = new Label("(Click to copy)");
        resultsTitleLabelSub.setFont(Font.font("SF Mono", 12));
        resultsTitleLabelSub.setPadding(new Insets(3, 0, 0, 120.3));
        resultsTitleLabelSub.setVisible(false);
        resultsTitleLabelSub.setManaged(false);

        resultsTitleLabelBox = new HBox();
        resultsTitleLabelBox.setAlignment(Pos.TOP_LEFT);
        resultsTitleLabelBox.setPadding(new Insets(-3, 0, -3, 13));
        resultsTitleLabelBox.getChildren().addAll(resultsTitleLabel, resultsTitleLabelSub);


        // 结果显示区域
        resultBox = new VBox(2);
        resultBox.setAlignment(Pos.TOP_CENTER);
        resultBox.setMaxWidth(resultWidth);
        resultBox.setPrefWidth(resultWidth);
        resultBox.setPadding(new Insets(0, 0, 0, 13));
        resultBox.setVisible(false);
        resultBox.setManaged(false);

        resultSeparator.setVisible(false);
        resultSeparator.setManaged(false);
        resultsTitleLabelBox.setVisible(false);
        resultsTitleLabelBox.setManaged(false);

        resultScrollPane = new ScrollPane(resultBox);
        resultScrollPane.setFitToWidth(true);
        resultScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        resultScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        resultScrollPane.setPannable(true);
        resultScrollPane.setVisible(false);
        resultScrollPane.setManaged(false);
        resultScrollPane.setPrefViewportHeight(0);

        // 修改 1：增加这一行，强制允许 ScrollPane 缩小到 0
        resultScrollPane.setMinHeight(0);

        // 修改 2：在 style 中加入 -fx-background-insets: 0; 去除自带的微小边框影响
        resultScrollPane.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        // 监听 resultBox 的高度变化，一旦高度增加，就自动滚动到最底部
        resultBox.heightProperty().addListener((observable, oldValue, newValue) -> {
            // 设置垂直滚动条的值为最大值 (通常默认 vmax 就是 1.0)
            resultScrollPane.setVvalue(resultScrollPane.getVmax());
        });

        // 主布局容器
        VBox mainBox = new VBox(boxSpacing);
        mainBox.setPadding(new Insets(boxSpacing));
        mainBox.setSpacing(boxSpacing);
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.getChildren().add(sceneLabel);
        mainBox.getChildren().add(line1);
        mainBox.getChildren().add(selectLabel);
        mainBox.getChildren().add(modeBox);
        mainBox.getChildren().add(line2);
        mainBox.getChildren().add(dynamicInputBox);
        mainBox.getChildren().add(line3);
        mainBox.getChildren().add(buttonBox);
        mainBox.getChildren().add(resultSeparator);
        mainBox.getChildren().add(resultsTitleLabelBox);
        mainBox.getChildren().add(resultScrollPane);

        // 创建场景并显示窗口
        Scene scene = new Scene(mainBox, windowWidth, Region.USE_COMPUTED_SIZE);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER && !generateButton.isDisabled()) {
                generateButton.fire();
            }
        });

        primaryStage.setTitle("RandList");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.sizeToScene();
        windowDecorationHeight = primaryStage.getHeight() - primaryStage.getScene().getHeight();
        primaryStage.centerOnScreen();
        primaryStage.setY(primaryStage.getY() - 150);
        //mainBox.requestFocus();
    }

    // 创建统一风格的输入框
    private TextField createInputField(String prompt, double width) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(width);
        field.setFont(Font.font("SF Mono", uiFontSize));
        field.setAlignment(Pos.CENTER);
        return field;
    }

    // 给输入框外面包一层：上方小Label + 下方TextField
    private VBox withLabel(String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setFont(Font.font("SF Mono", FontWeight.BOLD, 10));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);

        VBox box = new VBox(1);
        box.setAlignment(Pos.TOP_CENTER);
        box.getChildren().addAll(label, field);
        return box;
    }

    // 根据当前选中的模式切换输入区域
    private void updateDynamicInputBox(VBox dynamicInputBox) {
        dynamicInputBox.getChildren().clear();
        dynamicInputBox.setAlignment(Pos.TOP_CENTER);

        if (simpleButton.isSelected()) {
            lowerBound = createInputField("integer", smallFieldWidth);
            upperBound = createInputField("integer", smallFieldWidth);
            desiredAmount = createInputField("integer", smallFieldWidth);

            HBox simpleModeBox = new HBox(boxSpacing);
            simpleModeBox.setAlignment(Pos.CENTER);
            simpleModeBox.getChildren().addAll(
                    withLabel("Lower bound", lowerBound),
                    withLabel("Upper bound", upperBound),
                    withLabel("Amount", desiredAmount)
            );
            dynamicInputBox.getChildren().add(simpleModeBox);
        } else if (includeButton.isSelected() || excludeButton.isSelected()) {
            lowerBound = createInputField("integer", smallFieldWidth);
            upperBound = createInputField("integer", smallFieldWidth);
            desiredAmount = createInputField("integer", smallFieldWidth);

            HBox simpleRow = new HBox(boxSpacing);
            simpleRow.setAlignment(Pos.CENTER);
            simpleRow.getChildren().addAll(
                    withLabel("Lower bound", lowerBound),
                    withLabel("Upper bound", upperBound),
                    withLabel("Amount", desiredAmount)
            );

            TextField listField = createInputField("integer list: x,y,z...", resultWidth);
            VBox modeBox = new VBox(boxSpacing);
            modeBox.setAlignment(Pos.TOP_CENTER);
            modeBox.setFillWidth(false);

            if (includeButton.isSelected()) {
                includeList = listField;
                modeBox.getChildren().addAll(simpleRow, withLabel("Elements to include", includeList));
            } else {
                excludeList = listField;
                modeBox.getChildren().addAll(simpleRow, withLabel("Elements to exclude", excludeList));
            }

            dynamicInputBox.getChildren().add(modeBox);
        } else if (specifyButton.isSelected()) {
            specifyList = createInputField("string list: a,ß,text...", specifiedListWidth);
            specifyAmount = createInputField("integer", smallFieldWidth);

            HBox specifyModeBox = new HBox(boxSpacing);
            specifyModeBox.setAlignment(Pos.CENTER);
            specifyModeBox.getChildren().addAll(
                    withLabel("Specified list", specifyList),
                    withLabel("Amount", specifyAmount)
            );
            dynamicInputBox.getChildren().add(specifyModeBox);
        }
        restoreCurrentModeInputs();
    }

    private void saveCurrentModeInputs() {
        if (simpleButton.isSelected()) {
            inputMemory.setSimpleLowerBound(lowerBound != null ? lowerBound.getText() : "");
            inputMemory.setSimpleUpperBound(upperBound != null ? upperBound.getText() : "");
            inputMemory.setSimpleAmount(desiredAmount != null ? desiredAmount.getText() : "");
        } else if (includeButton.isSelected()) {
            inputMemory.setIncludeLowerBound(lowerBound != null ? lowerBound.getText() : "");
            inputMemory.setIncludeUpperBound(upperBound != null ? upperBound.getText() : "");
            inputMemory.setIncludeAmount(desiredAmount != null ? desiredAmount.getText() : "");
            inputMemory.setIncludeList(includeList != null ? includeList.getText() : "");
        } else if (excludeButton.isSelected()) {
            inputMemory.setExcludeLowerBound(lowerBound != null ? lowerBound.getText() : "");
            inputMemory.setExcludeUpperBound(upperBound != null ? upperBound.getText() : "");
            inputMemory.setExcludeAmount(desiredAmount != null ? desiredAmount.getText() : "");
            inputMemory.setExcludeList(excludeList != null ? excludeList.getText() : "");
        } else if (specifyButton.isSelected()) {
            inputMemory.setSpecifyList(specifyList != null ? specifyList.getText() : "");
            inputMemory.setSpecifyAmount(specifyAmount != null ? specifyAmount.getText() : "");
        }
    }

    private void restoreCurrentModeInputs() {
        if (simpleButton.isSelected()) {
            lowerBound.setText(inputMemory.getSimpleLowerBound());
            upperBound.setText(inputMemory.getSimpleUpperBound());
            desiredAmount.setText(inputMemory.getSimpleAmount());
        } else if (includeButton.isSelected()) {
            lowerBound.setText(inputMemory.getIncludeLowerBound());
            upperBound.setText(inputMemory.getIncludeUpperBound());
            desiredAmount.setText(inputMemory.getIncludeAmount());
            includeList.setText(inputMemory.getIncludeList());
        } else if (excludeButton.isSelected()) {
            lowerBound.setText(inputMemory.getExcludeLowerBound());
            upperBound.setText(inputMemory.getExcludeUpperBound());
            desiredAmount.setText(inputMemory.getExcludeAmount());
            excludeList.setText(inputMemory.getExcludeList());
        } else if (specifyButton.isSelected()) {
            specifyList.setText(inputMemory.getSpecifyList());
            specifyAmount.setText(inputMemory.getSpecifyAmount());
        }
    }

    private void updateResultScrollState(Stage primaryStage) {
        double fixedX = primaryStage.getX();
        double fixedY = primaryStage.getY();

        if (windowDecorationHeight < 0) {
            windowDecorationHeight = primaryStage.getHeight() - primaryStage.getScene().getHeight();
        }

        Region root = (Region) primaryStage.getScene().getRoot();

        resultScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        if (!resultScrollPane.isVisible()) {
            resultScrollPane.setPrefViewportHeight(0);

            root.applyCss();
            root.layout();

            double desiredSceneHeight = root.prefHeight(windowWidth);
            primaryStage.setHeight(Math.min(desiredSceneHeight + windowDecorationHeight, maximumHeight));
            primaryStage.setX(fixedX);
            primaryStage.setY(fixedY);
            return;
        }

        root.applyCss();
        root.layout();

        double contentHeight;
        contentHeight = resultBox.prefHeight(resultWidth);
        resultScrollPane.setPrefViewportHeight(contentHeight);


        root.applyCss();
        root.layout();

        double desiredSceneHeight = root.prefHeight(windowWidth);
        double allowedSceneHeight = maximumHeight - windowDecorationHeight;

        if (desiredSceneHeight > allowedSceneHeight) {
            double overflow = desiredSceneHeight - allowedSceneHeight;
            double targetViewportHeight = Math.max(80, contentHeight - overflow);

            resultScrollPane.setPrefViewportHeight(targetViewportHeight);
            resultScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            root.applyCss();
            root.layout();
            primaryStage.setHeight(maximumHeight);
        } else {
            primaryStage.setHeight(desiredSceneHeight + windowDecorationHeight);
        }


        primaryStage.setX(fixedX);
        primaryStage.setY(fixedY);

    }

    // 添加一条结果：左侧编号固定宽度，右侧内容自动换行
    private void appendResult(String resultText) {
        Label currentPrefixLabel;
        Label currentContentLabel;
        String currentPrefix = prefix;

        if (!currentPrefix.equals("Error:")) {
            resultCount++;
            currentPrefix = String.format("%02d: ", resultCount);

            currentPrefixLabel = new Label(currentPrefix);
            currentPrefixLabel.setFont(Font.font("SF Mono", 12));
            currentPrefixLabel.setMinWidth(resultPrefixWidth);
            currentPrefixLabel.setPrefWidth(resultPrefixWidth);
            currentPrefixLabel.setMaxWidth(resultPrefixWidth);
            currentPrefixLabel.setAlignment(Pos.TOP_LEFT);

            String copiedText = currentPrefix + resultText;
            currentPrefixLabel.setOnMouseClicked(event -> {
                javafx.scene.input.ClipboardContent clipboardContent = new javafx.scene.input.ClipboardContent();
                clipboardContent.putString(copiedText);
                javafx.scene.input.Clipboard.getSystemClipboard().setContent(clipboardContent);
            });
            currentPrefixLabel.setOnMouseEntered(event -> {
                currentPrefixLabel.setFont(Font.font("SF Mono", FontWeight.BOLD, 12));
                resultsTitleLabelSub.setVisible(true);
                resultsTitleLabelSub.setManaged(true);
            });
            currentPrefixLabel.setOnMouseExited(event -> {
                currentPrefixLabel.setFont(Font.font("SF Mono", 12));
                resultsTitleLabelSub.setVisible(false);
                resultsTitleLabelSub.setManaged(false);
            });
            currentPrefixLabel.setStyle("-fx-cursor: hand;");

            currentContentLabel = new Label(resultText);
            currentContentLabel.setWrapText(true);
            currentContentLabel.setFont(Font.font("SF Mono", 12));
            currentContentLabel.setMinWidth(resultWidth - resultPrefixWidth);
            currentContentLabel.setPrefWidth(resultWidth - resultPrefixWidth);
            currentContentLabel.setMaxWidth(resultWidth - resultPrefixWidth);
            currentContentLabel.setAlignment(Pos.TOP_LEFT);
        } else {
            currentPrefixLabel = new Label(currentPrefix);
            currentPrefixLabel.setFont(Font.font("SF Mono", 12));
            currentPrefixLabel.setMinWidth(resultPrefixWidth);
            currentPrefixLabel.setPrefWidth(resultPrefixWidth);
            currentPrefixLabel.setMaxWidth(resultPrefixWidth);
            currentPrefixLabel.setAlignment(Pos.TOP_LEFT);

            String copiedText = currentPrefix + " " + resultText;
            currentPrefixLabel.setOnMouseClicked(event -> {
                javafx.scene.input.ClipboardContent clipboardContent = new javafx.scene.input.ClipboardContent();
                clipboardContent.putString(copiedText);
                javafx.scene.input.Clipboard.getSystemClipboard().setContent(clipboardContent);
            });
            currentPrefixLabel.setOnMouseEntered(event -> {
                currentPrefixLabel.setFont(Font.font("SF Mono", FontWeight.BOLD, 12));
                resultsTitleLabelSub.setVisible(true);
                resultsTitleLabelSub.setManaged(true);
            });
            currentPrefixLabel.setOnMouseExited(event -> {
                currentPrefixLabel.setFont(Font.font("SF Mono", 12));
                resultsTitleLabelSub.setVisible(false);
                resultsTitleLabelSub.setManaged(false);
            });
            currentPrefixLabel.setStyle("-fx-cursor: hand;");

            currentContentLabel = new Label(resultText);
            currentContentLabel.setWrapText(true);
            currentContentLabel.setFont(Font.font("SF Mono", 12));
            currentContentLabel.setStyle("-fx-text-fill: #b63f3a;");
            currentContentLabel.setMinWidth(resultWidth - resultPrefixWidth);
            currentContentLabel.setPrefWidth(resultWidth - resultPrefixWidth);
            currentContentLabel.setMaxWidth(resultWidth - resultPrefixWidth);
            currentContentLabel.setAlignment(Pos.TOP_LEFT);
        }

        HBox row = new HBox(0);
        row.setAlignment(Pos.TOP_LEFT);
        row.setMinWidth(resultWidth);
        row.setPrefWidth(resultWidth);
        row.setMaxWidth(resultWidth);
        row.getChildren().addAll(currentPrefixLabel, currentContentLabel);

        resultSeparator.setVisible(true);
        resultSeparator.setManaged(true);
        resultsTitleLabelBox.setVisible(true);
        resultsTitleLabelBox.setManaged(true);
        resultBox.setVisible(true);
        resultBox.setManaged(true);
        resultScrollPane.setVisible(true);
        resultScrollPane.setManaged(true);
        resultBox.getChildren().add(row);
    }

    // 清空所有结果并隐藏结果区域
    private void clearResults() {
        resultCount = 0;
        errorCount = 0;
        lastExceptionMessage = null;
        resultBox.getChildren().clear();
        resultSeparator.setVisible(false);
        resultSeparator.setManaged(false);
        resultsTitleLabelBox.setVisible(false);
        resultsTitleLabelBox.setManaged(false);
        resultBox.setVisible(false);
        resultBox.setManaged(false);
        resultScrollPane.setVisible(false);
        resultScrollPane.setManaged(false);
        resultScrollPane.setVvalue(0);
        resultsTitleLabelSub.setVisible(false);
        resultsTitleLabelSub.setManaged(false);
        clearButton.setText("Clear All");
    }
}

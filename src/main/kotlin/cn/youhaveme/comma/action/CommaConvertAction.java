package cn.youhaveme.comma.action;

import cn.youhaveme.comma.settings.CommaSettingsState;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查询值转 IN_SQL 插件
 * @author youhaveme.cn
 */
public class CommaConvertAction extends AnAction {
    Logger log = LoggerFactory.getLogger(CommaConvertAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            throw new IllegalStateException("Required data is not available");
        }
        SelectionModel selectionModel = editor.getSelectionModel();
        String clipboardContent = this.getClipboardContent();
        // 没有选中的文本并且剪切板没有内容时不做任何处理
        if (StringUtils.isBlank(selectionModel.getSelectedText()) && StringUtils.isBlank(clipboardContent)) {
            return;
        }
        String pendingText = StringUtils.isBlank(selectionModel.getSelectedText()) ? clipboardContent : selectionModel.getSelectedText();
        // 如已转换过，则不再转换，弹框提示
        if (pendingText.contains("('") || pendingText.contains("')")) {
            showNotification("", "重复转换", NotificationType.WARNING, true);
            return;
        }
        // 正则表达式匹配每行的代码并添加单引号和逗号
        Pattern pattern = Pattern.compile("(.+)");
        Matcher matcher = pattern.matcher(pendingText);
        StringBuilder selectedText = new StringBuilder(1000);
        int lineNum = 0;
        // 这一坨后续优化一下，现在不想优化
        if (isSymbolCheckBoxSelected()) {
            while (matcher.find()) {
                ++lineNum;
                selectedText.append(" '").append(StringUtils.trim(matcher.group(1))).append("',").append("\n");
            }
        } else {
            while (matcher.find()) {
                ++lineNum;
                selectedText.append(" ").append(StringUtils.trim(matcher.group(1))).append(",").append("\n");
            }
        }
        // 处理文本缩进格式，并加上括号封装 IN语句
        int limitMergeLines = this.getSettingLimitMergeLines();
        String modifiedText = handleSelectedText(lineNum > limitMergeLines, selectedText.toString());
        if (StringUtils.isNotBlank(selectionModel.getSelectedText())) {
            WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), modifiedText));
        } else {
            WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> editor.getDocument().insertString(editor.getCaretModel().getOffset(), modifiedText));
        }
    }

    /**
     * 弹框通知
     * @param title 标题
     * @param content 弹框内容
     * @param notificationType 弹框类型
     * @param isAutoClose 是否自动关闭
     */
    private void showNotification(String title, String content, NotificationType notificationType, boolean isAutoClose) {
        Notification notification = new Notification("Notice", title, content, notificationType);
        notification.notify(null);
        if (isAutoClose) {
            Executors.newSingleThreadScheduledExecutor().schedule(notification::expire, 3, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取系统剪切板内容
     * @return 剪切板内容
     */
    private String getClipboardContent() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * 获取配置中的最大允许合并行数
     * @return 行数: int
     */
    private int getSettingLimitMergeLines() {
        CommaSettingsState.State settings = Objects.requireNonNull(CommaSettingsState.getInstance().getState());
        return Integer.parseInt(settings.limitMergeLines);
    }

    /**
     * 获取配置中的是否添加符号
     * @return T or F
     */
    private boolean isSymbolCheckBoxSelected() {
        CommaSettingsState.State settings = Objects.requireNonNull(CommaSettingsState.getInstance().getState());
        return settings.symbolCheckBoxSelected;
    }

    /**
     * 处理结果文本
     * @param isOriginalStyle 是否输出原始样式
     * @param selectedTextString 结果文本
     * @return result
     */
    private String handleSelectedText(boolean isOriginalStyle, String selectedTextString) {
        String result = "(";
        if (isOriginalStyle) {
            result += StringUtils.removeStart(StringUtils.removeEnd(selectedTextString, ",\n"), " ");
        } else {
            result += StringUtils.removeStart(StringUtils.removeEnd(selectedTextString, ",\n"), " ").replaceAll("\n", "");
            // v1.3.2-beta: 部分用户反馈说这个功能有点鸡肋，基本上用不到，暂且移除，后续优化
            // if (CommaSettingsState.getInstance().trimWhiteSpaceStatus) {
            //     result = result.replaceAll(" ", "");
            // }
        }

        result += ");";
        return result;
    }

}

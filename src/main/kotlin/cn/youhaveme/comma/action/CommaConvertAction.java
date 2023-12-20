package cn.youhaveme.comma.action;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.youhaveme.comma.settings.CommaSettingsState;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查询值转 IN_SQL 插件
 * @author youhaveme.cn
 */
public class CommaConvertAction extends AnAction {
    Log log = LogFactory.get();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        SelectionModel selectionModel = editor.getSelectionModel();
        String clipboardContent = this.getClipboardContent();
        // 没有选中的文本并且剪切板没有内容时不做任何处理
        if (StrUtil.isBlank(selectionModel.getSelectedText()) && StrUtil.isBlank(clipboardContent)) {
            return;
        }
        String pendingText = StrUtil.isBlank(selectionModel.getSelectedText()) ? clipboardContent : selectionModel.getSelectedText();
        // 如已转换过，则不再转换，弹框提示
        if (pendingText.contains("('") || pendingText.contains("')")) {
            showNotification("Warning", "Duplicate conversion", NotificationType.WARNING, true);
            return;
        }
        // 正则表达式匹配每行的代码并添加单引号和逗号
        Pattern pattern = Pattern.compile("(.+)");
        Matcher matcher = pattern.matcher(pendingText);
        StringBuilder selectedText = new StringBuilder(1000);
        int lineNum = 0;
        while (matcher.find()) {
            ++lineNum;
            selectedText.append(" '").append(StrUtil.trim(matcher.group(1))).append("',").append("\n");
        }
        // 处理文本缩进格式，并加上括号封装 IN语句
        int limitMergeLines = this.getSettingLimitMergeLines();
        String modifiedText = handleSelectedText(lineNum > limitMergeLines, selectedText.toString());
        if (StrUtil.isNotBlank(selectionModel.getSelectedText())) {
            WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
                editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), modifiedText);
            });
        } else {
            WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
                editor.getDocument().insertString(editor.getCaretModel().getOffset(), modifiedText);
            });
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
        if (!isAutoClose) {
            return;
        }
        // 延迟关闭通知
        new Thread(() -> {
            try {
                // 等待3秒
                Thread.sleep(3000);
                // 关闭通知
                notification.expire();
            } catch (InterruptedException e) {
                log.error(e);
            }
        }).start();
    }

    /**
     * 获取系统剪切板内容
     * @return 剪切板内容
     */
    private String getClipboardContent() {
        String clipboardContent = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            clipboardContent = (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            log.error("获取剪切板内容异常", e);
        }
        return clipboardContent;
    }

    /**
     * 获取配置中的最大允许合并行数
     * @return 行数: int
     */
    private int getSettingLimitMergeLines() {
        CommaSettingsState settings = CommaSettingsState.getInstance();
        return Integer.parseInt(settings.limitMergeLines);
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
            result += StrUtil.removePrefix(StrUtil.removeSuffix(selectedTextString, ",\n"), " ");
        } else {
            result += StrUtil.removePrefix(StrUtil.removeSuffix(selectedTextString, ",\n"), " ").replaceAll("\n", "");
            // v1.3.2-beta: 部分用户反馈说这个功能有点鸡肋，基本上用不到，暂且移除，后续优化
            // if (CommaSettingsState.getInstance().trimWhiteSpaceStatus) {
            //     result = result.replaceAll(" ", "");
            // }
        }

        result += ");";
        return result;
    }

}

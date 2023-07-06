package cn.youhaveme.comma;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查询值转 IN_SQL 插件
 * @author youhaveme.cn
 */
public class CommaInAction extends AnAction {
    Log log = LogFactory.get();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        SelectionModel selectionModel = editor.getSelectionModel();
        // 没有选中的文本不做任何处理
        if (StrUtil.isBlank(selectionModel.getSelectedText())) {
            return;
        }
        // 如已转换过，则不再转换，弹框提示
        if (selectionModel.getSelectedText().contains("('") || selectionModel.getSelectedText().contains("')")) {
            showNotification("Title", "重复转换", NotificationType.INFORMATION);
            return;
        }
        // 正则表达式匹配每行的代码并添加单引号和逗号
        Pattern pattern = Pattern.compile("(.+)");
        Matcher matcher = pattern.matcher(selectionModel.getSelectedText());
        StringBuilder selectedText = new StringBuilder(1000);
        while (matcher.find()) {
            selectedText.append(" '").append(StrUtil.trim(matcher.group(1))).append("',").append("\n");
        }
        // 处理文本缩进格式，并加上括号封装 IN语句
        String modifiedText = "(" + StrUtil.removePrefix(StrUtil.removeSuffix(selectedText.toString(), ",\n"), " ") + ");";
        // 替换选中的文本为处理过的IN_SQL语句
        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
            editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), modifiedText);
        });
    }

    /**
     * 弹框通知
     * @param title 标题
     * @param content 弹框内容
     * @param notificationType 弹框类型
     */
    private void showNotification(String title, String content, NotificationType notificationType) {
        Notification notification = new Notification("Notice", title, content, notificationType);
        notification.notify(null);
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
}

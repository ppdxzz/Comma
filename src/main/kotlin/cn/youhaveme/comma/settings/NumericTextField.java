package cn.youhaveme.comma.settings;

import com.intellij.ui.components.JBTextField;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumericTextField extends JBTextField {

    public NumericTextField() {
        super();
        setDocument(new NumericDocument());
    }

    private class NumericDocument extends PlainDocument {
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }
            // 只允许输入数字
            if (str.matches("\\d*")) {
                super.insertString(offs, str, a);
            }
        }
    }

}

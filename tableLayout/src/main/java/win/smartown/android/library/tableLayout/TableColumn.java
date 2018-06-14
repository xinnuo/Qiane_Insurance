package win.smartown.android.library.tableLayout;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TableColumn extends LinearLayout {

    private String[] content;
    private Callback callback;
    private float maxTextViewWidth;

    public TableColumn(Context context, String[] content, Callback callback) {
        super(context);
        this.content = content;
        this.callback = callback;
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("TableColumn", "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (callback.getTableLayout().getTableMode() == 1) {
            setMeasuredDimension(
                    callback.getTableLayout().getTableRowWidth() / callback.getTableLayout().getTableColumnCount(),
                    callback.getTableLayout().getTableRowHeight() * getChildCount());
        } else {
            setMeasuredDimension(
                    (int) (callback.getTableLayout().getTableColumnPadding() * 2 + maxTextViewWidth),
                    callback.getTableLayout().getTableRowHeight() * getChildCount());
        }

        if (maxTextViewWidth == 0) initContent();
    }

    private void init() {
        Log.i("TableColumn", "init");
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
    }

    private void initContent() {
        int padding = callback.getTableLayout().getTableColumnPadding();
        maxTextViewWidth = 0;
        ArrayList<TextView> textViews = new ArrayList<>();
        int tabMode = callback.getTableLayout().getTableMode();
        int childWidth = callback.getTableLayout().getTableRowWidth() / callback.getTableLayout().getTableColumnCount();

        for (String text : content) {
            if (text == null) text = "";
            TextView textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, callback.getTableLayout().getTableTextSize());
            textView.setTextColor(callback.getTableLayout().getTableTextColor());

            int sytleId = callback.getTableLayout().getTableTextStyle();
            if (sytleId != -1)
                textView.setTextAppearance(getContext(), callback.getTableLayout().getTableTextStyle());

            maxTextViewWidth = Math.max(maxTextViewWidth, Util.measureTextViewWidth(textView, text));
            textView.setGravity(getTextGravity(callback.getTableLayout().getTableTextGravity()));
            textView.setPadding(padding, 0, padding, 0);
            textView.setText(text);
            textViews.add(textView);
        }
        LayoutParams layoutParams = new LayoutParams(
                tabMode == 0 ? (int) (padding * 2 + maxTextViewWidth) : childWidth,
                callback.getTableLayout().getTableRowHeight());
        for (TextView textView : textViews) {
            addView(textView, layoutParams);
        }
    }

    private int getTextGravity(int tableTextGravity) {
        switch (tableTextGravity) {
            case 0:
                return Gravity.CENTER;
            case 1:
                return Gravity.CENTER_VERTICAL;
            case 2:
                return Gravity.CENTER_VERTICAL | Gravity.END;
        }
        return Gravity.CENTER;
    }

    public void onClick(float y) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView textView = (TextView) getChildAt(i);
            if (textView.getBottom() >= y) {
                if (i == 0) {
                    return;
                }
                textView.setSelected(!textView.isSelected());
                textView.setBackgroundColor(textView.isSelected() ? callback.getTableLayout().getBackgroundColorSelected() : Color.TRANSPARENT);
                textView.setTextColor(textView.isSelected() ? callback.getTableLayout().getTableTextColorSelected() : callback.getTableLayout().getTableTextColor());
                return;
            }
        }
    }

    public interface Callback {
        TableLayout getTableLayout();
    }

}

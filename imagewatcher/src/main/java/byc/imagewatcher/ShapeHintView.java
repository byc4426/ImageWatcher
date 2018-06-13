package byc.imagewatcher;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ShapeHintView extends LinearLayout {

    private ImageView[] mDots;
    private int length = 0;
    private int lastPosition = 0;
    private int focusColor = Color.parseColor("#ffffffff");
    private int normalColor = Color.parseColor("#88ffffff");
    private Drawable dotNormal;
    private Drawable dotFocus;

    public ShapeHintView(Context context) {
        super(context);
    }

    public ShapeHintView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public ShapeHintView(Context context, int length, int focusColor, int normalColor) {
        super(context, null);
        this.focusColor = focusColor;
        this.normalColor = normalColor;
        initView(length);
    }

    public void initView(int length) {
        removeAllViews();
        lastPosition = 0;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        this.length = length;
        mDots = new ImageView[length];

        dotFocus = makeFocusDrawable();
        dotNormal = makeNormalDrawable();

        for (int i = 0; i < length; i++) {
            mDots[i] = new ImageView(getContext());
            LayoutParams dotLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            dotLp.setMargins(10, 0, 10, 0);
            mDots[i].setLayoutParams(dotLp);
            mDots[i].setBackgroundDrawable(dotNormal);
            addView(mDots[i]);
        }
        setCurrent(0);
    }

    public void setCurrent(int current) {
        if (current < 0 || current > length - 1) {
            return;
        }
        mDots[lastPosition].setBackgroundDrawable(dotNormal);
        mDots[current].setBackgroundDrawable(dotFocus);
        lastPosition = current;
    }


    public Drawable makeFocusDrawable() {
        GradientDrawable dotFocus = new GradientDrawable();
        dotFocus.setColor(focusColor);
        dotFocus.setCornerRadius(SizeUtil.dip2px(getContext(), 4));
        dotFocus.setSize(SizeUtil.dip2px(getContext(), 8), SizeUtil.dip2px(getContext(), 8));
        return dotFocus;
    }

    public Drawable makeNormalDrawable() {
        GradientDrawable dotNormal = new GradientDrawable();
        dotNormal.setColor(normalColor);
        dotNormal.setCornerRadius(SizeUtil.dip2px(getContext(), 4));
        dotNormal.setSize(SizeUtil.dip2px(getContext(), 8), SizeUtil.dip2px(getContext(), 8));
        return dotNormal;
    }
}
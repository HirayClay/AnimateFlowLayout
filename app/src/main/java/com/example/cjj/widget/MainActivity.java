package com.example.cjj.widget;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cjj.widget.fragment.CAlertDialog;
import com.example.cjj.widget.layout.FlowLayout;
import com.example.cjj.widget.layout.TagAdapter;
import com.example.cjj.widget.layout.TagFlowLayout;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    @Bind(R.id.flow)
    TagFlowLayout flow;
    @Bind(R.id.canvas)
    ImageView canvas;
    @Bind(R.id.canvas2)
    ImageView canvas2;
    private boolean show;
    private String key = "show";
    private int count;
    private ImageView img;
    private WindowManager windowManager;
    /*    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ButterKnife.bind(this);
        if (savedInstanceState == null)
            init();

    }


    private void init() {
//        CAlertDialog dialog = new CAlertDialog();
//        dialog.show(getFragmentManager(), null);
//        show = true;
        List<String> datas = Arrays.asList("杭州", "芸堇", "歪歪", "媛媛", "离职潮", "十宗罪", "张公馆餐厅", "可口可乐", "牙签", "千岛湖");
        flow.setAdapter(new TagAdapter<String>(datas) {
            @Override
            public View getView(FlowLayout parent, int position, String o) {
                TextView view = (TextView) getLayoutInflater().inflate(R.layout.item_flow, null);
                view.setText(o);
                return view;
            }
        });
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.i(TAG, "onSaveInstanceState: ");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState: ");
    }

    public void click(View v) {

//        ObjectAnimator animator = ObjectAnimator.ofInt(canvas,"mLeft",canvas.getLeft(),canvas.getLeft()+300).setDuration(1000);
//        animator.start();
        canvas.setLeft(canvas.getLeft()+100);
        /*count++;
        View t = flow.getChildAt(0);
        Bitmap drawingCache = t.getDrawingCache();
        if (drawingCache != null) {
            if (count == 1) {
                canvas.setImageBitmap(drawingCache);
            }
            else {
                canvas2.setImageBitmap(drawingCache);
            }
        }*/
    }


//        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
//        int result = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = getResources().getDimensionPixelSize(resourceId);
//        }
//        Toast.makeText(getApplication(), ""+contentTop+"---"+result, Toast.LENGTH_SHORT).show();
//        if (img == null) {
//            windowManager = getWindowManager();
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//            lp.flags = 1280;
//            lp.type = WindowManager.LayoutParams.TYPE_TOAST;
//            lp.format = PixelFormat.TRANSLUCENT;
//            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            lp.gravity = Gravity.LEFT | Gravity.TOP;
//            lp.x = 0;
//            lp.y = -30;
//            img = new ImageView(this);
//            img.setScaleType(ImageView.ScaleType.FIT_XY);
//            img.setImageBitmap(flow.getChildAt(0).getDrawingCache());
//            windowManager.addView(img, lp);
//
//            img.setOnTouchListener(this);
//        }
    class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }
}

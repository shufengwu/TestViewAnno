package com.sfw.anno.testviewanno;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Shufeng.Wu on 2016/11/24.
 */

public class Anno {
    public static void inject(Activity activity){
        Field [] fields;
        try {
            fields = activity.getClass().getDeclaredFields();

            for(Field field:fields){
                field.setAccessible(true);
                if(field.isAnnotationPresent(InjectView.class)){

                    InjectView injectView = field.getAnnotation(InjectView.class);
                    //利用反射调用findViewById方法
                    Method mActivity_findViewById = activity.getClass().getMethod("findViewById",int.class);
                    View view = (View) mActivity_findViewById.invoke(activity,injectView.value());
                    //View view = activity.findViewById(injectView.value());
                    field.set(activity,view);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public static void onClickInject(final Activity activity){
        Method [] methods;
        methods = activity.getClass().getDeclaredMethods();
        for (final Method method:methods){
            method.setAccessible(true);
            if(method.isAnnotationPresent(OnClick.class)){
                OnClick click = method.getAnnotation(OnClick.class);
                int [] values = click.value();
                for (int value:values){
                    Method mActivity_findViewById = null;
                    Method mView_setOnClickListener = null;
                    try {
                        mActivity_findViewById = activity.getClass().getMethod("findViewById",int.class);
                        final View view = (View) mActivity_findViewById.invoke(activity,value);
                        mView_setOnClickListener = View.class.getMethod("setOnClickListener", View.OnClickListener.class);
                        mView_setOnClickListener.invoke(view, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    method.invoke(activity,v);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void onClickProxyInject(Activity activity){
        
    }
}




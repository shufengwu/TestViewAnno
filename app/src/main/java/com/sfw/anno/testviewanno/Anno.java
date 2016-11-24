package com.sfw.anno.testviewanno;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shufeng.Wu on 2016/11/24.
 */

public class Anno {
    public static void inject(Activity activity) {
        Field[] fields;
        try {
            //获取activity的所有变量
            fields = activity.getClass().getDeclaredFields();
            //遍历所有变量
            for (Field field : fields) {
                field.setAccessible(true);
                //如果变量被@InjectView注解
                if (field.isAnnotationPresent(InjectView.class)) {
                    //获取InjectView对象
                    InjectView injectView = field.getAnnotation(InjectView.class);
                    //利用反射获得findViewById方法
                    Method mActivity_findViewById = activity.getClass().getMethod("findViewById", int.class);
                    //利用注解参数生成view对象
                    View view = (View) mActivity_findViewById.invoke(activity, injectView.value());
                    //不利用反射
                    //View view = activity.findViewById(injectView.value());

                    field.set(activity, view);
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

    /*
    未使用动态代理
     */
    public static void onClickInject(final Activity activity) {
        //获取所有方法
        Method[] methods= activity.getClass().getDeclaredMethods();
        //遍历所有方法
        for (final Method method : methods) {
            method.setAccessible(true);
            //如果方法被@OnClick注解
            if (method.isAnnotationPresent(OnClick.class)) {
                //获得OnClick的对象
                OnClick click = method.getAnnotation(OnClick.class);
                //获得注解参数
                int[] values = click.value();
                for (int value : values) {
                    Method mActivity_findViewById = null;
                    Method mView_setOnClickListener = null;
                    try {
                        //利用反射获得findViewById方法
                        mActivity_findViewById = activity.getClass().getMethod("findViewById", int.class);
                        //获取view
                        final View view = (View) mActivity_findViewById.invoke(activity, value);
                        //利用反射获得setOnClickListener方法
                        mView_setOnClickListener = View.class.getMethod("setOnClickListener", View.OnClickListener.class);
                        //利用反射调用setOnClickListener方法
                        mView_setOnClickListener.invoke(view, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    //反射调用method方法
                                    method.invoke(activity, v);
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

    /*
    使用动态代理
     */

    public static void onClickProxyInject(Activity activity) {
        //获取所有方法
        Method[] methods = activity.getClass().getDeclaredMethods();
        //遍历所有方法
        for (final Method method : methods) {
            method.setAccessible(true);
            //如果方法被@OnClick注解
            if (method.isAnnotationPresent(OnClick.class)) {
                //获得OnClick的对象
                OnClick click = method.getAnnotation(OnClick.class);
                //获得注解参数
                int[] values = click.value();
                for (int value : values) {
                    Method mActivity_findViewById = null;
                    Method mView_setOnClickListener = null;
                    try {
                        //利用反射获得findViewById方法
                        mActivity_findViewById = activity.getClass().getMethod("findViewById", int.class);
                        View view = (View) mActivity_findViewById.invoke(activity, value);
                        //利用反射获得setOnClickListener方法
                        mView_setOnClickListener = View.class.getMethod("setOnClickListener", View.OnClickListener.class);
                        mView_setOnClickListener.setAccessible(true);
                        //
                        InjectInvocationHandler injectInvocationHandler = new InjectInvocationHandler(activity);
                        injectInvocationHandler.add("onClick", method);
                        //View.OnClickListener代理类
                        Object o = Proxy.newProxyInstance(View.OnClickListener.class.getClassLoader(), new Class[]{View.OnClickListener.class}, injectInvocationHandler);
                        //反射执行setOnClickListener方法
                        mView_setOnClickListener.invoke(view, o);
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

    public static class InjectInvocationHandler implements InvocationHandler {

        private Object object;
        private Map<String, Method> map = new HashMap<>();

        public InjectInvocationHandler(Object object) {
            this.object = object;
        }

        //当代理类调用真实类的方法时调用
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //获得代理类执行的方法名
            String name = method.getName();
            //获得被注解的方法
            Method m = map.get(name);
            if (m != null)
                //执行此方法
                m.invoke(object, args);
            return null;
        }

        public void add(String name, Method method) {
            map.put(name, method);
        }

    }
}




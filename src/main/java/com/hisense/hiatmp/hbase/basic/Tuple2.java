package com.hisense.hiatmp.hbase.basic;

/**
 * 封装一个区别与Map的容器，
 *
 * 元素只有一个的情况，
 * 不需要遍历，
 * 直接可以取到其值
 * @author zhangyong
 * @date 2021/1/8  9:29
 */
public class Tuple2<T, M> {
    public T f0;
    public M f1;

    public Tuple2(T f0, M f1) {
        this.f0 = f0;
        this.f1 = f1;
    }

    public T getF0() {
        return f0;
    }

    public void setF0(T f0) {
        this.f0 = f0;
    }

    public M getF1() {
        return f1;
    }

    public void setF1(M f1) {
        this.f1 = f1;
    }

    @Override
    public String toString() {
        return "Tuple2{" +
                "f0=" + f0 +
                ", f1=" + f1 +
                '}';
    }
}

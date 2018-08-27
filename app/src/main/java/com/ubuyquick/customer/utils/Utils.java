package com.ubuyquick.customer.utils;

public class Utils {

    public interface OnItemClick {
        void onClick(int count);
    }

    public interface OnChange {
        void onChange(double mrp);
    }
}

package com.example.testproject;

public class reception {
    private int status;

    private content content;
    private static class content {
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;
    }

    @Override
    public String toString() {
        return "reception{" +
                "status=" + status +
                ", content=" + content +
                '}';
    }

    //定义 输出返回数据 的方法
    public void show() {
        System.out.println(status);

        System.out.println(content.from);
        System.out.println(content.to);
        System.out.println(content.vendor);
        System.out.println(content.out);
        System.out.println(content.errNo);
    }
}



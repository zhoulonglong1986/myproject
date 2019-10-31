package com.example.toroapi;

public abstract class MyThread extends Thread {

    protected boolean suspend = false;//ÊÇ·ñÔÝÍ£

    protected String control = "";


    public void setSuspend(boolean suspend) {
        if (!suspend) {
            synchronized (control) {
                control.notifyAll();
            }
        }
        this.suspend = suspend;
    }

    public boolean isSuspend() {
        return this.suspend;
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            synchronized (control) {
                if (suspend) {
                    try {
                        control.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.runPersonelLogic();
        }
    }

    /**
     * Âß¼­´úÂëÌå
     */
    protected abstract void runPersonelLogic();


}

package com.example.toroapi;

public abstract class SchThread extends MyThread {


    public boolean play=true;

    @Override
    public void run() {

           while(true){
               synchronized (control) {
                   if (suspend) {//ÊÇ·ñÔÝÍ£
                       try {
                           control.wait();
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }
               }
               if(play){//ÊÇ·ñÍ£Ö¹²¥·Å
                   this.runPersonelLogic();
               }else{
                   break;
               }

           }

    }
    /**
     * Âß¼­´úÂëÌå
     */
    protected abstract void runPersonelLogic();
    


}

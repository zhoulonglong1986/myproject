package com.example.toroapi;

public abstract class SchThread extends MyThread {


    public boolean play=true;

    @Override
    public void run() {

           while(true){
               synchronized (control) {
                   if (suspend) {//�Ƿ���ͣ
                       try {
                           control.wait();
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }
               }
               if(play){//�Ƿ�ֹͣ����
                   this.runPersonelLogic();
               }else{
                   break;
               }

           }

    }
    /**
     * �߼�������
     */
    protected abstract void runPersonelLogic();
    


}

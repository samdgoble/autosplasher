package com.bitinvert.reference;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class autoSplasher {
    private JFrame frame = new JFrame("autoSplasher");
    private JButton button = new JButton("Start autoSplasher");
    private JTextArea textArea = new JTextArea(11,40);
    private PointerInfo pointerInfo=MouseInfo.getPointerInfo();
    private Point point=pointerInfo.getLocation();
    private Random random=new Random();
    Robot robot=new Robot();

    private Object lock = new Object();
    private volatile boolean paused = true;

    //The range between clicks is given by: clickMin<x<clickMax
    private long clickMin=5000; //Change these (milliseconds)
    private long clickMax=10000; //Change these (milliseconds)
    private long clickDelay;
    private long clickNext;
    private boolean timeToClick=true;


    private int clickCounter=0;
    private int scriptCounter=0;
    private int ab1=0;
    private int ab2=0;
    private int ab3=0;
    private int ab4=0;
    private int ab1T=0;
    private int ab2T=0;
    private int ab3T=0;
    private int ab4T=0;


    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new autoSplasher();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public autoSplasher() throws AWTException {

        counter.start();
        button.addActionListener(pauseResume);

        textArea.setLineWrap(true);
        frame.add(button,java.awt.BorderLayout.NORTH);
        frame.add(textArea,java.awt.BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        int x=(int) point.getX();
        int y=(int) point.getY();
    }

    private Thread counter = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true) {
                work();
            }
        }
    });

    private void work() {
        while (!paused) {
            allowPause();

            if (timeToClick) {
                Timer timer=new Timer();
                TimerTask timerTask=new TimerTask() {
                    @Override
                    public void run() {
                        timeToClick=true;
                        singleClick();
                    }
                };
                updateClickDelay();
                timer.schedule(timerTask, clickDelay);
                timeToClick=false;
            }

            guiUpdate();
            done();

        }
    }

    private void updateClickDelay() {
        clickDelay = (long) (Math.random() * (clickMax - clickMin)) + clickMin;

        ab1 = (int) Math.floor(Math.random() * 5);
        ab2 = (int) Math.floor(Math.random() * 15);
        ab3 = (int) Math.floor(Math.random() * 25);
        ab4 = (int) Math.floor(Math.random() * 35);

        if (ab1 == 0) {
            clickDelay = clickDelay - (clickDelay / 5);
            ab1T++;
        }
        if (ab2 == 0) {
            clickDelay = clickDelay - (2 * clickDelay / 5);
            ab2T++;
        }
        if (ab3 == 0) {
            clickDelay = clickDelay - (3 * clickDelay / 5);
            ab3T++;
        }
        if (ab4 == 0) {
            clickDelay = clickDelay - (4 * clickDelay / 5);
            ab4T++;
        }
        clickNext=System.currentTimeMillis()+(int)clickDelay;
    }

   private void guiUpdate() {

        pointerInfo=MouseInfo.getPointerInfo();
        point=pointerInfo.getLocation();
        textArea.setText("autoSplasher " +
                "\n"+formatTime(System.currentTimeMillis())+
                "\nX Position: "+(int) point.getX()+
                "\nY Position: "+(int) point.getY()+
                "\nTime To Next Click: "+formatTime(clickNext-System.currentTimeMillis())+
                "\nClick Delay: "+(int) clickDelay+
                "\nClick Counter: "+clickCounter+
                "\nScript Counter: "+scriptCounter+
                "\nStates - AB1: "+ab1+" | AB2: "+ab2+" | AB3: "+ab3+" | AB4: "+ab4+
                "\nTotals - AB1: "+ab1T+" | AB2: "+ab2T+" | AB3: "+ab3T+" | AB4: "+ab4T);
    }

    private void singleClick() {
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            clickCounter++;
    }

    private void done() {
        scriptCounter++;
    }

    private void allowPause() {
        synchronized(lock) {
            while(paused) {
                try {
                    lock.wait();
                } catch(InterruptedException e) {
                }
            }
        }
    }

    private String formatTime(final long time)
    {
        long ms =time , s = ms / 1000, m = s / 60, h = m / 60;
        ms%= 1000;
        s %= 60;
        m %= 60;
        h %= 24;
        return String.format("%02d:%02d:%02d:%02d", h, m, s, ms);
    }

    private java.awt.event.ActionListener pauseResume =
            e -> {
                paused = !paused;
                button.setText(paused?"Resume":"Pause");
                synchronized(lock) {
                    lock.notifyAll();
                }
            };

}
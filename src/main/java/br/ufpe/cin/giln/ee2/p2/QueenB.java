package br.ufpe.cin.giln.ee2.p2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class QueenB {
    public static final int TOLERANCE = 1;
    public static final int WAIT_TIME = 10;

    public void run(int O, ArrayList<Job> jobs){
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(O);
        Queue<Job> queue = new LinkedList<>();

        for(Job job: jobs){
            queue.add(job);
        }

        while(!queue.isEmpty()){
            Job job = queue.remove();
            if(executor.getActiveCount() == O) {
                try {
                    Thread.sleep(WAIT_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

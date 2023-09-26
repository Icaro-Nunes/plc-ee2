package br.ufpe.cin.giln.ee2.p2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QueenB {
    public static final int TOLERANCE = 1;
    public static final int WAIT_TIME = 10;
    public static final int TERMINATION_TIMEOUT = 1000;
    private Map<Integer, Boolean> map = new HashMap<>();
    
    public synchronized boolean dependenciesResolved(int[] dependencyList){
        for (int i : dependencyList) {
            if(!map.get(i))
                return false;
        }

        return true;
    }

    public synchronized void completeTask(int taskId){
        System.out.println("tarefa " + taskId + " feita");
        map.put(taskId, true);
    }

    public void run(int O, ArrayList<Job> jobs){
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(O);
        Queue<Job> queue = new LinkedList<>();

        for(Job job: jobs){
            queue.add(job);
            map.put(job.getId(), false);
        }


        try {
            while(!queue.isEmpty()){
                Job job = queue.peek();
                if(executor.getActiveCount() == O) {
                    try {
                        Thread.sleep(WAIT_TIME);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    queue.remove();

                    if(dependenciesResolved(job.getDependencies())) {
                        executor.submit(job);
                        Thread.sleep(job.getTime() + TOLERANCE);
                    } else {
                        queue.add(job);
                    }
                }
            }

            executor.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS);
            executor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

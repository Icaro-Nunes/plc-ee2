package br.ufpe.cin.giln.ee2.p1;

import java.util.List;
import java.util.PriorityQueue;

public class PlaneMonitor {
    public int availableTracks = 0;
    private int completedTasks = 0;

    private PriorityQueue<Integer> tasksToExec = new PriorityQueue<>();

    public synchronized boolean acquired(int time){
        if(tasksToExec.isEmpty())
            return false;

        if(time == tasksToExec.peek())
            return true;

        return false;
    }

    public synchronized int getAvailableTracks(){
        return availableTracks;
    }

    public synchronized void occupyTrack(){
        availableTracks--;
        tasksToExec.poll();
    }

    public synchronized void deoccupyTrack(){
        availableTracks++;
        completedTasks++;
    }

    public synchronized int getCompletedTasks(){
        return completedTasks;
    }

    public void run(int N, int M, int K, List<Integer> departTimes, List<Integer> arrivalTimes){
        availableTracks = K;
        int necessaryTasks = N+M;
        long startupTime = System.currentTimeMillis();

        PriorityQueue<Integer> taskQueue = new PriorityQueue<>(necessaryTasks);
        
        for(int i = 0; i < N; i++){
            int time = departTimes.get(i);
            Thread thread = new Thread(
                new PlaneTask(this, startupTime, time)
            );
            taskQueue.add(time);
            tasksToExec.add(time);
            thread.start();
        }

        for(int i = 0; i < M; i++){
            int time = arrivalTimes.get(i);
            Thread thread = new Thread(
                new PlaneTask(this, startupTime, time)
            );
            taskQueue.add(time);
            tasksToExec.add(time);
            thread.start();
        }

        while(
            completedTasks < necessaryTasks
        ){
            if(taskQueue.isEmpty()) {
                try {
                    Thread.sleep(600);
                    break;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            int smallestTime = taskQueue.peek();

            if(System.currentTimeMillis() - startupTime >= smallestTime){
                taskQueue.poll();
                taskQueue.add(smallestTime+500);
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    }
}

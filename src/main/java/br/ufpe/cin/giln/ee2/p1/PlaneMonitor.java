package br.ufpe.cin.giln.ee2.p1;

import java.util.List;
import java.util.PriorityQueue;

public class PlaneMonitor {
    public int availableTracks = 0;
    private int completedTasks = 0;

    public synchronized int getAvailableTracks(){
        return availableTracks;
    }

    public synchronized void occupyTrack(){
        availableTracks--;
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
            thread.start();
        }

        for(int i = 0; i < M; i++){
            int time = arrivalTimes.get(i);
            Thread thread = new Thread(
                new PlaneTask(this, startupTime, time)
            );
            taskQueue.add(time);
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
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    }
}

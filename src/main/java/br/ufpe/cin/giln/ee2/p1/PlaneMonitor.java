package br.ufpe.cin.giln.ee2.p1;

import java.util.List;
import java.util.PriorityQueue;

/*
    This class is the plane monitor, being responsible
    for notifying all planes whenever there is a track
    available
*/
public class PlaneMonitor {
    public int availableTracks = 0;
    private int completedTasks = 0;
    static final int TOO_CLOSE_MARGIN = 1;
    private PriorityQueue<Integer> tasksToExec = new PriorityQueue<>();
    PriorityQueue<Integer> taskQueue = new PriorityQueue<>();

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
            long waitTime = 0;

            synchronized (this) {
                if(taskQueue.isEmpty())
                    break;

                int smallestTime = taskQueue.peek();
                long currTime = System.currentTimeMillis() - startupTime;

                if(currTime >= smallestTime){
                    taskQueue.poll();
                    taskQueue.add(smallestTime+500+TOO_CLOSE_MARGIN);
                    notifyAll();
                }

                if(!taskQueue.isEmpty())
                    smallestTime = taskQueue.peek();

                waitTime = Math.max(0,
                    smallestTime - currTime
                );
            }

            try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
}

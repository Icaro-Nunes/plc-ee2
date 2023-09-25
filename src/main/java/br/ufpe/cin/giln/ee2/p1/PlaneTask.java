package br.ufpe.cin.giln.ee2.p1;

public class PlaneTask implements Runnable {
    public enum TaskState {
        NEW,
        RUNNING,
        DONE
    }
    public TaskState state = TaskState.NEW;

    private PlaneMonitor monitor;
    private long originTime;
    private int taskTime;

    public PlaneTask(PlaneMonitor m, long o, int t){
        monitor = m; 
        originTime = o;
        taskTime = t;
    }

    public synchronized void run(){
        boolean condition = false;

        try {
            synchronized(monitor){
                while(!condition){
                    monitor.wait();
                    if(System.currentTimeMillis() - originTime >= taskTime){
                        condition = true;
                        if(monitor.getAvailableTracks() > 0)
                            monitor.occupyTrack();
                    }
                }
            }

            long execStart = System.currentTimeMillis();
            while(System.currentTimeMillis() - execStart <= 500);

            synchronized(monitor){
                monitor.deoccupyTrack();
                System.out.println("Avião do horário " + taskTime + " saindo");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

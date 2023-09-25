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

                    if(!monitor.acquired(taskTime))
                        continue;

                    long diff = System.currentTimeMillis() - originTime;
                    if(diff >= taskTime){
                        if(monitor.getAvailableTracks() > 0){
                            condition = true;
                            monitor.occupyTrack();
                        }
                    }
                }
            }

            long execStart = System.currentTimeMillis();
            Thread.sleep(500);

            long realFlightTime = (execStart - originTime);

            synchronized(monitor){
                monitor.deoccupyTrack();
                System.out.println("Horario esperado: " + taskTime + "; Horário real : " + realFlightTime + "; Atraso: " + (realFlightTime - taskTime));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package br.ufpe.cin.giln.ee2.p2;

public class Job implements Runnable {
    private int id;
    private int time;
    private int[] dependencies;
    private QueenB monitor;

    public Job(QueenB qb, int i, int t, int[] d){
        monitor = qb;
        id = i;
        time = t;
        dependencies = d;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(int[] dependencies) {
        this.dependencies = dependencies;
    }

	@Override
	public void run() {
        try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        monitor.completeTask(id);
	}
}

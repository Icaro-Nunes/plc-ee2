package br.ufpe.cin.giln.ee2.p2;

public class Job {
    private int id;
    private int time;
    private int[] dependencies;

    public Job(int i, int t, int[] d){
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
}

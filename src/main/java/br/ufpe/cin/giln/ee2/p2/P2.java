package br.ufpe.cin.giln.ee2.p2;

import br.ufpe.cin.giln.ee2.common.RunnableProblem;

import java.util.ArrayList;

public class P2 implements RunnableProblem {
    @Override
    public boolean run(String[] args) {
        try {
            int i = 0;

            String line = args[i];
            i++;

            String[] content = line.split(" ");
            int O = Integer.parseInt(content[0]);
            int N = Integer.parseInt(content[1]);

            int N_offset = i + N;

            ArrayList<Job> jobs = new ArrayList<>();

            for (; i < N_offset; i++) {
                line = args[i];

                content = line.split(" ");

                int id  = Integer.parseInt(content[0]);
                int time = Integer.parseInt(content[1]);
                ArrayList<Integer> dependencies =new ArrayList<>();

                for(int j = 2; j < content.length; j++)
                    dependencies.add(
                            Integer.parseInt(content[j])
                    );

                Job job = new Job(
                        id,
                        time,
                        dependencies.stream().mapToInt(v -> v).toArray()
                );

                jobs.add(job);
            }


            return true;
        } catch (IndexOutOfBoundsException e){
            return false;
        }
    }
}

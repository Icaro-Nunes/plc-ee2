package br.ufpe.cin.giln.ee2.p1;
import java.util.ArrayList;

import br.ufpe.cin.giln.ee2.common.*;

public class P1 implements RunnableProblem{
    public boolean run(String[] args){
        int i = 0;
        ArrayList <Integer> departTimes = new ArrayList<>();
        
        if(i >= args.length)
            return false;

        int N = Integer.parseInt(args[i]);
        i++;

        int N_offset = i+N;

        for(; i < N_offset; i++){
            int index = i;

            if(index >= args.length)
                return false;

            departTimes.add(
                Integer.parseInt(args[i])
            );
        }

        ArrayList <Integer> arrivalTimes = new ArrayList<>();

        if(i >= args.length)
            return false;

        int M = Integer.parseInt(args[i]);
        i++;

        int M_offset = i+M;

        for(; i < M_offset; i++){
            int index = i;

            if(index >= args.length)
                return false;

            arrivalTimes.add(
                Integer.parseInt(args[i])
            );
        }
        
        if(i >= args.length)
            return false;

        int K = Integer.parseInt(args[i]);
        i++;

        PlaneMonitor monitor = new PlaneMonitor();

        monitor.run(N, M, K, departTimes, arrivalTimes);


        return true;
    }
}

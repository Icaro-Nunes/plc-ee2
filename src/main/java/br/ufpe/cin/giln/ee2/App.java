package br.ufpe.cin.giln.ee2;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import br.ufpe.cin.giln.ee2.common.RunnableProblem;
import br.ufpe.cin.giln.ee2.p1.P1;
import br.ufpe.cin.giln.ee2.p2.P2;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            BufferedReader file = new BufferedReader(new FileReader("testData/p2/test1.txt"));

            ArrayList<String> params = new ArrayList<>();

            String line = file.readLine();

            while(line != null){
                params.add(line);
                line = file.readLine();
            }

            RunnableProblem prog = new P2();

            if(
                !prog.run(
                    params.toArray(new String[params.size()])
                )
            )
                System.out.println("Problema na execução!!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File not Found!");
        }
    }
}

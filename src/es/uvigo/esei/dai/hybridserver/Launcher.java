package es.uvigo.esei.dai.hybridserver;

import java.util.LinkedHashMap;

public class Launcher {
    public static void main(String[] args) {
        if(args.length > 1){
            System.out.println("Demasiados argumentos");
            System.exit(1);
        }
        if(args.length == 0) {
            new HybridServer().start();
        }else{
            new HybridServer(new LinkedHashMap<>()).start();
        }

    }
}

package server;


import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentCounter {

    static int counter = 0; // a global counter

    private static ReentrantLock counterLock = new ReentrantLock(true);


    public static void incrementCounter(){
        counterLock.lock();

        try{
            counter++;
        }finally{
            counterLock.unlock();
        }
    }


    public static void decrementCounter(){
        counterLock.lock();

        try{
            counter--;
        }finally{
            counterLock.unlock();
        }
    }
}

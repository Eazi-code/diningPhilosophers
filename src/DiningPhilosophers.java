import java.util.Random;

public class DiningPhilosophers {

    Philosopher[] philosophers;
    Fork[] forks;
    Thread[] threads;

    int number = 5;

    public static void main(String[] args){
        DiningPhilosophers obj = new DiningPhilosophers();
        obj.init();
        obj.startThinkingEating();
    }

    public void init(){

        System.out.println("Dining philosophers problem!");

        philosophers = new Philosopher[number]; //can be different number of philosophers

        forks = new Fork[number]; //same number as philosophers

        threads = new Thread[number]; //we use 5 threads, every philosopher has own thread

        for (int x = 0; x < number; x++){
            philosophers[x] = new Philosopher(x + 1);
            forks[x] = new Fork(x + 1);
        }
    }

    public void startThinkingEating(){

        for (int y = 0; y < number; y++){
            final int index = y;
            threads[y] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        philosophers[index].start(forks[index],
                                forks[(index+1) % number]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            threads[y].start();
        }
    }

    public class Fork{

        private int forkId;
        private boolean status;
        Fork(int forkId){
            this.forkId = forkId;
            this.status = true;
        }

        public synchronized boolean pick (int philosopherId) throws InterruptedException{

            int counter = 0;
            int waitUntil = new Random().nextInt(10) +5;

            while(!status){
                Thread.sleep(new Random().nextInt(100) +50);

                counter++;

                if (counter > waitUntil){
                    return false;
                }
            }
            status = false;
            return true;
        }

        public synchronized void free() throws InterruptedException {
            status = true;
        }
    }

    public class Philosopher{

        private int philosopherId;
        private Fork left, right;

        public Philosopher(int philosopherId){
            this.philosopherId = philosopherId;
        }

        public void start(Fork left, Fork right) throws InterruptedException{

            this.left = left;
            this.right = right;

            while (true){
                if(new Random().nextBoolean()){
                    eat();
                }else{
                    think();
                }
            }
        }

        public void think() throws InterruptedException{

            System.out.println("Philosopher number: " + philosopherId + " is now thinking.");
            Thread.sleep(new Random().nextInt(1000) +100);
            System.out.println("Philosopher number: " + philosopherId + " has stopped thinking");
        }
        public void eat() throws InterruptedException{

            boolean rightPick = false;  //starts as false
            boolean leftPick = false;

            System.out.println("Philosopher number: " + philosopherId + " wants to eat");
            System.out.println("Philosopher number: " + philosopherId + " is picking up the Fork: " + left.forkId);
            leftPick = left.pick(philosopherId); //pick function, if successful then returns true and goes to right fork

            if(!leftPick){
                return;
            }

            System.out.println("Philosopher number: " + philosopherId + " is picking up the Fork: " + right.forkId);
            rightPick = right.pick(philosopherId); //pick function, if successful then returns true

            if(!rightPick){
                left.free();
                return;
            }

            System.out.println("Philosopher number " + philosopherId + " is eating.");
            Thread.sleep(new Random().nextInt(1000) +100);

            left.free();
            right.free();

            System.out.println("Philosopher number: " + philosopherId + " has stopped eating and freed the Forks.");
        }
    }
}

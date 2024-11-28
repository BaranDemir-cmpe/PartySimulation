package os_project;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PartySimulation {
    private static final int NUM_GUESTS = 10;
    private static final int NUM_BOREKS = 30;
    private static final int NUM_CAKES = 15;
    private static final int NUM_DRINKS = 30;

    private static int borekTray = 5;
    private static int cakeTray = 5;
    private static int drinkTray = 5;

    private static int remainingBoreks = NUM_BOREKS;
    private static int remainingCakes = NUM_CAKES;
    private static int remainingDrinks = NUM_DRINKS;

    private static final Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_GUESTS + 1);

        for (int i = 1; i <= NUM_GUESTS; i++) {
            executor.execute(new Guest(i));
        }

        executor.execute(new Waiter());

        executor.shutdown();
    }

    static class Guest implements Runnable {
        private int id;
        private int borekCount = 0;
        private int cakeCount = 0;
        private int drinkCount = 0;

        Guest(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (borekCount < 4 || cakeCount < 2 || drinkCount < 4) {
                lock.lock();
                try {
                    if (borekCount < 4 && borekTray > 0) {
                        borekTray--;
                        borekCount++;
                        System.out.println("Guest " + id + " eats a borek. Borek count: " + borekCount + ". Borek tray: " + borekTray);
                    }
                    if (cakeCount < 2 && cakeTray > 0) {
                        cakeTray--;
                        cakeCount++;
                        System.out.println("Guest " + id + " eats a slice of cake. Cake count: " + cakeCount + ". Cake tray: " + cakeTray);
                    }
                    if (drinkCount < 4 && drinkTray > 0) {
                        drinkTray--;
                        drinkCount++;
                        System.out.println("Guest " + id + " drinks. Drink count: " + drinkCount + ". Drink tray: " + drinkTray);
                    }
                } finally {
                    lock.unlock();
                }
                try {
                    Thread.sleep((int)(Math.random() * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static class Waiter implements Runnable {
        @Override
        public void run() {
            while (remainingBoreks > 0 || remainingCakes > 0 || remainingDrinks > 0) {
                lock.lock();
                try {
                    if (borekTray <= 1 && remainingBoreks > 0) {
                        int refillAmount = Math.min(5 - borekTray, remainingBoreks);
                        borekTray += refillAmount;
                        remainingBoreks -= refillAmount;
                        System.out.println("Waiter refills borek tray. Borek tray: " + borekTray + ". Remaining boreks: " + remainingBoreks);
                    }
                    if (cakeTray <= 1 && remainingCakes > 0) {
                        int refillAmount = Math.min(5 - cakeTray, remainingCakes);
                        cakeTray += refillAmount;
                        remainingCakes -= refillAmount;
                        System.out.println("Waiter refills cake tray. Cake tray: " + cakeTray + ". Remaining cakes: " + remainingCakes);
                    }
                    if (drinkTray <= 1 && remainingDrinks > 0) {
                        int refillAmount = Math.min(5 - drinkTray, remainingDrinks);
                        drinkTray += refillAmount;
                        remainingDrinks -= refillAmount;
                        System.out.println("Waiter refills drink tray. Drink tray: " + drinkTray + ". Remaining drinks: " + remainingDrinks);
                    }
                } finally {
                    lock.unlock();
                }
                try {
                    Thread.sleep((int)(Math.random() * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

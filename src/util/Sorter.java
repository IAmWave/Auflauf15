package util;

import java.util.ArrayList;

/**
 *
 * @author colander
 */
public class Sorter {

    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(6);
        list.add(2);
        list.add(1);
        list.add(3);
        list = new Sorter().quickSort(list);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    public Sorter() {
    }

    public ArrayList<Integer> quickSort(ArrayList<Integer> ar) {
        if (ar.size() == 1) {
            return ar;
        } else {
            ArrayList<Integer> smaller = new ArrayList<>();
            ArrayList<Integer> bigger = new ArrayList<>();
            //pocita ze pivot je na min
            int pivot = ar.get(0);
            for (int i = 1; i < ar.size(); i++) {
                if (ar.get(i) > pivot) {
                    bigger.add(ar.get(i));
                } else {
                    smaller.add(ar.get(i));
                }
            }
            if (smaller.size() > 1) {
                smaller = quickSort(smaller);
            }
            if (bigger.size() > 1) {
                bigger = quickSort(bigger);
            }
            smaller.add(pivot);
            for (int i = 0; i < bigger.size(); i++) {
                smaller.add(bigger.get(i));
            }
            return smaller;
        }
    }
}

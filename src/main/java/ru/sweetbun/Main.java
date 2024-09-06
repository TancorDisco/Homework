package ru.sweetbun;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        //2
        CustomLinkedList<Integer> list = new CustomLinkedList<>();

        System.out.println(list.add(1));
        System.out.println(list.get(0));
        System.out.println(list.remove(0));
        System.out.println(list.contains(1));
        System.out.println(list.addAll(new ArrayList<>(List.of(1, 2, 4))));

        //3
        Stream<Integer> stream = Stream.of(1, 3, 5, 8, 9);
        CustomLinkedList<Integer> res = stream.reduce(
                new CustomLinkedList<>(),
                (list1, element) -> {
                    list1.add(element);
                    return list1;
                },
                (list1, list2) -> {
                    for (Integer el : list2) list1.add(el);
                    return list1;
                }
        );
        for (Integer el : res) {
            System.out.print(el);
        }
    }
}

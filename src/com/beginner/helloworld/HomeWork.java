package com.beginner.helloworld;
import java.util.*;

public class HomeWork {
    /*
    Practice makes perfect.
     */

    /*
    1. Define a function that receive a string as argument and prints it backwards. For example, the string
    “Automation” gets printed as: “noitamotuA”
        */
    public static String backwards(String strToReverse) {
        return new StringBuilder(strToReverse).reverse().toString();
    }

    /*
    2. Define a function that takes a list of strings and prints them, one per line, in a rectangular frame.
    ["Hello", "World", "in", "a", "frame"]
     */
    public static void addFrame(String[] textArray) {
        int biggestString = 0;
        for (String text: textArray) {
            if (text.length() > biggestString) {
                biggestString = text.length();
            }
        }
        System.out.println("*".repeat(biggestString + 4));
        for (String str: textArray) {
            System.out.println("* " + str + " ".repeat(biggestString - str.length()) + " *");
        }
        System.out.println("*".repeat(biggestString + 4));
    }

    /*
    3. Define a function that receive a list with numbers and strings and return a list only with the numbers without
    duplicates.
     */
    public ArrayList<Object> removeDuplicates(ArrayList<Object> lst) {
        ArrayList<Object> onlyNumbers = new ArrayList<>();
        for (Object item: lst) {
            if (item instanceof Integer || item instanceof Double) {
                if (onlyNumbers.size() == 0) {
                    onlyNumbers.add(item);
                } else if (!onlyNumbers.contains(item)) {
                    onlyNumbers.add(item);
                }
            }
        }
        return onlyNumbers;
    }


    /*
    4. Define a function that receive as a single argument a list of strings like the below variable and return a
    dictionary that contains as keys “Video”, “Audio”, “Other” and as values a list with the entries specific for that
    key.
     */
    public static Map<String, ArrayList<String>> group(String[] entries) {
        Map<String, ArrayList<String>> result = new HashMap<>();

        // values of the dictionary
        ArrayList<String> vid = new ArrayList<>();
        ArrayList<String> aud = new ArrayList<>();
        ArrayList<String> oth = new ArrayList<>();

        // sorting by format
        for (String entry: entries) {
            List<String> videoFormats = Arrays.asList("mp4", "mov", "wmv", "avi");
            List<String> audioFormats = Arrays.asList("mp3", "wav", "aac");
            String toAdd = entry.substring(entry.lastIndexOf('.') + 1);
            if (videoFormats.contains(toAdd)) {
                vid.add(entry);
            } else if (audioFormats.contains(toAdd)) {
                aud.add(entry);
            } else {
                oth.add(entry);
            }
        }

        // adding to dictionary
        result.put("Video", vid);
        result.put("Audio", aud);
        result.put("Other", oth);

        return result;
    }



    /*
     5. Find out how many rabbits are after N years knowing the following
        n_years = input
        number_rabbits = input
        rabbits_births_number = input
        Each rabbit can give birth only within the first two years
        Rabbits live only four years
     */
    public static Integer fib(int i, int j) {
        // Seed the sequence with the 1 pair, then in their reproductive year.
        ArrayList<Integer> generations = new ArrayList<>();
        generations.add(1);
        generations.add(1);

        int count = 2;
        while (count < i) {
            if (count < j) {
                // recurrence relation before rabbits start dying (fib seq Fn = Fn-2 + Fn-1)
                generations.add(generations.get(generations.size() - 2) + generations.get(generations.size() - 1));
            } else if (count == j || count == j + 1){
                // base cases for subtracting rabbit deaths (1 death in first 2 death gens)
                System.out.println("in base cases for newborns (1st+2nd gen. deaths)");
                // # Fn = Fn-2 + Fn-1 - 1
                generations.add((generations.get(generations.size() - 2) + generations.get(generations.size() - 1) - 1));
            } else {
                // the recurrence relation here is Fn-2 + Fn-1 - Fn-(j+1)
                generations.add((generations.get(generations.size() - 2) + generations.get(generations.size() - 1)) -
                        (generations.get(generations.size() - (j+1))));
            }
            count++;
        }
        return generations.get(generations.size() - 1);
    }

    public static void main(String[] args) {

        // 1
        String toReverse = "Automation";
        System.out.println("Text to reverse is: " + toReverse);
        System.out.println("Reversed text is: " + backwards(toReverse));


        // 2
        String[] frame = {"Hello", "World", "in", "a", "frame"};
        addFrame(frame);


        // 3
        // to_be_used = [1, 3, 67, "1", "62", "Foo", "3", 5, 1, 3, False, 1.3]
        HomeWork task3 = new HomeWork();
        ArrayList<Object> toBeUsed = new ArrayList<>();
        toBeUsed.add(1);
        toBeUsed.add(1);
        toBeUsed.add(67);
        toBeUsed.add("1");
        toBeUsed.add("62");
        toBeUsed.add("Foo");
        toBeUsed.add("3");
        toBeUsed.add(5);
        toBeUsed.add(1);
        toBeUsed.add(3);
        toBeUsed.add(false);
        toBeUsed.add(1.3);

        // Original array
        System.out.println(toBeUsed);
        // Only numbers
        System.out.println(task3.removeDuplicates(toBeUsed));


        // 4
        // results = ["Entry One.mp4", "Entry Two.wav", "Entry Three.jpg", "Entry Four.mng", "Entry Five.png",
        // "Entry Six.csv"]
        String[] results = {"Entry One.mp4","Entry Two.wav", "Entry Three.jpg", "Entry Four.mng",
                "Entry Five.png", "Entry Six.csv", "Entry Seven.mp3"};
        System.out.println(group(results));


        //5
        // getting input
        Scanner requiredInfo = new Scanner(System.in);
        System.out.println("Enter years to run.");
        int nYears = Integer.parseInt(requiredInfo.nextLine());
        System.out.println("Ty. Now enter the number of rabbits.");
        int  numberRabbits = Integer.parseInt(requiredInfo.nextLine());

        int res = fib(nYears, numberRabbits);
        System.out.println("Here's how the total population looks by generation: \n" + res);
    }
}

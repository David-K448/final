import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);

        int menuSelect;
        int pageNumInput = -1;
        long refInput = -1;
        boolean refValid = false;
        boolean numValid = false;

        while (true) {
            System.out.println(
                    "\n\nMenu:\n\t0 - Exit\n\t1 - Input N Pages\n\t2 - Input the reference string\n\t3 - Simulate the OPT algorithm\n\t4 - Simulate the NEW algorithm\n\nSelect Option:\n");
            menuSelect = s.nextInt();
            s.nextLine(); // clear next line character

            switch (menuSelect) {
                case 0:
                    System.out.println("Goodbye!");
                    s.close();
                    System.exit(0);
                    break;
                case 1:
                    System.out.println("Option 1  selected.\n");

                    boolean validPage = false;
                    while (!validPage) {
                        System.out.println("Please enter N number of pages.");
                        pageNumInput = s.nextInt();
                        s.nextLine();
                        if (pageNumInput >= 2 && pageNumInput <= 8) {
                            System.out.println("\nValid Input recieved.");
                            validPage = true;
                            numValid = true;
                        } else {
                            System.out.println("Invalid Input recieved, min length = 2, max length = 8");
                            pageNumInput = -1;
                        }
                    }
                    break;
                case 2:
                    System.out.println("Option 2 selected.\n");
                    boolean validRef = false;
                    while (!validRef) {
                        System.out.println("Please enter reference number.");
                        if (s.hasNextLong()) { // check if ref is a long
                            refInput = s.nextLong();
                            s.nextLine(); // consume the new line character
                            int refLength = String.valueOf(refInput).length();
                            if (refLength <= 20 && refLength >= 3 && refInput > 0) {
                                System.out.println("\nValid Input received.");
                                System.out.println(refInput);
                                validRef = true;
                                refValid = true;
                            } else {
                                System.out
                                        .println("Invalid long - must be a positive long with digit count from 3-20.");
                            }
                        } else {
                            s.nextLine(); // consume the invalid input
                            System.out.println("Invalid input - please enter a valid long.");
                        }
                    }
                    break;

                case 3:
                    System.out.println("Option 3 selected.\n");
                    if(numValid){
                        if(refValid){
                            runOPT(pageNumInput, refInput);
                        } else {
                            System.out.println("Invalid Ref, unable to run OPT.");
                        }
                    } else {
                        System.out.println("Invalid page count, unable to run OPT.");
                    }
                    
                    break;
                case 4:
                    System.out.println("Option 4 selected.\n");
                    if(numValid){
                        if(refValid){
                            runNew(pageNumInput, refInput);
                        } else {
                            System.out.println("Invalid Ref, unable to run NEW.");
                        }
                    } else {
                        System.out.println("Invalid page count, unable to run NEW.");
                    }
                    
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
        }
    }

    public static int findSecondLargest(int[] arr) {
        int[] sortedArr = Arrays.stream(arr)
                .boxed()
                .sorted(Collections.reverseOrder())
                .mapToInt(Integer::intValue)
                .toArray();
        return sortedArr[1];
    }

    public static int indexOf(int[] arr, int searchValue) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == searchValue) {
                return i;
            }
        }
        return -1; // not found
    }

    private static void runNew(int pageNumInput, long refInput) {
        int currentFrame = 0;
        int faultCount = 0;
        int victim = -1;
        int max = -1;
        boolean fault;
        int index;

        ArrayList<Integer> refInputAL = new ArrayList<>();
        String refString = String.valueOf(refInput);
        int[] refInputArr = new int[String.valueOf(refInput).length()]; // initialize reference string array

        for (int i = 0; i < refString.length(); i++) {
            refInputArr[i] = Character.getNumericValue(refString.charAt(i));
        } // fill the array

        // System.out.println(Arrays.toString(refInputArr));

        for (int i = 0; i < String.valueOf(refInput).length(); i++) {
            refInputAL.add(i, Character.getNumericValue(refString.charAt(i)));
        }
        // previous block of code gets the refstring and puts it into both an array and
        // array list for later use

        // generate empty template table, print to console

        ArrayList<Integer> memory = new ArrayList<>(pageNumInput);
        ArrayList<Integer> refListAL = new ArrayList<>();

        for (int i : refInputAL) {
            refListAL.add(i);
        }

        String[][] outPutTable = makeTable(pageNumInput, refInputAL);

        System.out.println("Current Table\n");
        printTable(outPutTable);

        System.out.print("\nPress Enter to continue... ");

        promptEnterKey();

        int[] arrayCounter = new int[pageNumInput]; // initialize array counter to number of pages
        Arrays.fill(arrayCounter, 0); // initialize array with 0s

        for (int i = 0; i < refInputAL.size(); i++) {
            // checks if all pages have been filled yet
            if (!memory.contains(refInputAL.get(i))) {
                if (memory.size() < pageNumInput) {
                    memory.add(currentFrame, refInputAL.get(i));
                    refListAL.remove((Integer) refInputAL.get(i));
                    arrayCounter[memory.size() - 1] = 1; // update counter for new page
                    for (int k = 0; k <= pageNumInput - 1; k++) {
                        if (k != memory.size() - 1 && k <= memory.size() - 1) {
                            arrayCounter[k]++;
                        }
                    }
                    ++currentFrame;
                    fault = true;
                    if (i >= pageNumInput) {
                        faultCount++;
                    }
                } else { // Page fault on full memory, swap
                    fault = true;
                    if (i >= pageNumInput) {
                        faultCount++;
                    }
                    // Step 1 remove current item.
                    int temp = refListAL.get(0);
                    refListAL.remove(0);

                    int secondLargest = findSecondLargest(arrayCounter);
                    // System.out.println("second="+secondLargest);
                    // System.out.println("second index="+indexOf(arrayCounter, secondLargest));

                    // memory.set(memory.indexOf(victim), temp); // Swap
                    memory.set(indexOf(arrayCounter, secondLargest), temp); // Swap
                    // System.out.println("max="+max);
                    // System.out.println("tempindex="+memory.indexOf(max));
                    // System.out.println("temp="+temp);
                    // System.out.println("tempindex="+memory.indexOf(temp));
                    // System.out.println("vict="+victim);
                    // System.out.println("victind="+memory.indexOf(victim));
                    // System.out.println(memory.toString());
                    for (int k = 0; k <= pageNumInput - 1; k++) {
                        if (k != memory.indexOf(temp)) {
                            arrayCounter[k]++;
                        } else {
                            arrayCounter[k] = 1;
                        }

                    }

                    max = -1; // Reset max
                }
            } else { // Memory contains reference
                fault = false;
                for (int k = 0; k <= pageNumInput - 1; k++) {
                    if (k <= memory.size() - 1) {
                        arrayCounter[k]++;
                    }
                }
                refListAL.remove(0);
            }

            for (int j = 0; j < memory.size(); ++j) {
                outPutTable[j + 1][i + 1] = String.valueOf(memory.get(j));
            }

            if (fault && i >= pageNumInput) {
                outPutTable[pageNumInput + 1][i + 1] = "F";
                if (victim != -1)
                    outPutTable[pageNumInput + 2][i + 1] = String.valueOf(victim);
            }

            System.out.println("Current Table\n");
            printTable(outPutTable);

            System.out.println("\nArray Counter:");
            System.out.println(Arrays.toString(arrayCounter));

            System.out.print("\nPress Enter to continue... ");
            promptEnterKey();
        }

        System.out.println("\nOPT  is now complete");
        System.out.println("A total of " + faultCount + " faults occurred");

    }

    private static void runOPT(int pageNumInput, long refInput) {
        int currentFrame = 0;
        int faultCount = 0;
        int victim = -1;
        int max = -1;
        boolean fault;
        int index;

        ArrayList<Integer> refInputAL = new ArrayList<>();
        String refString = String.valueOf(refInput);
        int[] refInputArr = new int[String.valueOf(refInput).length()]; // initialize reference string array

        for (int i = 0; i < refString.length(); i++) {
            refInputArr[i] = Character.getNumericValue(refString.charAt(i));
        } // fill the array

        // System.out.println(Arrays.toString(refInputArr));

        for (int i = 0; i < String.valueOf(refInput).length(); i++) {
            refInputAL.add(i, Character.getNumericValue(refString.charAt(i)));
        }
        // previous block of code gets the refstring and puts it into both an array and
        // array list for later use

        // generate empty template table, print to console

        ArrayList<Integer> memory = new ArrayList<>(pageNumInput);
        ArrayList<Integer> refListAL = new ArrayList<>();

        for (int i : refInputAL) {
            refListAL.add(i);
        }

        String[][] outPutTable = makeTable(pageNumInput, refInputAL);

        System.out.println("Current Table\n");
        printTable(outPutTable);

        System.out.print("\nPress Enter to continue... ");

        promptEnterKey();

        for (int i = 0; i < refInputAL.size(); i++) {
            // checks if all pages have been filled yet
            if (!memory.contains(refInputAL.get(i))) {
                if (memory.size() < pageNumInput) {
                    memory.add(currentFrame, refInputAL.get(i));
                    refListAL.remove((Integer) refInputAL.get(i));
                    ++currentFrame;
                    fault = true;
                    if (i >= pageNumInput) {
                        faultCount++;
                    }
                } else { // Page fault on full memory, swap
                    fault = true;
                    if (i >= pageNumInput) {
                        faultCount++;
                    }
                    // Step 1 remove current item.
                    int temp = refListAL.get(0);
                    refListAL.remove(0);

                    // Step 2 find ref that will not be used for longest period
                    for (int m : memory) {
                        index = refListAL.indexOf(m);

                        // Simplest case, ref is never seen again
                        if (index == -1) {
                            victim = m;
                            break;
                        }

                        if (index > max) { // Find max index into reference string
                            victim = m; // that will be the victim unless an index
                            max = index; // of -1 comes by.
                        }
                    }

                    memory.set(memory.indexOf(victim), temp); // Swap
                    max = -1; // Reset max
                }
            } else { // Memory contains reference
                fault = false;
                refListAL.remove(0);
            }

            for (int j = 0; j < memory.size(); ++j) {
                outPutTable[j + 1][i + 1] = String.valueOf(memory.get(j));
            }

            if (fault && i >= pageNumInput) {
                outPutTable[pageNumInput + 1][i + 1] = "F";
                if (victim != -1)
                    outPutTable[pageNumInput + 2][i + 1] = String.valueOf(victim);
            }

            System.out.println("Current Table\n");
            printTable(outPutTable);

            System.out.print("\nPress Enter to continue... ");
            promptEnterKey();
        }

        System.out.println("\nOPT  is now complete");
        System.out.println("A total of " + faultCount + " faults occurred");

    }

    public static void promptEnterKey() {
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    private static void printTable(String[][] outPutTable) {
        String separator = "-";
        String columnSeparator = "|";
        int[] columnWidths = new int[outPutTable[0].length];

        // Find the maximum width of each column
        for (int col = 0; col < outPutTable[0].length; col++) {
            int maxWidth = 0;
            for (int row = 0; row < outPutTable.length; row++) {
                if (outPutTable[row][col] != null && outPutTable[row][col].length() > maxWidth) {
                    maxWidth = outPutTable[row][col].length();
                }
            }
            columnWidths[col] = maxWidth + 2; // Add padding of 2 spaces
        }

        // Print the table with separators
        for (String[] row : outPutTable) {
            for (int col = 0; col < row.length; col++) {
                if (col == 0) {
                    System.out.printf("%-" + columnWidths[0] + "s", row[0]);
                } else {
                    if (row[col] == null) {
                        System.out.printf("%" + columnWidths[col] + "s", separator.repeat(columnWidths[col]));
                    } else {
                        System.out.printf("%-" + columnWidths[col] + "s", row[col]);
                    }
                }
                System.out.print(columnSeparator);
            }
            System.out.println();
            System.out.println(
                    separator.repeat(Arrays.stream(columnWidths).sum() + columnSeparator.length() * (row.length - 1)));
        }
    }

    private static String[][] makeTable(int pageNumInput, ArrayList<Integer> refInputAL) {
        String[][] table = new String[pageNumInput + 3][refInputAL.size() + 1];
        table[0][0] = "Reference String";
        for (int i = 1; i <= pageNumInput; i++) {
            table[i][0] = "Physical Frame " + (i - 1);
        }
        table[pageNumInput + 1][0] = "Page Faults";
        table[pageNumInput + 2][0] = "Victim Frames";

        for (int i = 0; i < refInputAL.size(); i++) {
            table[0][i + 1] = String.valueOf(refInputAL.get(i));
        }

        return table;
    }

}

// 245675145236345356
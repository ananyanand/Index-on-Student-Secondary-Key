import java.io.*;
import java.util.Scanner;

public class Secondindex {
    public static int count;
    public static final int[] Address_list = new int[100];
    public static final String[] Name_list = new String[100];
    public static Scanner s = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
       Secondindex obj = new SecondIndex();
        obj.create_index();
        int ch;
        System.out.println("******Menu******");
        System.out.println("1. Add Record");
        System.out.println("2. Search Record");
        System.out.println("3. Remove Record");
        System.out.println("4. Exit");
        System.out.println("****************");
        while (true) {
            System.out.println("\nPlease enter your choice:");
            ch = s.nextInt();
            s.nextLine();
            switch (ch) {
                case 1:
                    obj.insert();
                    break;
                case 2:
                    obj.search();
                    break;
                case 3:
                    obj.remove();
                    break;
                case 4:
                    System.out.println("Do you want to exit? (Y/N)");
                    if (s.next().equalsIgnoreCase("y")) {
                        System.out.println("Program Ended");
                        System.exit(0);
                    }
                    break;
                default:
                    System.out.println("Invalid Option");
            }
        }
    }

    public void create_index() throws IOException, ArrayIndexOutOfBoundsException {
        count = -1;
        long pos;
        RandomAccessFile file = new RandomAccessFile("E:\\engg\\additional stuff\\mini project\\f1.txt", "r");
        pos = file.getFilePointer();
        String s;
        while ((s = file.readLine()) != null) {
            String[] result = s.split("\\|");
            if (!result[0].startsWith("*")) { // Ignore deleted records
                count++;
                Name_list[count] = result[1];
                Address_list[count] = (int) pos;
            }
            pos = file.getFilePointer();
        }
        file.close();
        sort_index();
    }

    public void sort_index() throws IOException {
        for (int i = 0; i <= count; i++) {
            for (int j = i + 1; j <= count; j++) {
                if (Name_list[i].compareTo(Name_list[j]) > 0) {
                    String temp = Name_list[i];
                    Name_list[i] = Name_list[j];
                    Name_list[j] = temp;

                    int temp1 = Address_list[i];
                    Address_list[i] = Address_list[j];
                    Address_list[j] = temp1;
                }
            }
        }
    }

    public void insert() throws IOException, FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File("E:\\engg\\additional stuff\\mini project\\f1.txt"), true));

        System.out.println("Enter USN,Name,Sem and Branch ");
        String usn = s.nextLine();
        String name = s.nextLine();
        String sem = s.nextLine();
        String branch = s.nextLine();
        String b = usn + "|" + name + "|" + sem + "|" + branch + "|" + "$";

        pw.println(b);
        pw.close();

        create_index();
    }

    public void search() throws IOException {
        int pos;
        System.out.println("Enter the name to be searched");
        String key = s.nextLine();

        int t = 0;
        pos = search_index(key);

        if (pos != -1) {
            display_record(pos);

            t = pos;
            while ((t < count) && (Name_list[++t].equals(key)))
                display_record(t);

            t = pos;
            while ((t >= 0) && (Name_list[--t].equals(key)))
                display_record(t);
        } else
            System.out.println("Record not found");
    }

    public int search_index(String key) {
        int low = 0, high = count, mid = 0;
        while (low <= high) {
            mid = (low + high) / 2;
            if (Name_list[mid].equals(key))
                return mid;

            if (Name_list[mid].compareTo(key) > 0)
                high = mid - 1;

            if (Name_list[mid].compareTo(key) < 0)
                low = mid + 1;
        }
        return -1;
    }

    public void display_record(int pos) throws IOException {
        RandomAccessFile file = new RandomAccessFile("E:\\engg\\additional stuff\\mini project\\f1.txt", "r");

        int address = Address_list[pos];
        String usn = "", sem = "", branch = "", name = "";

        file.seek(address);
        String s = file.readLine();

        while (s != null) {
            String[] result = s.split("\\|");
            if (!result[0].startsWith("*")) { // Ignore deleted records
                usn = result[0];
                name = result[1];
                sem = result[2];
                branch = result[3];
                System.out.println("\nRecord Details");
                System.out.println("USN: " + usn);
                System.out.println("Name: " + name);
                System.out.println("Sem: " + sem);
                System.out.println("Branch: " + branch);
                break;
            }
            address = (int) file.getFilePointer();
            s = file.readLine();
        }
        file.close();
    }

    public void remove() throws IOException {
        int pos, t;

        System.out.println("Enter the key to be deleted");
        String key = s.nextLine();

        pos = search_index(key);
        if (pos != -1) {
            delete_from_file(pos);

            t = pos;
            while ((t < count) && (Name_list[++t].equals(key)))
                delete_from_file(t);

            t = pos;
            while ((t >= 0) && (Name_list[--t].equals(key)))
                delete_from_file(t);

            create_index();
        } else
            System.out.println("Record not found");
    }

    public void delete_from_file(int pos) throws IOException {
        display_record(pos);

        RandomAccessFile file = new RandomAccessFile("E:\\engg\\additional stuff\\mini project\\f1.txt", "rw");
        System.out.println("Are you sure you want to delete? (Y/N)");
        String ch = s.nextLine();

        if (ch.equalsIgnoreCase("y")) {
            int address = Address_list[pos];
            file.seek(address);
            file.writeBytes("*"); // Mark the record as deleted
            System.out.println("Record is deleted");

            // Append the deleted record to the deleted_records.txt file
            RandomAccessFile deletedFile = new RandomAccessFile("E:\\engg\\additional stuff\\mini project\\deleted_records.txt", "rw");
            deletedFile.seek(deletedFile.length());
            String deletedRecord = file.readLine();
            deletedFile.writeBytes(deletedRecord + "\n");
            deletedFile.close();
        }
        file.close();
    }
}

import java.io.*;
import java.util.*;

public class BankManagementSystem {
    private static final String DATA_FILE = "accounts.dat";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Account> accounts = loadAccounts();

        while (true) {
            showMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    createAccount(sc, accounts);
                    break;
                case "2":
                    deposit(sc, accounts);
                    break;
                case "3":
                    withdraw(sc, accounts);
                    break;
                case "4":
                    checkBalance(sc, accounts);
                    break;
                case "5":
                    listAllAccounts(accounts);
                    break;
                case "6":
                    updateAccount(sc, accounts);
                    break;
                case "7":
                    deleteAccount(sc, accounts);
                    break;
                case "8":
                    saveAccounts(accounts);
                    System.out.println("Exiting. Data saved.");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice — please enter 1-8.");
            }

            // After each operation, save to file to persist changes
            saveAccounts(accounts);
            System.out.println("\nPress Enter to continue...");
            sc.nextLine();
        }
    }

    private static void showMenu() {
        System.out.println("=== Bank Management System ===");
        System.out.println("1. Create new account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Check balance");
        System.out.println("5. List all accounts");
        System.out.println("6. Update account holder name");
        System.out.println("7. Delete account");
        System.out.println("8. Exit");
        System.out.print("Choose an option (1-8): ");
    }

    private static List<Account> loadAccounts() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Account> list = (List<Account>) obj;
                return list;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println("Could not read data file. Starting with empty account list.");
            return new ArrayList<>();
        }
    }

    private static void saveAccounts(List<Account> accounts) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    private static int nextAccountNumber(List<Account> accounts) {
        int max = 1000; // start at 1001
        for (Account a : accounts) {
            if (a.getAccountNumber() > max) max = a.getAccountNumber();
        }
        return max + 1;
    }

    private static Account findAccount(List<Account> accounts, int accNo) {
        for (Account a : accounts) {
            if (a.getAccountNumber() == accNo) return a;
        }
        return null;
    }

    private static void createAccount(Scanner sc, List<Account> accounts) {
        System.out.print("Enter account holder name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Enter initial deposit (>=0): ");
        double init;
        try {
            init = Double.parseDouble(sc.nextLine().trim());
            if (init < 0) {
                System.out.println("Initial deposit cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        int accNo = nextAccountNumber(accounts);
        Account acc = new Account(accNo, name, init);
        accounts.add(acc);
        System.out.println("Account created successfully! Account number: " + accNo);
    }

    private static void deposit(Scanner sc, List<Account> accounts) {
        System.out.print("Enter account number: ");
        int accNo = readInt(sc);
        if (accNo == -1) return;

        Account acc = findAccount(accounts, accNo);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.print("Enter deposit amount: ");
        double amt;
        try {
            amt = Double.parseDouble(sc.nextLine().trim());
            acc.deposit(amt);
            System.out.println("Deposit successful. New balance: " + acc.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void withdraw(Scanner sc, List<Account> accounts) {
        System.out.print("Enter account number: ");
        int accNo = readInt(sc);
        if (accNo == -1) return;

        Account acc = findAccount(accounts, accNo);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.print("Enter withdrawal amount: ");
        double amt;
        try {
            amt = Double.parseDouble(sc.nextLine().trim());
            boolean ok = acc.withdraw(amt);
            if (ok) {
                System.out.println("Withdrawal successful. New balance: " + acc.getBalance());
            } else {
                System.out.println("Insufficient funds. Current balance: " + acc.getBalance());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void checkBalance(Scanner sc, List<Account> accounts) {
        System.out.print("Enter account number: ");
        int accNo = readInt(sc);
        if (accNo == -1) return;

        Account acc = findAccount(accounts, accNo);
        if (acc == null) {
            System.out.println("Account not found.");
        } else {
            System.out.println(acc);
        }
    }

    private static void listAllAccounts(List<Account> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        System.out.println("All accounts:");
        for (Account a : accounts) {
            System.out.println(a);
        }
    }

    private static void updateAccount(Scanner sc, List<Account> accounts) {
        System.out.print("Enter account number: ");
        int accNo = readInt(sc);
        if (accNo == -1) return;

        Account acc = findAccount(accounts, accNo);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.println("Current name: " + acc.getName());
        System.out.print("Enter new name: ");
        String newName = sc.nextLine().trim();
        if (newName.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        acc.setName(newName);
        System.out.println("Name updated.");
    }

    private static void deleteAccount(Scanner sc, List<Account> accounts) {
        System.out.print("Enter account number to delete: ");
        int accNo = readInt(sc);
        if (accNo == -1) return;

        Account acc = findAccount(accounts, accNo);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }

        System.out.println("Are you sure you want to delete account " + accNo + " (yes/no)? ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (confirm.equals("yes") || confirm.equals("y")) {
            accounts.remove(acc);
            System.out.println("Account deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static int readInt(Scanner sc) {
        String s = sc.nextLine().trim();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return -1;
        }
    }
}

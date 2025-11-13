import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    private int accountNumber;
    private String name;
    private double balance;

    public Account(int accountNumber, String name, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.balance = initialDeposit;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void deposit(double amt) {
        if (amt > 0) {
            balance += amt;
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
    }

    public boolean withdraw(double amt) {
        if (amt <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (amt > balance) return false; // insufficient funds
        balance -= amt;
        return true;
    }

    @Override
    public String toString() {
        return String.format("AccountNo: %d | Name: %s | Balance: %.2f", accountNumber, name, balance);
    }
}

/*
 * ============================================================
 *  CAFE MANAGEMENT SYSTEM
 *  Author      : Tinsae Ayalew (github.com/Asant444)
 *  Language    : Java
 *  Description : A console-based cafe management system with
 *                menu ordering, table management, billing,
 *                and a daily sales report.
 * ============================================================
 */

import java.util.*;
import java.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

// ─────────────────────────────────────────────
//  MenuItem
// ─────────────────────────────────────────────
class MenuItem {
    private int id;
    private String name;
    private String category;
    private double price;
    private boolean available;

    public MenuItem(int id, String name, String category, double price) {
        this.id = id; this.name = name;
        this.category = category; this.price = price;
        this.available = true;
    }
    public int getId()           { return id; }
    public String getName()      { return name; }
    public String getCategory()  { return category; }
    public double getPrice()     { return price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean a) { this.available = a; }

    @Override
    public String toString() {
        return String.format("  [%2d] %-28s ETB %6.2f %s",
            id, name, price, available ? "" : "[UNAVAILABLE]");
    }
}

// ─────────────────────────────────────────────
//  OrderItem
// ─────────────────────────────────────────────
class OrderItem {
    private MenuItem item;
    private int quantity;

    public OrderItem(MenuItem item, int qty) {
        this.item = item; this.quantity = qty;
    }
    public MenuItem getItem()   { return item; }
    public int getQuantity()    { return quantity; }
    public void addQuantity(int q) { this.quantity += q; }
    public double getSubtotal() { return item.getPrice() * quantity; }
}

// ─────────────────────────────────────────────
//  Order
// ─────────────────────────────────────────────
class Order {
    private static int counter = 1000;
    private int orderId;
    private int tableNumber;
    private String customerName;
    private List<OrderItem> items;
    private LocalDateTime orderTime;
    private String status; // OPEN, PAID
    private double taxRate = 0.15;

    public Order(int tableNumber, String customerName) {
        this.orderId      = ++counter;
        this.tableNumber  = tableNumber;
        this.customerName = customerName;
        this.items        = new ArrayList<>();
        this.orderTime    = LocalDateTime.now();
        this.status       = "OPEN";
    }

    public int getOrderId()       { return orderId; }
    public int getTableNumber()   { return tableNumber; }
    public String getStatus()     { return status; }
    public List<OrderItem> getItems() { return items; }
    public LocalDateTime getOrderTime() { return orderTime; }

    public void addItem(MenuItem menuItem, int qty) {
        for (OrderItem oi : items) {
            if (oi.getItem().getId() == menuItem.getId()) {
                oi.addQuantity(qty); return;
            }
        }
        items.add(new OrderItem(menuItem, qty));
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }
    public double getTax()     { return getSubtotal() * taxRate; }
    public double getTotal()   { return getSubtotal() + getTax(); }

    public void pay() { this.status = "PAID"; }

    public void printReceipt() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println("\n  ╔══════════════════════════════════════════╗");
        System.out.println("  ║          TINSAE'S CAFE — RECEIPT         ║");
        System.out.println("  ╠══════════════════════════════════════════╣");
        System.out.printf ("  ║  Order #: %-32d║%n", orderId);
        System.out.printf ("  ║  Table  : %-32d║%n", tableNumber);
        System.out.printf ("  ║  Name   : %-32s║%n", customerName);
        System.out.printf ("  ║  Time   : %-32s║%n", orderTime.format(fmt));
        System.out.println("  ╠══════════════════════════════════════════╣");
        System.out.printf ("  ║  %-24s %5s  %8s║%n", "Item", "Qty", "Price");
        System.out.println("  ║  ──────────────────────────────────────  ║");
        for (OrderItem oi : items) {
            System.out.printf("  ║  %-24s x%-4d ETB%7.2f║%n",
                oi.getItem().getName(), oi.getQuantity(), oi.getSubtotal());
        }
        System.out.println("  ╠══════════════════════════════════════════╣");
        System.out.printf ("  ║  Subtotal:                     ETB%7.2f║%n", getSubtotal());
        System.out.printf ("  ║  Tax (15%%):                    ETB%7.2f║%n", getTax());
        System.out.printf ("  ║  TOTAL:                        ETB%7.2f║%n", getTotal());
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println("       Thank you! github.com/Asant444\n");
    }
}

// ─────────────────────────────────────────────
//  CafeSystem
// ─────────────────────────────────────────────
class CafeSystem {
    private List<MenuItem>  menu    = new ArrayList<>();
    private List<Order>     orders  = new ArrayList<>();
    private boolean[]       tables;
    private int             tableCount = 10;
    private Scanner         sc = new Scanner(System.in);

    public CafeSystem() {
        tables = new boolean[tableCount + 1]; // 1-indexed
        loadMenu();
    }

    private void loadMenu() {
        // Coffee & Drinks
        menu.add(new MenuItem(1,  "Espresso",          "Coffee",  35));
        menu.add(new MenuItem(2,  "Macchiato",         "Coffee",  45));
        menu.add(new MenuItem(3,  "Cappuccino",        "Coffee",  55));
        menu.add(new MenuItem(4,  "Latte",             "Coffee",  60));
        menu.add(new MenuItem(5,  "Cold Brew",         "Coffee",  70));
        menu.add(new MenuItem(6,  "Fresh Juice",       "Drinks",  50));
        menu.add(new MenuItem(7,  "Smoothie",          "Drinks",  75));
        menu.add(new MenuItem(8,  "Sparkling Water",   "Drinks",  30));
        // Food
        menu.add(new MenuItem(9,  "Injera Firfir",     "Food",   120));
        menu.add(new MenuItem(10, "Sandwich",          "Food",    90));
        menu.add(new MenuItem(11, "Salad Bowl",        "Food",    85));
        menu.add(new MenuItem(12, "Pastry",            "Food",    45));
        menu.add(new MenuItem(13, "Tiramisu",          "Dessert", 95));
        menu.add(new MenuItem(14, "Cheesecake Slice",  "Dessert", 85));
    }

    // ── Utilities ──────────────────────────────
    private void line(char c, int len) { System.out.println("  " + String.valueOf(c).repeat(len)); }

    private void header() {
        line('═', 50);
        System.out.println("         ☕  TINSAE'S CAFE  ☕");
        System.out.println("         github.com/Asant444");
        line('═', 50);
        System.out.println();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  Please enter a valid number."); }
        }
    }

    private MenuItem findMenuItem(int id) {
        return menu.stream().filter(m -> m.getId()==id).findFirst().orElse(null);
    }

    private Order findOrder(int tableNum) {
        return orders.stream()
            .filter(o -> o.getTableNumber()==tableNum && o.getStatus().equals("OPEN"))
            .findFirst().orElse(null);
    }

    // ── Display Menu ───────────────────────────
    private void displayMenu() {
        System.out.println("\n  ── MENU ───────────────────────────────────────\n");
        String[] cats = {"Coffee","Drinks","Food","Dessert"};
        for (String cat : cats) {
            System.out.println("  " + cat.toUpperCase());
            line('-', 48);
            menu.stream()
                .filter(m -> m.getCategory().equals(cat))
                .forEach(System.out::println);
            System.out.println();
        }
    }

    // ── Table Status ───────────────────────────
    private void displayTables() {
        System.out.print("\n  TABLES:  ");
        for (int i = 1; i <= tableCount; i++) {
            System.out.printf("[%s T%2d] ", tables[i] ? "●" : "○", i);
            if (i % 5 == 0) System.out.print("\n           ");
        }
        System.out.println("\n  ● Occupied  ○ Available\n");
    }

    // ── New Order ──────────────────────────────
    private void newOrder() {
        System.out.println("\n  ── NEW ORDER ──────────────────────────────────");
        displayTables();

        int table = readInt("  Table number (1-" + tableCount + "): ");
        if (table < 1 || table > tableCount) { System.out.println("  Invalid table."); return; }
        if (tables[table]) { System.out.println("  Table " + table + " is already occupied."); return; }

        System.out.print("  Customer name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) name = "Guest";

        Order order = new Order(table, name);
        tables[table] = true;
        orders.add(order);

        System.out.println("\n  Order #" + order.getOrderId() + " created for Table " + table + ".");
        addItemsToOrder(order);
    }

    // ── Add Items ──────────────────────────────
    private void addItemsToOrder(Order order) {
        displayMenu();
        while (true) {
            int id = readInt("  Add item ID (0 to finish): ");
            if (id == 0) break;
            MenuItem item = findMenuItem(id);
            if (item == null)           { System.out.println("  Item not found."); continue; }
            if (!item.isAvailable())    { System.out.println("  Item unavailable."); continue; }
            int qty = readInt("  Quantity: ");
            if (qty <= 0) continue;
            order.addItem(item, qty);
            System.out.printf("  ✔ Added %dx %s — ETB %.2f%n",
                qty, item.getName(), item.getPrice()*qty);
        }
        System.out.println("\n  Items added. Current total: ETB " +
            String.format("%.2f", order.getTotal()));
    }

    // ── View & Add to existing order ───────────
    private void viewOrder() {
        System.out.println("\n  ── VIEW ORDER ─────────────────────────────────");
        int table = readInt("  Enter table number: ");
        Order order = findOrder(table);
        if (order == null) { System.out.println("  No open order for table " + table + "."); return; }

        System.out.println("\n  Order #" + order.getOrderId() + " — Table " + table);
        line('-', 44);
        for (OrderItem oi : order.getItems()) {
            System.out.printf("  %-26s x%d  ETB %.2f%n",
                oi.getItem().getName(), oi.getQuantity(), oi.getSubtotal());
        }
        line('-', 44);
        System.out.printf("  Subtotal: ETB %.2f | Tax: ETB %.2f | Total: ETB %.2f%n%n",
            order.getSubtotal(), order.getTax(), order.getTotal());

        System.out.print("  Add more items? (y/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("y"))
            addItemsToOrder(order);
    }

    // ── Process Payment ────────────────────────
    private void processPayment() {
        System.out.println("\n  ── PAYMENT ────────────────────────────────────");
        int table = readInt("  Enter table number: ");
        Order order = findOrder(table);
        if (order == null) { System.out.println("  No open order for table " + table + "."); return; }

        order.printReceipt();

        double total  = order.getTotal();
        double paid   = 0;
        while (paid < total) {
            System.out.printf("  Amount tendered (need ETB %.2f): ETB ", total);
            try { paid = Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { paid = 0; }
            if (paid < total) System.out.println("  Insufficient amount.");
        }
        System.out.printf("  Change: ETB %.2f%n", paid - total);
        order.pay();
        tables[table] = false;
        System.out.println("  ✔ Payment complete. Table " + table + " is now free.\n");
    }

    // ── Daily Report ───────────────────────────
    private void dailyReport() {
        System.out.println("\n  ── DAILY SALES REPORT ─────────────────────────");
        List<Order> paidOrders = orders.stream()
            .filter(o -> o.getStatus().equals("PAID"))
            .collect(java.util.stream.Collectors.toList());

        if (paidOrders.isEmpty()) { System.out.println("  No completed orders yet.\n"); return; }

        double totalRevenue = 0, totalTax = 0;
        Map<String, Double> catRevenue = new LinkedHashMap<>();

        for (Order o : paidOrders) {
            totalRevenue += o.getTotal();
            totalTax     += o.getTax();
            for (OrderItem oi : o.getItems()) {
                catRevenue.merge(oi.getItem().getCategory(),
                    oi.getSubtotal(), Double::sum);
            }
        }

        line('─', 46);
        System.out.printf("  Orders Completed : %d%n", paidOrders.size());
        System.out.printf("  Total Revenue    : ETB %.2f%n", totalRevenue);
        System.out.printf("  Tax Collected    : ETB %.2f%n", totalTax);
        line('─', 46);
        System.out.println("  Revenue by Category:");
        catRevenue.forEach((cat, rev) ->
            System.out.printf("    %-12s ETB %.2f%n", cat, rev));
        line('─', 46);
        System.out.println();
    }

    // ── Toggle Menu Item ───────────────────────
    private void toggleItem() {
        displayMenu();
        int id = readInt("  Item ID to toggle availability: ");
        MenuItem item = findMenuItem(id);
        if (item == null) { System.out.println("  Item not found."); return; }
        item.setAvailable(!item.isAvailable());
        System.out.println("  ✔ " + item.getName() + " is now " +
            (item.isAvailable() ? "AVAILABLE" : "UNAVAILABLE") + ".\n");
    }

    // ── Main Loop ──────────────────────────────
    public void run() {
        while (true) {
            header();
            displayTables();
            System.out.println("  MENU");
            line('-', 40);
            System.out.println("  1. New Order");
            System.out.println("  2. View / Add to Order");
            System.out.println("  3. Process Payment");
            System.out.println("  4. Daily Sales Report");
            System.out.println("  5. Toggle Item Availability");
            System.out.println("  0. Exit");
            line('-', 40);

            int choice = readInt("  Choice: ");
            switch (choice) {
                case 1: newOrder();        break;
                case 2: viewOrder();       break;
                case 3: processPayment();  break;
                case 4: dailyReport();     break;
                case 5: toggleItem();      break;
                case 0:
                    System.out.println("\n  Goodbye! — Tinsae Ayalew\n");
                    return;
                default:
                    System.out.println("  Invalid option.");
            }
            System.out.print("  Press Enter to continue...");
            sc.nextLine();
        }
    }
}

// ─────────────────────────────────────────────
//  MAIN
// ─────────────────────────────────────────────
public class CafeManagement {
    public static void main(String[] args) {
        new CafeSystem().run();
    }
}

package cbox.assignments;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.*;

import static cbox.assignments.Console.print;
import static cbox.assignments.Helper.currDate;

// Contains methods to simplify small operations.
class Helper {
    public static String currDate(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static String joinBy(String str1, String str2, char joinChar) {
        return str1 + joinChar + str2;
    }

    public static String padStr(String source, int length, char padChar) {
        int num = length - source.length();
        char[] padding = new char[(num > 0) ? num : 0];
        Arrays.fill(padding, padChar);
        return source + String.valueOf(padding);
    }
}

// Simplifies the process of building SQL statements.
class SQLBuilder {
    private StringBuilder query = new StringBuilder();
    @Override
    public String toString() {
        return query.toString();
    }
    public SQLBuilder select(String[] select, String from) {
        query.append("SELECT ");
        for (String cols: select) {
            query.append(cols).append(", ");
        }
        int len = query.length();
        query.replace(len-2, len, " ");
        query.append("FROM ").append(from).append(" ");
        return this;
    }
    public SQLBuilder select(String select, String from) {
        select(new String[]{select}, from);
        return this;
    }
    public SQLBuilder join(String join, String on1, String on2) {
        query.append("INNER JOIN ").append(join).append(" ");
        query.append("ON ").append(on1).append(" = ").append(on2).append(" ");
        return this;
    }
}

// Manages interaction with database.
class DataSource implements AutoCloseable {
    private static final DataSource dataSource = new DataSource();

    private final String DB_NAME = "QACinemas.db";
    private final String CONNECTION_STRING = "jdbc:sqlite:data\\databases\\" + DB_NAME;
    private final Connection conn;

    private enum Tables {
        CUSTOMERS,
        MOVIES,
        SALES,
        SHOWS
    }
    private enum CustTable {
        _ID(1),
        NAME(2),
        AGE(3),
        GENDER(4);

        private int idx;
        CustTable(int idx) {
            this.idx = idx;
        }
        public int getIdx() {
            return idx;
        }
        public String fullyQual() {
            return "CUSTOMERS" + '.' + toString();
        }
    }
    private enum MoviesTable {
        _ID(1),
        NAME(2),
        DURATION(3),
        RELEASE(4);

        private int idx;
        MoviesTable(int idx) {
            this.idx = idx;
        }
        public int getIdx() {
            return idx;
        }
        public String fullyQual() {
            return "MOVIES" + '.' + toString();
        }
    }
    private enum SalesTable {
        _ID(1),
        CUSTOMER_ID(2),
        MOVIE_ID(3),
        DATE(4),
        COST(5),
        SCREEN(6);

        private int idx;
        SalesTable(int idx) {
            this.idx = idx;
        }
        public int getIdx() {
            return idx;
        }
        public String fullyQual() {
            return "SALES" + '.' + toString();
        }
    }
    private enum ShowsTable {
        _ID(1),
        MOVIE_ID(2),
        TIME(3),
        SCREEN(4);

        private int idx;
        ShowsTable(int idx) {
            this.idx = idx;
        }
        public int getIdx() {
            return idx;
        }
        public String fullyQual() {
            return "SHOWS" + '.' + toString();
        }
    }

    // TODO: Can constants be public?
    public static class Customer {
        private int id;
        private String name;
        private int age;
        private String gender;
        @Override
        public String toString() {
            String name = Helper.padStr(getName(), 25, '.');
            return String.format("\t[id]:%-3d [name]:%s [age]:%-3d [gender]:%-6s",
                                 getId(), name, getAge(), getGender());
        }
        public Customer(int id, String name, int age, String gender) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.gender = gender;
        }
        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public int getAge() {
            return age;
        }
        public String getGender() {
            return gender;
        }
    }
    public static class Movie {
        private int id;
        private String name;
        private int duration;
        private String release;
        @Override
        public String toString() {
            String name = Helper.padStr(getName(), 20, '.');
            return String.format("\t[id]:%-3d [name]:%s [dur]:%-3d [rel]:%-10s",
                                 getId(), name, getDuration(), getRelease());
        }
        public Movie(int id, String name, int duration, String release) {
            this.id = id;
            this.name = name;
            this.duration = duration;
            this.release = release;
        }
        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public int getDuration() {
            return duration;
        }
        public String getRelease() {
            return release;
        }
    }
    public static class Sale {
        private int id;
        private String custName;
        private String movieName;
        private String date;
        private double cost;
        private int screen;
        @Override
        public String toString() {
            String name =  Helper.padStr(getCustName(), 25, '.');
            String movie = Helper.padStr(getMovieName(), 25, '.');
            return String.format("\t[id]:%-3d [name]:%s [movie]:%s [date]:%-10s [cost]:%.2f [screen]:%d",
                    getId(), name, movie, getDate(), getCost(), getScreen());
        }
        public Sale(int id, String custName, String movieName, String date, double cost, int screen) {
            this.id = id;
            this.custName = custName;
            this.movieName = movieName;
            this.date = date;
            this.cost = cost;
            this.screen = screen;
        }
        public int getId() {
            return id;
        }
        public String getCustName() {
            return custName;
        }
        public String getMovieName() {
            return movieName;
        }
        public String getDate() {
            return date;
        }
        public double getCost() {
            return cost;
        }
        public int getScreen() {
            return screen;
        }
    }
    public static class Show {
        private int id;
        private String movie;
        private String time;
        private int screen;
        @Override
        public String toString() {
            String name = Helper.padStr(getMovie(), 25, '.');
            return String.format("\t[id]:%-3d [name]:%s [dur]:%-5s [screen]:%d",
                    getId(), name, getTime(), getScreen());
        }
        public Show(int id, String movie, String time, int screen) {
            this.id = id;
            this.movie = movie;
            this.time = time;
            this.screen = screen;
        }
        public int getId() {
            return id;
        }
        public String getMovie() {
            return movie;
        }
        public String getTime() {
            return time;
        }
        public int getScreen() {
            return screen;
        }
    }
    public static class ShowConcise {
        private String time;
        private int screen;
        @Override
        public String toString() {
            return "[Time: " + time + ", Screen: " + screen + "]";
        }
        public ShowConcise(String time, int screen) {
            this.time = time;
            this.screen = screen;
        }
        public String getTime() {
            return time;
        }
        public int getScreen() {
            return screen;
        }
    }

    // Singleton: Only need one instance of this class.
    private DataSource() {
        Connection tmpConn = null;
        while(tmpConn == null) {
            try {
                tmpConn = DriverManager.getConnection(CONNECTION_STRING);
            } catch(SQLException e) {
                print("Couldn't connect to " + DB_NAME + " database: " + e.getMessage());
                print("Trying again in three seconds...");
                try {
                    Thread.sleep(3000);
                } catch(InterruptedException ie) {}
            }
        }
        conn = tmpConn;
    }

    public static DataSource get() {
        return dataSource;
    }

    public List<Customer> getCustomers() {
        List<Customer> customers = new LinkedList<>();
        String query = new SQLBuilder().select("*", Tables.CUSTOMERS.toString()).toString();
        Logging.get().print(query);
        try(Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(query)) {
            while(res.next()) {
                int id = res.getInt(CustTable._ID.getIdx());
                String name = res.getString(CustTable.NAME.getIdx());
                int age = res.getInt(CustTable.AGE.getIdx());
                String gender = res.getString(CustTable.GENDER.getIdx());
                customers.add(new Customer(id, name, age, gender));
            }
        } catch(SQLException e) {
            print("Couldn't create statement/results: " + e.getMessage());
        }
        return customers;
    }

    public List<Movie> getMovies() {
        List<Movie> movies = new LinkedList<>();
        String query = new SQLBuilder().select("*", Tables.MOVIES.toString()).toString();
        Logging.get().print(query);
        try(Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(query)) {
            while(res.next()) {
                int id = res.getInt(MoviesTable._ID.getIdx());
                String name = res.getString(MoviesTable.NAME.getIdx());
                int duration = res.getInt(MoviesTable.DURATION.getIdx());
                String release = res.getString(MoviesTable.RELEASE.getIdx());
                movies.add(new Movie(id, name, duration, release));
            }
        } catch(SQLException e) {
            print("Couldn't create statement/results: " + e.getMessage());
        }
        return movies;
    }

    public Map<Integer, String> getMoviesConcise() {
        Map<Integer, String> moviesConcise = new HashMap<>();
        List<Movie> movies = getMovies();
        for(Movie movie: movies) {
            moviesConcise.put(moviesConcise.size()+1, movie.name);
        }
        return moviesConcise;
    }

    public List<Sale> getSales() {
        List<Sale> sales = new LinkedList<>();
        String[] cols = new String[]{SalesTable._ID.fullyQual(), CustTable.NAME.fullyQual(),
                                     MoviesTable.NAME.fullyQual(), SalesTable.DATE.fullyQual(),
                                     SalesTable.COST.fullyQual(), SalesTable.SCREEN.fullyQual()};
        String query = new SQLBuilder()
                .select(cols, Tables.SALES.toString())
                .join(Tables.CUSTOMERS.toString(),
                      SalesTable.CUSTOMER_ID.fullyQual(), CustTable._ID.fullyQual())
                .join(Tables.MOVIES.toString(),
                      SalesTable.MOVIE_ID.fullyQual(), MoviesTable._ID.fullyQual())
                .toString();
        Logging.get().print(query);
        try(Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(query)) {
            while(res.next()) {
                int id = res.getInt(1);
                String custName = res.getString(2);
                String movieName = res.getString(3);
                String date = res.getString(4);
                double cost = res.getDouble(5);
                int screen = res.getInt(6);
                sales.add(new Sale(id, custName, movieName, date, cost, screen));
            }
        } catch(SQLException e) {
            print("Couldn't create statement/results: " + e.getMessage());
        }
        return sales;
    }

    public List<Show> getShows() {
        List<Show> shows = new LinkedList<>();
        String[] cols = new String[]{ShowsTable._ID.fullyQual(), MoviesTable.NAME.fullyQual(),
                                     ShowsTable.TIME.fullyQual(), ShowsTable.SCREEN.fullyQual()};
        String query = new SQLBuilder()
                .select(cols, Tables.SHOWS.toString())
                .join(Tables.MOVIES.toString(),
                      ShowsTable.MOVIE_ID.fullyQual(), MoviesTable._ID.fullyQual())
                .toString();
        Logging.get().print(query);
        try(Statement statement = conn.createStatement();
            ResultSet res = statement.executeQuery(query)) {
            while(res.next()) {
                int id = res.getInt(1);
                String movie = res.getString(2);
                String time = res.getString(3);
                int screen = res.getInt(4);
                shows.add(new Show(id, movie, time, screen));
            }
        } catch(SQLException e) {
            print("Couldn't create statement/results: " + e.getMessage());
        }
        return shows;
    }

    public Map<Integer, ShowConcise> getShowsConcise(String movie) {
        Map<Integer, ShowConcise> showsConcise = new HashMap<>();
        List<Show> shows = getShows();
        for(Show show: shows) {
            if(show.getMovie().equals(movie)) {
                showsConcise.put(showsConcise.size()+1,
                                 new ShowConcise(show.getTime(), show.getScreen()));
            }
        }
        return showsConcise;
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch(SQLException e) {
            print("Couldn't close " + DB_NAME + " database: " + e.getMessage());
        }
    }
}

// Logs console output to file for historical/debugging purposes.
class Logging implements AutoCloseable {
    private static final Logging inst = new Logging();

    private final Logger logger;
    private FileHandler fileHandler;

    private Logging() {
        logger = Logger.getLogger("QA Cinemas");

        // Stop logging to console.
        final Logger rootLogger = Logger.getLogger("");
        final Handler handler = rootLogger.getHandlers()[0];
        if (handler instanceof ConsoleHandler) {
            rootLogger.removeHandler(handler);
        }

        // Output everything to log.
        logger.setLevel(Level.ALL);

        // Determine date/time for file name.
        final String dt = currDate("yyyy-MM-dd HH-mm-ss");
        try {
            // Create logging file.
            final String path = "data\\logs\\QA Cinemas (" + dt + ").txt";
            final File file = new File(path);
            file.createNewFile();

            // Complete setup.
            fileHandler = new FileHandler(path);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch(IOException e) {
            Console.print("Couldn't create file or FileHandler: " + e.getMessage());
        }
    }

    public static Logging get() {
        return inst;
    }

    public void print(String str) {
        logger.info(str);
    }

    @Override
    public void close() {
        fileHandler.close();
    }
}

// Manages interaction with program through the console.
class Console {
    private static final String[] OPTIONS_STRS;
    private static final Scanner sc;

    static {
        OPTIONS_STRS = new String[]{"Enter 1: For help.",
                                   "Enter 2: To add a customer booking.",
                                   "Enter 3: To view customers.",
                                   "Enter 4: To view movies currently being shown.",
                                   "Enter 5: To view showing times.",
                                   "Enter 6: To view sales history.",
                                   "Enter 7: To exit."};
        sc = new Scanner(System.in);
    }

    public static int getOptionsLength() {
        return OPTIONS_STRS.length;
    }

    public static void help() {
        for(String option: OPTIONS_STRS) {
            print(option);
        }
    }

    public static void print(String str) {
        print(str, true);
    }

    public static void print(String str, boolean newLine) {
        if (newLine) {
            str += '\n';
        }
        System.out.print(str);
        Logging.get().print(str);
    }

    public static int getInt(String msg, int max) {
        int choice = -1;
        while(choice == -1) {
            print(msg, false);
            String input = sc.nextLine();
            print("You entered: " + input + ". ", false);
            if(input.matches("[0-9]+")) {
                choice = Integer.valueOf(input);
                choice = (choice >= 1 && choice <= max) ? choice : -1;
            }
            if(choice < 1 || choice > max) {
                print("This is invalid.", false);
            }
            System.out.println();
        }

        return choice;
    }

    public static String getString(String msg) {
        String str = null;
        while(str == null) {
            print(msg, false);
            String input = sc.nextLine();
            print("You entered: " + input + ". ", false);
            if(!input.isEmpty() && input.matches("[a-zA-Z]+")) {
                str = input;
            } else {
                print("This is invalid.", false);
            }
            System.out.println();
        }
        return str.trim().toLowerCase();
    }
}

public class QACinemas {

    public enum TicketType {
        STANDARD(8),
        OAP(6),
        STUDENT(6),
        CHILD(4);

        private double cost;
        TicketType(double cost) {
            this.cost = cost;
        }
        public double getCost() {
            return cost;
        }
        public static boolean contains(String str) {
            for(TicketType type: values()) {
                if(type.toString().equals(str)) {
                    return true;
                }
            }
            return false;
        }
        public static String allToString() {
            StringBuilder sb = new StringBuilder();
            for(TicketType tt: TicketType.values()) {
                sb.append(tt.toString()).append(" ");
            }
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        }
    }
    public static class Transaction {
        private int customers;
        private String customerName;
        private String movieName;
        private String date;
        private String time;
        private int screen;
        private double cost;
        public Transaction(int customers, String customerName, String movieName, String date,
                           String time, int screen, double cost) {
            this.customers = customers;
            this.customerName = customerName;
            this.movieName = movieName;
            this.date = date;
            this.time = time;
            this.screen = screen;
            this.cost = cost;
        }
        public int getCustomers() {
            return customers;
        }
        public String getCustomerName() {
            return customerName;
        }
        public String getMovieName() {
            return movieName;
        }
        public String getDate() {
            return date;
        }
        public double getCost() {
            return cost;
        }
        public int getScreen() {
            return screen;
        }
    }

    public void exec() {
        print("Welcome to QA Cinema's ticket booking system.");
        System.out.println();
        Console.help();
        System.out.println();

        loop();
    }

    private void loop() {
        boolean quit = false;
        while(!quit) {
            int choice = Console.getInt("Enter your choice: ", Console.getOptionsLength());
            switch(choice) {
                case 1:
                    Console.help();
                    break;
                case 2:
                    addCustomer();
                    break;
                case 3:
                    viewCustomers();
                    break;
                case 4:
                    viewMovies();
                    break;
                case 5:
                    viewShows();
                    break;
                case 6:
                    viewSales();
                    break;
                case 7:
                    exit();
                    quit = true;
                    break;
            }
            System.out.println();
        }
    }

    public String getMovieChoice() {
        print("The following movies are available: ");
        Map<Integer, String> moviesMap = DataSource.get().getMoviesConcise();
        print(moviesMap.toString());
        int choice = Console.getInt("Enter the index of the movie: ", moviesMap.size());
        return moviesMap.get(choice);
    }

    public DataSource.ShowConcise getShowTimeChoice(String movie) {
        print("The following show times are available: ");
        Map<Integer, DataSource.ShowConcise> showsMap;
        showsMap = DataSource.get().getShowsConcise(movie);
        print(showsMap.toString());
        int choice = Console.getInt("Enter the index for the show time: ", showsMap.size());
        return showsMap.get(choice);
    }

    public double getCost(int customerCount) {
        double cost = 0;
        for(int i = 1; i <= customerCount; i++) {
            String ticketStr = null;
            print("The following ticket types are available: ");
            print(TicketType.allToString());
            while(!TicketType.contains(ticketStr)) {
                ticketStr = Console.getString("What ticket type does customer "
                                              + i + "/" + customerCount + " require? ");
            }
            cost += TicketType.valueOf(ticketStr).getCost();
        }
        return cost;
    }

    public boolean isValidTransaction(Transaction trans) {
        print("The transaction details are as follows: ");
        trans.toString();
        System.out.println();

        String confirm = Console.getString("Is this correct (y/n)? ");
        return confirm.equals('y');
    }

    // Can store customer details for data analytics or to create memberships.
    // Specify customer name as null when making a sale to indicate a group sale.
    private void addCustomer() {
        int custCount = Console.getInt("Enter number of customers: ", 10);
        System.out.println();

        String custName = (custCount == 1) ? Console.getString("Enter customer name: ") : null;
        System.out.println();

        String movieName = getMovieChoice();
        System.out.println();

        DataSource.ShowConcise showConcise = getShowTimeChoice(movieName);
        System.out.println();
        String time = showConcise.getTime();
        int screen = showConcise.getScreen();

        String date = currDate("dd/mm/yyyy");

        double cost = getCost(custCount);

        Transaction trans = new Transaction(
                custCount, custName, movieName, date, time, screen, cost);
        if(isValidTransaction(trans)) {
            processTransaction(trans);
            print("Sale is complete, printing tickets...");
        }
    }

    private void viewCustomers() {
        print("The following customers have previously bought tickets: ");

        List<DataSource.Customer> customers = DataSource.get().getCustomers();
        for(DataSource.Customer c: customers) {
            print(c.toString());
        }
    }

    private void viewMovies() {
        print("The following movies are being shown: ");

        List<DataSource.Movie> movies = DataSource.get().getMovies();
        for(DataSource.Movie m: movies) {
            print(m.toString());
        }
    }

    private void viewShows() {
        print("The show times are as follows: ");

        List<DataSource.Show> shows = DataSource.get().getShows();
        for(DataSource.Show s: shows) {
            print(s.toString());
        }
    }

    private void viewSales() {
        print("The sales history is as follows: ");

        List<DataSource.Sale> sales = DataSource.get().getSales();
        for(DataSource.Sale s: sales) {
            print(s.toString());
        }
    }

    private void exit() {
        print("Thank you for using QA Cinema's ticket booking system.");
        Logging.get().close();
    }
}

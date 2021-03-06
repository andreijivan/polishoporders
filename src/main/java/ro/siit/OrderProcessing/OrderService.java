package ro.siit.OrderProcessing;

import ro.siit.OrderDetails.DisplayedOrder;
import ro.siit.OrderDetails.SoldItem;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderService {

    private Connection connection;

    public List<DisplayedOrder> getAllOrders() {
        List<DisplayedOrder> orders = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM poliorders");
            ResultSet rs = ps.executeQuery();

            prepareDisplayedOrder(orders, ps, rs);

        } catch (Exception throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        orders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return orders;
    }

    public List<DisplayedOrder> getAllOrdersEuro() {
        List<DisplayedOrder> ordersEuro = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM poliorderseuro");
            ResultSet rs = ps.executeQuery();

            prepareDisplayedOrder(ordersEuro, ps, rs);

        } catch (Exception throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        ordersEuro.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return ordersEuro;
    }

    public List<DisplayedOrder> displayFinalizedCashOrders() throws ParseException {
        List<DisplayedOrder> finalizedCashOrders = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM comenzifinalizatecash");
            displayOrderList(finalizedCashOrders, ps);
            ps.close();
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        Date compareDate = cal.getTime();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            for (DisplayedOrder archivedOrder : finalizedCashOrders) {
                Date archiveDate = new SimpleDateFormat("yyyy-MM-dd").parse(archivedOrder.getDataComanda());
                if (archiveDate.compareTo(compareDate) < 0) {
                    PreparedStatement qs = connection.prepareStatement("INSERT INTO arhiva SELECT * from comenzifinalizatecash WHERE cod_comanda = ?");
                    qs.setInt(1, archivedOrder.getCodComanda());
                    qs.executeUpdate();
                    qs.close();
                    PreparedStatement zs = connection.prepareStatement("DELETE FROM comenzifinalizatecash WHERE cod_comanda = ?");
                    zs.setInt(1, archivedOrder.getCodComanda());
                    zs.executeUpdate();
                    zs.close();
                }
            }
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        finalizedCashOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return finalizedCashOrders;
    }

    public List<DisplayedOrder> displayFinalizedBankOrders() throws ParseException {
        List<DisplayedOrder> finalizedBankOrders = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM comenzifinalizatebanca");
            displayOrderList(finalizedBankOrders, ps);
            ps.close();
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) {  /* ignored */ }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        Date compareDate = cal.getTime();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            for (DisplayedOrder archivedOrder : finalizedBankOrders) {
                Date archiveDate = new SimpleDateFormat("yyyy-MM-dd").parse(archivedOrder.getDataComanda());
                if (archiveDate.compareTo(compareDate) < 0) {
                    PreparedStatement qs = connection.prepareStatement("INSERT INTO arhiva SELECT * from comenzifinalizatebanca WHERE cod_comanda = ?");
                    qs.setInt(1, archivedOrder.getCodComanda());
                    qs.executeUpdate();
                    qs.close();
                    PreparedStatement zs = connection.prepareStatement("DELETE FROM comenzifinalizatebanca WHERE cod_comanda = ?");
                    zs.setInt(1, archivedOrder.getCodComanda());
                    zs.executeUpdate();
                    zs.close();
                }
            }
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }

        finalizedBankOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return finalizedBankOrders;
    }

    public List<DisplayedOrder> displayFinalizedCardOrders() {
        List<DisplayedOrder> finalizedCardOrders = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM comenzifinalizatecard");
            displayOrderList(finalizedCardOrders, ps);
            ps.close();
        } catch (Exception throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        Date compareDate = cal.getTime();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            for (DisplayedOrder archivedOrder : finalizedCardOrders) {
                Date archiveDate = new SimpleDateFormat("yyyy-MM-dd").parse(archivedOrder.getDataComanda());
                if (archiveDate.compareTo(compareDate) < 0) {
                    PreparedStatement qs = connection.prepareStatement("INSERT INTO arhiva SELECT * from comenzifinalizatecard WHERE cod_comanda = ?");
                    qs.setInt(1, archivedOrder.getCodComanda());
                    qs.executeUpdate();
                    qs.close();
                    PreparedStatement zs = connection.prepareStatement("DELETE FROM comenzifinalizatecard WHERE cod_comanda = ?");
                    zs.setInt(1, archivedOrder.getCodComanda());
                    zs.executeUpdate();
                    zs.close();
                }
            }
        } catch (SQLException | ClassNotFoundException | ParseException throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        finalizedCardOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return finalizedCardOrders;
    }

    public List<DisplayedOrder> displayVirtualOrders() {
        List<DisplayedOrder> virtualOrders = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM produsevirtuale");
            displayOrderList(virtualOrders, ps);
            ps.close();
        } catch (Exception throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        Date compareDate = cal.getTime();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            for (DisplayedOrder archivedVirtualOrder : virtualOrders) {
                Date archiveDate = new SimpleDateFormat("yyyy-MM-dd").parse(archivedVirtualOrder.getDataComanda());
                if (archiveDate.compareTo(compareDate) < 0) {
                    PreparedStatement qs = connection.prepareStatement("INSERT INTO arhiva SELECT * from produsevirtuale WHERE cod_comanda = ?");
                    qs.setInt(1, archivedVirtualOrder.getCodComanda());
                    qs.executeUpdate();
                    qs.close();
                    PreparedStatement zs = connection.prepareStatement("DELETE FROM produsevirtuale WHERE cod_comanda = ?");
                    zs.setInt(1, archivedVirtualOrder.getCodComanda());
                    zs.executeUpdate();
                    zs.close();
                }
            }
        } catch (SQLException | ClassNotFoundException | ParseException throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        virtualOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return virtualOrders;
    }

    public List<DisplayedOrder> displayLocalOrders() {
        List<DisplayedOrder> localOrders = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM poliorders");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString(1);
                int nr = rs.getInt(2);
                int codComanda = rs.getInt(3);
                String dataComanda = rs.getString(4);
                String client = rs.getString(5);
                String produse = rs.getString(6);
                String adresa = rs.getString(7);
                String localitate = rs.getString(8);
                String codPostal = rs.getString(9);
                String tara = rs.getString(10);
                String telefon = rs.getString(11);
                String email = rs.getString(12);
                String observatii = rs.getString(13);
                int valoareProduse = rs.getInt(14);
                String incasat = rs.getString(15);
                String state = rs.getString(16);
                String valoareLivrare = rs.getString(17);

                DisplayedOrder checkOrder = new DisplayedOrder(status, nr, codComanda, dataComanda, client, produse, adresa, localitate, codPostal, tara, telefon, email, observatii, valoareProduse, incasat, state, valoareLivrare);
                if (checkOrder.getLocalitate().contains("TM")) {
                    localOrders.add(new DisplayedOrder(status, nr, codComanda, dataComanda, client, produse, adresa, localitate, codPostal, tara, telefon, email, observatii, valoareProduse, incasat, state, valoareLivrare));
                }
            }
            ps.close();
        } catch (Exception throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        localOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return localOrders;
    }

    public List<DisplayedOrder> displayNationalOrders() {
        List<DisplayedOrder> nationalOrders = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM poliorders");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString(1);
                int nr = rs.getInt(2);
                int codComanda = rs.getInt(3);
                String dataComanda = rs.getString(4);
                String client = rs.getString(5);
                String produse = rs.getString(6);
                String adresa = rs.getString(7);
                String localitate = rs.getString(8);
                String codPostal = rs.getString(9);
                String tara = rs.getString(10);
                String telefon = rs.getString(11);
                String email = rs.getString(12);
                String observatii = rs.getString(13);
                int valoareProduse = rs.getInt(14);
                String incasat = rs.getString(15);
                String state = rs.getString(16);
                String valoareLivrare = rs.getString(17);

                DisplayedOrder checkOrder = new DisplayedOrder(status, nr, codComanda, dataComanda, client, produse, adresa, localitate, codPostal, tara, telefon, email, observatii, valoareProduse, incasat, state, valoareLivrare);
                if (checkOrder.getTara().equals("RO") && !checkOrder.getLocalitate().contains("TM")) {
                    nationalOrders.add(new DisplayedOrder(status, nr, codComanda, dataComanda, client, produse, adresa, localitate, codPostal, tara, telefon, email, observatii, valoareProduse, incasat, state, valoareLivrare));
                }
            }
            ps.close();
        } catch (Exception throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        nationalOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return nationalOrders;
    }

    public List<DisplayedOrder> displayInternationalOrders() {
        List<DisplayedOrder> internationalOrders = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM poliorders");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString(1);
                int nr = rs.getInt(2);
                int codComanda = rs.getInt(3);
                String dataComanda = rs.getString(4);
                String client = rs.getString(5);
                String produse = rs.getString(6);
                String adresa = rs.getString(7);
                String localitate = rs.getString(8);
                String codPostal = rs.getString(9);
                String tara = rs.getString(10);
                String telefon = rs.getString(11);
                String email = rs.getString(12);
                String observatii = rs.getString(13);
                int valoareProduse = rs.getInt(14);
                String incasat = rs.getString(15);
                String state = rs.getString(16);
                String valoareLivrare = rs.getString(17);

                DisplayedOrder checkOrder = new DisplayedOrder(status, nr, codComanda, dataComanda, client, produse, adresa, localitate, codPostal, tara, telefon, email, observatii, valoareProduse, incasat, state, valoareLivrare);
                if (!checkOrder.getTara().equals("RO")) {
                    internationalOrders.add(new DisplayedOrder(status, nr, codComanda, dataComanda, client, produse, adresa, localitate, codPostal, tara, telefon, email, observatii, valoareProduse, incasat, state, valoareLivrare));
                }
            }
            ps.close();
        } catch (Exception throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        internationalOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return internationalOrders;
    }

    public List<DisplayedOrder> orderExists(String nameOrCode) {
        OrderService orderService = new OrderService();
        List<DisplayedOrder> foundOrders = new ArrayList<>();

        List<DisplayedOrder> allOrders = orderService.getAllOrders();
        if (nameOrCode.matches("[0-9]+")) {

            for (DisplayedOrder order : allOrders) {
                if (order.getCodComanda() == Integer.parseInt(nameOrCode)) {
                    foundOrders.add(order);
                }
            }
            List<DisplayedOrder> totalFinalizedOrders = orderService.getTotalRevenue();
            for (DisplayedOrder finalizedOrder : totalFinalizedOrders) {
                if (finalizedOrder.getCodComanda() == Integer.parseInt(nameOrCode)) {
                    foundOrders.add(finalizedOrder);
                }
            }
            List<DisplayedOrder> virtualOrders = orderService.displayVirtualOrders();
            for (DisplayedOrder virtualOrder : virtualOrders) {
                if (virtualOrder.getCodComanda() == Integer.parseInt(nameOrCode)) {
                    foundOrders.add(virtualOrder);
                }
            }
            List<DisplayedOrder> deletedOrders = orderService.getDeletedOrders();
            for (DisplayedOrder deletedOrder : deletedOrders) {
                if (deletedOrder.getCodComanda() == Integer.parseInt(nameOrCode)) {
                    foundOrders.add(deletedOrder);
                }
               /* List<DisplayedOrder> euroOrders = orderService.getAllOrdersEuro();
                for (DisplayedOrder euroOrder : euroOrders) {
                    if (euroOrder.getCodComanda() == Integer.parseInt(nameOrCode)) {
                        foundOrders.add(euroOrder);
                    }
                }*/
            }
        } else {
            for (DisplayedOrder order : allOrders) {
                if (order.getClient().toLowerCase().contains(nameOrCode.toLowerCase())) {
                    foundOrders.add(order);
                }
            }
            List<DisplayedOrder> totalFinalizedOrders = orderService.getTotalRevenue();
            for (DisplayedOrder finalizedOrder : totalFinalizedOrders) {
                if (finalizedOrder.getClient().toLowerCase().contains(nameOrCode.toLowerCase())) {
                    foundOrders.add(finalizedOrder);
                }
            }
            List<DisplayedOrder> virtualOrders = orderService.displayVirtualOrders();
            for (DisplayedOrder virtualOrder : virtualOrders) {
                if (virtualOrder.getClient().toLowerCase().contains(nameOrCode.toLowerCase())) {
                    foundOrders.add(virtualOrder);
                }
            }
            List<DisplayedOrder> deletedOrders = orderService.getDeletedOrders();
            for (DisplayedOrder deletedOrder : deletedOrders) {
                if (deletedOrder.getClient().toLowerCase().contains(nameOrCode.toLowerCase())) {
                    foundOrders.add(deletedOrder);
                }
            }
         /*   List<DisplayedOrder> euroOrders = orderService.getAllOrdersEuro();
            for (DisplayedOrder euroOrder : deletedOrders) {
                if (euroOrder.getClient().toLowerCase().contains(nameOrCode.toLowerCase())) {
                    foundOrders.add(euroOrder);
                }
            }*/
        }
        foundOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return foundOrders;
    }

    public List<DisplayedOrder> getDeletedOrders() {
        List<DisplayedOrder> deletedOrders = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM comenzianulate");
            ResultSet rs = ps.executeQuery();

            prepareDisplayedOrder(deletedOrders, ps, rs);

        } catch (Exception throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        deletedOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return deletedOrders;
    }

    public List<DisplayedOrder> getArchive() {
        List<DisplayedOrder> archivedOrders = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM arhiva");
            ResultSet rs = ps.executeQuery();

            prepareDisplayedOrder(archivedOrders, ps, rs);

        } catch (Exception throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        archivedOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return archivedOrders;
    }

    public List<DisplayedOrder> getTotalRevenue() {
        List<DisplayedOrder> totalRevenueOrders = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM comenzifinalizatecash");
            displayOrderList(totalRevenueOrders, ps);
            ps.close();
            PreparedStatement qs = connection.prepareStatement("SELECT * FROM comenzifinalizatecard");
            displayOrderList(totalRevenueOrders, qs);
            qs.close();
            PreparedStatement zs = connection.prepareStatement("SELECT * FROM comenzifinalizatebanca");
            displayOrderList(totalRevenueOrders, zs);
            zs.close();
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) { /* ignored */ }
        }
        totalRevenueOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda).reversed());
        return totalRevenueOrders;
    }

    public Map<String, Integer> centralizedResults(Date begin, Date end) throws ParseException {

        List<DisplayedOrder> intervalOrders = getIntervalOrders(begin, end);

        int materialeCard = 0;
        int materialeTB = 0;
        int materialeGLS = 0;
        int materialeCash = 0;
        int materialeTotal;

        int carduriCard = 0;
        int carduriTB = 0;
        int carduriGLS = 0;
        int carduriCash = 0;
        int carduriTotal;

        int donatieCard = 0;
        int donatieTB = 0;
        int donatieGLS = 0;
        int donatieCash = 0;
        int donatieTotal;

        int biletVirtualCard = 0;
        int biletVirtualTB = 0;
        int biletVirtualGLS = 0;
        int biletVirtualCash = 0;
        int biletVirtualTotal;

        int transportCard = 0;
        int transportTB = 0;
        int transportGLS = 0;
        int transportTotal;

        for (DisplayedOrder order : intervalOrders) {
            List<String> items = convertProductsString(order);
            /*for (String item : items) {
                System.out.println(item);
            }*/
            for (String produse : items) {
                Matcher totalMatcher = Pattern.compile("\\d+ lei").matcher(produse);
                Matcher totalMatcherNoSpace = Pattern.compile("\\d+lei").matcher(produse);
                if (order.getStatus().contains("Achitat online CARD")) {
                    if (totalMatcher.find()) {
                        if (produse.contains("Bilet virtual")) {
                            biletVirtualCard += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Cutia virtual")) {
                            donatieCard += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Card de membru oficial")) {
                            carduriCard += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else {
                            materialeCard += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        }
                    } else if (totalMatcherNoSpace.find()) {
                        if (produse.contains("Bilet virtual")) {
                            biletVirtualCard += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Cutia virtual")) {
                            donatieCard += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Card de membru oficial")) {
                            carduriCard += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else {
                            materialeCard += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        }
                    }
                    transportCard += Integer.parseInt(order.getValoareLivrare());
                } else if (order.getStatus().contains("Achitat online TRANSFER BANCAR.")) {
                    if (totalMatcher.find()) {
                        if (produse.contains("Bilet virtual")) {
                            biletVirtualTB += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Cutia virtual")) {
                            donatieTB += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Card de membru oficial")) {
                            carduriTB += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else {
                            materialeTB += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        }
                    } else if (totalMatcherNoSpace.find()) {
                        if (produse.contains("Bilet virtual")) {
                            biletVirtualTB += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Cutia virtual")) {
                            donatieTB += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Card de membru oficial")) {
                            carduriTB += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else {
                            materialeTB += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        }
                    }
                    transportTB += Integer.parseInt(order.getValoareLivrare());
                } else if (order.getStatus().contains("Plata ramburs. Livrare curier")) {
                    if (totalMatcher.find()) {
                        if (produse.contains("Bilet virtual")) {
                            biletVirtualGLS += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Cutia virtual")) {
                            donatieGLS += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Card de membru oficial")) {
                            carduriGLS += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else {
                            materialeGLS += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        }
                    } else if (totalMatcherNoSpace.find()) {
                        if (produse.contains("Bilet virtual")) {
                            biletVirtualGLS += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Cutia virtual")) {
                            donatieGLS += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Card de membru oficial")) {
                            carduriGLS += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else {
                            materialeGLS += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        }
                    }
                    transportGLS += Integer.parseInt(order.getValoareLivrare());
                } else if (order.getStatus().contains("Plata ramburs. Ridicare personala din magazin.")) {
                    if (totalMatcher.find()) {
                        if (produse.contains("Bilet virtual")) {
                            biletVirtualCash += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Cutia virtual")) {
                            donatieCash += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Card de membru oficial")) {
                            carduriCash += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        } else {
                            materialeCash += Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                        }
                    } else if (totalMatcherNoSpace.find()) {
                        if (produse.contains("Bilet virtual")) {
                            biletVirtualCash += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Cutia virtual")) {
                            donatieCash += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else if (produse.contains("Card de membru oficial")) {
                            carduriCash += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        } else {
                            materialeCash += Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                        }
                    }
                }
            }
        }
        materialeTotal = materialeCard + materialeTB + materialeGLS + materialeCash;
        carduriTotal = carduriCard + carduriTB + carduriGLS + carduriCash;
        donatieTotal = donatieCard + donatieTB + donatieGLS + donatieCash;
        biletVirtualTotal = biletVirtualCard + biletVirtualTB + biletVirtualGLS + biletVirtualCash;
        transportTotal = transportCard + transportTB + transportGLS;

        int subTotalCard = materialeCard + carduriCard + donatieCard + biletVirtualCard;
        int subTotalTB = materialeTB + carduriTB + donatieTB + biletVirtualTB;
        int subTotalGLS = materialeGLS + carduriGLS + donatieGLS + biletVirtualGLS;
        int subTotalCash = materialeCash + carduriCash + donatieCash + biletVirtualCash;
        int subTotalTotal = subTotalCard + subTotalTB + subTotalGLS + subTotalCash;
        int TotalTotal = materialeTotal + carduriTotal + donatieTotal + biletVirtualTotal + transportTotal;

        Map<String, Integer> finalResults = new HashMap<>();
        finalResults.put("materialeCard", materialeCard);
        finalResults.put("materialeTB", materialeTB);
        finalResults.put("materialeGLS", materialeGLS);
        finalResults.put("materialeCash", materialeCash);
        finalResults.put("materialeTotal", materialeTotal);

        finalResults.put("carduriCard", carduriCard);
        finalResults.put("carduriTB", carduriTB);
        finalResults.put("carduriGLS", carduriGLS);
        finalResults.put("carduriCash", carduriCash);
        finalResults.put("carduriTotal", carduriTotal);

        finalResults.put("donatieCard", donatieCard);
        finalResults.put("donatieTB", donatieTB);
        finalResults.put("donatieGLS", donatieGLS);
        finalResults.put("donatieCash", donatieCash);
        finalResults.put("donatieTotal", donatieTotal);

        finalResults.put("biletVirtualCard", biletVirtualCard);
        finalResults.put("biletVirtualTB", biletVirtualTB);
        finalResults.put("biletVirtualGLS", biletVirtualGLS);
        finalResults.put("biletVirtualCash", biletVirtualCash);
        finalResults.put("biletVirtualTotal", biletVirtualTotal);

        finalResults.put("subTotalCard", subTotalCard);
        finalResults.put("subTotalTB", subTotalTB);
        finalResults.put("subTotalGLS", subTotalGLS);
        finalResults.put("subTotalCash", subTotalCash);
        finalResults.put("subTotalTotal", subTotalTotal);

        finalResults.put("transportCard", transportCard);
        finalResults.put("transportTB", transportTB);
        finalResults.put("transportGLS", transportGLS);
        finalResults.put("transportTotal", transportTotal);

        finalResults.put("TOTALCARD", subTotalCard + transportCard);
        finalResults.put("TOTALTB", subTotalTB + transportTB);
        finalResults.put("TOTALGLS", subTotalGLS + transportGLS);
        finalResults.put("TOTALCASH", subTotalCash);
        finalResults.put("TOTALTOTAL", TotalTotal);

       /* for (Map.Entry<String, Integer> entry : finalResults.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }*/

        return finalResults;
    }

    public List<SoldItem> soldProducts(Date begin, Date end) throws ParseException {

        List<SoldItem> soldProducts = new ArrayList<>();
        List<DisplayedOrder> intervalOrders = getIntervalOrders(begin, end);


        for (DisplayedOrder order : intervalOrders) {
            List<String> items = convertProductsString(order);
            for (String products : items) {
                Matcher matcherQuantity = Pattern.compile("\\d+").matcher(products);
                Matcher totalMatcher = Pattern.compile("\\d+ lei").matcher(products);
                Matcher totalMatcherNoSpace = Pattern.compile("\\d+lei").matcher(products);

                if (matcherQuantity.find() && totalMatcher.find()) {
                    int quantity = Integer.parseInt(matcherQuantity.group());
                    int price = Integer.parseInt(String.valueOf(totalMatcher.group()).replaceAll("[^0-9]", ""));
                    String productOnly = products.replaceAll("^\\d+ x ([^(]*)\\([^()]+\\)", "$1");
                    if (productOnly.endsWith("|| ")) productOnly = productOnly.substring(0, productOnly.length() - 3);
                    else if (productOnly.endsWith("||"))
                        productOnly = productOnly.substring(0, productOnly.length() - 2);
                    soldProducts.add(new SoldItem(productOnly, quantity, price));
                } else if (matcherQuantity.find() && totalMatcherNoSpace.find()) {
                    int quantity = Integer.parseInt(matcherQuantity.group());
                    int price = Integer.parseInt(String.valueOf(totalMatcherNoSpace.group()).replaceAll("[^0-9]", ""));
                    String productOnly = products.replaceAll("^\\d+ x ([^(]*)\\([^()]+\\)", "$1");
                    if (productOnly.endsWith("|| ")) productOnly = productOnly.substring(0, productOnly.length() - 3);
                    else if (productOnly.endsWith("||"))
                        productOnly = productOnly.substring(0, productOnly.length() - 2);
                    soldProducts.add(new SoldItem(productOnly, quantity, price));
                }

            }
        }
        /*for (soldItem soldItem: soldProducts){
            System.out.println(soldItem.toString());
        }*/
        return calculateSingleProductsTotal(soldProducts);
    }

    private void prepareDisplayedOrder(List<DisplayedOrder> orders, PreparedStatement ps, ResultSet rs) throws SQLException {
        while (rs.next()) {
            String status = rs.getString(1);
            int nr = rs.getInt(2);
            int codComanda = rs.getInt(3);
            String dataComanda = rs.getString(4);
            String client = rs.getString(5);
            String produse = rs.getString(6);
            String adresa = rs.getString(7);
            String localitate = rs.getString(8);
            String codPostal = rs.getString(9);
            String tara = rs.getString(10);
            String telefon = rs.getString(11);
            String email = rs.getString(12);
            String observatii = rs.getString(13);
            int valoareProduse = rs.getInt(14);
            String incasat = rs.getString(15);
            String state = rs.getString(16);
            String valoareLivrare = rs.getString(17);
            DisplayedOrder checkOrder = new DisplayedOrder(status, nr, codComanda, dataComanda, client, produse, adresa, localitate, codPostal, tara, telefon, email, observatii, valoareProduse, incasat, state, valoareLivrare);
            orders.add(checkOrder);
        }
        ps.close();
    }

    private void displayOrderList(List<DisplayedOrder> finalizedOrders, PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String status = rs.getString(1);
            int nr = rs.getInt(2);
            int codComanda = rs.getInt(3);
            String dataComanda = rs.getString(4);
            String client = rs.getString(5);
            String produse = rs.getString(6);
            String adresa = rs.getString(7);
            String localitate = rs.getString(8);
            String codPostal = rs.getString(9);
            String tara = rs.getString(10);
            String telefon = rs.getString(11);
            String email = rs.getString(12);
            String observatii = rs.getString(13);
            int valoareProduse = rs.getInt(14);
            String incasat = rs.getString(15);
            String state = rs.getString(16);
            String valoareLivrare = rs.getString(17);

            finalizedOrders.add(new DisplayedOrder(status, nr, codComanda, dataComanda, client, produse, adresa, localitate, codPostal, tara, telefon, email, observatii, valoareProduse, incasat, state, valoareLivrare));
        }
    }

    private List<String> convertProductsString(DisplayedOrder displayedOrder) {

        String products = displayedOrder.getProduse();
        List<String> items = new ArrayList<>();

        while (products.length() > 72) {
            String x = products.substring(0, products.indexOf("||"));
            items.add(x);
            products = products.substring(products.indexOf("||") + 3);

        }
        items.add(products);

        return items;
    }

    private List<DisplayedOrder> getIntervalOrders(Date begin, Date end) throws ParseException {
        List<DisplayedOrder> allOrders = new OrderService().getTotalRevenue();
        allOrders.addAll(new OrderService().displayVirtualOrders());
        List<DisplayedOrder> intervalOrders = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (DisplayedOrder order : allOrders) {
            Date orderDate = simpleDateFormat.parse(order.getDataComanda());
            if ((orderDate.after(begin) || orderDate.equals(begin)) && (orderDate.before(end) || orderDate.equals(end))) {
                intervalOrders.add(order);
                //****
                // System.out.println(order.getCodComanda());
            }
        }
        intervalOrders.sort(Comparator.comparingInt(DisplayedOrder::getCodComanda));
        return intervalOrders;
    }

    private List<SoldItem> calculateSingleProductsTotal(List<SoldItem> soldProducts) {
        List<SoldItem> singleProductsTotal = new ArrayList<>();

        Set<String> onlyNamesOfProducts = new TreeSet<>();
        for (SoldItem itemString : soldProducts) {
            onlyNamesOfProducts.add(itemString.getName());
        }

        // for (String name: onlyNamesOfProducts) System.out.println(name);

        for (String only : onlyNamesOfProducts) {
            singleProductsTotal.add(new SoldItem(only, 0, 0));
        }

        for (SoldItem soldItem : soldProducts) {
            for (SoldItem only : singleProductsTotal) {
                if (only.getName().equals(soldItem.getName())) {
                    only.setPrice(only.getPrice() + soldItem.getPrice());
                    only.setQuantity(only.getQuantity() + soldItem.getQuantity());
                }

            }
        }
        for (SoldItem soldItem : singleProductsTotal)
            System.out.println(soldItem.getName() + " cant " + soldItem.getQuantity() + " total " + soldItem.getPrice());

        return singleProductsTotal;
    }

}

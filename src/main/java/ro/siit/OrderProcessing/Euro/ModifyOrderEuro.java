package ro.siit.OrderProcessing.Euro;

import com.fasterxml.jackson.databind.ObjectMapper;
import ro.siit.OrderDetails.DisplayedOrder;
import ro.siit.OrderProcessing.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


@WebServlet(urlPatterns = {"/modifyOrderEuro"})
public class ModifyOrderEuro extends HttpServlet {

    OrderService orderService = new OrderService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        String oldOrderJSONEuro = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        DisplayedOrder oldOrder = new DisplayedOrder();

        Scanner scanner = new Scanner(oldOrderJSONEuro).useDelimiter("[^0-9]+");
        int codComandaModify = scanner.nextInt();
        List<DisplayedOrder> allOrders = orderService.getAllOrdersEuro();
        for (DisplayedOrder order: allOrders){
            if (order.getCodComanda() == codComandaModify){
                oldOrder = order;
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        DisplayedOrder editedOrder = objectMapper.readValue(oldOrderJSONEuro, DisplayedOrder.class);
        editedOrder.setState(oldOrder.getState());

        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));

            PreparedStatement ps = connection.prepareStatement
                    ("DELETE FROM poliorderseuro WHERE cod_comanda = ?");
            ps.setInt(1, oldOrder.getCodComanda());
            ps.executeUpdate();
            ps.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                /* ignored */
            }
        }
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
            PreparedStatement qs = connection.prepareStatement
                    ("INSERT INTO poliorderseuro (status, nr, cod_comanda, data_comanda, client, produse, adresa, localitate, cod_postal, tara, telefon, email, observatii, valoare_produse, incasat, state, cost_transport) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            insertDBEuro(editedOrder, qs);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                /* ignored */
            }
            }
                List<DisplayedOrder> totalOrders = orderService.getAllOrdersEuro();
                req.setAttribute("orders", totalOrders);
                req.getRequestDispatcher("/jsps/tableEuro.jsp").forward(req, resp);
            }

    public static void insertDBEuro(DisplayedOrder editedOrder, PreparedStatement qs) throws SQLException {
        qs.setString(1, editedOrder.getStatus());
        qs.setInt(2, editedOrder.getCodComanda());
        qs.setInt(3, editedOrder.getCodComanda());
        qs.setString(4, editedOrder.getDataComanda());
        qs.setString(5, editedOrder.getClient());
        qs.setString(6, editedOrder.getProduse());
        qs.setString(7, editedOrder.getAdresa());
        qs.setString(8, editedOrder.getLocalitate());
        qs.setString(9, editedOrder.getCodPostal());
        qs.setString(10, editedOrder.getTara());
        qs.setString(11, editedOrder.getTelefon());
        qs.setString(12, editedOrder.getEmail());
        qs.setString(13, editedOrder.getObservatii());
        qs.setInt(14, editedOrder.getValoareProduse());
        qs.setString(15, String.valueOf(editedOrder.getValoareProduse()));
        qs.setString(16, editedOrder.getState());
        qs.setString(17, editedOrder.getValoareLivrare());

        qs.executeUpdate();
        qs.close();
    }
}

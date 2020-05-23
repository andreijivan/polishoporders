package ro.siit.OrderProcessing;

import ro.siit.OrderDetails.DisplayedOrder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/eliminateVirtualOrders"})
public class EliminateVirtualOrders extends HttpServlet {

    private OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("A mers endpoint-ul");
        List<DisplayedOrder> orderWithoutVirtual = orderService.noVirtualOrders();
        System.out.println("Orders size " + orderWithoutVirtual.size());
        req.setAttribute("orders",orderWithoutVirtual);
        req.getRequestDispatcher("/jsps/table.jsp").forward(req,resp);

    }
}

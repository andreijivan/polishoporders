package ro.siit.OrderProcessing;

import ro.siit.OrderDetails.DisplayedOrder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
@WebServlet(urlPatterns = {"/showVirtualOrders"})
public class ShowVirtualOrders extends HttpServlet {
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<DisplayedOrder> totalOrders = orderService.displayVirtualOrders();
        req.setAttribute("orders",totalOrders);
        req.setAttribute("noOfOrders", totalOrders.size());
        req.getRequestDispatcher("/jsps/virtualOrdersTable.jsp").forward(req,resp);
    }
}

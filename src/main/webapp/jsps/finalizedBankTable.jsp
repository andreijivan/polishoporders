<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<table class="table table-hover">
    <thead>
    <tr>
        <th scope="col">Status</th>
        <th scope="col">Action</th>
        <th scope="col">Cod comanda</th>
        <th scope="col">Data</th>
        <th scope="col">Client</th>
        <th scope="col">Produse</th>
        <th scope="col">Adresa</th>
        <th scope="col">Localitate</th>
        <th scope="col">Cod Postal</th>
        <th scope="col">Tara</th>
        <th scope="col">Numar telefon</th>
        <th scope="col">Email</th>
        <th scope="col">Observatii</th>
        <th scope="col">Valoare produse</th>
        <th scope="col">Incasat</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="order" items="${orders}">
        <tr>
            <td><c:out value="${order.status}" /></td>
            <td>
                <button class="btn btn-outline-success butonRevocare" type="submit" data-cod_comanda="${order.codComanda}">Revocare</button>
            </td>
            <td><c:out value="${order.codComanda}" /></td>
            <td><c:out value="${order.dataComanda}" /></td>
            <td><c:out value="${order.client}" /></td>
            <td><c:out value="${order.produse}" /></td>
            <td><c:out value="${order.adresa}" /></td>
            <td><c:out value="${order.localitate}" /></td>
            <td><c:out value="${order.codPostal}" /></td>
            <td><c:out value="${order.tara}" /></td>
            <td><c:out value="${order.telefon}" /></td>
            <td><c:out value="${order.email}" /></td>
            <td><c:out value="${order.observatii}" /></td>
            <td><c:out value="${order.valoareProduse}" /></td>
            <td><c:out value="${order.incasat}" /></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<script>
    $(".butonRevocare").click(function () {
        let codComanda = $(this).data("cod_comanda");
        $.ajax({
            type: "POST",
            url: "https://polishoporders.herokuapp.com/revokeFinalizedBankOrder" ,
            contentType: "application/json",
            data: {
                "codComanda": codComanda
            },
            success: function (data) {
                console.log(data);
                $("#tableDiv").html(data);
            },
            error: function () {
                $("#tableDiv").html("A aparut o eroare. Reincercati");
            }
        });
    })
</script>


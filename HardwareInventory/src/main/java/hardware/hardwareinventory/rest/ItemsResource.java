package hardware.hardwareinventory.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Path;
import hardware.hardwareinventory.database.ItemDAO;
import hardware.hardwareinventory.models.Items;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

/**
 * REST Web Service
 *
 * @author Milan
 */
@Path("items")
public class ItemsResource {
    private final ItemDAO itemDAO = new ItemDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllItems() {
        try {
            return Response.ok(toJsonArray(itemDAO.findAll())).build();
        } catch (SQLException ex) {
            return Response.serverError()
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchItems(@QueryParam("q") String searchText) {
        if (searchText == null) {
            searchText = "";
        }

        try {
            return Response.ok(toJsonArray(itemDAO.search(searchText))).build();
        } catch (SQLException ex) {
            return Response.serverError()
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }

    private String toJsonArray(List<Items> items) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                json.append(",");
            }
            json.append(items.get(i).toJson());
        }
        json.append("]");
        return json.toString();
    }
}

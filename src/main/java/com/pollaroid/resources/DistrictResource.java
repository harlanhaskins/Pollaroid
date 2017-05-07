package com.pollaroid.resources;

import com.pollaroid.database.DistrictDAO;
import com.pollaroid.models.District;
import com.pollaroid.models.Voter;
import lombok.AllArgsConstructor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * A resource for accessing information about districts.
 */
@Path("/districts")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class DistrictResource {
    private DistrictDAO districtDAO;

    @GET
    @Path("/{id}/representative")
    public Optional<Voter> representative(@PathParam("id") long districtID) {
        try {
            return districtDAO.getRepresentative(districtID);
        } catch (SQLException e) {
            throw new PollaroidError(e.getMessage());
        }
    }

    /**
     * Gets all the districts registered with the system.
     * @return All districts as JSON.
     */
    @GET
    public List<District> all() {
        try {
            return districtDAO.all();
        } catch (SQLException e) {
            throw new PollaroidError(e.getMessage());
        }
    }
}

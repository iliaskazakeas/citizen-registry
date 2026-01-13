package gr.registry.service;

import gr.registry.domain.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;

@Path("/citizens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CitizenResource {

    private static final CitizenRepository repository = new CitizenRepository();

    /*
    @POST
    public Response createCitizen(Citizen citizen) {
        
        if (repository.findById(citizen.getIdNumber()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Πολίτης με αυτόν τον ΑΤ υπάρχει ήδη"))
                    .build();
        }

        try {
            repository.save(citizen);
            return Response.status(Response.Status.CREATED).entity(citizen).build();
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    */
    
    @POST
    public Response createCitizen(Citizen citizen) {
        if (citizen == null || citizen.getIdNumber() == null || citizen.getIdNumber().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Το πεδίο 'idNumber' είναι υποχρεωτικό"))
                    .build();
        }

        if (repository.findById(citizen.getIdNumber()) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Πολίτης με αυτόν τον ΑΤ υπάρχει ήδη"))
                    .build();
        }

        try {
            repository.save(citizen);
            return Response.status(Response.Status.CREATED).entity(citizen).build();
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }


    @GET
    @Path("/{idNumber}")
    public Response getCitizen(@PathParam("idNumber") String idNumber) {
        Citizen citizen = repository.findById(idNumber);
        if (citizen == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Δεν βρέθηκε πολίτης με αυτόν τον ΑΤ"))
                    .build();
        }
        return Response.ok(citizen).build();
    }

    @DELETE
    @Path("/{idNumber}")
    public Response deleteCitizen(@PathParam("idNumber") String idNumber) {
        Citizen citizen = repository.findById(idNumber);
        if (citizen == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Δεν βρέθηκε πολίτης προς διαγραφή"))
                    .build();
        }
        repository.delete(idNumber);
        return Response.ok(Map.of("message", "Διαγράφηκε επιτυχώς")).build();
    }

    @PUT
    @Path("/{idNumber}")
    public Response updateCitizen(@PathParam("idNumber") String idNumber,
                                  Map<String, String> updates) {
        Citizen citizen = repository.findById(idNumber);
        if (citizen == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Δεν υπάρχει πολίτης για ενημέρωση"))
                    .build();
        }

        try {
            citizen.updateOptionalFields(updates.get("afm"), updates.get("address"));
            repository.update(citizen);
            return Response.ok(citizen).build();
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/search")
    public Response searchCitizens(@QueryParam("firstName") String firstName,
                                   @QueryParam("lastName") String lastName,
                                   @QueryParam("gender") Gender gender) {
        List<Citizen> all = repository.findAll();
        List<Citizen> result = all.stream()
                .filter(c -> (firstName == null || c.getFirstName().equalsIgnoreCase(firstName)))
                .filter(c -> (lastName == null || c.getLastName().equalsIgnoreCase(lastName)))
                .filter(c -> (gender == null || c.getGender() == gender))
                .toList();

        return Response.ok(result).build();
    }
}

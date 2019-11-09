package de.dowinter.api;

import de.dowinter.core.Checkable;
import de.dowinter.core.CheckablesRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@Path("/checkables")
public class CheckablesResource {

    @Context
    UriInfo uriInfo;

    @Inject
    CheckablesRepository checkables;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response.ok(checkables.all()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCheckable(@PathParam("id") Long id) {
        Optional<Checkable> checkable = checkables.withId(id);
        if (checkable.isPresent()) {
            return Response.ok(checkable.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Transactional
    public Response addCheckable(Checkable c) {
        checkables.save(c);
        return Response
                .created(uriInfo
                        .getAbsolutePathBuilder()
                        .path(c.getId().toString())
                        .build())
                .build();
    }

    @POST
    @Path("/{id}/toggle")
    @Transactional
    public Response toggleCheckable(@PathParam("id") Long id) {
        Optional<Checkable> checkable = checkables.withId(id);

        if (checkable.isPresent()) {
            checkable.get().setChecked(!checkable.get().isChecked());
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
package de.dowinter.checklist.api;

import de.dowinter.checklist.core.Checkable;
import de.dowinter.checklist.core.CheckablesRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@Path("/checklists/{cid}/checkables")
public class CheckablesResource {

    @Context
    UriInfo uriInfo;

    @Inject
    CheckablesRepository checkables;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@PathParam("cid") String checklistId) {
        return Response.ok(checkables.getAllCheckablesFromChecklist(checklistId)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCheckable(@PathParam("cid") String checklistId, @PathParam("id") Long checkableId) {
        Optional<Checkable> checkable = checkables.getCheckableFromChecklist(checklistId, checkableId);
        if (checkable.isPresent()) {
            return Response.ok(checkable.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Transactional
    public Response addCheckable(@PathParam("cid") String checklistId, Checkable c) {
        checkables.addCheckable(checklistId, c);
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
    public Response toggleCheckable(@PathParam("cid") String checklistId, @PathParam("id") Long checkableId) {
        Optional<Checkable> checkable = checkables.getCheckableFromChecklist(checklistId, checkableId);

        if (checkable.isPresent()) {
            checkable.get().setChecked(!checkable.get().isChecked());
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
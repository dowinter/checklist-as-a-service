package de.dowinter.checklist.api;

import de.dowinter.checklist.core.Checklist;
import de.dowinter.checklist.core.ChecklistRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@Path("/checklists")
public class ChecklistsResource {

    @Inject
    ChecklistRepository checklists;

    @Context
    UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response.ok(checklists.all()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChecklist(@PathParam("id") String checklistId) {
        Optional<Checklist> checklist = checklists.withId(checklistId);

        if (checklist.isPresent()) {
            return Response.ok(checklist.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response addChecklist(Checklist checklist) {
        boolean saved = checklists.save(checklist);
        if (saved) {
            return Response
                    .created(uriInfo.getAbsolutePath())
                    .build();
        } else {
            return Response.status(Response.Status.CONFLICT)
                    .build();
        }
    }
}

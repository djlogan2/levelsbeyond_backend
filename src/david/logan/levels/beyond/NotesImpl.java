package david.logan.levels.beyond;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;

@Path("fuck")
public class NotesImpl extends ResourceConfig {
	
	public NotesImpl() {
		packages("david.logan.levels.beyond");
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response newNote(Note body) {
		return Response.status(200).entity(new Note("New note method")).build();
	}

	@GET
	@Path("{id : \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNote(@PathParam("id") int id) {
		return Response.status(200).entity(new Note("Get note method")).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllNotes() {
		return Response.status(200).entity(new Note("All notes method")).build();
	}
}

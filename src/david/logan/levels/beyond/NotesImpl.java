package david.logan.levels.beyond;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("api/notes")
public class NotesImpl {

	private LBDao dao = LBDao.getDAO();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response newNote(Note note) {
		if(note.id != null)
			return Response.status(Response.Status.BAD_REQUEST).entity(new Error("Cannot have id in a new note")).build();
		note.id = dao.addNote(note);
		return Response.status(Response.Status.OK).entity(note).build();
	}

	@GET
	@Path("/{id : \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNote(@PathParam("id") int id) {
		Note note = dao.getNote(id);
		if(note == null)
			return Response.status(Response.Status.OK).entity(new Error("Nonexistant note")).build();
		else
			return Response.status(Response.Status.OK).entity(note).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllNotes(@QueryParam("search") String searchParm) {
		if(searchParm != null && !searchParm.isEmpty())
		{
			List<Note> notes = dao.getAllNotes(searchParm);
			return Response.status(Response.Status.OK).entity(notes).build();
		} else {
			List<Note> notes = dao.getAllNotes();
			return Response.status(Response.Status.OK).entity(notes).build();
		}
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateNote(Note note) {
		dao.updateNote(note);
		return Response.status(Response.Status.OK).entity(note).build();
	}
	
	
	@DELETE
	@Path("/DELETE_ALL_NOTES")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAllNotes() {
		dao.deleteAllNotes();
		return Response.status(Response.Status.OK).entity(new Error("ok")).build();
	}

	@DELETE
	@Path("{id : \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNote(@PathParam("id") int id) {
		dao.deleteNote(id);
		return Response.status(Response.Status.OK).entity(new Error("ok")).build();
	}

}

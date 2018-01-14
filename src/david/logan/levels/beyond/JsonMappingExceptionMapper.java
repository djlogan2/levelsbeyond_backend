package david.logan.levels.beyond;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonMappingException;

@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException>{
    @Override
    public Response toResponse(JsonMappingException exception)
    {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new Error("Unknown fields in note object"))
                .type( MediaType.APPLICATION_JSON)
                .build();
    }   
}
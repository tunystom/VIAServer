package cz.fel.cvut.via.rest;

import java.sql.SQLException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ExceptionMapper;

@Provider public class SQLExceptionMapper implements ExceptionMapper<java.sql.SQLException>
{
    @Override public Response toResponse(SQLException exception)
    {
        return Response.status(500).entity(exception.getMessage()).type(MediaType.TEXT_PLAIN).build();
    }
}

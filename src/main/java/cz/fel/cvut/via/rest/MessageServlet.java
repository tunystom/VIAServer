package cz.fel.cvut.via.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * The message resource.
 */
@Path("/messages")
public class MessageServlet
{
    private static DataSource dataSource;

    public MessageServlet()
    {
        try
        {
            dataSource = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/msgdb");
        }
        catch (Exception e)
        {
            System.err.println("Cannot connect to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @GET
    @Path("/")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<Message> getMessages(@javax.ws.rs.core.Context HttpHeaders requestHeaders) throws SQLException
    {
        int id = -1, limit = -1;
        
        MultivaluedMap<String,String> headers = requestHeaders.getRequestHeaders() ;
        
        if (headers.containsKey("X-Last-Received"))
        {
            id = Integer.parseInt(headers.get("X-Last-Received").get(0)) ;
        }

        if (headers.containsKey("X-Limit"))
        {
            limit = Integer.parseInt(headers.get("X-Limit").get(0)) ;
        }
        
        Connection connection = null;
        PreparedStatement statement = null;

        try
        {
            connection = dataSource.getConnection();

            if (id < 0)
            {
                statement = connection.prepareStatement("SELECT * FROM messages ORDER BY ts ASC");
            }
            else if (limit < 0)
            {
                statement = connection.prepareStatement("SELECT * FROM messages WHERE id > ? ORDER BY ts ASC");
                statement.setInt(1, id);
            }
            else
            {
                statement = connection.prepareStatement("SELECT * FROM messages WHERE id > ? ORDER BY ts ASC LIMIT ?") ;
                statement.setInt(1, id);
                statement.setInt(2, limit);
            }

            ResultSet results = statement.executeQuery();

            List<Message> messages = new ArrayList<Message>();

            while (results.next())
            {
                messages.add(new Message(results.getInt("id"), results.getString("author"), results.getString("content"), results.getTimestamp("ts")));
            }

            return messages;
        }
        finally
        {
            if (statement != null)
            {
                try
                {
                    statement.close();
                }
                catch (SQLException e)
                {
                    /* At least should be logged. */
                }
            }

            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    /* At least should be logged. */
                }
            }
        }
    }
    
    @GET
    @Path("/{messageId}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getMessage(@PathParam("messageId") Integer messageId) throws SQLException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        try
        {
            connection = dataSource.getConnection();

            statement = connection.prepareStatement("SELECT * FROM messages WHERE id = ?");

            statement.setInt(1, messageId);

            ResultSet results = statement.executeQuery();

            if (results.next())
            {
                return Response.status(Status.OK).entity(new Message(results.getInt("id"), results.getString("author"), results.getString("content"), results.getTimestamp("ts"))).build();
            }
            else
            {
                return Response.status(Status.NOT_FOUND).build();
            }
        }
        finally
        {
            if (statement != null)
            {
                try
                {
                    statement.close();
                }
                catch (SQLException e)
                {
                    /* At least should be logged. */
                }
            }

            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    /* At least should be logged. */
                }
            }
        }
    }

    @GET
    @Path("/author/{author}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<Message> getMessagesByAuthor(@PathParam("author") String author) throws SQLException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        try
        {
            connection = dataSource.getConnection();

            statement = connection.prepareStatement("SELECT * FROM messages WHERE author = ?");

            statement.setString(1, author);

            ResultSet results = statement.executeQuery();

            if (!results.isBeforeFirst())
            {
                throw new WebApplicationException(Status.NOT_FOUND);
            }

            List<Message> messages = new ArrayList<Message>();

            while (results.next())
            {
                messages.add(new Message(results.getInt("id"), results.getString("author"), results.getString("content"), results.getTimestamp("ts")));
            }

            return messages;
        }
        finally
        {
            if (statement != null)
            {
                try
                {
                    statement.close();
                }
                catch (SQLException e)
                {
                    /* At least should be logged. */
                }
            }

            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    /* At least should be logged. */
                }
            }
        }
    }

    @POST
    @Path("/")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces(MediaType.TEXT_PLAIN)
    public Response storeMessage(Message message, @javax.ws.rs.core.Context UriInfo uriInfo) throws SQLException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        try
        {
            connection = dataSource.getConnection();

            statement = connection.prepareStatement("INSERT INTO messages (author, content) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, message.getAuthor());
            statement.setString(2, message.getContent());

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();

            rs.next();

            return Response.created(uriInfo.getAbsolutePathBuilder().path(Integer.toString(rs.getInt(1))).build()).build();
        }
        finally
        {
            if (statement != null)
            {
                try
                {
                    statement.close();
                }
                catch (SQLException e)
                {
                    /* At least should be logged. */
                }
            }

            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    /* At least should be logged. */
                }
            }
        }
    }
}

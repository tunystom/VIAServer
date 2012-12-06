package cz.fel.cvut.via;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import cz.fel.cvut.via.rest.Message;

public class ShowMessagesServlet extends HttpServlet
{
    private static DataSource dataSource = null;
    private static boolean    dataSourceInitialized = false;
    
    private static synchronized void initialize() throws NamingException
    {
        if (dataSource == null)
        {
            dataSource = (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/msgdb");
            dataSourceInitialized = true ;
        }
    }

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if (!dataSourceInitialized)
        {
            try
            {
                initialize();
            }
            catch (Exception exception)
            {
                PrintWriter writer = new PrintWriter(response.getOutputStream());
                writer.write("<body><h1>Exception</h1><p>" + exception.getMessage() + "</p></body>");
                writer.close();
                return;
            }
        }
        
        List<Message>     messages   = new ArrayList<Message>();
        Connection        connection = null ;
        PreparedStatement statement  = null ;
        
        try
        {
            connection = dataSource.getConnection();
            
            statement = connection.prepareStatement("SELECT * FROM messages ORDER BY ts DESC");
            
            ResultSet results = statement.executeQuery();
    
            while (results.next())
            {
                messages.add(new Message(results.getInt("id"), results.getString("author"), results.getString("content"), results.getTimestamp("ts")));
            }
        }
        catch (SQLException exception)
        {
            request.setAttribute("error", exception.getMessage()) ;
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
        
        request.setAttribute("messages", messages) ;
        
        request.getRequestDispatcher("/messages.jsp").forward(request, response) ;
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
}

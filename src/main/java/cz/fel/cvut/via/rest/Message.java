package cz.fel.cvut.via.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Message
{
    @XmlElement(required = false)
    private Integer id;
    
    @XmlElement(required = true)
    private String author;

    @XmlElement(required = true)
    private String content;

    @XmlElement(required = false)
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date timestamp;

    public Message()
    {
    }

    public Message(String author, String content)
    {
        this(0, author, content, new Date());
    }

    public Message(String author, String content, Date timestamp)
    {
        this(0, author, content, new Date());
    }
    
    public Message(Integer id, String author, String content)
    {
        this(id, author, content, new Date());
    }

    public Message(Integer id, String author, String content, Date timestamp)
    {
        this.id        = id;
        this.author    = author;
        this.content   = content;
        this.timestamp = timestamp;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String message)
    {
        this.content = message;
    }

    public Date getTimestamp()
    {        
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }
}

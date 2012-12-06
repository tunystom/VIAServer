<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>VIA Message Server</title>
</head>
<body>
	<c:choose>
		<c:when test="${error != null}">
		  <b>Error:</b>&nbsp;${error}
		</c:when>
		<c:otherwise>
			<h1>Messages</h1>
			<c:forEach var="m" items="${messages}">
				<p>
				  At <fmt:timeZone value="Europe/Prague"><fmt:formatDate value="${m.timestamp}" pattern="HH:mm:ss dd/MM/yyyy" /></fmt:timeZone> <b>${m.author}</b> wrote:&nbsp;"${m.content}"
				</p>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</body>
</html>
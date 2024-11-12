<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Form Submission Result</title>
    <link rel="stylesheet" type="text/css" href="css/result.css">
</head>
<body>
<div class="container">
    <h1>Top Ranked Result</h1>
    <c:choose>
        <c:when test="${empty documents}">
            <p>No documents found.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="document" items="${documents}">
                <p>Position: ${document.rankingPosition}</p>
                <p>Title: ${document.title}</p>
                <p>Authors: ${document.authors}</p>
<%--                <p>Content: ${document.content}</p>--%>
<%--                <p>Abstract: ${document.paperAbstract}</p>--%>
                <p>Score: ${document.score}</p>
                <hr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
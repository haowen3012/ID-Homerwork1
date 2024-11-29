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
        <c:when test="${empty tables}">
            <p>No documents found.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="table" items="${tables}">
                <p>Position: ${table.rankingPosition}</p>
                <p>Caption: ${table.caption}</p>
                <p>Table: ${table.table}</p>
                <p>Footnotes: ${table.footnotes}</p>
                <p>References: ${table.references}</p>
                <p>Score: ${table.score}</p>
                <hr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
<!DOCTYPE html>
<html>
    <head>
        <title>Customer Support</title>
    </head>
    <body>
        <security:authorize access="isAuthenticated()">
        <c:url var="logoutUrl" value="/logout"/>
        <form action="${logoutUrl}" method="post">
            <input type="submit" value="Log out" />
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
        </security:authorize> 
        <h2>All Post</h2>

        <security:authorize access="hasRole('ADMIN')">    
            <a href="<c:url value="/user" />">Manage User Accounts</a><br /><br />
        </security:authorize>
        <security:authorize access="isAuthenticated()">
        <a href="<c:url value="/ticket/create" />">New Post</a>  
        </security:authorize>       
        <a href="<c:url value="/ticket/listlecture" />">lecture</a>
        <a href="<c:url value="/ticket/listlab" />">lab</a>
        <a href="<c:url value="/ticket/listother" />">other</a>
        <a href="<c:url value="/ticket/list" />">all</a>
                <security:authorize access="isAnonymous()">
        <a href="<c:url value="/login" />">login</a>
        </security:authorize>  
        <br /><br />
        <c:choose>
            <c:when test="${fn:length(ticketDatabase) == 0}">
                <i>0 Post.</i>
            </c:when>
            <c:otherwise>
                <c:forEach items="${ticketDatabase}" var="entry2">
                    <c:if test="${entry2.categories == 'lecture'}" >
                        Post #${entry2.id}:
                        <a href="<c:url value="/ticket/view/${entry2.id}" />"> 
                            <c:out value="${entry2.subject}" /></a>
                        (Post by: <c:out value="${entry2.customerName}" />) 
                         <security:authorize access="isAuthenticated()">
                    <security:authorize access="hasRole('ADMIN') or principal.username=='${entry2.customerName}'">            
                        [<a href="<c:url value="/ticket/edit/${entry2.id}" />">Edit</a>]
                    </security:authorize>
                        </security:authorize>
                    <security:authorize access="hasRole('ADMIN')">            
                        [<a href="<c:url value="/ticket/delete/${entry2.id}" />">Delete</a>]
                    </security:authorize>
                    <hr>
                    </c:if>             
                </c:forEach>         
            </c:otherwise>
        </c:choose>
    </body>
</html>
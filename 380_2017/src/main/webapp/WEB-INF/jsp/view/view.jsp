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

        <h2>Post #${ticketId}: <c:out value="${ticket.subject}" /></h2>(Number of Comment :<c:out value="${numberComment}" />)
                  <security:authorize access="isAuthenticated()">
        <security:authorize access="hasRole('ADMIN') or principal.username=='${ticket.customerName}'"> 

            [<a href="<c:url value="/ticket/edit/${ticketId}" />">Edit</a>]
        </security:authorize>
            </security:authorize>
        <security:authorize access="hasRole('ADMIN')">            
            [<a href="<c:url value="/ticket/delete/${ticketId}" />">Delete</a>]
        </security:authorize>
        <br /><br />
        <i>Post by - <c:out value="${ticket.customerName}" /></i><br /><br />
        Categories: <c:out value="${ticket.categories}"/><br /><br />
        <c:out value="${ticket.body}" /><br /><br />
         <c:if test="${attachmentlist !=null}">
            File(s):
            <c:forEach items="${attachmentlist}" var="attachment"
                       varStatus="status">
                <c:if test="${!status.first}">, </c:if>
                <a href="<c:url value="/ticket/${ticketId}/attachment/${attachment.name}" />">
                    <c:out value="${attachment.name}" /></a>       
            </c:forEach><br /><br />
        </c:if>
            
            <c:choose>
            <c:when test="${fn:length(selectedReply) == 0}">
                <i>There are no reply in the system.</i>
            </c:when>
            <c:otherwise>
 
                <c:forEach items="${selectedReply}" var="entry">
                      Reply Name <c:out value="${entry.replyName}"/>
                    <br/>
                    (reply content: <c:out value="${entry.replybody}" />)                   
                    <c:forEach items="${entry.attachments}" var="attachment"
                               varStatus="status">                     
                        <c:if test="${!status.first}">, </c:if>
                        <a href="<c:url value="/reply/${entry.id}/attachment/${attachment.name}" />">
                            <c:out value="${attachment.name}" /></a>       
                        </c:forEach>
                    <br /><br />
                    <security:authorize access="hasRole('ADMIN')">            
                        [<a href="<c:url value="/reply/delete/${entry.id}" />">Delete</a>]
                    </security:authorize>
                    <br/><br/>
                </c:forEach>
            </c:otherwise>
        </c:choose>
                <security:authorize access="isAuthenticated()">
         <a href="<c:url value="/reply/${ticketId}" />">Reply</a>  
         </security:authorize>
        <a href="<c:url value="/ticket" />">Return to list tickets</a>
    </body>
</html>
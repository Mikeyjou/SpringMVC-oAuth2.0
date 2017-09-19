<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head></head>
    <body>
        <div>
            <% if (request.getParameter("error") != null) { %>
            <div>${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
            <% } %>

            <form role="form" action="j_spring_security_check" method="post">
                <input type="text" name="j_username" placeholder="Username" required autofocus>
                <input type="password" name="j_password" placeholder="Password" required>
                <label>
                    <input type="checkbox" value="remember-me"> Remember me
                </label>
                <input type="hidden" name="spring-security-redirect"/>
                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}"/>

                <button type="submit">Sign in</button>
            </form>
        </div>
    </body>
</html>
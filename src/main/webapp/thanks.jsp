<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Murach's Java Servlets and JSP</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css"/>
</head>
<body>

<h1>Thanks for Joining!</h1>

<p>Thanks, ${user.firstName}! You've been added to our email list at
<strong>${user.email}</strong>.</p>

<p style="color:red;">${errorMessage}</p>

<p><a href="emailList">Back to the join page</a></p>

</body>
</html>

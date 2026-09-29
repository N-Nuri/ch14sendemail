<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Murach's Java Servlets and JSP</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css"/>
</head>
<body>

<h1>Join Our Email List</h1>

<p>Enter your information below to join our email list. We'll send you a
confirmation email as soon as you join.</p>

<form action="emailList" method="post">
    <input type="hidden" name="action" value="add">
    <p>
        <label>First name:</label><br>
        <input type="text" name="firstName" required>
    </p>
    <p>
        <label>Last name:</label><br>
        <input type="text" name="lastName" required>
    </p>
    <p>
        <label>Email address:</label><br>
        <input type="email" name="email" required>
    </p>
    <p><input type="submit" value="Join"></p>
</form>

</body>
</html>

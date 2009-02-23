<#-- @ftlvariable name="cluster" type="com.elasticgrid.model.Cluster" -->
<html>

<head>
    <title>Applications on ${cluster.getName()}</title>
</head>

<body>

<h1>Applications on ${cluster.getName()}</h1>
<ul>
    <#list cluster.getApplications() as application>
        <li>${application.getName()}</li>
    </#list>
</ul>


<form method="post" action="/eg/rio/applications" enctype="multipart/form-data">
    <label for="oar">OAR to deploy:</label>
    <input id="oar" name="oar" type="file"/>
    <input type="submit" value="Deploy"/>
</form>

</body>

</html>
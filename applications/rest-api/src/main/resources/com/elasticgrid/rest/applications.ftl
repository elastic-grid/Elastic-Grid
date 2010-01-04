<#--

    Elastic Grid
    Copyright (C) 2008-2010 Elastic Grid, LLC.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

-->
<#-- @ftlvariable name="cluster" type="com.elasticgrid.model.Cluster" -->
<html>

<head>
    <title>Applications on ${cluster.getName()}</title>
</head>

<body>

<h1>Applications on ${cluster.getName()}</h1>
<ul>
    <#list cluster.applications as application>
        <li>
            ${application.getName()}<br/>
            <ul>
                <#list application.services as service>
                <li>${service.getName()}</li>
                </#list>
            </ul>
        </li>
    </#list>
</ul>


<form method="post" action="/eg/${cluster.getName()}/applications" enctype="multipart/form-data">
    <label for="oar">OAR to deploy:</label>
    <input id="oar" name="oar" type="file"/>
    <input type="submit" value="Deploy"/>
</form>

</body>

</html>
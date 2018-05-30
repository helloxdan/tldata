<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>App版本管理</title>
	<meta name="decorator" content="adminlte"/>
	<script type="text/javascript">
		$(document).ready(function() {

		});
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		function resetQuery() {
			$("#type").val("");
			$("#state").val("");
			$("#version").val("");
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/appVersion/">App版本列表</a></li>
		<shiro:hasPermission name="sys:appVersion:edit"><li><a href="${ctx}/sys/appVersion/form">添加App版本</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="appVersion" action="${ctx}/sys/appVersion/" method="post" class="form-inline form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		 
			<div class="form-group"><label>App类型：</label>
				<form:select path="type" class="form-control">
					<form:option value="" label=""/>
					<form:options items="${appTypes}" itemLabel="name" itemValue="code" htmlEscape="false"/>
				</form:select>
			</div>
			<div class="form-group"><label>版本号：</label>
				<form:input path="version" htmlEscape="false" maxlength="20" class="form-control"/>
			</div>
			<div class="form-group"><label>状态：</label>
				<form:select path="state" class="form-control">
					<form:option value="" label=""/>
					<form:options items="${states}" itemLabel="name" itemValue="code" htmlEscape="false"/>
				</form:select>
			</div>
			<div class="form-group">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</div>
			<div class="form-group">
				<input id="btnReset" class="btn" type="button" value="重置" onclick="resetQuery()"/>
			</div>		 
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>类型</th>
				<th>版本号</th>
				<th>版本编码</th>
				<th>版本名称</th>
				<th>下载地址</th>
				<th>状态</th>
				<shiro:hasPermission name="sys:appVersion:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="appVersion">
			<tr>
				<td><a href="${ctx}/sys/appVersion/form?id=${appVersion.id}">
					${appVersion.type}
				</a></td>
				<td>
					${appVersion.version}
				</td>
				<td>
					${appVersion.code}
				</td>
				<td>
					${appVersion.name}
				</td>
				<td>
					${appVersion.url}
				</td>				
				<td>
					${appVersion.state.name}
				</td>
				<shiro:hasPermission name="sys:appVersion:edit"><td>
    				<a href="${ctx}/sys/appVersion/form?id=${appVersion.id}">修改</a>
					<a href="${ctx}/sys/appVersion/delete?id=${appVersion.id}" onclick="return confirmx('确认要删除该App版本吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div>${page}</div>
</body>
</html>
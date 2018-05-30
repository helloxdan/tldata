<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>生成方案管理</title>
<meta name="decorator" content="adminlte" />
<script type="text/javascript">
		$(document).ready(function() {
			var fitstBoxSolidH =  $(".box-solid").eq(0).height();
			var muHeight = $(window).height() - fitstBoxSolidH -20 -20 -20;
			$(".minHeight").css('min-height', muHeight);
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
<style>

</style>
</head>
<body>

	<div class="divWrap">
		<div class=" addData">
			<div class="box box-solid">
				<div class="box-header with-border">
					<h3 class="box-title">生成方案列表</h3>
				</div>
				<form:form id="searchForm" modelAttribute="genScheme"
					action="${ctx}/gen/genScheme/" method="post"
					class="form-inline form-search">
					<input id="pageNo" name="pageNo" type="hidden"
						value="${page.pageNo}" />
					<input id="pageSize" name="pageSize" type="hidden"
						value="${page.pageSize}" />
					<div class="cxtj">
						<div class="form-group cxtj_text">
							<label>方案名称 ：</label>
							<form:input path="name" htmlEscape="false" maxlength="50"
								class="form-control" />
						</div>
						<div class="form-group cxtj_text">
							<input id="btnSubmit" class="btn btn-blue" type="submit"
								value="查询" />
						</div>
					</div>
				</form:form>
				<sys:message content="${message}" />
			</div>
		</div>
		<div class=" addData">
			<div class="box box-solid minHeight">
				<div class="form-group cxtj_text" style="margin-bottom:10px;">
					<a href="${ctx}/gen/genScheme/form" class="btn btn-blue"
						style="padding: 4px 10px;">新增</a> <input id="btnReset"
						class="btn btn-blue" type="reset" style="padding: 4px 10px;"
						value="删除" />
				</div>
				<div class="table-responsive">
					<table id="contentTable"
						class="table table-hover table-bordered table-condensed">
						<thead>
							<tr>
								<th><input type="checkbox" id="checkAll" /></th>
								<th>方案名称</th>
								<th>生成模块</th>
								<th>模块名</th>
								<th>功能名</th>
								<th>功能作者</th>
								<!-- <shiro:hasPermission name="gen:genScheme:edit">
									<th>操作</th>
								</shiro:hasPermission> -->
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${page.list}" var="genScheme">
								<tr>
									<td><input type="checkbox" id="select-${asset.id}"
										class="asset_checkbox" value="${asset.id}" />
									</td>
									<td><a href="${ctx}/gen/genScheme/form?id=${genScheme.id}">${genScheme.name}</a>
									</td>
									<td>${genScheme.packageName}</td>
									<td>${genScheme.moduleName}${not empty
										genScheme.subModuleName?'.':''}${genScheme.subModuleName}</td>
									<td>${genScheme.functionName}</td>
									<td>${genScheme.functionAuthor}</td>
									<%-- <shiro:hasPermission name="gen:genScheme:edit">
										<td><a
											href="${ctx}/gen/genScheme/form?id=${genScheme.id}">修改</a> <a
											href="${ctx}/gen/genScheme/delete?id=${genScheme.id}"
											onclick="return confirmx('确认要删除该生成方案吗？', this.href)">删除</a></td>
									</shiro:hasPermission> --%>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="pagination">${page}</div>
			</div>
		</div>
	</div>



</body>
</html>

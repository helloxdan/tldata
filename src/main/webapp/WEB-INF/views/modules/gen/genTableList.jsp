<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>业务表管理</title>
<meta name="decorator" content="adminlte" />
<script type="text/javascript">
	$(document).ready(function() {
		var fitstBoxSolidH =  $(".box-solid").eq(0).height();
		var muHeight = $(window).height() - fitstBoxSolidH -20 -20 -20;
		$(".minHeight").css('min-height', muHeight);
	});
	function page(n, s) {
		if (n)
			$("#pageNo").val(n);
		if (s)
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
					<h3 class="box-title">业务表列表</h3>
				</div>
				<form:form id="searchForm" modelAttribute="genTable"
					action="${ctx}/gen/genTable/" method="post"
					class="form-inline form-search">
					<input id="pageNo" name="pageNo" type="hidden"
						value="${page.pageNo}" />
					<input id="pageSize" name="pageSize" type="hidden"
						value="${page.pageSize}" />
					<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}"
						callback="page();" />
					<div class="cxtj">
						<div class="form-group cxtj_text">
							<label>表名：</label>
							<form:input path="nameLike" htmlEscape="false" maxlength="50"
								class="form-control" />
						</div>
						<div class="form-group cxtj_text">
							<label>说明：</label>
							<form:input path="comments" htmlEscape="false" maxlength="50"
								class="form-control" />
						</div>
						<div class="form-group cxtj_text">
							<label>父表表名：</label>
							<form:input path="parentTable" htmlEscape="false" maxlength="50"
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
					<a href="${ctx}/gen/genTable/form" class="btn btn-blue"
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
								<th class="sort-column name">表名</th>
								<th>说明</th>
								<th>类名</th>
								<th class="sort-column parent_table">父表</th>
								<!-- <shiro:hasPermission name="gen:genTable:edit">
									<th>操作</th>
								</shiro:hasPermission> -->
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${page.list}" var="genTable">
								<tr>
									<td><input type="checkbox" id="select-${asset.id}"
										class="asset_checkbox" value="${asset.id}" />
									</td>
									<td><a href="${ctx}/gen/genTable/form?id=${genTable.id}">${genTable.name}</a>
									</td>
									<td>${genTable.comments}</td>
									<td>${genTable.className}</td>
									<td title="点击查询子表"><a href="javascript:"
										onclick="$('#parentTable').val('${genTable.parentTable}');$('#searchForm').submit();">${genTable.parentTable}</a>
									</td>
									<%-- <shiro:hasPermission name="gen:genTable:edit">
										<td><a href="${ctx}/gen/genTable/form?id=${genTable.id}">修改</a>
											<a href="${ctx}/gen/genTable/delete?id=${genTable.id}"
											onclick="return confirmx('确认要删除该业务表吗？', this.href)">删除</a></td>
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

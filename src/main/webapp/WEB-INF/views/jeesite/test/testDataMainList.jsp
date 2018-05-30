<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>主子表管理</title>
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
					<h3 class="box-title">主子表列表</h3>
				</div>
				<form:form id="searchForm" modelAttribute="testDataMain"
					action="${ctx}/test/testDataMain/" method="post"
					class="form-inline form-search">
					<input id="pageNo" name="pageNo" type="hidden"
						value="${page.pageNo}" />
					<input id="pageSize" name="pageSize" type="hidden"
						value="${page.pageSize}" />
					<div class="cxtj">
						<div class="form-group cxtj_text">
							<label>归属用户：</label>
							<sys:treeselect id="user" name="user.id"
								value="${testDataMain.user.id}" labelName="user.name"
								labelValue="${testDataMain.user.name}" title="用户"
								url="/sys/office/treeData?type=3" cssClass="input-small"
								allowClear="true" notAllowSelectParent="true" />
						</div>
						<div class="form-group cxtj_text">
							<label>名称：</label>
							<form:input path="name" htmlEscape="false" maxlength="100"
								class="form-control" />
						</div>
						<div class="form-group cxtj_text">
							<label>性别：</label>
							<form:select path="sex" class="form-control">
								<form:option value="" label="--请选择--" />
								<form:options items="${fns:getDictList('sex')}"
									itemLabel="label" itemValue="value" htmlEscape="false" />
							</form:select>
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
					<a href="${ctx}/test/testDataMain/form" class="btn btn-blue"
						style="padding: 4px 10px;">新增</a> <input id="btnReset"
						class="btn btn-blue" type="reset" style="padding: 4px 10px;"
						value="删除" />
				</div>
				<div class="table-responsive">
					<table id="contentTable"
						class="table table-striped table-bordered table-condensed">
						<thead>
							<tr>
								<th><input type="checkbox" id="checkAll" />
								<th>归属用户</th>
								<th>名称</th>
								<th>更新时间</th>
								<th>备注信息</th>
								<!-- <shiro:hasPermission name="test:testDataMain:edit">
									<th>操作</th>
								</shiro:hasPermission> -->
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${page.list}" var="testDataMain">
								<tr>
									<td><input type="checkbox" id="select-${asset.id}"
										class="asset_checkbox" value="${asset.id}" /></td>
									<td><a
										href="${ctx}/test/testDataMain/form?id=${testDataMain.id}">
											${testDataMain.user.name} </a></td>
									<td>${testDataMain.name}</td>
									<td><fmt:formatDate value="${testDataMain.updateDate}"
											pattern="yyyy-MM-dd HH:mm:ss" />
									</td>
									<td>${testDataMain.remarks}</td>
									<%-- <shiro:hasPermission name="test:testDataMain:edit">
										<td><a
											href="${ctx}/test/testDataMain/form?id=${testDataMain.id}">修改</a>
											<a
											href="${ctx}/test/testDataMain/delete?id=${testDataMain.id}"
											onclick="return confirmx('确认要删除该主子表吗？', this.href)">删除</a>
										</td>
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
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>系统租户管理</title>
<meta name="decorator" content="adminlte" />
<script type="text/javascript">
	$(document).ready(function() {
		var fitstBoxSolidH =  $(".box-solid").eq(0).height();
		
		var muHeight = $(window).height() - fitstBoxSolidH -20 -20 -20;
		$(".minHeight").css('min-height', muHeight);

		$("#checkAll").click(
				function() {
					var checked = $(this).is(':checked');
					 // console.log($("input[type='checkbox'].list_checkbox").length);
					// console.log('checked:'+$(this).is(':checked'));
					if (checked) {
						$("input[type='checkbox'].list_checkbox").each(function(){  
		                    this.checked=true;  
		                });  
					} else {
						$("input[type='checkbox'].list_checkbox").each(function(){  
		                    this.checked=false;  
		                });  
					}
		});
		$("#btnDel").click(function() {
			var ids = [];
			$("input[type='checkbox']:checkbox:checked.list_checkbox").each(
					function(i, v) {
						//console.log(i,$(this).val());
						ids.push("" + $(this).val() + "");
					});
			ids = ids.join(",");
			console.log(ids);
			if (ids === '') {
				top.$.jBox.tip('请选择记录！', 'warning');
				return;
			}
			confirmx("确定要删除选中记录吗？",function(){
				loading('系统处理中，请稍候……');
				$.post('${ctx}/sys/tenant/del', {
					ids : ids
				}, function(result) {
					if (result.success) { 
						//刷新页面
						document.location.reload();
					}else{
						top.$.jBox.tip(result.msg, 'warning');
					}
				});
			},function(){
				console.log('取消 删除');
			});
		
		 }); //del click		
	});
	function page(n, s) {
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
					<h3 class="box-title">系统租户列表</h3>
				</div>
				<form:form id="searchForm" modelAttribute="tenant"
					action="${ctx}/sys/tenant/" method="post"
					class="form-inline form-search">
					<input id="pageNo" name="pageNo" type="hidden"
						value="${page.pageNo}" />
					<input id="pageSize" name="pageSize" type="hidden"
						value="${page.pageSize}" />
					<div class="cxtj">
						<div class="form-group cxtj_text">
							<label>租户名称：</label>
							<form:input path="name" htmlEscape="false" maxlength="100"
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
					<a href="${ctx}/sys/tenant/form" class="btn btn-blue"
						style="padding: 4px 10px;">新增</a> <input id="btnDel"
						class="btn btn-blue" type="button" style="padding: 4px 10px;"
						value="删除" />
				</div>
				<div class="table-responsive">
					<table id="contentTable"
						class="table table-hover table-bordered table-condensed">
						<thead>
							<tr>
								<th><input type="checkbox" id="checkAll" /></th>
								<th>编号</th>
								<th>租户名称</th>
								<th>简码</th>
								<th>地址</th>
								<th>负责人</th>
								<th>租户描述</th>
								<th>性质</th>
								<th>类别</th>
								<shiro:hasPermission name="sys:tenant:edit">
									<th>操作</th>
								</shiro:hasPermission>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${page.list}" var="tenant">
								<tr>
									<td><input type="checkbox" id="select-${tenant.id}"
										class="list_checkbox" value="${tenant.id}" />
									</td>
									<td>${tenant.sn}</td>
									<td><a href="${ctx}/sys/tenant/form?id=${tenant.id}">
											${tenant.name} </a>
									</td>
									<td>${tenant.code}</td>
									<td title="${tenant.address}">${fns:abbr(tenant.address,15)}</td>
									<td>${tenant.master}</td>
									<td title="${tenant.description}">${fns:abbr(tenant.description,25)}</td>
									<td>${tenant.type}</td>
									<td>${tenant.className}</td>
									<shiro:hasPermission name="sys:tenant:edit">
										<td><c:if test="${tenant.id!='system'}">
												<a
													href="${ctx}/sys/role/form?id=${tenant.id}R0&office.id=${tenant.id}O1">授权</a>
											</c:if>
									</shiro:hasPermission>
								</tr>
							</c:forEach>
						</tbody>
					</table>

				</div>
				<div class="pagination">${page}</div>
			</div>
		</div>
</body>
</html>
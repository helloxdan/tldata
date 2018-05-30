<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>字典管理</title>
<meta name="decorator" content="adminlte" />
<script type="text/javascript">
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
	$(function(){
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
				top.$.jBox.tip('请选择字典！', 'warning');
				return;
			}
			confirmx("确定要删除选中字典吗？",function(){
				loading('系统处理中，请稍候……');
				$.post('${ctx}/sys/dict/del', {
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
</script>
<style>


</style>
</head>
<body>
	<div class="divWrap">
		<div class=" addData">
			<div class="box box-solid">
				<div class="box-header with-border">
					<h3 class="box-title">字典列表</h3>
				</div>
				<form:form id="searchForm" modelAttribute="dict"
					action="${ctx}/sys/dict/" method="post"
					class="form-inline form-search">
					<input id="pageNo" name="pageNo" type="hidden"
						value="${page.pageNo}" />
					<input id="pageSize" name="pageSize" type="hidden"
						value="${page.pageSize}" />
					<div class="cxtj">
						<div class="form-group cxtj_text">
							<label>类型：</label>
							<form:select id="type" path="type" class="form-control">
								<form:option value="" label="" />
								<form:options items="${typeList}" htmlEscape="false" />
							</form:select>
						</div>
						<div class="form-group cxtj_text">
							<label>描述 ：</label>
							<form:input path="description" htmlEscape="false" maxlength="50"
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
					<a href="${ctx}/sys/dict/form?sort=10" class="btn btn-blue"
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
								<th>键值</th>
								<th>标签</th>
								<th>类型</th>
								<th>描述</th>
								<th>排序</th>
								<shiro:hasPermission name="sys:dict:edit">
									<th>操作</th>
								</shiro:hasPermission>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${page.list}" var="dict">
								<tr>
									<td><input type="checkbox" id="select-${dict.id}"
										class="list_checkbox" value="${dict.id}" />
									</td>
									<td>${dict.value}</td>
									<td><a href="${ctx}/sys/dict/form?id=${dict.id}">${dict.label}</a>
									</td>
									<td><a href="javascript:"
										onclick="$('#type').val('${dict.type}');$('#searchForm').submit();return false;">${dict.type}</a>
									</td>
									<td>${dict.description}</td>
									<td>${dict.sort}</td>
									<shiro:hasPermission name="sys:dict:edit">
										<td>
											<%-- <a href="${ctx}/sys/dict/form?id=${dict.id}">修改</a>
											<a href="${ctx}/sys/dict/delete?id=${dict.id}&type=${dict.type}" onclick="return confirmx('确认要删除该字典吗？', this.href)">删除</a> --%>
											<a href="<c:url value='${fns:getAdminPath()}/sys/dict/form?type=${dict.type}&sort=${dict.sort+10}'><c:param name='description' value='${dict.description}'/></c:url>">添加键值</a>
										</td>
									</shiro:hasPermission>
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
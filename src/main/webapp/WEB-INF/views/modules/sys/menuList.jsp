<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>菜单管理</title>
<meta name="decorator" content="adminlte" />
<%@include file="/WEB-INF/views/include/treetable.jsp"%>
<script type="text/javascript">
		$(document).ready(function() {
			var fitstBoxSolidH =  $(".box-solid").eq(0).height();
			
			var muHeight = $(window).height() - fitstBoxSolidH -20 -20;
			$(".minHeight").css('min-height', muHeight);
			$("#treeTable").treeTable({expandLevel : 3}).show();
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
					top.$.jBox.tip('请选择菜单！', 'warning');
					return;
				}
				confirmx("确定要删除选中菜单吗？",function(){
					loading('系统处理中，请稍候……');
					$.post('${ctx}/sys/menu/del', {
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
    	function updateSort() {
			loading('正在提交，请稍等...');
	    	$("#listForm").attr("action", "${ctx}/sys/menu/updateSort");
	    	$("#listForm").submit();
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
					<h3 class="box-title">菜单列表</h3>
				</div>
				<sys:message content="${message}" />
			</div>
		</div>
		<div class=" addData">
			<div class="box box-solid minHeight ">
				<div class="form-group cxtj_text" style="margin-bottom:10px;">
					<a href="${ctx}/sys/menu/form" class="btn" >新增</a>

				<!-- 	<input id="btnReset" class="btn btn-blue" type="reset"
						style="padding: 4px 10px;" value="删除" /> 
							<input id="btnDel" class="btn btn-blue" type="button" value="删除" />-->
				</div>

				<form id="listForm" method="post">
					<div class="table-responsive">
						<table id="treeTable"
							class="table table-hover table-bordered table-condensed ">
							<thead>
								<tr>
									<th><input type="checkbox" id="checkAll" />名称</th>
									
									<th>链接</th>
									<th style="text-align:center;">排序</th>
									<th>可见</th>
									<th>权限标识</th>
									<shiro:hasPermission name="sys:menu:edit">
										<th>操作</th>
									</shiro:hasPermission>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${list}" var="menu">
									<tr id="${menu.id}"
										pId="${menu.parent.id ne '1'?menu.parent.id:'0'}">

										<td nowrap>
									<input type="checkbox" id="select-${menu.id}" class="list_checkbox" value="${menu.id}" /> 
											
									<i class="icon-${not empty menu.icon?menu.icon:' hide'}"></i><a
											href="${ctx}/sys/menu/form?id=${menu.id}">${menu.name}</a>
										</td>
										<td title="${menu.href}">${fns:abbr(menu.href,30)}</td>
										<td style="text-align:center;"><shiro:hasPermission
												name="sys:menu:edit">
												<input type="hidden" name="ids" value="${menu.id}" />
												<input name="sorts" type="text" value="${menu.sort}"
													style="width:50px;margin:0;padding:0;text-align:center;">
											</shiro:hasPermission> <shiro:lacksPermission name="sys:menu:edit">
												${menu.sort}
											</shiro:lacksPermission></td>
										<td>${menu.isShow eq '1'?'显示':'隐藏'}</td>
										<td title="${menu.permission}">${fns:abbr(menu.permission,30)}</td>
										<shiro:hasPermission name="sys:menu:edit">
											<td nowrap>
												<a href="${ctx}/sys/menu/form?parent.id=${menu.id}">添加下级菜单</a>
												<%-- <a href="${ctx}/sys/menu/form?id=${menu.id}">修改</a>--%>
												<a href="${ctx}/sys/menu/delete?id=${menu.id}"
												onclick="return confirmx('要删除该菜单及所有子菜单项吗？', this.href)">删除</a> 
											</td>
										</shiro:hasPermission>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<shiro:hasPermission name="sys:menu:edit">
							<div class="form-actions pagination-left">
								<input id="btnSubmit" class="btn btn-blue" type="button"
									value="保存排序" onclick="updateSort();" />
								<c:if test="${fns:getUser().admin}">
									<input id="btnSubmit" class="btn btn-blue" type="button"
										value="重置所有租户的默认授权菜单" onclick="alert('开发中……');" />
								</c:if>
							</div>
						</shiro:hasPermission>
					</div>
				</form>

			</div>

		</div>
	</div>


</body>
</html>
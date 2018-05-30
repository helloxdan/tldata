]<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>角色管理</title>
<meta name="decorator" content="adminlte" />
<script>
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
				top.$.jBox.tip('请选择记录！', 'warning');
				return;
			}
			confirmx("确定要删除选中记录吗？",function(){
				loading('系统处理中，请稍候……');
				$.post('${ctx}/sys/role/del', {
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
					<h3 class="box-title">角色列表</h3>
				</div>
				<sys:message content="${message}" />
			</div>
		</div>
		<div class=" addData">

			<div class="box box-solid minHeight">
				<div class="form-group cxtj_text">
					<a href="${ctx}/sys/role/form" class="btn" >新增</a>

					<input id="btnDel" class="btn btn-blue" type="button"
						style="padding: 4px 10px;" value="删除" />
				</div>
				<div class="table-responsive">
					<table id="contentTable"
						class="table table-hover table-bordered table-condensed">
						<thead>
							<tr>
								<th><input type="checkbox" id="checkAll" />
								<th>角色名称</th>
								<th>英文名称</th>
								<th>归属机构</th>
								<th>数据范围</th>

							</tr>
						</thead>
						<c:forEach items="${list}" var="role">
							<tr>
								<td><input type="checkbox" id="select-${role.id}"
									class="list_checkbox" value="${role.id}" /></td>
								<td><a href="form?id=${role.id}">${role.name}</a></td>
								<td><a href="form?id=${role.id}">${role.enname}</a></td>
								<td>${role.office.name}</td>
								<td>${fns:getDictLabel(role.dataScope, 'sys_data_scope',
									'无')}</td>

							</tr>
						</c:forEach>
					</table>
				</div>
			</div>
		</div>
	</div>


</body>
</html>
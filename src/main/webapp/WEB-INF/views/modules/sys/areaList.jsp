<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>区域管理</title>
<meta name="decorator" content="adminlte" />
<%@include file="/WEB-INF/views/include/treetable.jsp"%>
<script type="text/javascript">
		$(document).ready(function() {
			var fitstBoxSolidH =  $(".box-solid").eq(0).height();
			var muHeight = $(window).height() - fitstBoxSolidH -20 -20 -20;
			$(".minHeight").css('min-height', muHeight);
			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			var data = ${fns:toJson(list)}, rootId = "0";
			addRow("#treeTableList", tpl, data, rootId, true);
			$("#treeTable").treeTable({expandLevel : 5});

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
					$.post('${ctx}/sys/area/del', {
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
		function addRow(list, tpl, data, pid, root){
			for (var i=0; i<data.length; i++){
				var row = data[i];
				if ((${fns:jsGetVal('row.parentId')}) == pid){
					$(list).append(Mustache.render(tpl, {
						dict: {
							type: getDictLabel(${fns:toJson(fns:getDictList('sys_area_type'))}, row.type)
						}, pid: (root?0:pid), row: row
					}));
					addRow(list, tpl, data, row.id);
				}
			}
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
					<h3 class="box-title">区域列表</h3>
				</div>
				<sys:message content="${message}" />
			</div>
		</div>
		<div class=" addData">
			<div class="box box-solid minHeight">
				<div class="form-group cxtj_text" style="margin-bottom:10px;">
					<a href="${ctx}/sys/area/form" class="btn" >新增</a> 
					<input id="btnDel" class="btn btn-blue" type="button"
						style="padding: 4px 10px;" value="删除" />
				</div>
				<div class="table-responsive">
					<table id="treeTable"
						class="table table-hover table-bordered table-condensed">
						<thead>
							<tr>
								<th><input type="checkbox" id="checkAll" />区域名称</th>

								<th>区域编码</th>
								<th>区域类型</th>
								<th>备注</th>
								<shiro:hasPermission name="sys:area:edit">
									<th>操作</th>
								</shiro:hasPermission>
							</tr>
						</thead>
						<tbody id="treeTableList"></tbody>
					</table>
				</div>
			</div>
		</div>
	</div>


	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			
			<td><input type="checkbox" id="select-{{row.id}}" class="list_checkbox" value="{{row.id}}" /><a href="${ctx}/sys/area/form?id={{row.id}}">{{row.name}}</a></td>
			<td>{{row.code}}</td>
			<td>{{dict.type}}</td>
			<td>{{row.remarks}}</td>
			<shiro:hasPermission name="sys:area:edit"><td>
				
				
				<a href="${ctx}/sys/area/form?parent.id={{row.id}}">添加下级区域</a> 
			</td></shiro:hasPermission>
		</tr>
	</script>
</body>
</html>
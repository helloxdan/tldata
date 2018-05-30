<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>机构管理</title>
<meta name="decorator" content="adminlte" />
<%@include file="/WEB-INF/views/include/treetable.jsp"%>
<script type="text/javascript">
		$(document).ready(function() {
			var topHeight = $(".box-solid").eq(0).height();
			var muHeight = $(window).height() - topHeight -20 -20;
			$(".minHeight").css('min-height',muHeight);
			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			var data = ${fns:toJson(list)}, rootId = "${not empty office.id ? office.id : '0'}";
			addRow("#treeTableList", tpl, data, rootId, true);
			$("#treeTable").treeTable({expandLevel : 5});
			
			$("#checkAll").click(function() {
				var checked = $(this).is(':checked'); 
				if (checked) {
					$("input[type='checkbox'].list_checkbox").each(function() {
						this.checked = true;
					});
				} else {
					$("input[type='checkbox'].list_checkbox").each(function() {
						this.checked = false;
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
					top.$.jBox.tip('请选择机构！', 'warning');
					return;
				}
				confirmx("确定要删除选中机构吗？",function(){
					loading('系统处理中，请稍候……');
					$.post('${ctx}/sys/office/del', {
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
							type: getDictLabel(${fns:toJson(fns:getDictList('sys_office_type'))}, row.type)
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
					<h3 class="box-title">机构列表</h3>
				</div>
				<%-- <form:form id="searchForm" modelAttribute="report"
					action="${ctx}/sys/office/" method="post"
					class="form-inline form-search">
					<input id="pageNo" name="pageNo" type="hidden"
						value="${page.pageNo}" />
					<input id="pageSize" name="pageSize" type="hidden"
						value="${page.pageSize}" />

					<div class="cxtj">
						<div class="form-group cxtj_text">
							<label>机构名称：</label>
							<form:input path="name" htmlEscape="false" maxlength="100"
								class="form-control" />
						</div>
						<div class="form-group cxtj_text">
							<label>机构类型：</label>
							<form:input path="type" htmlEscape="false" maxlength="100"
								class="form-control" />
						</div>
						<div class="form-group cxtj_text">
							<label>机构编码：</label>
							<form:input path="code" htmlEscape="false" maxlength="100"
								class="form-control" />
						</div>
						<div class="form-group cxtj_text">
							<input id="btnSubmit" class="btn btn-blue" type="submit"
								value="查询" />
						</div>
						<div class="form-group cxtj_text">
							<input id="btnReset" class="btn" type="reset" value="重置" />
						</div>
					</div>
				</form:form> --%>
				<sys:message content="${message}" />
			</div>
		</div>
		<div class=" addData">
			<div class="box box-solid minHeight marginBottom0">
				<div class="form-group cxtj_text" style="margin-bottom:10px;">
					<a href="${ctx}/sys/office/form?parent.id=${office.id}" class="btn" >新增</a>

					<!-- <input id="btnReset" class="btn btn-blue" type="reset"
						style="padding: 4px 10px;" value="删除" /> -->
						<input id="btnDel" class="btn btn-blue" type="button" value="删除" />
				</div>
				<div class="table-responsive">
					<table id="contentTable"
						class="table table-hover table-bordered table-condensed">
						<thead>
							<tr>
								<th><input type="checkbox" id="checkAll" />
								</th>
								<th>机构名称</th>
								<th>归属区域</th>
								<th>机构编码</th>
								<th>机构类型</th>
								<th>备注</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody id="treeTableList"></tbody>
					</table>
					<script type="text/template" id="treeTableTpl">
 
						<tr id="{{row.id}}" pId="{{pid}}">
							<td><input type="checkbox" id="select-{{row.id}}"
									class="list_checkbox" value="{{row.id}}" />
							</td>
							<td><a href="${ctx}/sys/office/form?id={{row.id}}">{{row.name}}</a></td>
							<td>{{row.area.name}}</td>
							<td>{{row.code}}</td>
							<td>{{dict.type}}</td>
							<td>{{row.remarks}}</td>
							<shiro:hasPermission name="sys:office:edit"><td>
								
								<a href="${ctx}/sys/office/form?parent.id={{row.id}}">添加下级机构</a> 
							</td></shiro:hasPermission>
							
						</tr>
					</script>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
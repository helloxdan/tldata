<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>树结构管理</title>
<meta name="decorator" content="adminlte" />
<%@include file="/WEB-INF/views/include/treetable.jsp"%>
<script type="text/javascript">
		$(document).ready(function() {
			var fitstBoxSolidH =  $(".box-solid").eq(0).height();
			var muHeight = $(window).height() - fitstBoxSolidH -20 -20 -20;
			$(".minHeight").css('min-height', muHeight);
			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			var data = ${fns:toJson(list)}, ids = [], rootIds = [];
			for (var i=0; i<data.length; i++){
				ids.push(data[i].id);
			}
			ids = ',' + ids.join(',') + ',';
			for (var i=0; i<data.length; i++){
				if (ids.indexOf(','+data[i].parentId+',') == -1){
					if ((','+rootIds.join(',')+',').indexOf(','+data[i].parentId+',') == -1){
						rootIds.push(data[i].parentId);
					}
				}
			}
			for (var i=0; i<rootIds.length; i++){
				addRow("#treeTableList", tpl, data, rootIds[i], true);
			}
			$("#treeTable").treeTable({expandLevel : 5});
		});
		function addRow(list, tpl, data, pid, root){
			for (var i=0; i<data.length; i++){
				var row = data[i];
				if ((${fns:jsGetVal('row.parentId')}) == pid){
					$(list).append(Mustache.render(tpl, {
						dict: {
						blank123:0}, pid: (root?0:pid), row: row
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
					<h3 class="box-title">树结构列表</h3>
				</div>
				<form:form id="searchForm" modelAttribute="testTree"
					action="${ctx}/test/testTree/" method="post"
					class="form-inline form-search">
					<div class="cxtj">
						<div class="form-group cxtj_text">
							<label>名称：</label>
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
					<a href="${ctx}/test/testTree/form" class="btn btn-blue"
						style="padding: 4px 10px;">新增</a> <input id="btnReset"
						class="btn btn-blue" type="reset" style="padding: 4px 10px;"
						value="删除" />
				</div>
				<div class="table-responsive">
					<table id="treeTable"
						class="table table-hover table-bordered table-condensed">
						<thead>
							<tr>
								<th><input type="checkbox" id="checkAll" />
								</th>
								<th>名称</th>
								<th>排序</th>
								<th>更新时间</th>
								<th>备注信息</th>
								<!-- <shiro:hasPermission name="test:testTree:edit">
									<th>操作</th>
								</shiro:hasPermission> -->
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
			<td>
				<input type="checkbox" id="select-${asset.id}" class="asset_checkbox" value="${asset.id}" />
			</td>
			<td>
				<a href="${ctx}/test/testTree/form?id={{row.id}}">
					{{row.name}}
				</a>
			</td>
			<td>
				{{row.sort}}
			</td>
			<td>
				{{row.updateDate}}
			</td>
			<td>
				{{row.remarks}}
			</td>
			
		</tr>
	</script>
</body>
</html>
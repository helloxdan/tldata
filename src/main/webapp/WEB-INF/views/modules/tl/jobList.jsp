<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>工作任务管理</title>
	<meta name="decorator" content="adminlte"/>
	<script type="text/javascript">
		$(document).ready(function() {
			var muHeight = $(window).height() - 170;
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
					$.post('${ctx}/tl/job/del', {
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
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }

	</script>
</head>
<body>
	<div class="addData">
	<div class="box box-solid">
			<div class="box-header with-border">
				<h3 class="box-title">工作任务列表</h3>
			</div>
	<form:form id="searchForm" modelAttribute="job" action="${ctx}/tl/job/" method="post" class="form-inline form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/> 
		<div class="cxtj">
			<div class="form-group cxtj_text"><label>任务名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>群组ID：</label>
				<form:input path="groupId" htmlEscape="false" maxlength="11" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>来源群ID：</label>
				<form:input path="fromGroupId" htmlEscape="false" maxlength="11" class="form-control"/>
			</div>
			<div class="form-group cxtj_text">
						<input id="btnSubmit" class="btn btn-blue" type="submit"
							value="查询" />
						<input id="btnReset" class="btn btn-blue" type="reset" value="重置" />
					</div>
			</div>		 
	</form:form>
	<sys:message content="${message}"/>
	</div>
	</div>
	
	<div class=" addData">
		<div class="box box-solid minHeight">
			<div class="form-group cxtj_text">
				<a href="${ctx}/tl/job/form" class="btn btn-blue">新增</a>
				<input id="btnDel" class="btn btn-blue" type="button" value="删除" />
			</div>	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" id="checkAll" /></th>
				<th class="sort-column a.id">任务ID</th>
				<th class="sort-column a.name">任务名称</th> 
				<th class="sort-column a.group_link">目标群组link</th>
				<th class="sort-column a.usernum">用户数</th>
				<th class="sort-column a.account_num">需要账号数量</th>
				<th class="sort-column a.day">几天完成</th>
				<th class="sort-column a.boss">老板</th>
				<th class="sort-column a.status">状态</th>
				<th >操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="job">
			<tr>
				<td><input type="checkbox" id="select-${job.id}"
								class="list_checkbox" value="${job.id}" />
				</td>
				<td><a href="${ctx}/tl/job/form?id=${job.id}">
					${job.id}
				</a></td>
				<td>
					${job.name}
				</td>
				 
				<td>
					${job.groupUrl}
				</td>
			 
				<td>
					${job.usernum}
				</td>
				<td>
					${job.accountNum}
				</td>
				<td>
					${job.day}
				</td>
				<td>
					${job.boss}
				</td>
				 <td>
					${job.status}
				</td>
				<td>
						<a href="${ctx}/tl/jobTask/dispatch?jobId=${job.id}">分配任务</a>
						
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div>${page}</div>
	</div>
	</div>
</body>
</html>
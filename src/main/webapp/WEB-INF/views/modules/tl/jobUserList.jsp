<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>邀请用户管理</title>
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
					$.post('${ctx}/tl/jobUser/del', {
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
				<h3 class="box-title">邀请用户列表</h3>
			</div>
	<form:form id="searchForm" modelAttribute="jobUser" action="${ctx}/tl/jobUser/" method="post" class="form-inline form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/> 
		<div class="cxtj">
			<div class="form-group cxtj_text"><label>任务ID：</label>
				<form:input path="jobId" htmlEscape="false" maxlength="64" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>登录账号：</label>
				<form:input path="account" htmlEscape="false" maxlength="64" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>用户来源群组：</label>
				<form:input path="fromGroup" htmlEscape="false" maxlength="11" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>用户ID：</label>
				<form:input path="userid" htmlEscape="false" maxlength="11" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>用户名：</label>
				<form:input path="username" htmlEscape="false" maxlength="100" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>是否已邀请：</label>
				<form:select path="status" class="form-control">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
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
				<a href="${ctx}/tl/jobUser/form" class="btn btn-blue">新增</a>
				<input id="btnDel" class="btn btn-blue" type="button" value="删除" />
			</div>	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" id="checkAll" /></th>
				<th class="sort-column a.job_id">任务ID</th>
				<th class="sort-column a.account">登录账号</th>
				<th class="sort-column a.from_group">用户来源群组</th>
				<th class="sort-column a.userid">用户ID</th>
				<th class="sort-column a.username">用户名</th>
				<th class="sort-column a.user_hash">访问码</th>
				<th class="sort-column a.status">是否已邀请</th>
				<th class="sort-column a.update_date">update_date</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jobUser">
			<tr>
				<td><input type="checkbox" id="select-${jobUser.id}"
								class="list_checkbox" value="${jobUser.id}" />
				</td>
				<td><a href="${ctx}/tl/jobUser/form?id=${jobUser.id}">
					${jobUser.jobId}
				</a></td>
				<td>
					${jobUser.account}
				</td>
				<td>
					${jobUser.fromGroup}
				</td>
				<td>
					${jobUser.userid}
				</td>
				<td>
					${jobUser.username}
				</td>
				<td>
					${jobUser.userHash}
				</td>
				<td>
					${fns:getDictLabel(jobUser.status, 'yes_no', '')}
				</td>
				<td>
					<fmt:formatDate value="${jobUser.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
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
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>群组管理</title>
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
					$.post('${ctx}/tl/group/del', {
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
		
		function runJob(run){
			var url="${ctx}/tl/group/startSchedule";
			if(!run){ 
				url="${ctx}/tl/group/stopSchedule";
			}
			$.post(url, { }, function(result) {
				if (result.success) { 
					top.$.jBox.tip(result.data, 'info');
					//刷新页面
					document.location.reload();
				}else{
					top.$.jBox.tip(result.msg, 'warning');
				}
			});
		}
		
	</script>
</head>
<body>
	<div class="addData">
	<div class="box box-solid">
			<div class="box-header with-border">
				<h3 class="box-title">群组列表</h3>
			</div>
	<form:form id="searchForm" modelAttribute="group" action="${ctx}/tl/group/" method="post" class="form-inline form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/> 
		<div class="cxtj">
			<div class="form-group cxtj_text"><label>群组名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="200" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>邀请link：</label>
				<form:input path="url" htmlEscape="false" maxlength="100" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>是否频道：</label>
				<form:select path="isChannel" class="form-control">
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
				<a href="${ctx}/tl/group/form" class="btn btn-blue">新增</a>
				<input id="btnDel" class="btn btn-blue" type="button" value="删除" />
				|<a href="javascript:runJob(true)" class="btn btn-blue">启动采集用户调度</a>
				<a href="javascript:runJob(false)" class="btn btn-blue">关闭调度</a>
			</div>	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" id="checkAll" /></th>
				<th class="sort-column a.id">群组ID</th>
				<th class="sort-column a.name">群组名称</th>
				<th class="sort-column a.url">邀请link</th>
				<th class="sort-column a.is_channel">是否频道</th>
				<th class="sort-column a.usernum">用户数</th>
				<th class="sort-column a.update_num">更新次数</th>
				<th class="sort-column a.offset_">offset</th>
				<th class="">是否被剔除</th>
				<th >操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="group">
			<tr>
				<td><input type="checkbox" id="select-${group.id}"
								class="list_checkbox" value="${group.id}" />
				</td>
				<td><a href="${ctx}/tl/group/form?id=${group.id}">
					${group.id}
				</a></td>
				<td>
					${group.name}
				</td>
				<td>
					${group.url}
				</td>
				<td>
					${fns:getDictLabel(group.isChannel, 'yes_no', '否')}
				</td>
				<td>
					${group.usernum}
				</td>
				<td>
					${group.updateNum}
				</td>
				<td>
					${group.offset}
				</td>
				<td>
					${fns:getDictLabel(group.out, 'yes_no', '否')}
				</td>
				 
				<td> 
					<%--  <a href="${ctx}/tl/job/form?groupId=${group.id}">新建任务</a>
					  <a href="${rctx}/api/tl/grepUsers?phone=8618566104318&chatId=${group.id}">拉取现有用户</a>
					  <a href="${rctx}/api/tl/groupInfo?chatId=${group.id}">详情</a> --%>
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
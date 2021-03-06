<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>系统消息管理</title>
	<meta name="decorator" content="adminlte"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#addSms').click(function(){
				window.location='${ctx}/sys/msg/form?type=4';
			});
			$('#addSysMsg').click(function(){
				window.location='${ctx}/sys/msg/form?type=3';
			});
			
			var muHeight = $(window).height() -170;
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
					$.post('${ctx}/sys/msg/del', {
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
	<div class=" addData">
		<div class="box box-solid ">
			<div class="box-header with-border">
				<h3 class="box-title">消息列表</h3>
			</div>
	<form:form id="searchForm" modelAttribute="msg" action="${ctx}/sys/msg/" method="post" class="form-inline form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<form:hidden path="sendState" />
		<div class="form-group"><label>类型：</label>
				<form:select path="type" class="form-control">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('msg_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
			<div class="form-group"><label>标题：</label>
				<form:input path="title" htmlEscape="false" maxlength="1000" class="form-control"/>
			</div>
			<div class="form-group">
				<input id="btnSubmit" class="btn  btn-blue" type="submit" value="查询" />
			</div>
			<div class="form-group">
				<input id="btnReset" class="btn btn-blue" type="reset" value="重置" />
			</div>
	</form:form>
	<sys:message content="${message}"/>
	</div></div>
	
	<div class=" addData ">
		<div class="box box-solid minHeight">
			<div class="form-group cxtj_text" style="margin-bottom:10px;">
				<shiro:hasPermission name="sys:msg:edit">
				<a  id="addSms" href="${ctx}/sys/msg/form" class="btn btn-blue"
					style="padding: 4px 10px;">新增短信</a> 
					<a  id="addSysMsg" href="${ctx}/sys/msg/form" class="btn btn-blue"
					style="padding: 4px 10px;">新增站内信</a> 
					<input id="btnDel" class="btn btn-blue" type="button" value="删除" />
					</shiro:hasPermission>
			</div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" id="checkAll" /></th>
				<th>标题</th>
				<th>接收方</th>
				<th class="sort-column user_type">用户类型</th>
				<th class="sort-column type">消息类型</th>
				<th>内容</th> 
				<th>推送时间</th>
				<th>推送结果</th>
				<th>备注</th>
				 <th>操作</th> 
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="msg">
			<tr>
				<td><input type="checkbox" id="select-${msg.id}"
								class="list_checkbox" value="${msg.id}" />
							</td>
				<td>
					<c:if test="${  msg.sendState !='0' }">
					<a href="${ctx}/sys/msg/view?id=${msg.id}">${msg.title}</a>
					</c:if>
					<c:if test="${  msg.sendState =='0' }">
					<a href="${ctx}/sys/msg/form?id=${msg.id}">${msg.title}</a>
					</c:if>
				</td>
				<td>
					${msg.userDesc}
				</td>
				<td>
					${msg.userType}
				</td>
				<td>
					${msg.typeLabel}
				</td>
				<td title="${msg.content}">${fns:abbr(msg.content,25)}
				</td>  
				<td style='<c:if test="${msg.isTask}">color:red;</c:if>'>
					<c:if test="${not empty msg.sendTime}">
					<fmt:formatDate value="${msg.sendTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</c:if>
					<c:if test="${empty msg.sendTime}">
					<fmt:formatDate value="${msg.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</c:if>
				</td>
				<td>
					${msg.sendStateLabel}
				</td>  
				<td>
					${msg.remark}
				</td> 
    			<td>
    			<c:if test="${  msg.sendState !='0' }">
    			<a href="${ctx}/sys/msg/view?id=${msg.id}">查看内容</a></c:if>
    			<c:if test="${msg.sendState=='0' }">
				<shiro:hasPermission name="sys:msg:edit">
    				<a href="${ctx}/sys/msg/form?id=${msg.id}">修改</a>
					<a href="${ctx}/sys/msg/delete?id=${msg.id}" onclick="return confirmx('确认要删除该系统消息吗？', this.href)">删除</a>
				</shiro:hasPermission>
				</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	</div></div>
</body>
</html>
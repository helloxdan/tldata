<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>消息模板管理</title>
	<meta name="decorator" content="adminlte"/>
	<script type="text/javascript">
		$(document).ready(function() {
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
					top.$.jBox.tip('请选择模板！', 'warning');
					return;
				}
				confirmx("确定要删除选中模板吗？",function(){
					loading('系统处理中，请稍候……');
					$.post('${ctx}/sys/msgTpl/del', {
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
				<h3 class="box-title">消息模板列表</h3>
			</div>
	<form:form id="searchForm" modelAttribute="msgTpl" action="${ctx}/sys/msgTpl/" method="post" class="form-inline form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		 
			<div class="form-group"><label>消息类型：</label>
				<form:select path="type" class="form-control">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('msg_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
			<div class="form-group"><label>模板内容：</label>
				<form:input path="content" htmlEscape="false" maxlength="1000" class="form-control"/>
			</div>
			<div class="form-group">
				<input id="btnSubmit" class="btn  btn-blue" type="submit" value="查询" />
			</div>
			<div class="form-group">
				<input id="btnReset" class="btn btn-blue" type="reset" value="重置" />
			</div>		 
	</form:form>
	<sys:message content="${message}"/>
		</div>
	</div>
	
	<div class=" addData ">
		<div class="box box-solid minHeight">
			<div class="form-group cxtj_text" style="margin-bottom:10px;">
				<shiro:hasPermission name="sys:msgTpl:edit">
				<a href="${ctx}/sys/msgTpl/form" class="btn btn-blue"
					style="padding: 4px 10px;">新增</a> 
					<input id="btnDel" class="btn btn-blue" type="button" value="删除" />
					</shiro:hasPermission>
			</div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" id="checkAll" /></th>
				<th>消息类型</th>
				<th>模板标题</th>
				<th>模板内容</th>
				<th>是否关闭</th>
				<th>更新时间</th>
				<shiro:hasPermission name="sys:msgTpl:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="msgTpl">
			<tr>
				<td><input type="checkbox" id="select-${msgTpl.id}"
								class="list_checkbox" value="${msgTpl.id}" />
							</td>
				<td> ${fns:getDictLabel(msgTpl.type, 'msg_type', '')}
				 </td>
				<td>
					<a href="${ctx}/sys/msgTpl/form?id=${msgTpl.id}">
					${msgTpl.title}
				</a>
				</td>
				<td>
					${msgTpl.content}
				</td>
				<td>
					${fns:getDictLabel(msgTpl.status, 'cms_status', '')}
				</td>
				<td>
					<fmt:formatDate value="${msgTpl.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sys:msgTpl:edit"><td>
    				<a href="${ctx}/sys/msgTpl/form?id=${msgTpl.id}">修改</a>
					<a href="${ctx}/sys/msgTpl/delete?id=${msgTpl.id}" onclick="return confirmx('确认要删除该消息模板吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div>${page}</div>
	</div>
	</div>
</body>
</html>
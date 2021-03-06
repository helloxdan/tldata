<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>好友用户管理</title>
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
					$.post('${ctx}/tl/tlUser/del', {
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
				<h3 class="box-title">好友用户列表</h3>
			</div>
	<form:form id="searchForm" modelAttribute="tlUser" action="${ctx}/tl/tlUser/" method="post" class="form-inline form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/> 
		<div class="cxtj">
			<div class="form-group cxtj_text"><label>用户id：</label>
				<form:input path="id" htmlEscape="false" maxlength="11" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>用户名：</label>
				<form:input path="username" htmlEscape="false" maxlength="50" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>语言区：</label>
				<form:input path="langcode" htmlEscape="false" maxlength="50" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>最新发言时间：</label>
				<div class="input-group">
				<input name="beginMsgTime" type="text" readonly="readonly" maxlength="20" class="form-control"
					value="<fmt:formatDate value="${tlUser.beginMsgTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="endMsgTime" type="text" readonly="readonly" maxlength="20" class="form-control"
					value="<fmt:formatDate value="${tlUser.endMsgTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
				 </div>
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
				<a href="${ctx}/tl/tlUser/form" class="btn btn-blue">新增</a>
				<input id="btnDel" class="btn btn-blue" type="button" value="删除" />
			</div>	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" id="checkAll" /></th>
				<th class="sort-column a.id">用户id</th>
				<th class="sort-column a.username">用户名</th>
				<th class="sort-column a.firstname">firstname</th>
				<th class="sort-column a.lastname">lastname</th>
				<th class="sort-column a.language">语言区</th>
				<th class="sort-column a.msg_num">消息数</th>
				<th class="sort-column a.msg_time">最新发言时间</th>
				<th class="sort-column a.star">评级</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="tlUser">
			<tr>
				<td><input type="checkbox" id="select-${tlUser.id}"
								class="list_checkbox" value="${tlUser.id}" />
				</td>
				<td><a href="${ctx}/tl/tlUser/form?id=${tlUser.id}">
					${tlUser.id}
				</a></td>
				<td>
					${tlUser.username}
				</td>
				<td>
					${tlUser.firstname}
				</td>
				<td>
					${tlUser.lastname}
				</td>
				<td>
					${tlUser.langcode}
				</td>
				<td>
					${tlUser.msgNum}
				</td>
				<td>
					<fmt:formatDate value="${tlUser.msgTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${tlUser.star}
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
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>登录账号管理</title>
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
					$.post('${ctx}/tl/account/del', {
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
		//批量增加用户
		function addBatch(){
			var url="${ctx}/tl/account/addBatch";
			 var num = window.prompt('输入新增数量', '');
			if(!num) return;
		//alert('-'+num+'-');
			 $.post(url,{num:num},function(res){
				 if(res.success){
					 showTip("添加注册操作完成");
					 document.location.reload();
				 }else{
					 alert(res.msg);
				 }
			 });
		}
		
		function initAccount(){
			var url="${rctx}/api/tl/account/init"; 
			 $.post(url,{},function(res){
				 if(res.success){
					 showTip("操作完成");
					 document.location.reload();
				 }else{
					 alert(res.msg);
				 }
			 });
		}
		
		function startBatch(){
			var url="${rctx}/api/tl/startBatch";
			 var num = window.prompt('输入启动账号数量', '');
			 if(!num) return;
			 
			 $.post(url,{num:num},function(res){
				 if(res.success){
					 showTip("操作完成");
					 //document.location.reload();
				 }else{
					 alert(res.msg);
				 }
			 });
		}
		function stop(id){
			var url="${rctx}/api/tl/stop";
			 $.post(url,{phone:id},function(res){
				 if(res.success){
					 showTip("stop操作完成");
					 //document.location.reload();
				 }else{
					 alert(res.msg);
				 }
			 });
		}
		
		function setAdmin(account){
			var url="${ctx}/tl/account/setAdmin";
			 
			 $.post(url,{phone:account},function(res){
				 if(res.success){
					 showTip("操作完成");
					// document.location.reload();
				 }else{
					 alert(res.msg);
				 }
			 });
		}
		//加入群组
		function joinGroup(account){
			var url="${rctx}/api/tl/importInvite";
			 var link = window.prompt('输入群组邀链接', '');
			 if(!link) return;
			 link=encodeURI(link);
			 $.post(url,{phone:account,url:link},function(res){
				 if(res.success){
					 showTip("操作完成");
					// document.location.reload();
				 }else{
					 alert(res.msg);
				 }
			 });
		}
		function signup(account){
			var url="${rctx}/api/tl/start";
			 $.post(url,{phone:account},function(res){
				 if(res.success){
					 showTip("提交登录操作完成");
					 if(res.data!='ALREADYLOGGED'){
						 var code = window.prompt('输入验证码', '');
						 if(code=='') return;
						 $.post('${rctx}/api/tl/setAuthCode',{phone:account,code:code},function(res){
							 if(res.success){
								 showTip("提交验证码操作完成");
								 document.location.reload();
							 }else{
								 alert(res.msg);
							 }
						 })
					 }else{
						 document.location.reload();
					 }
				 }else{
					 alert(res.msg);
				 }
			 }).done(function(){
				/*  var code = window.prompt('输入验证码', '');
				 $.post('${rctx}/api/tl/setAuthCode',{phone:account,code:code},function(res){
					 if(res.success){
						 alert("操作完成");
					 }else{
						 alert(res.msg);
					 }
				 }) */
			 }).fail(function() {
				    alert( "error" );
			  });
		}
		function signin(account){
			var url="${rctx}/api/tl/start";
			 $.post(url,{phone:account },function(res){
				 if(res.success){
					 showTip("提交登录操作完成");
					 if(res.data!='ALREADYLOGGED'){
						 var code = window.prompt('输入验证码', '');
						 if(code=='') return;
						 $.post('${rctx}/api/tl/setAuthCode',{phone:account,code:code},function(res){
							 if(res.success){
								 showTip("提交验证码操作完成");
								 document.location.reload();
							 }else{
								 alert(res.msg);
							 }
						 })
					 }else{
						 document.location.reload();
					 }
				 }else{
					 alert(res.msg);
				 }
			 }).done(function(){
				
				 
			 }).fail(function() {
				    alert( "error" );
			  });
		}
	</script>
</head>
<body>
	<div class="addData">
	<div class="box box-solid">
			<div class="box-header with-border">
				<h3 class="box-title">登录账号列表</h3>
			</div>
	<form:form id="searchForm" modelAttribute="account" action="${ctx}/tl/account/" method="post" class="form-inline form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/> 
		<div class="cxtj">
		<div class="form-group cxtj_text"><label> 手机号：</label>
				<form:input path="id" htmlEscape="false" maxlength="15" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>用户名：</label>
				<form:input path="name" htmlEscape="false" maxlength="15" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>用户状态：</label>
				<form:select path="role" class="form-control">
					<form:option value="" label=""/> 
					<form:option value="0" label="正常"/>
					<form:option value="1" label="管理员"/>
					<form:option value="2" label="异常"/>
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
				<a href="${ctx}/tl/account/form" class="btn btn-blue hide">新增</a>
					<a onclick="addBatch()"class="btn btn-blue">新增注册</a>
				<input id="btnDel" class="btn btn-blue" type="button" value="删除" />
				
					<a onclick="initAccount()"class="btn btn-blue">账号初始状态</a>
						<a onclick="startBatch()" class="btn btn-blue ">批量启动</a>
			</div>	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" id="checkAll" /></th>
				<th class="sort-column a.id">手机号</th>
				<th class="sort-column a.name">账号名</th>
				<th class="sort-column a.login_date">最近登录时间</th>
				<th class="sort-column a.status">账号状态</th>
				<th class="sort-column a.usernum">储备用户数量</th>
				<th class="sort-column a.groupnum">群组数量</th>
				<th class="sort-column a.role_">管理员</th>
				<th class="sort-column a.update_date">更新时间</th>
				<th >操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="account">
			<tr>
				<td><input type="checkbox" id="select-${account.id}"
								class="list_checkbox" value="${account.id}" />
				</td>
				<td><a href="${ctx}/tl/account/form?id=${account.id}">
					${account.id}
				</a></td>
				<td>
					${account.name}
				</td>
				<td>
					<fmt:formatDate value="${account.loginDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(account.status, 'account_status', '')}
				</td>
				<td>
					${account.usernum}
				</td>
				<td>
					${account.groupnum}
				</td>
				<td>
					${account.role}
				</td>
				<td>
					<fmt:formatDate value="${account.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td> <c:if test="${account.status=='none' }">
					 <a href="javascript:signup('${account.id}')">注册</a>
					  </c:if>
					 <c:if test="${account.status=='ready' }">
					 <a href="javascript:signin('${account.id}')">启动</a>
					 </c:if>
					 <c:if test="${account.status=='run' }">
					 <a href="javascript:joinGroup('${account.id}')">入群</a>
					 <a href="javascript:stop('${account.id}')">停止</a>
					 </c:if>
					 <a href="javascript:setAdmin('${account.id}')">管理员</a>
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
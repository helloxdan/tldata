<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>邀请用户管理</title>
	<meta name="decorator" content="adminlte"/>
	<script type="text/javascript">
		$(document).ready(function() {
			var muHeight = $(window).height() - 20;
			$(".minHeight").css('min-height',muHeight);
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.hasClass('groupInput') || element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
						if(error.text()=='')
							 error.html('必填信息');
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<div class="addData">
		<div class="box box-solid minHeight">
	<form:form id="inputForm" modelAttribute="jobUser" action="${ctx}/tl/jobUser/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		
			<div class="box-header with-border">
				<h3 class="box-title">邀请用户-
				<shiro:hasPermission name="tl:jobUser:edit">${not empty jobUser.id ? '修改':'添加'}</shiro:hasPermission>
				<shiro:lacksPermission name="tl:jobUser:edit">查看</shiro:lacksPermission>
				</h3>
				<div class="box-tools pull-right">
					<shiro:hasPermission name="tl:jobUser:edit"><input id="btnSubmit" class="btn btn-blue" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn btn-blue" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</div>
			<!-- /.box-header -->
			<!-- form start -->
			<div class="box-body">
			<div class="panel panel-default">
			   <div class="panel-body">		
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">任务ID：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="jobId" htmlEscape="false" maxlength="64" class="form-control "/>
			</div>
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">登录账号：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="account" htmlEscape="false" maxlength="64" class="form-control "/>
			</div>
			</div>
			<div class="form-group">
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">用户来源群组：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="fromGroup" htmlEscape="false" maxlength="11" class="form-control  digits"/>
			</div>
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">用户ID：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="userid" htmlEscape="false" maxlength="11" class="form-control  digits"/>
			</div>
			</div>
			<div class="form-group">
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">用户名：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="username" htmlEscape="false" maxlength="100" class="form-control "/>
			</div>
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">访问码：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="userHash" htmlEscape="false" maxlength="20" class="form-control  digits"/>
			</div>
			</div>
			<div class="form-group">
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">是否已邀请：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:select path="status" class="form-control ">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
	 	</div><!-- /.row col -->
		</div>
	 	</div><!-- /.panel -->
		</div>
			<!-- /.box-body --> 
	</form:form>
	</div>
	</div>
</body>
</html>
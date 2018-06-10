<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>调度任务管理</title>
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
	<form:form id="inputForm" modelAttribute="jobTask" action="${ctx}/tl/jobTask/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="action" name="action" type="hidden" value="${jobTask.action}"/>
		<sys:message content="${message}"/>
		
			<div class="box-header with-border">
				<h3 class="box-title">调度任务-
				<shiro:hasPermission name="tl:jobTask:edit">${not empty jobTask.id ? '修改':'添加'}</shiro:hasPermission>
				<shiro:lacksPermission name="tl:jobTask:edit">查看</shiro:lacksPermission>
				</h3>
				<div class="box-tools pull-right">
					<shiro:hasPermission name="tl:jobTask:edit"><input id="btnSubmit" class="btn btn-blue" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
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
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">任务类型：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="type" htmlEscape="false" maxlength="50" class="form-control "/>
			</div>
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">来源群组链接：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="groupId" htmlEscape="false" maxlength="200" class="form-control "/>
			</div>
			</div>
			<div class="form-group">
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">开始位置：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="offsetNum" htmlEscape="false" maxlength="11" class="form-control  digits"/>
			</div>
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">记录数：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="limitNum" htmlEscape="false" maxlength="11" class="form-control  digits"/>
			</div>
			</div>
			<div class="form-group">
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">任务状态：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:select path="status" class="form-control ">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('jobtask_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">获取用户数：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="usernum" htmlEscape="false" maxlength="11" class="form-control  digits"/>
			</div>
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
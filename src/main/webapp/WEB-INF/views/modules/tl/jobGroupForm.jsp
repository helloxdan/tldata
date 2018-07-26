<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>任务采集群组列表管理</title>
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
	<form:form id="inputForm" modelAttribute="jobGroup" action="${ctx}/tl/jobGroup/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		
			<div class="box-header with-border">
				<h3 class="box-title">任务采集群组列表-
				<shiro:hasPermission name="tl:jobGroup:edit">${not empty jobGroup.id ? '修改':'添加'}</shiro:hasPermission>
				<shiro:lacksPermission name="tl:jobGroup:edit">查看</shiro:lacksPermission>
				</h3>
				<div class="box-tools pull-right">
					<shiro:hasPermission name="tl:jobGroup:edit"><input id="btnSubmit" class="btn btn-blue" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
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
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">群组名称：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="groupName" htmlEscape="false" maxlength="200" class="form-control "/>
			</div>
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">群组link：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="groupUrl" htmlEscape="false" maxlength="100" class="form-control "/>
			</div>
			</div>
			<div class="form-group">
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">用户数量：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="usernum" htmlEscape="false" maxlength="11" class="form-control  digits"/>
			</div>
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">抽取数据的索引偏移数：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<form:input path="offset" htmlEscape="false" maxlength="11" class="form-control "/>
			</div>
			</div>
			<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">upcate_date：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<div class="input-group">
				<input name="upcateDate" type="text" readonly="readonly" maxlength="20" class="form-control "
					value="<fmt:formatDate value="${jobGroup.upcateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
					<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
				 </div>
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
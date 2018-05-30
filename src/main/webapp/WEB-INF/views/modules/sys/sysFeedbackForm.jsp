<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>问题反馈管理</title>
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
	<form:form id="inputForm" modelAttribute="sysFeedback" action="${ctx}/sys/sysFeedback/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		
			<div class="box-header with-border">
				<h3 class="box-title">问题反馈-
				<shiro:hasPermission name="sys:sysFeedback:edit">${not empty sysFeedback.id ? '修改':'添加'}</shiro:hasPermission>
				<shiro:lacksPermission name="sys:sysFeedback:edit">查看</shiro:lacksPermission>
				</h3>
				<div class="box-tools pull-right">
					<shiro:hasPermission name="sys:sysFeedback:edit"><input id="btnSubmit" class="btn btn-blue" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
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
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">问题类型：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:select path="type" class="form-control required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('feedback_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">标题：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="title" htmlEscape="false" maxlength="100" class="form-control required"/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">问题内容：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:textarea path="content" htmlEscape="false" rows="3" maxlength="500" class="form-control required"/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">报告人：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<sys:treeselect2 id="reportBy" name="reportBy.id" value="${sysFeedback.reportBy.id}" labelName="reportBy.name" labelValue="${sysFeedback.reportBy.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="required" allowClear="true" notAllowSelectParent="true"/>
			</div>
			 
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">报告时间：</label>
			<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
				<div class="input-group">
				<input name="reportDate" type="text" readonly="readonly" maxlength="20" class="form-control required"
					value="<fmt:formatDate value="${sysFeedback.reportDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
					<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
				 </div>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">备注：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:textarea path="remarks" htmlEscape="false" rows="2" maxlength="100" class="form-control "/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">回复内容：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:textarea path="replyContent" htmlEscape="false" rows="3" maxlength="500" class="form-control required"/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
	 			<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">回复人：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<sys:treeselect2 id="replyBy" name="replyBy.id" value="${sysFeedback.replyBy.id}" labelName="replyBy.name" labelValue="${sysFeedback.replyBy.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="" allowClear="true" notAllowSelectParent="true"/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">附件：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:hidden id="attachment" path="attachment" htmlEscape="false" maxlength="100" class="form-control"/>
				<sys:ckfinder input="attachment" type="files" uploadPath="/sys/sysFeedback" selectMultiple="true"/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">状态：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:select path="state" class="form-control ">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('order_state')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
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
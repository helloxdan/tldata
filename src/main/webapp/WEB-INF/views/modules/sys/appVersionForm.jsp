<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>App版本管理</title>
<meta name="decorator" content="adminlte" />
<script type="text/javascript">
	$(document).ready(
			function() {
				//$("#name").focus();
				$("#inputForm")
						.validate(
								{
									submitHandler : function(form) {
										loading('正在提交，请稍等...');
										form.submit();
									},
									errorContainer : "#messageBox",
									errorPlacement : function(error, element) {
										$("#messageBox").text("输入有误，请先更正。");
										if (element.is(":checkbox")
												|| element.is(":radio")
												|| element.parent().is(
														".input-append")) {
											error.appendTo(element.parent()
													.parent());
										} else {
											error.insertAfter(element);
										}
									}
								});
			});

	// 限制版本号只能输入整形数	
	function versionKeyup(obj) {
		if (obj.value.length == 1) {
			obj.value = obj.value.replace(/[^1-9]/g, '');
		} else {
			obj.value = obj.value.replace(/\D/g, '');
		}
	}

	function versionAfterPaste(obj) {
		if (obj.value.length == 1) {
			obj.value = obj.value.replace(/[^1-9]/g, '');
		} else {
			obj.value = obj.value.replace(/\D/g, '');
		}
	}
</script>
</head>
<body>
	<div class="addData">
		<div class="box box-solid minHeight">
			<form:form id="inputForm" modelAttribute="appVersion"
				action="${ctx}/sys/appVersion/save" method="post"
				class="form-horizontal">
				<form:hidden path="id" />
				<sys:message content="${message}" />
				<div class="box-header with-border">
					<h3 class="box-title">App版本详情</h3>
					<div class="box-tools">
						<shiro:hasPermission name="sys:appVersion:edit">
							<input id="btnSubmit" class="btn btn-primary" type="submit"
								value="保 存" />&nbsp;</shiro:hasPermission>
						<input id="btnCancel" class="btn" type="button" value="返 回"
							onclick="history.go(-1)" />
					</div>
				</div>
				<!-- /.box-header -->
				<!-- form start -->
				<div class="box-body">
					<div class="panel panel-default">
						<div class="panel-body">
							<div class="form-group">
								<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">类型：</label>
								<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
									<form:select path="type" class="form-control required">
										<form:options items="${appTypes}" itemLabel="name"
											itemValue="code" htmlEscape="false" />
									</form:select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">版本号：</label>
								<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
									<form:input path="version" htmlEscape="false" maxlength="32"
										class="form-control required" onkeyup="versionKeyup(this)"
										onafterpaste="versionAfterPaste(this)" />
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">版本编码：</label>
								<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
									<form:input path="code" htmlEscape="false" maxlength="20"
										class="form-control" />
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">版本名称：</label>
								<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
									<form:input path="name" htmlEscape="false" maxlength="20"
										class="form-control " />
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">版本说明：</label>
								<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
									<form:textarea path="description" htmlEscape="false" rows="3" maxlength="500" class="form-control required"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">下载地址：</label>
								<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
									<form:hidden  path="url" htmlEscape="false" maxlength="200" class="form-control"/>
									<sys:ckfinder input="url" type="" uploadPath="" selectMultiple="false"/>
								</div>
							</div>
							<div class="form-group">
								<label class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">状态：</label>
								<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
									<form:select path="state" class="form-control required">
										<form:options items="${states}" itemLabel="name"
											itemValue="code" htmlEscape="false" />
									</form:select>
								</div>
							</div>

						</div>
					</div>
				</div>
				<!-- /.box-body -->
		</div>
		</form:form>
	</div>
	</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>机构管理</title>
<meta name="decorator" content="adminlte" />
<script type="text/javascript">
	$(document).ready(function() {
		var muHeight = $(window).height();
		$(".minHeight").css('min-height',muHeight);
		$("#name").focus();
		$("#inputForm").validate(
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
</script>
<style>

</style>
</head>
<body>

	<div class="content addData">
		<div class="">
			<form:form id="inputForm" modelAttribute="office"
				action="${ctx}/sys/office/save" method="post"
				class="form-horizontal">
				<form:hidden path="id" />
				<sys:message content="${message}" />
				<div class="box box-solid minHeight marginBottom0">
					<div class="box-header with-border">
						<h3 class="box-title">
							机构 -
							<shiro:hasPermission name="sys:office:edit">${not empty office.id?'修改':'新增'}</shiro:hasPermission>
							<shiro:lacksPermission name="sys:office:edit">查看</shiro:lacksPermission>
						</h3>
					</div>
					<div class="box-body">
						<div class="panel panel-default">
							<div class="panel-body">
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">上级机构:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<sys:treeselect id="office" name="parent.id"
													value="${office.parent.id}" labelName="parent.name"
													labelValue="${office.parent.name}" title="机构"
													url="/sys/office/treeData" extId="${office.id}"
													cssClass="form-control"
													allowClear="${office.currentUser.admin}" />
											</div>

											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">归属区域：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<sys:treeselect id="area" name="area.id"
													value="${office.area.id}" labelName="area.name"
													labelValue="${office.area.name}" title="区域"
													url="/sys/area/treeData" cssClass="required form-control" />
											</div>
											
										</div>
									</div>
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">机构名称:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="name" htmlEscape="false" maxlength="50"
													class="required form-control"
													style="width: 94%;display: inline-block;" />
												<span class="help-inline"><font color="red">*</font>
												</span>
											</div>

											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">机构编码:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="code" htmlEscape="false" maxlength="50"
													class="form-control" />
											</div>
										</div>
									</div>
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">机构类型:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:select path="type" class="form-control">
													<form:options items="${fns:getDictList('sys_office_type')}"
														itemLabel="label" itemValue="value" htmlEscape="false"
														class="form-control" />
												</form:select>
											</div>

											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">机构级别:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:select path="grade" class="form-control">
													<form:options
														items="${fns:getDictList('sys_office_grade')}"
														itemLabel="label" itemValue="value" htmlEscape="false" />
												</form:select>
											</div>
										</div>
									</div>
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">是否可用:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:select path="useable" class="form-control">
													<form:options items="${fns:getDictList('yes_no')}"
														itemLabel="label" itemValue="value" htmlEscape="false" />
												</form:select>
											</div>

											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">主负责人:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<sys:treeselect id="primaryPerson" name="primaryPerson.id"
													value="${office.primaryPerson.id}"
													labelName="office.primaryPerson.name"
													labelValue="${office.primaryPerson.name}" title="用户"
													url="/sys/office/treeData?type=3" allowClear="true"
													notAllowSelectParent="true" cssClass="form-control" />
											</div>
										</div>
									</div>
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">副负责人:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<sys:treeselect id="deputyPerson" name="deputyPerson.id"
													value="${office.deputyPerson.id}"
													labelName="office.deputyPerson.name"
													labelValue="${office.deputyPerson.name}" title="用户"
													url="/sys/office/treeData?type=3" allowClear="true"
													notAllowSelectParent="true" cssClass="form-control" />
											</div>

											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">联系地址:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="address" htmlEscape="false" maxlength="50"
													class="form-control" />
											</div>
										</div>
									</div>
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">邮政编码:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="zipCode" htmlEscape="false" maxlength="50"
													class="form-control" />
											</div>

											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">负责人:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="master" htmlEscape="false" maxlength="50"
													class="form-control" />
											</div>
										</div>
									</div>
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">电话:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="phone" htmlEscape="false" maxlength="50"
													class="form-control" />
											</div>

											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">传真:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="fax" htmlEscape="false" maxlength="50"
													class="form-control" />
											</div>
										</div>
									</div>
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">邮箱:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="email" htmlEscape="false" maxlength="50"
													class="form-control" />
											</div>

											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">备注:</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:textarea path="remarks" htmlEscape="false" rows="3"
													maxlength="200" class="input-xlarge" style="width:100%;" />
											</div>
										</div>
									</div>
									<div class="col-md-12">
										<div class="form-group">
											<c:if test="${empty office.id}">

												<label
													class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">快速添加下级部门:</label>
												<div class="col-md-5 col-sm-5 col-ls-5 col-xs-5"
													style="margin-top: 9px;">
													<form:checkboxes path="childDeptList"
														items="${fns:getDictList('sys_office_common')}"
														itemLabel="label" itemValue="value" htmlEscape="false" />
												</div>

											</c:if>
										</div>
									</div>
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2"></label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<shiro:hasPermission name="sys:office:edit">
													<input id="btnSubmit" class="btn btn-blue" type="submit"
														value="保 存" />&nbsp;</shiro:hasPermission>
												<input id="btnCancel" class="btn btn-blue" type="button" value="返 回"
													onclick="history.go(-1)" />
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>

</body>
</html>
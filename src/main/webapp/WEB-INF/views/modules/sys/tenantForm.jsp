<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>系统租户管理</title>
<meta name="decorator" content="adminlte" />
<%@include file="/WEB-INF/views/include/upload.jsp"%>
<script type="text/javascript">
	$(document).ready(
			function() {
				var muHeight = $(window).height() - 20;
				$(".minHeight").css('min-height', muHeight);
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

				initFileInput(false);

				$('#logo1').on('fileselect', function(event, numFiles, label) {
					console.log("fileselect");
					//$('#logo1').fileinput('clearStack'); 
				});
			});

	function initFileInput(reset) {
		$("#logo1")
				.fileinput(
						{
							language : 'zh', //设置语言
							theme : 'fa',
							showUpload : false,
							showCaption : false,
							dropZoneEnabled : false,
							browseClass : "btn btn-primary",
							allowedFileExtensions : [ 'jpg', 'png' ],//接收的文件后缀
							fileType : "image",
							previewFileIcon : "<i class='glyphicon glyphicon-king'></i>",
							frameClass : 'krajee-default i-file-logo',
							previewSettings : {
								image : {
									width : "100px",
									height : "100px"
								}
							},
							fileActionSettings : {
								showDrag : false
							},
							overwriteInitial : true,
							initialPreviewAsData : true,
							initialPreviewShowDelete : false,
							initialPreview : [ "${rctx}/api/sys/download?file=${tenant.logo}" ],
							initialPreviewConfig : [ {
								caption : "公司LOGO",
								width : 200,
								url : "deleteUrl",
								key : 1
							}, ]
						});
	}
</script>
<style>
</style>
</head>
<body>

	<div class="content addData">
		<form:form id="inputForm" modelAttribute="tenant"
			action="${ctx}/sys/tenant/save" method="post"
			enctype="multipart/form-data">
			<form:hidden path="id" />
			<sys:message content="${message}" />

			<div class="form-horizontal">
				<div class="box box-solid minHeight">
					<div class="box-header with-border">
						<h3 class="box-title">
							系统租户 -
							<shiro:hasPermission name="sys:tenant:edit">${not empty tenant.id?'修改':'新增'}</shiro:hasPermission>
							<shiro:lacksPermission name="sys:tenant:edit">查看</shiro:lacksPermission>
						</h3>
						<div class="box-tools pull-right">
							<shiro:hasPermission name="sys:tenant:edit">
								<input id="btnSubmit" class="btn btn-blue" type="submit"
									value="保 存" />&nbsp;
									<c:if test="${not empty tenant.id}">
									<input id="btnInitData" class="btn btn-blue" type="button"
										value="初始化菜单授权" onclick="initData()" title="初始化租户的授权菜单" />&nbsp;
									</c:if>
							</shiro:hasPermission>
							<input id="btnCancel" class="btn btn-blue" type="button"
								value="返 回" onclick="history.go(-1)" />
						</div>
					</div>
					<div class="box-body">
						<div class="panel panel-default">

							<div class="panel-heading">
								<h3 class="panel-title">公司详情</h3>
							</div>
							<div class="panel-body">
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">LOGO：</label>
											<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
												<form:hidden path="logo" />
												<input type="file" id="logo1" name="file"
													class="form-control " /> <span class="help-inline">推荐：长:300px,宽:300px</span>
											</div>
										</div>
									</div>
								</div>
								<!-- end row -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">公司编码：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="sn" htmlEscape="false" maxlength="32"
													class="form-control" readonly="true" />
												<span class="help-inline">自动生成,例如：C0001，S0001</span>
											</div>
										</div>
									</div>
								</div>
								<!-- end row -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">简称：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="code" htmlEscape="false" maxlength="32"
													class="form-control required" />
												<span class="help-inline"><font color="red">*</font>必填，用英文字母表示，如nfyy（南方公司）,gdzyy（广东中公司）等
												</span>
											</div>
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">公司名称：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="name" htmlEscape="false" maxlength="100"
													class="form-control required" />
												<span class="help-inline"><font color="red">*</font>
												</span>
											</div>
										</div>
									</div>
								</div>
								<!-- end row -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">负责人：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="master" htmlEscape="false" maxlength="100"
													class="form-control " />
											</div>

											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">联系方式：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="phone" htmlEscape="false" maxlength="200"
													class="form-control " />
											</div>
										</div>
									</div>
								</div>
								<!-- end row -->
								
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">公司性质：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:select path="type" class="form-control">
													<form:options
														items="${fns:getDictList('hospital_property')}"
														itemLabel="label" itemValue="value" htmlEscape="false" />
												</form:select>
											</div>
											
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">公司类别：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:select path="className" class="form-control">
													<form:options
														items="${fns:getDictList('hospital_category')}"
														itemLabel="label" itemValue="value" htmlEscape="false" />
												</form:select>
											</div>
											
										</div>
									</div>
								</div>
								<!-- end row -->
								 
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">公司描述：</label>
											<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
												<form:textarea path="description" htmlEscape="false"
													style="width:100%;" rows="4" maxlength="1000" />
											</div>
										</div>
									</div>
								</div>
								<!-- end row -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">公司地址：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="address" htmlEscape="false"
													maxlength="255" class="form-control " />
											</div>
											
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">传真：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="fax" htmlEscape="false" maxlength="200"
													class="form-control " />
											</div>
										</div>
									</div>
								</div>
								<!-- end row -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
										<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">网址：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="url" htmlEscape="false" maxlength="100"
													class="form-control " />
											</div>
											
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">邮箱：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:input path="email" htmlEscape="false" maxlength="200"
													class="form-control " />
											</div>
										</div>
									</div>
								</div>
								<!-- end row -->
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">备注：</label>
											<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
												<form:textarea path="remarks" htmlEscape="false" rows="4"
													style="width:100%;" maxlength="255" />
											</div>
										</div>
									</div>
								</div>
								<!-- end row -->
								
								<div class="row">
									<div class="col-md-12">
										<div class="form-group">
											<label
												class="col-md-2 col-sm-2 col-ls-2 col-xs-2 control-label">是否可用：</label>
											<div class="col-md-3 col-sm-3 col-ls-3 col-xs-3">
												<form:select path="status" class="form-control">
													<form:options items="${fns:getDictList('yes_no')}"
														itemLabel="label" itemValue="value" htmlEscape="false" />
												</form:select>
											</div>
										</div>
									</div>

								</div>
								<!-- end row -->
							</div>
						</div>	
					</div>
					<!-- end pane body -->
				</div>
			</div>
		</form:form>
	</div>
	<form:form id="inputForm" modelAttribute="tenant"
		action="${ctx}/sys/tenant/save" method="post"
		enctype="multipart/form-data" class="form-horizontal hidden">
		<div class="box-body">

			<div class="form-group"></div>
			<div class="form-group"></div>
			<div class="form-group"></div>
			<div class="form-group"></div>
			<div class="form-group"></div>
			<div class="form-group"></div>
			<div class="form-group"></div>
			<div class="form-group"></div>


			<!-- /.box-body -->
			<!-- <div class="box-footer"></div> -->
			<!-- /.box-footer -->
		</div>
	</form:form>
</body>
</html>
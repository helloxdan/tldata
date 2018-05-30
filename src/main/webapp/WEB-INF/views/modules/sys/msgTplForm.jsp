<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>消息模板管理</title>
	<meta name="decorator" content="adminlte"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			var type=$('#type').val();
			toggleHelp(type);
			$('#type').change(function(){
				toggleHelp($(this).val());
				var content=$('#content').val();
				type=$(this).val();
				if(content==''){
					if(type=='4'){ 
						$('#content').val('【邀您看车】正文请替换此处内容 退订回T');
					}else{
						$('#content').val('');
					}
				}
			});
			
		});
		
		function toggleHelp(type){
			if(type=='4'){
				$('#smsTypeHelp').show();
				$('#msgTypeHelp').hide();
			}
			if(type=='3'){
				$('#smsTypeHelp').hide();
				$('#msgTypeHelp').show();
			}
		}
	</script>
</head>
<body>
	<div class="addData content">
	<form:form id="inputForm" modelAttribute="msgTpl" action="${ctx}/sys/msgTpl/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="box box-solid">
			<div class="box-header with-border">
				<h3 class="box-title">消息模板-<shiro:hasPermission name="sys:msgTpl:edit">${not empty alterForm.id?'修改':'新增'}</shiro:hasPermission>
						<shiro:lacksPermission name="sys:msgTpl:edit">查看</shiro:lacksPermission></h3>
				<div class="box-tools">
					<shiro:hasPermission name="sys:msgTpl:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</div>
			<!-- /.box-header -->
			<!-- form start -->
			<div class="box-body">
					
		<div class="form-group">
			<label class="col-sm-2 control-label">消息类型：</label>
			<div class="col-sm-10">
				<form:select path="type" class="form-control required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('msg_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
					
		<div class="form-group">
			<label class="col-sm-2 control-label">模板标题：</label>
			<div class="col-sm-10">
				<form:input path="title" htmlEscape="false" minlength="1" maxlength="32"
									class="form-control required" />
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">模板内容：</label>
			<div class="col-sm-10">
				<form:textarea path="content" htmlEscape="false" rows="4" maxlength="1000" class="form-control required"/>
					<p id="smsTypeHelp" class="help-block" style="display:none;">
					<ol>
					以下短信内容不能通过审核：
					<li>政治、色情、暴力、赌博及其他违法信息</li>
					<li>含有病毒、恶意代码、色情、反动等不良信息或有害信息</li>
					<li>以虚伪不实的方式慌称或使人误认为与任何人或任何机构有关</li>
					<li>侵犯他人知识产权、或违反保密、雇佣或不披露协议披露他人保密信息</li>
					<li>粗话、脏话等不文明内容；让短信接收者难以理解的内容</li></ol>
		<a href="http://console.dingdongcloud.com" target="blank">短信平台：叮咚云</a></p>
					<p  id="msgTypeHelp" class="help-block" style="display:none;">站内信不支持html格式的文本。</p>
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">是否启用：</label>
			<div class="col-sm-10">
				<form:select path="status" class="form-control ">
					<form:options items="${fns:getDictList('cms_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
	 
		</div>
			<!-- /.box-body -->
			<div class="box-footer"></div>
			<!-- /.box-footer -->
		</div>
	</form:form>
	</div>
</body>
</html>
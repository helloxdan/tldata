<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>系统消息管理</title>
<meta name="decorator" content="adminlte" />
<script src="${ctxStatic}/modules/live/js/msg.js" type="text/javascript"></script>
<script type="text/javascript">
var msgForm=null;
		$(document).ready(function() {
			 msgForm=new MsgForm('outputDiv',false);
			 //初始化选择用户
			var userJson='${msg.userData}';//$('#userData').val();
			 console.log('init userData:',userJson);
			 msgForm.initUser(userJson);
		});
	</script>
</head>
<body>
	<div class="addData content">
	<form:form id="inputForm" modelAttribute="msg"
		action="${ctx}/sys/msg/save" method="post" class="form-horizontal">
		<form:hidden path="id" />
		<form:hidden path="status" />
		<form:hidden path="type" />
		<form:hidden path="userType" value="用户"/>
		<form:hidden path="sendState" />
		<form:hidden path="userDesc" />
		<form:hidden path="userData" htmlEscape="false"/>
		<sys:message content="${message}" />
		<div class="box box-solid">
			<div class="box-header with-border">
				<h3 class="box-title"></h3>
				<div class="box-tools">
					<input id="btnCancel" class="btn" type="button" value="关闭"
						onclick="history.go(-1)" />
				</div>
			</div>
			<!-- /.box-header -->
			<!-- form start -->
			<div class="box-body">
				<div class="row">
					<div class="col-sm-12">

						<div class="form-group">
							<label class="col-sm-2 control-label">标题：</label>
							<div class="col-sm-10">
								<form:input path="title" htmlEscape="false" maxlength="32"
									class="form-control required" />
							</div>
						</div>

						<div class="form-group">
							<label class="col-sm-2 control-label">接收人：</label>
							<div class="col-sm-10">
								<div id="outputDiv" class="form-control outputBox">
								</div>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label">正文：</label>
							<div class="col-sm-10">
								<form:textarea path="content" htmlEscape="false" rows="10"
									maxlength="1000" class="form-control required" />
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label">发送时间：</label>
							<div class="col-sm-10">
								 <fmt:formatDate value="${msg.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
							</div>
						</div>

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
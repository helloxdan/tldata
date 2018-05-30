<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fns" uri="/WEB-INF/tlds/fns.tld" %>
<%@ taglib prefix="sys" tagdir="/WEB-INF/tags/sys" %>
<c:set var="ctxStatic" value="${pageContext.request.contextPath}/static"/>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>${fns:getConfig('productName')} 登录</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <!-- Bootstrap 3.3.7 -->
  <link rel="stylesheet" href="${ctxStatic}/bootstrap/3.3.7/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="${ctxStatic}/bootstrap/3.3.7/css/font-awesome.min.css">
  <!-- Ionicons -->
  <link rel="stylesheet" href="${ctxStatic}/bootstrap/3.3.7/ionicons/ionicons.min.css">
  <!-- Theme style -->
  <link rel="stylesheet" href="${ctxStatic}/lte/css/AdminLTE.min.css">
  <!-- iCheck -->
  <link rel="stylesheet" href="${ctxStatic}/plugins/iCheck/square/blue.css">
  <link href="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.css" type="text/css" rel="stylesheet" />
  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->

  <!-- Google Font
  <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600,700,300italic,400italic,600italic">
   -->
   <link rel="stylesheet" href="${ctxStatic}/modules/em/css/login.css">
</head>
<body class="hold-transition login-page">
  <div class="login-logo">
    	${fns:getConfig('productName')}
  </div>
<div class="login-box">
  <!-- /.login-logo -->
  <div class="login-box-body">
    <p class="login-box-msg">系统认证</p>

    <form id="loginForm" action="${pageContext.request.contextPath}${fns:getAdminPath()}/login" method="post">
      <div class="form-group has-feedback">
        <input type="text" class="form-control required" placeholder="账号"  id="username" name="username">
        <span class="glyphicon glyphicon-user form-control-feedback" ></span>
      </div>
      <div class="form-group has-feedback">
        <input type="password" class="form-control required" placeholder="密码" id="password" name="password">
        <span class="glyphicon glyphicon-lock form-control-feedback"></span>
      </div>
      <c:if test="${isValidateCodeLogin}">
				<div class="form-group has-feedback">
					<label class="input-label mid" for="validateCode">验证码</label>
					<sys:validateCode name="validateCode"
						inputCssStyle="margin-bottom:0;" />
				</div>
			</c:if>  
      <div id="messageBox" class="">
			<label id="loginError" class="error ${empty message ? 'hide'  : ''}">${message}</label>
      </div>
      <div class="row">
        <div class="col-xs-8">
          <div class="checkbox icheck">
            <label>
              <input type="checkbox"  id="rememberMe" name="rememberMe" ${rememberMe ? 'checked' : ''}>记住我
            </label>
          </div>
        </div>
        <!-- /.col -->
        <div class="col-xs-4">
          <button type="submit" class="btn btn-login btn-block btn-flat">登录</button>
        </div>
        <!-- /.col -->
      </div>
    </form>
	<!--
    <div class="social-auth-links text-center">
      <p>- OR -</p>
      <a href="#" class="btn btn-block btn-social btn-facebook btn-flat"><i class="fa fa-facebook"></i>微信</a>
      <a href="#" class="btn btn-block btn-social btn-google btn-flat"><i class="fa fa-google-plus"></i>微博</a>
    </div>
    
<!--
    <a href="#">忘记密码</a><br>
    <a href="register.html" class="text-center">注册</a>
 -->
  </div>
  <!-- /.login-box-body -->
</div>   
<!-- /.login-box -->
<footer class="footer" >
     <p> <a href="http://www.icaremore.com.cn" target="_blank">${fns:getConfig('productSupport')}</a> &nbsp;&nbsp;${fns:getConfig('registeNO')} </p>
     <p> Copyright ©${fns:getConfig('copyrightYear')}&nbsp; All rights reserved.</p>
 </footer> 
<!-- jQuery 3.1.1 -->
<script src="${ctxStatic}/plugins/jQuery/jquery-3.1.1.min.js"></script>
<!-- Bootstrap 3.3.7 -->
<script src="${ctxStatic}/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.js" type="text/javascript"></script>
<!-- iCheck -->
<script src="${ctxStatic}/plugins/iCheck/icheck.min.js"></script>
<script>
  $(document)
	.ready(
			function() {
				$('input').iCheck({
				      checkboxClass: 'icheckbox_square-blue',
				      radioClass: 'iradio_square-blue',
				      increaseArea: '20%' // optional
				    });
				
				$("#loginForm")
						.validate(
								{
									rules : {
										validateCode : {
											remote : "${pageContext.request.contextPath}/servlet/validateCodeServlet"
										}
									},
									messages : {
										username : {
											required : "请填写用户名."
										},
										password : {
											required : "请填写密码."
										},
										validateCode : {
											remote : "验证码不正确.",
											required : "请填写验证码."
										}
									},
									errorLabelContainer : "#messageBox",
									errorPlacement : function(error,
											element) {
										error.appendTo($("#loginError")
												.parent());
									}
								});
			});
  
		// 如果在框架或在对话框中，则弹出提示并跳转到首页
		if (self.frameElement && self.frameElement.tagName == "IFRAME"
			|| $('#left').length > 0 || $('.jbox').length > 0) {
		alert('未登录或登录超时。请重新登录，谢谢！');
		top.location = "${ctx}";
		}
</script>
</body>
</html>

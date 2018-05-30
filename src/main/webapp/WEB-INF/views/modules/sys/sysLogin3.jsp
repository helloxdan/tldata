<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>${fns:getConfig('productName')} 登录</title>
<meta name="decorator" content="blank" />

<script type="text/javascript">
	$(document)
			.ready(
					function() {
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
<style type="text/css">
html,body,table {
	background-color: transparent;
	width: 100%;
	text-align: center;
}

.form-signin-heading {
	font-family: Helvetica, Georgia, Arial, sans-serif, 黑体;
	font-size: 36px;
	margin-bottom: 20px;
	color: #0663a2;
}

.form-signin {
	position: relative;
	text-align: left;
	width: 300px;
	padding: 25px 29px 29px;
	margin: 0 auto 20px;
	background-color: #fff;
	border: 1px solid #e5e5e5;
	-webkit-border-radius: 5px;
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
	-moz-box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
	box-shadow: 0 1px 2px rgba(0, 0, 0, .05);
}

.form-signin .checkbox {
	margin-bottom: 10px;
	color: #0663a2;
}

.form-signin .input-label {
	font-size: 16px;
	line-height: 23px;
	color: #999;
}

.form-signin .input-block-level {
	font-size: 16px;
	height: auto;
	margin-bottom: 15px;
	padding: 7px;
	*width: 283px;
	*padding-bottom: 0;
	_padding: 7px 7px 9px 7px;
}

.form-signin .btn.btn-large {
	font-size: 16px;
}

.form-signin #themeSwitch {
	position: absolute;
	right: 15px;
	bottom: 10px;
}

.form-signin div.validateCode {
	padding-bottom: 15px;
}

.mid {
	vertical-align: middle;
}

#messageBox {
	
	margin-top: 10px;
}

.alert {
	position: relative;
	width: 285px;
	margin: 0 auto;
	*padding-bottom: 0px;
}

label.error {
	background: none;
	width: 270px;
	font-weight: normal;
	color: inherit;
	margin: 0;
}

.loginWrap {
	box-sizing: border-box;
}

body {
	background: url(../static/images/dl_di1.jpg) top center no-repeat fixed;
	background-size: cover;
}
/* .loginWrap .login__bg {
	background: url(../static/images/dl_di.png) center center/100% 100% no-repeat;
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
} */
.loginWrap .login__main { /* position: absolute;
	top: 50%;
	left: 50%;
	-webkit-transform: translate(-50%, -50%);
	transform: translate(-50%, -50%); */
	width: 340px;
	/* margin: 445px auto 0; */
	position: absolute;
	top: 56%;
	left: 50%;
	-webkit-transform: translate(-50%, -50%);
	transform: translate(-50%, -50%);
}

.loginWrap form input.input-block-level {
	padding: 6px 6px;
	height: 32px;
	border: 1px solid #dddddd;
	color: #b6b6b6;
}

.rememberMeWrap {
	text-align: left;
	display: block !important;
	margin-bottom: 10px;
	color: #666666;
}

.validateCode {
	text-align: left;
}

.loginWrap form .loginBtn {
	display: inline-block;
	padding: 6px 6px;
	border: none;
	background-color: #0aa0f5;
	color: #ffffff;
	border-radius: 4px;
	background-color: #0aa0f5;
	color: #ffffff;
}

.loginWrap .footer {
	position: fixed;
	bottom: 10px;
	left: 50%;
	color: #666666;
	width: 564px;
	margin-left: -282px;
}

.loginWrap .input-block-level,.loginWrap .loginBtn {
	width: 100%;
}

.loginWrap .footer a {
	color: #666666;
}

.hidden {
	display: none;
}
</style>
</head>
<body>

	<div class="content loginWrap">

		<!--[if lte IE 6]><br/><div class='alert alert-block' style="text-align:left;padding-bottom:10px;"><a class="close" data-dismiss="alert">x</a><h4>温馨提示：</h4><p>你使用的浏览器版本过低。为了获得更好的浏览体验，我们强烈建议您 <a href="http://browsehappy.com" target="_blank">升级</a> 到最新版本的IE浏览器，或者使用较新版本的 Chrome、Firefox、Safari 等。</p></div><![endif]-->


		<form id="loginForm" class="login__main" action="${ctx}/login"
			method="post">


			<input type="text" id="username" name="username"
				class="input-block-level required" value="admin" placeholder="登录名">
			<input type="password" id="password" name="password"
				class="input-block-level required" value="admin" placeholder="密码">
			<c:if test="${isValidateCodeLogin}">
				<div class="validateCode">
					<label class="input-label mid" for="validateCode">验证码</label>
					<sys:validateCode name="validateCode"
						inputCssStyle="margin-bottom:0;" />
				</div>
			</c:if>

			<label for="rememberMe" title="下次不需要再登录" class="rememberMeWrap"><input
				type="checkbox" id="rememberMe" name="rememberMe" ${rememberMe ? 'checked' : ''}/>
				记住我</label> <input class="loginBtn" type="submit" value="登 录" />
			<div class="header">
				<div id="messageBox"
					class="alert alert-error ${empty message ? 'hide' : ''}">
					<button data-dismiss="alert" class="close">×</button>
					<label id="loginError" class="error">${message}</label>
				</div>
			</div>
		</form>
		<div class="footer">
			Copyright &copy; ${fns:getConfig('copyrightYear')} &nbsp;&nbsp; <a href="http://www.icaremore.com.cn" target="_blank">广州智爱康美科技有限公司</a>  &nbsp;&nbsp;${fns:getConfig('registeNO')}
			
		</div>
	</div>
	<script>
		var input = document.createElement('input');
		if ("placeholder" in input) {
			//alert('支持');
		} else {
			//alert('不支持');
			var JPlaceHolder = {
				//检测  
				_check : function() {
					return 'placeholder' in document.createElement('input');
				},
				//初始化  
				init : function() {
					if (!this._check()) {
						this.fix();
					}
				},
				//修复  
				fix : function() {
					jQuery(':input[placeholder]')
							.each(
									function(index, element) {
										var self = $(this), txt = self
												.attr('placeholder');
										var pos = self.position(), h = '32px', paddingleft = self
												.css('padding-left');

										var holder = $('<span></span>').text(
												txt).css({
											position : 'absolute',
											left : pos.left,
											top : pos.top,
											height : h,
											lineHeight : h,
											paddingLeft : paddingleft,
											color : '#aaa'
										}).appendTo(self.parent());
										self.focus(function(e) {
											holder.hide();

										}).blur(function(e) {
											if (!self.val()) {
												holder.show();
											}
										});
										holder.click(function(e) {
											holder.hide();
											self.focus();
										});
									});
				}
			};
			//执行  
			jQuery(function() {
				JPlaceHolder.init();
			});

		}
	</script>
	<%-- <script src="${ctxStatic}/flash/zoom.min.js" type="text/javascript"></script> --%>
</body>
</html>
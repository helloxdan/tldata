<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>系统消息管理</title>
<meta name="decorator" content="adminlte" />
<script src="${ctxStatic}/common/msg.js" type="text/javascript"></script>
<script type="text/javascript">
var msgForm=null;
		$(document).ready(function() {
			 msgForm=new MsgForm('outputDiv',true);
			 //初始化选择用户 
			 var userJson='${msg.userData}';//$('#userData').val();
			 console.log('init userData:',userJson);
			 msgForm.initUser(userJson);
			 
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
			var content=$('#content').val();
			if(type=='4'){ 
				if(content==''){
					$('#content').val('【邀您看车】正文请替换此处内容  退订回T');
				}
			}
			
			$('#myTabs a').click(function (e) {
				  e.preventDefault();
				  //console.log($(this).html());
				  var userType=$(this).text();
				  if(userType=='用户列表')
					  userType='用户';
				  $('#userType').val(userType);
				  $(this).tab('show');
				  
				  //初始化数据,如果选择框没有值，就加载数据
				  var type=$(this).data('type');
				  var loaded=$("#"+type+"userSelect").data('loaded');
				  //console.log('len='+len);
				  if(!loaded){
					var key='13';
				  	loadUser(type+'user',key);
				  	$("#"+type+"userSelect").data('loaded',true);
				  }
				});
			
			 //默认加载用户列表
			 loadUser('vuser',"13");
			 $("#vuserSelect").data('loaded',true); 
			
			//搜索框事件
			$('.searchInput').keyup(function(e){
				//console.log(e.which);
				if(e.which == 13) {  
					jQuery("#"+$(this).data('searchbtn')).click();  
				} 
			});
			//内容
			//内容
			$('#content').blur(function(e){
				var type=$('#type').val();
				var content=$('#content').val();
				if(type=='4'){ 
					if(content.indexOf('退订回T')==-1){
						content=content+' 退订回T';		
						$('#content').val(content);
					}
				}
			});
			$('#content').keyup(function(e){
				var type=$('#type').val();
				var content=$('#content').val();
				if(type=='4'){
					console.log(content.indexOf('退订回T'));
					if(content.indexOf('退订回T')==-1){
						content=content+'退订回T';						 
					}
						$('#btnSend').attr("disabled",false); 
						$('#btnSendTask').attr("disabled",false); 
					var tpl='已输入 l0/70个字: 签名(l1)+内容(l2)+退订回T(4) ，消耗l3条短信';
					tpl=tpl.replace('l0',content.length);
					var matchs=content.match(/【(\S*)】/);//'【邀您看车】';
					if(!matchs || matchs.length==0){
						$('#smsContentTip').html('<span style="color:red;">短信开头必须带签名信息和退订回T，如【邀您看车】xxxx（短信内容） 退订回T</span>');
						$('#btnSend').attr("disabled",true); 
						$('#btnSendTask').attr("disabled",true); 
						return;
					}
					var sign=matchs[0];
					tpl=tpl.replace('l1',sign.length);
					tpl=tpl.replace('l2',content.length-sign.length);
					var num=Math.ceil( content.length/70);
					tpl=tpl.replace('l3',num);
					$('#smsContentTip').html(tpl);
				}else{
					var tpl='已输入 l0个字';
					tpl=tpl.replace('l0',content.length);
					$('#smsContentTip').html(tpl);
				}
			});
			
			$('#vsbtn').click(function(){
				console.log('用户搜索');
				loadUser('vuser',$("#vkey").val());
			});
			$('#ssbtn').click(function(){
				console.log('门店账号搜索');
				loadUser('suser',$("#skey").val());
			});
			$('#hsbtn').click(function(){
				console.log('门店销售搜索');
				loadUser('huser',$("#hkey").val());
			});
			
			$('.checkAllInput').click(function(){
				var check=$(this).is(':checked');
				if(!check) return;
				//console.log('check all ',check);
				var self=this;
				var type=$(self).data('type');
				var selectEle=$('#'+type+'Select');
				loading('共选择'+selectEle.find('option').length+'位用户，系统处理中，请稍候…');
				setTimeout(function(){
					//console.log('type='+type+selectEle.find('option').length);
					var options=selectEle.find('option').each(function(i,item){
						// console.log(i,item);
						var e=$(item);
						var id=item.value;
						var name=e.data('name');
						var mobile=e.data('mobile');
						msgForm.addUser(id,name,mobile);
					});
					
					selectEle.empty();
					closeLoading();
				},100);
				
				
				/* for(var i in options){
					debugger;
					var item=options.item(i);
					var id=item.attr('value');
					var name=item.data('name');
					var mobile=item.data('mobile');
					msgForm.addUser(id,name,mobile);
					
					item.remove();
				} */
				
			});
			
			$('.userSelect').dblclick(function(){
				var id=$(this).val();
				var name= $(this).find("option:selected").data('name');
				var mobile= $(this).find("option:selected").data('mobile');
				//selectUser(id,name,mobile);
				msgForm.selectUser($(this));
			});
			
			$('#btnClean').click(function(){
				msgForm.cleanUser();
			});
			$('#btnSend').click(function(){
				sendMsg('1');
			});
			$('#btnSendTask').click(function(){
				//先弹出选择时间窗口
				var datePickerHtml='<div class="form-group"><label class="control-label">选择定时发送时间：</label><input type="datetime-local" value="2017-09-24T13:59:59"/></div>';
				var datePickerHtml='<div class="form-group" style="padding:10px;"><label class="control-label" style="">选择定时发送时间：</label><input id="sendDate" name="sendDate" type="text" style="width:150px;" readonly="readonly" maxlength="50" class="input-mini Wdate" value="" onclick="WdatePicker({dateFmt:\'yyyy-MM-dd HH:mm:ss\',isShowClear:false});"/></div>';
				top.$.jBox.open(datePickerHtml, "定时发送",350,150,{
					buttons:{"发送":"ok",  "取消":true}, bottomText:"", submit:function(v, h, f){
						if (v=="ok"){
							$('#sendTime').val(h.find('#sendDate').val());
							console.log($('#sendTime').val());
							sendMsg('1');
					    	return true;
						}
				} 
			});
				//sendMsg();
			});
			$('#btnSave').click(function(){
				sendMsg('0');//草稿
			});
			$('#btnSelTpl').click(function(){
				selectTemplate();//选择模板
			});
			
		});
		 
		
		//查询用户列表
		function loadUser(utype,key){
			if(key=='') 
				key="13";
			var url='${rctx}/api/sys/user/searchuser';
			loading('系统查询中，请稍候…');
			$.post(url,{type:utype,key:key},function(data){
				console.log(utype+':',data);
				if(data.code=="0"){
					msgForm.setList(utype,data.data.list);
				}
				closeLoading();
			});
		}
		
		function selectUser(id,name,mobile){
			//console.log('选中：',id,name,mobile);	
			msgForm.selectUser(id,name,mobile);
		}
		
		function sendMsg(sendState){
			$('#sendState').val(sendState);//发送状态
			$('#userData').val(msgForm.getUserJson());//接收人json数据
			var list=msgForm.getSelected();
			console.log(list);
			if(list.length==0){
				alert('请选择接收人');
				return;
			}
			$("#hideBox").remove();
			var form=$("#inputForm");
	 		var hideBox=$('<div id="hideBox"></div>');
	 		for(var i in list){
	 			var item=list[i];
	 			var id='<input id="msgRecordList'+i+'_id" name="msgRecordList['+i+'].id" type="hidden" value=""/>';
	 			var uid='<input id="msgRecordList'+i+'_uid" name="msgRecordList['+i+'].user.id" type="hidden" value="'+item.id+'"/>';
	 			var uname='<input id="msgRecordList'+i+'_username" name="msgRecordList['+i+'].user.name" type="hidden" value="'+item.name+'"/>';
	 			var umobile='<input id="msgRecordList'+i+'_usermobile" name="msgRecordList['+i+'].user.mobile" type="hidden" value="'+item.mobile+'"/>';
				hideBox.append(id).append(uid).append(uname).append(umobile);
			}
			form.append(hideBox);
			
	 		var userDesc=list.length==1? list[0].name:list[0].name+'等'+list.length+'人';
	 		$('#userDesc').val(userDesc);
	 		form.submit();
		}
		
		
		//选择模板
		function selectTemplate(){
			var type=$('#type').val();
			var url='${ctx}/sys/msgTpl/find';
			$.post(url,{type:type},function(data){
				console.log(type+':',data);
				if(data.code=="0"){
			
					var htmlBox=$('<div> </div>');
					var html=$('<div class="form-group" style="padding:10px;"> </div>');
					htmlBox.append(html);
					//var json=[{id:'1',content:'模板1'},{id:'2',content:'模板2'},{id:'3',content:'模板3'}];
					var json=data.list;
					for(var i in json){
						var item=json[i];
						html.append('<div class="radio msgTemplteBox"><label><input name="msgTpl" class="checkTpl" type="radio" value="'+item.id+'">'+item.content+'</label></div>');
					}
					if(!json || json.length==0){
						html.html('空记录');
					}
					//console.log(htmlBox.html());
					top.$.jBox.open(htmlBox.html(), "选择模板",450,350,{
						buttons:{"确定":"ok",  "关闭":true}, bottomText:"", submit:function(v, h, f){
							if (v=="ok"){
								 //获得内容
								 var radio=h.find("input[name='msgTpl']:checked") ;
								 //console.log(radio.val());
								// console.log(radio.parent().text());
								$('#content').val(radio.parent().text());
						    	return true;
							}
					} 
					});
			
			 
				}else{
					alert('没有模板');
				}
			});
		}
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
		<form:hidden path="sendTime" />
		<form:hidden path="sendState" />
		<form:hidden path="userDesc" />
		<form:hidden path="userData" htmlEscape="false"/>
		<sys:message content="${message}" />
		<div class="box box-solid">
			<div class="box-header with-border">
				<h3 class="box-title">${msg.typeLabel}-
				<shiro:hasPermission
					name="sys:msg:edit">${not empty msg.id?'修改':'添加'}</shiro:hasPermission>
				<shiro:lacksPermission name="sys:msg:edit">查看</shiro:lacksPermission>
				</h3>
				<div class="box-tools">
					<shiro:hasPermission name="sys:msg:edit">
						<input id="btnSend" class="btn btn-primary" type="button"
							value="发送" />&nbsp; 
						<input id="btnSendTask" class="btn btn-primary" type="button"
							value="定时发送" />&nbsp; 
						<input id="btnSave" class="btn btn-primary" type="button"
							value="存草稿" />&nbsp;</shiro:hasPermission>
					<input id="btnCancel" class="btn" type="button" value="关闭"
						onclick="history.go(-1)" />
				</div>
			</div>
			<!-- /.box-header -->
			<!-- form start -->
			<div class="box-body">
				<div class="row">
					<div class="col-sm-7">

						<div class="form-group">
							<label class="col-sm-2 control-label"><em>*</em>标题：</label>
							<div class="col-sm-10">
								<form:input path="title" htmlEscape="false" maxlength="32"
									class="form-control required" />
							</div>
						</div>

						<div class="form-group">
							<label class="col-sm-2 control-label"><em>*</em>接收人(<span id="outputDivNum">0</span>)：</label>
							<div class="col-sm-10">
								<div id="outputDiv" class="form-control outputBox">
								</div>
								<input id="btnClean" class="btn btn-xs" type="button" value="清除" />
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label"><em>*</em>正文：</label>
							<div class="col-sm-10">
								<form:textarea path="content" htmlEscape="false" rows="6"
									maxlength="1000" class="form-control required" />
								<div style="margin-top:10px;">
									<span id="smsContentTip"></span>
									<input id="btnSelTpl" class="btn btn-xs btn-primary pull-right" type="button" value="选择消息模板" />
								</div>
								<c:if test="${msg.type=='4' }">
									<p class="help-block" >短信内容格式必须符合短信平台规则：【签名】xxxx（短信内容）退订回T。推荐引用消息模板编辑</p>
									<ol>
									以下短信内容不能通过审核：
									<li>政治、色情、暴力、赌博及其他违法信息</li>
									<li>含有病毒、恶意代码、色情、反动等不良信息或有害信息</li>
									<li>以虚伪不实的方式慌称或使人误认为与任何人或任何机构有关</li>
									<li>侵犯他人知识产权、或违反保密、雇佣或不披露协议披露他人保密信息</li>
									<li>粗话、脏话等不文明内容；让短信接收者难以理解的内容</li></ol>
								</c:if>
							</div>
						</div>

					</div>
					<div class="col-sm-5">
						   <!-- Nav tabs -->
					  <ul id="myTabs" class="nav nav-tabs" role="tablist">
					    <li role="presentation" class="active"><a href="#viewerlist" aria-controls="viewerlist" role="tab" data-toggle="tab" data-type="v">用户列表</a></li>
					   <!--  <li role="presentation"><a href="#shopuser" aria-controls="shopuser" role="tab" data-toggle="tab" data-type="s">门店账号</a></li>
					    <li role="presentation"><a href="#hostlist" aria-controls="hostlist" role="tab" data-toggle="tab" data-type="h">门店销售</a></li> -->
					  </ul>
					
					  <!-- Tab panes -->
					  <div class="tab-content">
					    <div role="tabpanel" class="tab-pane active" id="viewerlist">
					    	<div class="input-group">
						      <input type="text" id="vkey" class="form-control searchInput" data-searchbtn="vsbtn" placeholder="请输入用户名/手机号">
						      <span class="input-group-btn">
						        <button id="vsbtn" class="btn btn-default" type="button">搜索</button>
						      </span>
						    </div><!-- /input-group -->
					    	<select id="vuserSelect" multiple="true" class="form-control userSelect" style="height:180px;">
					    	</select>
					    	<div class="checkbox">
							    <label>
							      <input data-type="vuser" class="checkAllInput" type="checkbox">全选
							    </label>
							  </div>
					    </div>
					    <!-- 用户列表 end-->
					    <div role="tabpanel" class="tab-pane" id="shopuser">
							<div class="input-group">
						      <input type="text" id="skey" class="form-control searchInput" data-searchbtn="ssbtn" placeholder="请输入用户名/手机号">
						      <span class="input-group-btn">
						        <button id="ssbtn" class="btn btn-default" type="button">搜索</button>
						      </span>
						    </div><!-- /input-group -->
						    <select id="suserSelect" multiple="true" class="form-control userSelect" style="height:180px;">
					    	</select>
					    	<div class="checkbox">
							    <label>
							      <input data-type="suser" class="checkAllInput" type="checkbox">全选
							    </label>
							  </div>
						</div>
					    <!-- 门店账号 end-->
					    <div role="tabpanel" class="tab-pane" id="hostlist">
					    	<div class="input-group">
						      <input type="text" id="hkey" class="form-control searchInput" data-searchbtn="hsbtn" placeholder="请输入用户名/手机号">
						      <span class="input-group-btn">
						        <button id="hsbtn" class="btn btn-default" type="button">搜索</button>
						      </span>
						    </div><!-- /input-group -->
						    <select id="huserSelect" multiple="true" class="form-control userSelect" style="height:180px;">
					    	</select>
					    	<div class="checkbox">
							    <label>
							      <input data-type="huser" class="checkAllInput" type="checkbox">全选
							    </label>
							  </div>
					    </div>
					    <!-- 门店销售 end-->
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
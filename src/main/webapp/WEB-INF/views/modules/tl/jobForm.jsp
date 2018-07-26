<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>工作任务管理</title>
	<meta name="decorator" content="adminlte"/>
	<script type="text/javascript">
	$.fn.dataTable.ext.errMode = 'none'; //不显示任何错误信息
		$(document).ready(function() {
			var muHeight = $(window).height() - 20;
			$(".minHeight").css('min-height',muHeight);
			//$("#name").focus();
			$.extend( true, $.fn.dataTable.defaults, {
					"dom": 'rtl',
					"language": {
						 "url": "${ctxStatic}/plugins/datatables/i18n/chinese.json"
						 },
					"fnDrawCallback"    : function(){
							       　　this.api().column(0).nodes().each(function(cell, i) {
							        　　　　cell.innerHTML =  i + 1;
							         　　});
					 }
				} );
			//加载资产明细
				loadAssetTable();
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
		
		function loadAssetTable() {
			var options = {
					"scrollX" : "auto",
					"scrollY" : "100%",
					"processing" : true,
					"serverSide" : true,
					"paging" : false,
					"rowId" : "id",
					 "ordering": false,
					"columns" : [ {
						"data" : null
					},{
						"data" : "groupId" 
					},
					{
						"data" : "groupName",
						
					}, {
						"data" : "groupUrl",
						
					}, {
						"data" : "usernum",
						
					}, {
						"data" : "offset" }
					,{
						"data" : "id",render: function(data, type, row, meta) {
							var div = '<input name="id0" id="id'+meta.row+'" type="hidden" value="'+data+'"/><a  href="javascript:delAsset(\''+data+'\')" class="">删除</a>';
							return div;
						}
					}  ],
					"ajax" : function(data, callback, settings) {
						console.log('ajax settings=%o', settings);
						console.log('ajax data=%o', data);
						//获取参数
						var id=$('#id').val();
						if(id==''){
							callback({ "data":  []});
							return;
						}
						var tempParams = {id:id}; 
						ajaxAssetData(tempParams,callback); 
					}
				};
			
			var ajaxAssetData=function(params,callback){
				var url='${ctx}/tl/job/group/list';
				$.post(url,params,function(result){
					var list=result.data.list;
					callback({  
						"data": result.data.list || []});
				});
			}	;
			
		assettable = $('#tableAsset').DataTable(options);
	}
		
		//导入设备
		function importAsset() {
			var id=$('#id').val();
			// 正常打开	
			var url = "iframe:${ctx}/tl/group/treeselect?type=&module=&checked=true&extId=&isAll=";
			top.$.jBox.open(url, "选择群组", 750, 500, {
				ajaxData : {
					selectIds : ''
				},
				buttons : {
					"确定" : "ok",
					"关闭" : true
				},
				submit : function(v, h, f) {
					if (v == "ok") {
						var tree = h.find("iframe")[0].contentWindow.tree;//h.find("iframe").contents();
						var ids = [], names = [], nodes = [];
						nodes = tree.getCheckedNodes(true);
						//nodes = tree.getSelectedNodes();
						for (var i = 0; i < nodes.length; i++) {//
							//过滤类型节点
							if (nodes[i].nodeType == 'office' || nodes[i].nodeType == 'folder') {
								continue;
							}//
							ids.push(nodes[i].id);
							names.push(nodes[i].name);//
							//break; // 如果为非复选框选择，则返回第一个选择  
						}
						console.log(ids.join(","));
						selectAsset(ids);
						//$("#alterById").val(ids.join(",").replace(/u_/ig,""));
						//$("#alterByName").val(names.join(","));
					}//ok end

				},
				loaded : function(h) {
					$(".jbox-content", top.document).css("overflow-y", "hidden");
				}
			});

			var selectAsset = function(ids) {
				var id=$("#id").val();
				if (ids && ids.length > 0) {
					console.log('添加群组:' + ids.join(","));
					//添加方法
					$.post('${ctx}/tl/job/group/add', {
						ids : ids.join(","),
						id:id 
					}, function(result) {
						if (result.success) {
							top.$.jBox.tip('添加成功', 'success');
							if(id=='')
								$("#id").val(result.id);
							//刷新资产列表
							reloadAssetTable();
						} else {
							top.$.jBox.tip('添加失败', 'error');
						}
					});
				}
			}
		}
		
		//刷新群组列表
		function reloadAssetTable()  {
			if(assettable)
			assettable.ajax.reload();
		}
		//删除群组
		function delAsset(id) {
			top.$.jBox.confirm("确定要移除关联群组？","系统提示",function(v,h,f){
				if(v=="ok"){ 
					$.post('${ctx}/tl/job/group/del', {
						id:id
					}, function(result) {
						if (result.success) {
							top.$.jBox.tip('删除成功', 'success');
							//刷新资产列表
							reloadAssetTable();
						} else {
							top.$.jBox.tip('删除失败', 'error');
						}
					});
				}
			},{buttonsFocus:1});
		}
	</script>
</head>
<body>
	<div class="addData">
		<div class="box box-solid minHeight">
	<form:form id="inputForm" modelAttribute="job" action="${ctx}/tl/job/save" method="post" class="form-horizontal">
		<form:hidden path="isNewRecord"/>
		<sys:message content="${message}"/>
		
			<div class="box-header with-border">
				<h3 class="box-title">工作任务-
				<shiro:hasPermission name="tl:job:edit">${not empty job.id ? '修改':'添加'}</shiro:hasPermission>
				<shiro:lacksPermission name="tl:job:edit">查看</shiro:lacksPermission>
				</h3>
				<div class="box-tools pull-right">
					<shiro:hasPermission name="tl:job:edit"><input id="btnSubmit" class="btn btn-blue" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
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
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">任务ID：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="id" htmlEscape="false" maxlength="100" class="form-control required"/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">任务名称：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="name" htmlEscape="false" maxlength="100" class="form-control required"/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">群组邀请Link：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="groupUrl" htmlEscape="false" maxlength="200" class="form-control "/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row hide">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">群组名称：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="groupName" htmlEscape="false" maxlength="200" class="form-control "/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row hide">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">群组ID：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="groupId" htmlEscape="false" maxlength="11" class="form-control  digits"/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row hide">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">来源群ID：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="fromGroupId" htmlEscape="false" maxlength="11" class="form-control  digits"/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row hide">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">来源群邀请码：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="fromGroupUrl" htmlEscape="false" maxlength="200" class="form-control "/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row hide">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">来源群名称：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="fromGroupName" htmlEscape="false" maxlength="50" class="form-control "/>
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">用户数：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="usernum" htmlEscape="false" maxlength="11" class="form-control  digits"  />
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">几天完成：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="day" htmlEscape="false" maxlength="11" class="form-control  digits" />
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">老板：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:input path="boss" htmlEscape="false" maxlength="100" class="form-control required"  />
				<!-- <span class="help-inline required">*</span> -->
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		<div class="row hide">
		<div class="col-md-12">
			<div class="form-group">
			<label class="control-label col-md-2 col-sm-2 col-ls-2 col-xs-2">任务状态：</label>
			<div class="col-md-8 col-sm-8 col-ls-8 col-xs-8">
				<form:select path="status" class="form-control required">
					<form:options items="${fns:getDictList('job_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<!-- <span class="help-inline required">*</span> -->
			</div>
			</div>
		</div>
	 	</div><!-- /.row col -->
		</div>
	 	</div><!-- /.panel -->
		</div>
			<!-- /.box-body -->
			<div class="box-footer"></div>
			<!-- /.box-footer -->
		 
		 
		 <div class=" addData">
			<div class="box box-solid minHeight">
				<div class="form-group cxtj_text" style="margin-bottom:10px;">
					<span
						style="color:#0aa0f5;padding: 0px 10px;font-size: 18px;margin: 0;line-height: 1;">采集群组列表</span>
					<c:if test="${action!='1'}">
					<a href="javascript:importAsset()" class="btn btn-blue i-edit-ctrl"
						role="button">导入</a>   <a href="javascript:deletAsset()"
						class="btn btn-blue i-edit-ctrl" role="button">删除</a>
					</c:if>
						<a href="javascript:reloadAssetTable()"
						class="btn btn-blue" role="button">刷新</a>
				</div>
				<div class="box-body table-responsive">
					<table id="tableAsset" class="table table-bordered table-hover">
						<thead>
							<tr class="header-normal">
								<!-- <th><input type="checkbox" id="checkAll" /></th> -->
								<th>序号</th>
								<th>群组id</th>
								<th>群组名称</th>
								<th>群组link</th>
								<th>用户数</th>
								<th>采集位置</th>
								<th>操作</th>  
							</tr>
						</thead>
						<tbody> 
						</tbody>
					</table>
					<div>${page}</div>
				</div>
			</div>
		</div>
	</form:form>
	</div>
	</div>
</body>
</html>
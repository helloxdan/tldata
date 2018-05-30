<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>用户管理</title>
<meta name="decorator" content="adminlte" />
<script type="text/javascript">
	$(document).ready(function() {
		var fitstBoxSolidH =  $(".box-solid").eq(0).height();
		
		var muHeight = $(window).height() - fitstBoxSolidH -20 -20;
		$(".minHeight").css('min-height', muHeight);
		$("#btnExport").click(function() {
			top.$.jBox.confirm("确认要导出用户数据吗？", "系统提示", function(v, h, f) {
				if (v == "ok") {
					$("#searchForm").attr("action", "${ctx}/sys/user/export");
					$("#searchForm").submit();
				}
			}, {
				buttonsFocus : 1
			});
			top.$('.jbox-body .jbox-icon').css('top', '55px');
		});
		$("#btnImport").click(function() {
			$.jBox($("#importBox").html(), {
				title : "导入数据",
				buttons : {
					"关闭" : true
				},
				bottomText : "导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"
			});
		});
		
		
		$("#checkAll").click(
				function() {
					var checked = $(this).is(':checked');
					 // console.log($("input[type='checkbox'].list_checkbox").length);
					// console.log('checked:'+$(this).is(':checked'));
					if (checked) {
						$("input[type='checkbox'].list_checkbox").each(function(){  
		                    this.checked=true;  
		                });  
					} else {
						$("input[type='checkbox'].list_checkbox").each(function(){  
		                    this.checked=false;  
		                });  
					}
		});
		$("#btnDel").click(function() {
			var ids = [];
			$("input[type='checkbox']:checkbox:checked.list_checkbox").each(
					function(i, v) {
						//console.log(i,$(this).val());
						ids.push("" + $(this).val() + "");
					});
			ids = ids.join(",");
			console.log(ids);
			if (ids === '') {
				top.$.jBox.tip('请选择记录！', 'warning');
				return;
			}
			confirmx("确定要删除选中记录吗？",function(){
				loading('系统处理中，请稍候……');
				$.post('${ctx}/sys/user/del', {
					ids : ids
				}, function(result) {
					if (result.success) { 
						//刷新页面
						document.location.reload();
					}else{
						top.$.jBox.tip(result.msg, 'warning');
					}
				});
			},function(){
				console.log('取消 删除');
			});
		
		 }); //del click	
	});
	function page(n, s) {
		if (n)
			$("#pageNo").val(n);
		if (s)
			$("#pageSize").val(s);
		$("#searchForm").attr("action", "${ctx}/sys/user/list");
		$("#searchForm").submit();
		return false;
	}
</script>
<style>




/* 特殊样式，不通用 */
.cxtj_text  .input-small {
	width: 125px !important;
}
.form-group label {
    text-align: right;
    width: 80px !important;
}

</style>
</head>
<body>
	<div class="divWrap">
		<div class=" addData">

			<div class="box box-solid">
				<div id="importBox" class="hide">
					<form id="importForm" action="${ctx}/sys/user/import" method="post"
						enctype="multipart/form-data" class="form-search"
						style="padding-left:20px;text-align:center;"
						onsubmit="loading('正在导入，请稍等...');">
						<br /> <input id="uploadFile" name="file" type="file"
							style="width:330px"  /><br /> <br /> <input
							id="btnImportSubmit" class="btn btn-blue" type="submit"
							value="   导    入   " /> <a
							href="${ctx}/sys/user/import/template">下载模板</a>
					</form>
				</div>
				<div class="box-header with-border">
					<h3 class="box-title">用户列表</h3>
				</div>
				<form:form id="searchForm" modelAttribute="user"
					action="${ctx}/sys/user/list" method="post"
					class="form-inline form-search ">
					<input id="pageNo" name="pageNo" type="hidden"
						value="${page.pageNo}" />
					<input id="pageSize" name="pageSize" type="hidden"
						value="${page.pageSize}" />
					<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}"
						callback="page();" />
					<div class="cxtj">
						<div>
							<div class="form-group cxtj_text" style="">
								<label>归属公司：</label>
								<sys:treeselect id="company" name="company.id"
									value="${user.company.id}" labelName="company.name"
									labelValue="${user.company.name}" title="公司"
									url="/sys/office/treeData?type=1" cssClass="input-small"
									allowClear="true" />
							</div>
							<div class="form-group cxtj_text">
								<label>归属部门：</label>
								<sys:treeselect id="office" name="office.id"
									value="${user.office.id}" labelName="office.name"
									labelValue="${user.office.name}" title="部门"
									url="/sys/office/treeData?type=2" cssClass="input-small"
									allowClear="true" notAllowSelectParent="true" />
							</div>
							<div class="form-group cxtj_text">
								<label>登录名：</label>
								<form:input path="loginName" htmlEscape="false" maxlength="50"
									class="form-control" />
							</div>
						</div>
						<div>
							<div class="form-group cxtj_text">
								<label>姓&nbsp;&nbsp;&nbsp;名：</label>
								<form:input path="name" htmlEscape="false" maxlength="50"
									class="form-control" />
							</div>
							<div class="form-group cxtj_text">
								<label>岗位状态：</label>
								<form:select path="postState" class="form-control">
									<form:option value="" label="--请选择--" />
									<form:options items="${fns:getDictList('job_status')}"
										itemLabel="label" itemValue="value" htmlEscape="false" />
								</form:select>
							</div>
							<div class="form-group cxtj_text" style="margin-left: 40px;">
								<input id="btnSubmit" class="btn btn-blue" type="submit"
									value="查询" onclick="return page();" />
							</div>
							<div class="form-group cxtj_text">
								<input id="btnExport" class="btn btn-blue" type="button"
									value="导出" />
							</div>
							<div class="form-group cxtj_text">
								<input id="btnImport" class="btn btn-blue" type="button"
									value="导入" />
							</div>
						</div>
					</div>
				</form:form>
				<sys:message content="${message}" />
			</div>
		</div>
		<div class=" addData">
			<div class="box box-solid minHeight marginBottom0">
				<div class="form-group cxtj_text" style="margin-bottom:10px;">
					<a href="${ctx}/sys/user/form" class="btn" >新增</a>

					<input id="btnReset" class="btn btn-blue" type="reset"
						style="padding: 4px 10px;" value="删除" />
				</div>
				<div class="table-responsive">
					<table id="contentTable"
						class="table table-hover table-bordered table-condensed">
						<thead>
							<tr>
								<th><input type="checkbox" id="checkAll" />
								<th>归属公司</th>
								<th>归属部门</th>
								<th class="sort-column login_name">登录名</th>
								<th class="sort-column name">姓名</th>
								<th>性别</th>
								<th>电话</th>
								<th>手机</th>
								<%--<th>角色</th> --%>

							</tr>
						</thead>
						<tbody>
							<c:forEach items="${page.list}" var="user">
								<tr>
									<td><input type="checkbox" id="select-${user.id}"
										class="list_checkbox" value="${user.id}" /></td>
									<td>${user.company.name}</td>
									<td>${user.office.name}</td>
									<td><a href="${ctx}/sys/user/form?id=${user.id}">${user.loginName}</a>
									</td>
									<td>${user.name}</td>
									<td>${fns:getDictLabel(user.gender, 'sex', '男')}</td>
									<td>${user.phone}</td>
									<td>${user.mobile}</td>
									<%--
				<td>${user.roleNames}</td> --%>

								</tr>
							</c:forEach>
						</tbody>
					</table>
					<div class="pagination">${page}</div>
				</div>
			</div>
		</div>
	</div>


</body>
</html>
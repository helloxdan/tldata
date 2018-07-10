<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>调度任务管理</title>
	<meta name="decorator" content="adminlte"/>
	<script type="text/javascript">
		$(document).ready(function() {
			var muHeight = $(window).height() - 170;
			$(".minHeight").css('min-height', muHeight);
			
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
					$.post('${ctx}/tl/jobTask/del', {
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
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		function runjob(id,taskid) {
			confirmx("确定数据已经准备就绪，开始执行任务吗？",function(){
				loading('系统处理中，请稍候……');
				$.post('${rctx}/api/tl/addUsers', {
					jobid : id,taskid:taskid
				}, function(result) {
					if (result.success) { 
						//刷新页面
						document.location.reload();
					}else{
						top.$.jBox.tip(result.msg, 'warning');
					}
				});
			},function(){
				console.log('取消执行任务');
			});
        	return false;
        }
		function fetchUser(id) {
			confirmx("确定开始执行收集用户任务吗？",function(){
				loading('系统处理中，请稍候……');
				$.post('${rctx}/api/tl/collectUsers', {
					jobid : id
				}, function(result) {
					if (result.success) { 
						//刷新页面
						document.location.reload();
					}else{
						top.$.jBox.tip(result.msg, 'warning');
					}
				});
			},function(){
				console.log('取消执行任务');
			});
        	return false;
        }
        function cleanJobUser(id) {
			confirmx("确定开始执行清洗用户数据任务吗？",function(){
				loading('系统处理中，请稍候……');
				$.post('${rctx}/api/tl/cleanJobUser', {
					jobid : id
				}, function(result) {
					if (result.success) { 
						//刷新页面
						document.location.reload();
					}else{
						top.$.jBox.tip(result.msg, 'warning');
					}
				});
			},function(){
				console.log('取消执行任务');
			});
        	return false;
        }
		
		//添加执行计划
		function addTasks(jobId) {
			 
			$('#itemModal').modal("show");
		}
		function saveTask() {
			var type = $('#fetchtype').val();
			var num = $('#num').val();
			var url = $('#url').val();
			var days = $('#days').val();
			var tags = $('#tags').val();
			var params = {
				jobid:'${jobTask.jobId}',
				'type' : type,
				num : num,
				url : url,
				days : days,
				tags : tags 
			};
			console.log('提交新增任务：', params);
			$.post('${ctx}/tl/jobTask/addTasks', params,
					function(result) {
						if (result.success) {
							top.$.jBox.tip('保存成功', 'success');
							// 
							document.location.reload();
						} else {
							top.$.jBox.tip('保存失败', 'error');
						}
						$('#itemModal').modal("hide");
					});

		}
		
		function fetchTypeChange(){
			 var type = $('#fetchtype').val();
			 if(type=='any'){
				 $('.ft-group').addClass('hide');
				 $('.ft-any').removeClass('hide');
			 }else{
				 $('.ft-group').removeClass('hide');
				 $('.ft-any').addClass('hide');
			 }
		}
	</script>
</head>
<body>
	<div class="addData">
	<div class="box box-solid">
			<div class="box-header with-border">
				<h3 class="box-title">调度任务--${jobTask.job.name }</h3>
			</div>
	<form:form id="searchForm" modelAttribute="jobTask" action="${ctx}/tl/jobTask/" method="post" class="form-inline form-search">
		<input id="jobId" name="jobId" type="hidden" value="${jobTask.jobId}"/>
		<input id="action" name="action" type="hidden" value="${jobTask.action}"/>
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/> 
		<div class="cxtj">
			 
			<div class="form-group cxtj_text"><label>登录账号：</label>
				<form:input path="account" htmlEscape="false" maxlength="64" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>任务类型：</label>
				<form:input path="type" htmlEscape="false" maxlength="50" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>开始位置：</label>
				<form:input path="offsetNum" htmlEscape="false" maxlength="11" class="form-control"/>
			</div>
			<div class="form-group cxtj_text">
						<input id="btnSubmit" class="btn btn-blue" type="submit"
							value="查询" />
						<input id="btnReset" class="btn btn-blue" type="reset" value="重置" />
					</div>
			</div>		 
	</form:form>
	<sys:message content="${message}"/>
	</div>
	</div>
	
	<div class=" addData">
		<div class="box box-solid minHeight">
			<div class="form-group cxtj_text">
				<a href="${ctx}/tl/jobTask/form?jobId=${jobTask.jobId}&type=fetch&action=dispatch" class="btn btn-blue hide">新增单一任务</a>
				<input id="btnAdds" class="btn btn-blue" type="button" value="批量新增" onclick="addTasks('${jobTask.jobId}')"/>
				<input id="btnDel" class="btn btn-blue" type="button" value="删除任务" />
			
					|<a href="javascript:fetchUser('${jobTask.jobId}')"  class="btn btn-blue hide"  >批量执行抽取用户</a>
					<a href="javascript:cleanJobUser('${jobTask.jobId}')"  class="btn btn-blue" title="删除重复数据，或者已经抽取的数据" >清洗用户数据</a>
				<a href="${ctx}/tl/jobUser/list?jobId=${jobTask.jobId}" class="btn btn-blue">用户列表</a>
					<a href="javascript:runjob('${jobTask.jobId}')"  class="btn btn-blue"  >开始拉人</a>
					<a href="${ctx}/tl/jobTask/dispatch?jobId=${jobTask.jobId}"  class="btn  btn-blue"  >刷新</a>
			</div>	
			<div class="form-group cxtj_text">
				<div>任务数：${jobTaskStats.taskNum}</div><div>待添加用户数：${jobTaskStats.userNum}</div><div>已添加用户数：${jobTaskStats.invitedUserNum}</div>
			</div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" id="checkAll" /></th>
				<th class="sort-column a.job_id">任务ID</th>
				<th class="sort-column j.name">任务名称</th>
				<th class="sort-column a.account">登录账号</th>
				<th class="sort-column a.type">任务类型</th>
				<th class="sort-column a.from_group_id">来源群组ID</th>
				<th class="sort-column a.from_group_url">来源群组URL</th>
				<th class="sort-column a.usernum">有效用户数</th>
				<th class="sort-column a.status">任务状态</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jobTask">
			<tr>
				<td><input type="checkbox" id="select-${jobTask.id}"
								class="list_checkbox" value="${jobTask.id}" />
				</td>
				<td><a href="${ctx}/tl/jobTask/form?id=${jobTask.id}&action=dispatch">
					${jobTask.jobId}
				</a></td>
				<td>
					${jobTask.job.name}
				</td>
				<td>
					${jobTask.account}
				</td>
				<td>
					${jobTask.type}
				</td>
				<td>
					${jobTask.groupId}
				</td><td>
					${jobTask.groupUrl}
				</td>
				 
				<td>
					${jobTask.usernum}
				</td>
				<td>
					${fns:getDictLabel(jobTask.status, 'jobtask_status', jobTask.status)}
				</td> 
				<td>
					<%-- <a href="${rctx}/api/tl/collectUsers?taskid=${jobTask.id}">抽取用户</a> --%>
					<a href="javascript:runjob('','${jobTask.id}')">开始拉人</a> 
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div>${page}</div>
	</div>
	</div>
	
	
	<!-- Modal 新增计划执行项 -->
	<div class="modal fade" id="itemModal" tabindex="-1" role="dialog"
		aria-labelledby="新增批量任务">
		<div class="modal-dialog" role="document"
			style="width: 50%; min-height: 400px; height: 80%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title">新增批量任务</h4>
				</div>
				<div class="modal-body">
					<!-- 表单 -->
					<form id="">
						 
						<div class="form-group">
							<label for="fetchtype">方式</label>  <select id="fetchtype"
								name="fetchtype" class="form-control" onchange="fetchTypeChange()">
								<option value="any">不限群组 </option>
								<option value="group">指定群组</option>
							</select>
						</div>
					 
						<div class="form-group ">
							<label for="num">拉人总数</label> <input type="text"
								class="form-control" id="num" placeholder="拉人总数"
								value="1">
						</div>
						 <div class="form-group ft-group hide">
							<label for="url">来源群组link</label> <input type="text"
								class="form-control" id="url" placeholder="来源群组link" >
						    
						</div>
						   <div class="form-group ft-group ft-any ">
							 <label for="url">过滤条件：</label>
						   
						</div><div class="form-group ft-any ">
							<label for="offset">最近活跃天数</label> 
							<input type="text"
								class="form-control" id="days" placeholder="天数"
								 value="30">
						</div>
						<div class="form-group  ft-any ">
							<label for="limit">来源群组标签</label> <input type="text"
								class="form-control" id="tags" placeholder="多个空格隔开"
								 value="">
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" onclick="saveTask()">保存</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>
	<!-- Modal end -->
</body>
</html>
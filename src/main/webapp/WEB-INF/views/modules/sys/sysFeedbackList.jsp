<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>问题反馈管理</title>
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
					$.post('${ctx}/sys/sysFeedback/del', {
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
	</script>
</head>
<body>
	<div class="addData">
	<div class="box box-solid">
			<div class="box-header with-border">
				<h3 class="box-title">问题反馈列表</h3>
			</div>
	<form:form id="searchForm" modelAttribute="sysFeedback" action="${ctx}/sys/sysFeedback/" method="post" class="form-inline form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/> 
		<div class="cxtj">
			<div class="form-group cxtj_text"><label>问题类型：</label>
				<form:select path="type" class="form-control">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('feedback_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
			<div class="form-group cxtj_text"><label>标题：</label>
				<form:input path="title" htmlEscape="false" maxlength="100" class="form-control"/>
			</div>
			<div class="form-group cxtj_text"><label>报告人：</label>
				<sys:treeselect2 id="reportBy" name="reportBy.id" value="${sysFeedback.reportBy.id}" labelName="reportBy.name" labelValue="${sysFeedback.reportBy.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="form-control" allowClear="true" notAllowSelectParent="true"/>
			</div>
			<div class="form-group cxtj_text"><label>报告时间：</label>
				<div class="input-group">
				<input name="beginReportDate" type="text" readonly="readonly" maxlength="20" class="form-control"
					value="<fmt:formatDate value="${sysFeedback.beginReportDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>   
				</div>-<div class="input-group">
				<input name="endReportDate" type="text" readonly="readonly" maxlength="20" class="form-control"
					value="<fmt:formatDate value="${sysFeedback.endReportDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
				 </div>
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
				<a href="${ctx}/sys/sysFeedback/form" class="btn btn-blue">新增</a>
				<input id="btnDel" class="btn btn-blue" type="button" value="删除" />
			</div>	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" id="checkAll" /></th>
				<th class="sort-column a.type">问题类型</th>
				<th class="sort-column a.title">标题</th>
				<th class="sort-column a.content">问题内容</th>
				<th class="sort-column a.report_by">报告人</th>
				<th class="sort-column a.report_date">报告时间</th>
				<th class="sort-column a.remarks">备注</th>
				<th class="sort-column a.reply_by">回复人</th>
				<th class="sort-column a.reply_content">回复内容</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysFeedback">
			<tr>
				<td><input type="checkbox" id="select-${sysFeedback.id}"
								class="list_checkbox" value="${sysFeedback.id}" />
				</td>
				<td><a href="${ctx}/sys/sysFeedback/form?id=${sysFeedback.id}">
					${fns:getDictLabel(sysFeedback.type, 'feedback_type', '')}
				</a></td>
				<td>
					${sysFeedback.title}
				</td>
				<td>
					${sysFeedback.content}
				</td>
				<td>
					${sysFeedback.reportBy.name}
				</td>
				<td>
					<fmt:formatDate value="${sysFeedback.reportDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${sysFeedback.remarks}
				</td>
				<td>
					${sysFeedback.replyBy.name}
				</td>
				<td>
					${sysFeedback.replyContent}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div>${page}</div>
	</div>
	</div>
</body>
</html>
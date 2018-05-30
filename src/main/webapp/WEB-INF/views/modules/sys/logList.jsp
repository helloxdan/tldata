<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>日志管理</title>
<meta name="decorator" content="adminlte" />
<script type="text/javascript">
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
	$(function(){
		var fitstBoxSolidH =  $(".box-solid").eq(0).height();
		var muHeight = $(window).height() - fitstBoxSolidH -20 -20 -20;
		$(".minHeight").css('min-height', muHeight);
	
	});
</script>
<style>

</style>
</head>
<body>

	<div class="divWrap">
		<div class=" addData">
			<div class="box box-solid">
				<form:form id="searchForm" action="${ctx}/sys/log/" method="post"
					class="form-inline form-search">
					<input id="pageNo" name="pageNo" type="hidden"
						value="${page.pageNo}" />
					<input id="pageSize" name="pageSize" type="hidden"
						value="${page.pageSize}" />
					<div class="cxtj">
						<div class="form-group cxtj_text">
							<label>操作菜单：</label><input id="title" name="title" type="text"
								maxlength="50" class="form-control" value="${log.title}" />
						</div>
						<div class="form-group cxtj_text">
							<label>用户ID：</label><input id="createBy.id" name="createBy.id"
								type="text" maxlength="50" class="form-control"
								value="${log.createBy.id}" />
						</div>
						<div class="form-group cxtj_text">
							<label>URI：</label><input id="requestUri" name="requestUri"
								type="text" maxlength="50" class="form-control"
								value="${log.requestUri}" />
						</div>
						<div class="form-group cxtj_text">
							<label>日期范围：&nbsp;</label><input id="beginDate" name="beginDate"
								type="text" readonly="readonly" maxlength="20"
								class="form-control Wdate"
								value="<fmt:formatDate value="${log.beginDate}" pattern="yyyy-MM-dd"/>"
								onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" />
						</div>
						<div class="form-group cxtj_text">
							<label style="width:auto;margin:0 10px;">--</label><input
								id="endDate" name="endDate" type="text" readonly="readonly"
								maxlength="20" class="form-control Wdate"
								value="<fmt:formatDate value="${log.endDate}" pattern="yyyy-MM-dd"/>"
								onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" />
						</div>
						<div class="form-group cxtj_text">
							<label for="exception" style="width:auto;margin-right:10px;"><input
								id="exception" name="exception" type="checkbox" ${log.exception
								eq '1'?' checked':''} value="1" />只查询异常信息</label>
						</div>
						<div class="form-group cxtj_text">
							<input id="btnSubmit" class="btn btn-blue" type="submit"
								value="查询" />
						</div>
					</div>

				</form:form>
				<sys:message content="${message}" />
			</div>
		</div>
		<div class=" addData">
			<div class="box box-solid minHeight">
				<div class="table-responsive">
					<table id="contentTable"
						class="table table-hover table-bordered table-condensed">
						<thead>
							<tr>
								<th>操作菜单</th>
								<th>操作用户</th>
								<th>所在公司</th>
								<th>所在部门</th>
								<th>URI</th>
								<th>提交方式</th>
								<th>操作者IP</th>
								<th>操作时间</th>
						</thead>
						<tbody>
							<%
								request.setAttribute("strEnter", "\n");
								request.setAttribute("strTab", "\t");
							%>
							<c:forEach items="${page.list}" var="log">
								<tr>
									<td>${log.title}</td>
									<td>${log.createBy.name}</td>
									<td>${log.createBy.company.name}</td>
									<td>${log.createBy.office.name}</td>
									<td><strong>${log.requestUri}</strong></td>
									<td>${log.method}</td>
									<td>${log.remoteAddr}</td>
									<td><fmt:formatDate value="${log.createDate}" type="both" />
									</td>
								</tr>
								<c:if test="${not empty log.exception}">
									<tr>
										<td colspan="8"
											style="word-wrap:break-word;word-break:break-all;">
											<%-- 					用户代理: ${log.userAgent}<br/> --%> <%-- 					提交参数: ${fns:escapeHtml(log.params)} <br/> --%>
											异常信息: <br />
											${fn:replace(fn:replace(fns:escapeHtml(log.exception),
											strEnter, '<br/>'), strTab, '&nbsp; &nbsp; ')}</td>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<div class="pagination">${page}</div>
			</div>

		</div>
	</div>



</body>
</html>
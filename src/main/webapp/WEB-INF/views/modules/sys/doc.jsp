<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--
 * CKFinder
 * ========
 * http://cksource.com/ckfinder
 * Copyright (C) 2007-2010, CKSource - Frederico Knabben. All rights reserved.
 *
 * The software, this file and its contents are subject to the CKFinder
 * License. Please read the license.txt file before using, installing, copying,
 * modifying or distribute this file or part of its contents. The contents of
 * this file is part of the Source Code of CKFinder.
-->
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- import the tag library -->
<%@ taglib uri="http://cksource.com/ckfinder" prefix="ckfinder" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>用户文档管理</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="robots" content="noindex, nofollow" />
	<style type="text/css">
		body, html, iframe, #ckfinder {
			margin: 0;
			padding: 0;
			border: 0;
			width: 100%;
			height: 100%;
			overflow: hidden;
		}
	</style>
	<script type="text/javascript">
		//选中缩略图的回调方法，只有设置回调参数后，才会出现缩略图的菜单按钮
		function selectThumbnail(fileUrl,data){
			console.log('fileUrl=',fileUrl,data);
			}
		function selectFile(fileUrl,data){
			console.log('fileUrl=',fileUrl,data);
		}
	</script>
</head>
<body>
	<sys:message content="${message}" />
	<!-- disableThumbnailSelection="false" selectThumbnailFunctionData="field1" selectThumbnailFunction="selectThumbnail" selectFunction="selectFile" -->
	<ckfinder:ckfinder basePath="../../../static/ckfinder/" id="testapp" width="100%" />
</body>
</html>

package com.thinkgem.jeesite.modules.tl.vo;

import java.io.Serializable;

import com.thinkgem.jeesite.common.persistence.BaseEntity;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 前端展示树的节点数据基类。如设备树，部门树等。
 * 
 * @author ThinkPad
 *
 */
public class TreeNode extends BaseEntity<TreeNode> implements Serializable {
	private static final long serialVersionUID = 9109180173450634584L;
	protected Boolean isTop = false; // 是否顶级节点
	protected String pId; // 父级编号
	protected String parentIds; // 所有父级编号
	protected String nodeType; // 节点类型
	protected String name; // 名称
	protected Integer sort; // 排序

	protected String model; // 型号
	protected String type; // 设备类型
	protected boolean isParent; // 是否父节点
	protected String description;//节点描述，用于提示
	
	protected boolean isbig; // 是否父节点判断是否大型设备

	protected String tenantId;

	public Boolean getIsTop() {
		return isTop;
	}

	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	// 如果没有设置所属租户，就取当前用户。
	public String getTenantId() {
		return tenantId == null ? UserUtils.getUserTenant().getId() : tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public boolean isIsParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	@Override
	public void preInsert() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preUpdate() {
		// TODO Auto-generated method stub

	}

	public boolean isIsbig() {
		return isbig;
	}

	public void setIsbig(boolean isbig) {
		this.isbig = isbig;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

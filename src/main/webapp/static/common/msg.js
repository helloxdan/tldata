
/**
 * 系统消息、短信发送表单的js工具类。
 */

var MsgForm=function(outputEle,editable){
	//已经选择的用户id，用户判断是否已选中
	this.selectedMap={};
	this.selected=[];
	this.data={}; 
	this.output=$('#'+outputEle);
	this.outputNum=$('#'+outputEle+'Num');
	this.editable=editable;
};

MsgForm.prototype.revertSelects=function(){
	for(var key in this.data){
		this.setSelectOptions(key,this.data[key]);
	}
};

MsgForm.prototype.setSelectOptions=function(type,list){
	if(!list) return ;
	var thisSelect=$('#'+type+'Select');
	thisSelect.empty();
	
	for(var i in list){
		var item=list[i];
		if(!this.selectedMap[item.id]){
			thisSelect.append('<option value="'+item.id+'" data-mobile="'+item.mobile+'" data-name="'+item.name+'">'+item.name+'('+item.mobile+')</option>');
		}
	}
};


MsgForm.prototype.setList=function(type,list){
	this.data[type]=list;
	 
	this.setSelectOptions(type,list);
};

MsgForm.prototype.initUser=function(jsonText){
	if(!jsonText || jsonText=='') return;
	var list=jQuery.parseJSON(jsonText);
	for(var i in list){
		var item=list[i];
		this.selectedMap[item.id]=item.n;
		this.selected.push({id:item.id,name:item.n,mobile:item.m});
	}
	
	this.renderOutput();
};

MsgForm.prototype.getUserJson=function(){
	var list =this.getSelected();
	var json=[];
	for(var i in list){
		var item=list[i];
		json.push({id:item.id,n:item.name,m:item.mobile});
	}
	return JSON.stringify(json);
};

MsgForm.prototype.cleanUser=function(){
	this.selectedMap={};
	this.selected=[];
	this.renderOutput();
	//重新放回select选择项中
	this.revertSelects();
};
MsgForm.prototype.addUser=function(id,name,mobile){
	this.selectedMap[id]=name;
	this.selected.push({id:id,name:name,mobile:mobile});
	
	this.renderOutput();
};
MsgForm.prototype.selectUser=function(thisSelect){
	var id=thisSelect.val();
	id=id.length>0?id[0]:"";
	if(this.selectedMap[id]) return;
	
	var name= thisSelect.find("option:selected").data('name');
	var mobile= thisSelect.find("option:selected").data('mobile');
	this.selectedMap[id]=name;
	this.selected.push({id:id,name:name,mobile:mobile});
	//console.log('选中：',id,name,mobile);	
	
	this.renderOutput();
	//删除select 中的元素
	thisSelect.find("option:selected").remove();
};

MsgForm.prototype.removeSelectedUser=function(id ){
	this.selectedMap[id]=null;
	var list=this.selected;
	this.selected=[];
	for(var i in list){
		var item=list[i];
		if(item && id==item.id){
			list[i]=null;
			continue;
		}
		this.selected.push(item);
	}
	
	this.outputNum.html(this.selected.length);
	//重新放回select选择项中
	this.revertSelects();
};
MsgForm.prototype.renderOutput=function( ){
	this.output.empty();
	var self=this;
	this.outputNum.html(this.selected.length);
	for(var i in this.selected){
		var item=this.selected[i];
		if(item==null) continue;
		
		var userBox=$('<div  id="userbox'+item.id+'" class="userBox">'+item.name+'</div>');
		if(this.editable){
			var delBtn=$('<button data-id="'+item.id+'" type="button" class="close" aria-label="Close"><span aria-hidden="true">&times;</span></button>');
			delBtn.click(function(){
				var boxid=$(this).data('id');
				$('#userbox'+boxid).remove();
				self.removeSelectedUser(boxid);
			});
			userBox.append(delBtn);
		}
		this.output.append(userBox);
	}
};

MsgForm.prototype.getSelected=function( ){
	var list=[];
	for(var i in this.selected){
		var item=this.selected[i];
		if(item)
			list.push(item);
	}
	return list;
};
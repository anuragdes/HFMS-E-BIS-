// For Quality Check Control Transaction:

/**
 * Developer : Tanvi Sappal 
 * Version : 1.0 
 * Date : 04/June/2009
 * 
 */
 
 // Item Category associate with Store Name:
function itemCategoryCombo()
{
   var mode ="ITEMCATEGORY"; 
   var url="QualityCheckControlTransCNT.cnt?hmode=ITEMCATEGORY&storeid="+document.forms[0].strStoreId.value;
   ajaxFunction(url,"1");
} 
// Group Name Associate with Store Name and Item Category:
function groupNameCombo()
{
   var mode ="GROUPNAME"; 
   var url="QualityCheckControlTransCNT.cnt?hmode=GROUPNAME&itemCatNo="+document.forms[0].strItemCategoryNo.value;
   ajaxFunction(url,"2");
} 

function subGroupNameCombo()
{
   var mode ="SUBGROUPNAME";  
   var url="QualityCheckControlTransCNT.cnt?hmode=SUBGROUPNAME&groupId="+document.forms[0].strGroupId.value;
   ajaxFunction(url,"3");
} 

function genItemNameCombo()
{
   var mode ="GENITEMNAME";  
   var url="QualityCheckControlTransCNT.cnt?hmode=GENITEMNAME&itemCatNo="+document.forms[0].strItemCategoryNo[document.forms[0].strItemCategoryNo.selectedIndex].value+
   				"&groupId="+document.forms[0].strGroupId[document.forms[0].strGroupId.selectedIndex].value+
   				"&subgrpid="+document.forms[0].strSubGroupId[document.forms[0].strSubGroupId.selectedIndex].value;
   ajaxFunction(url,"4");
} 

function itemNameCombo()
{
   var mode ="ITEMNAME";  
   var url="QualityCheckControlTransCNT.cnt?hmode=ITEMNAME&storeid="+document.forms[0].strStoreId.value+
   				"&itemCatNo="+document.forms[0].strItemCategoryNo[document.forms[0].strItemCategoryNo.selectedIndex].value+
   				"&groupId="+document.forms[0].strGroupId[document.forms[0].strGroupId.selectedIndex].value+
   				"&subgrpid="+document.forms[0].strSubGroupId[document.forms[0].strSubGroupId.selectedIndex].value+
   				"&genitemid="+document.forms[0].strGenericItemId[document.forms[0].strGenericItemId.selectedIndex].value;
   ajaxFunction(url,"5");
} 

function batchNoCombo()
{
   var mode ="BATCHNO";  
   document.forms[0].strIsConsumable.value = document.forms[0].strItemBrandId.value.split('^')[1];
   var url="QualityCheckControlTransCNT.cnt?hmode=BATCHNO&storeid="+document.forms[0].strStoreId.value+
   							"&itemCatNo="+document.forms[0].strItemCategoryNo[document.forms[0].strItemCategoryNo.selectedIndex].value
   									+"&genitemId="+document.forms[0].strGenericItemId[document.forms[0].strGenericItemId.selectedIndex].value
   									+"&itemId="+document.forms[0].strItemBrandId.value;
								
   ajaxFunction(url,"6");
  
} 

function getMemberDtl(mode)
{
	var mode=mode;
	var url="QualityCheckControlTransCNT.cnt?hmode="+mode+"&committeType="+document.forms[0].strCommitteeTypeId.value+"&itemCategNo="+document.forms[0].strItemCategoryNo.value;
	ajaxFunction(url,"7");
}

function itemCategory(){
   var mode ="VIEWITEM"; 
   var url="QualityCheckControlTransCNT.cnt?hmode=VIEWITEM&storeid="+document.forms[0].strStoreId.value;
   ajaxFunction(url,"8");
}
function unitNameCombo(brandId)
{
   var mode ="UNITNAME";  
   var url="QualityCheckControlTransCNT.cnt?hmode="+mode+"&itemId="+brandId;
   ajaxFunction(url,"9");
  
} 
function getAjaxResponse(res,mode)
{
	
	var objVal;
	if(mode=="1")
	{	
	   var err = document.getElementById("errMsg");
	   var temp1 = res.split("####");
	   if(temp1[0] == "ERROR")
	   {
         err.innerHTML = temp1[1];	
       }else
        {
          objVal= document.getElementById("itemCategoryId");
		  objVal.innerHTML = "<select name ='strItemCategoryNo' onChange='groupNameCombo()'>" + res + "</select>";
		  groupNameCombo();
        }
        resetScreen();
    }
    
   	if(mode=="2")
	{	
	   var err = document.getElementById("errMsg");
	   var temp1 = res.split("####");
	   if(temp1[0] == "ERROR")
	   {
         err.innerHTML = temp1[1];	
       }else
        {
       	  var temp = res.split('^');
       	  objVal= document.getElementById("groupId");
		  objVal.innerHTML = "<select name ='strGroupId' onChange='subGroupNameCombo()'>" + temp[0] + "</select>";
		  
		  document.getElementById('strCommitteeTypeDivId').innerHTML = "<select name='strCommitteeTypeId' id='strCommitteeTypeId' class='comboNormal' onchange='getMemberDtl(\"COMMITEEMEMBERDTL\");'>"+temp[1];+"</select>"
		  resetScreen();
        }
    }
    
    if(mode=="3")
	{	
	   var err = document.getElementById("errMsg");
	   var temp1 = res.split("####");
	   if(temp1[0] == "ERROR")
	   {
         err.innerHTML = temp1[1];	
       }else
        {
          objVal= document.getElementById("subGroupId");
		  objVal.innerHTML = "<select name ='strSubGroupId' onChange='genItemNameCombo()'>" + res + "</select>";
        }
        
        genItemNameCombo();
        resetScreen();
        
    }
    
    if(mode=="4")
	{	
	   var err = document.getElementById("errMsg");
	   var temp1 = res.split("####");
	   if(temp1[0] == "ERROR")
	   {
         err.innerHTML = temp1[1];	
       }else
       {
          objVal= document.getElementById("genItemId");
		  objVal.innerHTML = "<select name ='strGenericItemId' onChange='itemNameCombo()'>" + res + "</select>";
        }
        
        //itemNameCombo();
        //unitNameCombo();
    }
 
    if(mode=="5")
	{	
       var err = document.getElementById("errMsg");
	   var temp1 = res.split("####");
	   if(temp1[0] == "ERROR")
	   {
         err.innerHTML = temp1[1];	
       }else
        {
          objVal= document.getElementById("itemId");
		  objVal.innerHTML = "<select name ='strItemBrandId' onChange='batchNoCombo()'>" + res + "</select>";  
        }
    }
    
    if(mode=="6")
	{	
	
	var tempVal = document.forms[0].strGenericItemId.value.split('^');
	
	   var err = document.getElementById("errMsg");
	   var temp1 = res.split("####");
	   if(temp1[0] == "ERROR")
	   {
         err.innerHTML = temp1[1];	
       }else
        {
        	
        	document.forms[0].strIsConsumable.value = document.forms[0].strItemBrandId.value.split('^')[1];
        
		 	 if(document.forms[0].strItemCategoryNo.value=='1'){
       
       		 if(tempVal[1] == '0'){
	          
	          	 document.getElementById('strBatchDivId').style.display = "none";
			     document.getElementById('strBatchItemDivId').style.display = "none";
	          
	          }else{      
			          document.getElementById('strBatchDivId').style.display = "block";
			          document.getElementById('strBatchItemDivId').style.display = "none";
			          objVal= document.getElementById("batchNoId");
					  objVal.innerHTML = "<select name ='strBatchNo' onChange='stockPosition(this)'>" + res + "</select>";
					  document.getElementById("qualityDivId").style.display="none";
			      }
			   
		  }else{
		  
		 // alert("res-"+res);
		  		var temp = res.split('@');
		  
		   if(tempVal[1] == '0'){
		   	
		   		 document.getElementById('strBatchDivId').style.display = "none";
		   		
		   }else{
		   			
		   		 document.getElementById('strBatchDivId').style.display = "block";
		  	 	objVal= document.getElementById("batchNoId");
		   	
		   			objVal.innerHTML = "<select name ='strBatchNo' onchange='stockPosition(this);'>" + temp[0] + "</select>";
		   			document.getElementById("qualityDivId").style.display="none";
		   		
		   	
		   }
		  
		 
		 if(tempVal[2] == '0'){
		   	
		   		 document.getElementById('strBatchItemDivId').style.display = "none";
		   		
		   }else{
		   			//alert(" temp[1]-"+ temp[1]);
		   		  document.getElementById('strBatchItemDivId').style.display = "block";
		  	 	 objVal= document.getElementById("itemSlNoId");
		    	objVal.innerHTML = "<select name ='strItemSlNo' onChange='stockPosition(this)'>" + temp[1] + "</select>";
		    	document.getElementById("qualityDivId").style.display="none";
		   }
		 
		  
		  }
		 	 
		 	 
		 	 if(document.forms[0].strIsConsumable.value =='1'){
		document.getElementById("qualityDetailsDivId").style.display="block";
		}else{
		document.getElementById("qualityDetailsDivId").style.display="none";
		}
		 
	
        }
        
           unitNameCombo(document.forms[0].strItemBrandId.value);	
        
    }
    if(mode=="7")
    {
   		var err = document.getElementById("errMsg");
	   var temp1 = res.split("####");
	   if(temp1[0] == "ERROR")
	   {
         err.innerHTML = temp1[1];	
       }else
        {
			var objVal;
			document.getElementById("memberDtlInner").innerHTML=res;	
		}
	}
	if(mode=="8")
	{	
	   var err = document.getElementById("errMsg");
	   var temp1 = res.split("####");
	   if(temp1[0] == "ERROR")
	   {
         err.innerHTML = temp1[1];	
       }else
        {
          objVal= document.getElementById("ItemCatNameId");
		  objVal.innerHTML = "<select name ='strItemCategoryNo'>" + res + "</select>";
        }
    }
    if(mode=="9"){	
	
	
	
	   var err = document.getElementById("errMsg");
	   var temp1 = res.split("####");
	   if(temp1[0] == "ERROR")
	   {
         err.innerHTML = temp1[1];	
       }else
        {
		  document.getElementById('consumedQtyDivId').innerHTML = "<select name='strConsumedQtyUnitId' class='comboNormal' onchange='return quantityUnitValue();'>"+res+"</select>" ;
        }
    }
    
    
}

function stockPosition(obj)
{
	if(obj.value == '0'){
	document.getElementById("qualityDivId").style.display="none";
	return false;
	}
		var temp = obj.value.split('^');
	
			document.getElementById("avlQtyDivId").innerHTML = temp[2];
			document.getElementsByName('strInhandQty')[0].value = temp[2];
			
			if(temp[3]=="")
			{
				document.getElementById("poNoDivId").innerHTML = "--"
			}
			else{
				document.getElementById("poNoDivId").innerHTML = temp[3];
				}
		
			document.getElementsByName('strPONo')[0].value = temp[3];
			
			if(temp[4]=="")
			{
				document.getElementById("poDateDivId").innerHTML = "--"
			}else{
				document.getElementById("poDateDivId").innerHTML = temp[4];
			}
			document.getElementsByName('strPODate')[0].value = temp[4];
			
			if(temp[5]=="")
			{
				document.getElementById("suppliedByDivId").innerHTML = "--"
			}else{
				document.getElementById("suppliedByDivId").innerHTML = temp[5];
			}
			document.getElementsByName('strSupplierId')[0].value = temp[5];
			
					
	if(document.forms[0].strIsConsumable.value =='1'){
		document.getElementById("qualityDetailsDivId").style.display="block";
	}else{
		document.getElementById("qualityDetailsDivId").style.display="none";
	}
	
	document.getElementById("qualityDivId").style.display="block";
}

function itemStatus()
{
	if(document.forms[0].strItemStatus[1].checked);
	{
		document.getElementById("rejectDivId").style.display="block";
	}
}

function itemStatus2(){
	if(document.forms[0].strItemStatus[0].checked);
	{
		document.getElementById("rejectDivId").style.display="none";
	}
} 

function statusValues()
{
	document.forms[0].strItemStatus[0].checked = true;
	
	if(document.forms[0].strHidDistributionFlag.value == 1){
			document.forms[0].strDistributionFlag.checked = true;
		}
}

function openDivPopup()
{
		if(document.getElementsByName("strCommitteeTypeId")[0].value=="0")
   	 	{
   	 		alert("Please Select Approved By")
   	 	}
 	 	else{
  			 popup('memberDtl' , '130','250');
  		}
}
function closePopUpDiv()
{
	hide_popup('memberDtl');
}
function clearData()
{
	var size=document.getElementsByName("strMemberRecommendation").length;
	if(size>1){
		for(var i=0;i<size;i++){
			document.getElementsByName("strMemberRecommendation")[i].value="";
		}
	}
	else{
		document.getElementsByName("strMemberRecommendation")[0].value="";
	}
	
}

function goFuncView()               
{
        var hisValidator = new HISValidator("qualityCheckControlBean");  
         hisValidator.addValidation("strStoreId","dontselect=0","Please select Store Name");
        hisValidator.addValidation("strItemCategoryNo","dontselect=0","Please select an Item Category");
	 	
		var retVal = hisValidator.validate();
		 
		
	    if(retVal)
	    {
	            document.forms[0].strHidStoreName.value = document.forms[0].strStoreId[document.forms[0].strStoreId.selectedIndex].text;
	    		document.forms[0].strItemCategoryName.value=document.forms[0].strItemCategoryNo[document.forms[0].strItemCategoryNo.selectedIndex].text;
			    document.forms[0].hideItemCatId.value=document.forms[0].strItemCategoryNo.value;
			    document.forms[0].displayFlag.value="1";
		         	
	        	document.forms[0].hmode.value="VIEWITEMGO";
				document.forms[0].submit();
	           	
		}else{
		
		return false;
		}
	
	
}

function ViewHideandBlock(){
    document.getElementsByName('strView')[0].checked = true;
	if(document.forms[0].displayFlag.value=="1")
   		{
   		   document.getElementById("StoreDivId").style.display= "none";
   		   document.getElementById("StoreNameDivId").style.display= "block";
		   document.getElementById("ItemCatNameId").style.display= "none";
	       document.getElementById("ItemCatNamedivId").style.display= "block";
	       document.getElementById("qualityDivId").style.display= "block";
	       document.getElementById("imageDivId").style.display= "none";
	       document.getElementsByName('strView')[0].disabled=true;

		}else {
		   document.getElementById("StoreDivId").style.display= "block";
   		   document.getElementById("StoreNameDivId").style.display= "none";
		   document.getElementById("ItemCatNameId").style.display= "block";
	       document.getElementById("ItemCatNamedivId").style.display= "none";
	       document.getElementById("qualityDivId").style.display= "none";
	       document.getElementById("imageDivId").style.display= "block";
	     		
		}
	}	

function diplayView(){


	 if(document.getElementsByName('strView')[0].checked){
	 //alert("displayview---->"+document.getElementsByName('strView')[0].value);
    
   
     	document.forms[0].hmode.value="VIEW";
 	  	document.forms[0].displayFlag.value="0";
		document.forms[0].submit(); 
    
    	}else {
    	
    	document.forms[0].hmode.value="unspecified";
 	    document.forms[0].displayFlag.value="0";
		document.forms[0].submit(); 
    	
    	}
    	
	}  
function clearView()
{
	
	var url;
	var mode = "unspecified";
	document.forms[0].hmode.value=mode;
	document.forms[0].displayFlag.value="0";
	document.forms[0].submit();
 	

}
	
		
function GetIndex(index,endVal)
{
   // alert("index--.>"+index+"<-Total Recrd-->"+endVal);
          for(var i = 1; i <= endVal ; i++)
		  {
		  //  alert(i+"<---i-->"+document.getElementById("DivId"+i).style.display);
		    document.getElementById("DivId"+i).style.display="none";
		  }
		 // alert("before-->>"+document.getElementById("DivId"+index).style.display);
		  document.getElementById("DivId"+index).style.display="block";
		 // alert("before-->>"+index+"<div>"+document.getElementById("DivId"+index).style.display);
			 
}

function goFuncOnView(e)
{
   if(e.keyCode == 13)
   {
	 goFuncView();
	}
	else
	{
	 return false;
	}
} 

function validate1(){
	//alert("document.forms[0].strItemSlNo.value-"+document.forms[0].strItemSlNo.value);
	var hisValidator = new HISValidator("qualityCheckControlBean");
	  hisValidator.addValidation("strStoreId","dontselect=0","Please select Store Name");
	  hisValidator.addValidation("strItemCategoryNo","dontselect=0","Please select Item Category");
	  hisValidator.addValidation("strGroupId","dontselect=0","Please select Group Name");
	  hisValidator.addValidation("strGenericItemId", "dontselect=0", "Please select Generic Item Name" );
	  hisValidator.addValidation("strItemBrandId","dontselect=0","Please select Item Name");
	  
	  if(document.getElementById('strBatchDivId').style.display=="block"){
	   hisValidator.addValidation("strBatchNo","dontselect=0","Please select Batch No.");
	  }
	  
	  if(document.getElementById('strBatchItemDivId').style.display=="block"){
	  	hisValidator.addValidation("strItemSlNo","dontselect=0","Please select Item Serial No.");
	  }
	  if(document.forms[0].strIsConsumable.value=="1"){
	  	hisValidator.addValidation("strConsumedQty","req","Consumed Quantity is Mandatory Field ");
	  	hisValidator.addValidation("strConsumedQty","amount=7,2","Quantity should be in 00000.00 format");
	  	hisValidator.addValidation("strConsumedQtyUnitId","dontselect=0","Please select Unit Id");
	  }
	  hisValidator.addValidation("strFinalResult", "req", "Result is Mandatory Field" );
	  hisValidator.addValidation("strFinalResult", "maxlen=250", "Result should be equals to 250 Characters" );
	  hisValidator.addValidation("strRemarks", "maxlen=100", "Remarks should be equals to 100 Characters" );
	  //if(document.forms[0].strItemStatus.value=="2"){
	  		hisValidator.addValidation("strCommitteeTypeId","dontselect=0","Please select Approved By");
	  //}
	  retVal = hisValidator.validate();
	  if(retVal){
	  
	  		var avlQty = document.forms[0].strInhandQty.value;
		var consumedQty = document.forms[0].strConsumedQty.value;
		var consumedUnit = document.forms[0].strConsumedQtyUnitId.value;
		
		var temp1 = avlQty.split(" ");
		
		var temp = consumedUnit.split('^');
		
		var consumedValue = parseFloat(consumedQty) * parseFloat(temp[1]);
		
		if(consumedValue>parseFloat(avlQty))
		{
			
			alert("Consumed Quantity must be less than Avl Quantity");
			document.forms[0].strConsumedQty.value = "";
			document.forms[0].strConsumedQtyUnitId.value = "0"
		
		}
		
			document.forms[0].hmode.value = "INSERT";
			document.forms[0].submit();
		}else{

           return false;
           }

}

function getClear(){

	document.getElementById("qualityDivId").style.display="none";
  	document.forms[0].strStoreId.value='0';
  	document.forms[0].strItemCategoryNo.value='0';
  	document.forms[0].strGroupId.value='0';
  	document.forms[0].strSubGroupId.value='0';
  	document.forms[0].strGenericItemId.value='0';
  	document.forms[0].strItemBrandId.value='0';
  	document.forms[0].strBatchNo.value='0';
  	document.getElementById("errMsg").innerHTML = "";

}

// To cancel the Page:
 function cancel()
 {
    document.getElementById("errMsg").innerHTML = "";
    document.forms[0].hmode.value = "CANCEL";
    document.forms[0].submit();
 }

function distributionCheck(){
	
	//alert(document.getElementsByName('strDistributionFlag')[0].checked);
	if(document.getElementsByName('strDistributionFlag')[0].checked)
	{
		document.forms[0].strDistributionFlag.value = "1";
	}else{
		//alert("Hello");
		document.forms[0].strDistributionFlag.value = "0";
	}

	//alert(document.forms[0].strDistributionFlag.value);
}

function quantityUnitValue()
{
	if(document.forms[0].strConsumedQtyUnitId.value != 0){
		var avlQty = document.forms[0].strInhandQty.value;
		var consumedQty = document.forms[0].strConsumedQty.value;
		var consumedUnit = document.forms[0].strConsumedQtyUnitId.value;
		
		var temp1 = avlQty.split(" ");
		
		var temp = consumedUnit.split('^');
		
		var consumedValue = parseFloat(consumedQty) * parseFloat(temp[1]);
		
		if(consumedValue>parseFloat(avlQty))
		{
			
			alert("Consumed Quantity must be less than Avl Quantity");
			document.forms[0].strConsumedQty.value = "";
			document.forms[0].strConsumedQtyUnitId.selectedIndex = 0;	
		
		}
		checkUnitQty();
	}
	

}

function checkUnitQty() {

	
		var unit = document.forms[0].strConsumedQtyUnitId;
		var qty = document.forms[0].strConsumedQty;

		
			var qtyDcml = qty.value;

			var unitVl = unit.value.split('^');

			if (qtyDcml.indexOf('.') > -1 && unitVl[0] != '0') {

				alert("Qty must be an Integer ");
				qty.value = '';
				unit.selectedIndex = 0;				
				return false;
			}

}

function resetScreen(){
	
		
       			 document.getElementById('strBatchDivId').style.display = "none";
			     document.getElementById('strBatchItemDivId').style.display = "none";
			     document.getElementById("qualityDivId").style.display = "none";
			     document.getElementById("qualityDetailsDivId").style.display = "none";
	
	}




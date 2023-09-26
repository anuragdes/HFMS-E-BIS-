package accounts.paybill.reports.delegates;

import hisglobal.backutil.CommonDataBaseManip;
import hisglobal.backutil.HelperMethods;
import hisglobal.backutil.HisCombo;
import hisglobal.backutil.ServiceLocator;
import hisglobal.backutil.exception.CreateException;
import hisglobal.backutil.exception.DataNotFoundException;
import hisglobal.backutil.exception.DuplicateRecordFoundException;
import hisglobal.backutil.exception.GlobalException;
import hisglobal.backutil.exception.PaybillException;
import hr.pis.global.StaffClinic_Global_Data_Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import accounts.paybill.global.reports.dto.Pay_Salary_Rpt_DTO;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.pdf.PdfWriter;

public class Pay_Salary_Rpt_Delegates
{


	private final static String trnQuery = "accounts.properties.paybill.transactions.pay_resource_trn";
	/*private final static int bank_code=1;
	private final static int pan_code=4;
	private final static int gpf_code=3;*/
	
		
		public static synchronized void initializeEssentitals(HttpServletRequest req,HttpServletResponse res,Pay_Salary_Rpt_DTO dto) throws PaybillException,IllegalArgumentException
		{

			List hs_code= new ArrayList();
			if(req==null)
				throw new IllegalArgumentException("accounts.paybill.reports.delegates.Pay_Bank_Statement_Rpt_Delegates.initializeEssentitals():Invalid Arguments");
			
			try
			{	
				
				Pay_Salary_Rpt_DTO pbrDto=(Pay_Salary_Rpt_DTO)dto; 
				
				
				List keyList=new ArrayList();
				
				List keyList1=new ArrayList();
				
				keyList.add(pbrDto.getHOSPITAL_CODE());
				
				keyList1.add("1");
				
				
				String Query= HelperMethods.getQuery(2,"select.ACTPAY_SMPL_PBR_TRN.1",trnQuery);
				//String Query="select x,x from pivot where x>=2007 and x<=2013";
				 List ExtList = CommonDataBaseManip.getComboList(Query);
				 req.getSession().setAttribute("ExtList",ExtList);
				 Query = null;
				 ExtList = null;
				 
				 
				 if(dto.getStrEarHead().equalsIgnoreCase(""))
				 {
				 String EarHeadQuery= "select t.FSTR_HEAD_NAME code,t.FSTR_HEAD_NAME from ACTPAY_EARN_DEDUCT_PRINT_ORDER t where T.GNUM_ISVALID=? and T.FCH_HEAD_TYPE='E' order by code";
					//String Query="select x,x from pivot where x>=2007 and x<=2013";
					 ExtList = CommonDataBaseManip.getComboListWithoutSeletValue(EarHeadQuery,keyList1);
					 req.getSession().setAttribute("EarHeadList",ExtList);
					 EarHeadQuery = null;
					 ExtList = null;
				 }
					 if(dto.getStrDedHead().equalsIgnoreCase(""))
					 {
					 
					 String DedHeadQuery= "select t.FSTR_HEAD_NAME code,t.FSTR_HEAD_NAME from ACTPAY_EARN_DEDUCT_PRINT_ORDER t where T.GNUM_ISVALID=? and T.FCH_HEAD_TYPE='D' order by code";
						//String Query="select x,x from pivot where x>=2007 and x<=2013";
						 ExtList = CommonDataBaseManip.getComboListWithoutSeletValue(DedHeadQuery,keyList1);
						 req.getSession().setAttribute("DedHeadList",ExtList);
						 DedHeadQuery = null;
						 ExtList = null;
					 }
				 
				 
				 String intCategory="";
					intCategory=dto.getIntCategory();
					List catList=new ArrayList();
					List catSecondList=new ArrayList();
					List subCatList=new ArrayList();
					List subCat=new ArrayList();
					String catFirstMenyQuery="";
					String catSecondMenuQuery="";
					System.out.println("intCategory"+intCategory);
			System.out.println("salCatfirstMenu"+dto.getSalCatsecondMenuData());
				 	if(!dto.getIntCategory().equalsIgnoreCase("-1"))
				 	{
				 			System.out.println("inside 1111");
				 			if(dto.getSalCatsecondMenuData().equalsIgnoreCase(""))
				 				{	
				 					System.out.println("inside 2222");
				 					catFirstMenyQuery="select distinct a.NUM_SAL_CAT_ID,A.STR_CATEGORY_NAME from actpay_sal_cat_mst a where  A.GNUM_ISVALID=1  and a.GNUM_HOSPITAL_CODE= ?  order by A.STR_CATEGORY_NAME ";
				 					catList=CommonDataBaseManip.getComboListWithoutSeletValue(catFirstMenyQuery,keyList);
						 
				 					req.getSession().setAttribute("catFirstMenuList",catList);
				 					System.out.println("catList"+catList);
				 					//catFirstMenyQuery = null;
				 					//catList.clear();
				 					req.getSession().setAttribute("catSecondMenuList",catSecondList);
				 				}
				 			else
				 				{
				 					//String catQuery="select distinct T.NUM_SAL_CAT_ID,(select A.STR_CATEGORY_NAME from actpay_sal_cat_mst a where A.NUM_SAL_CAT_ID=T.NUM_SAL_CAT_ID and A.GNUM_ISVALID=1 and A.GNUM_HOSPITAL_CODE=T.GNUM_HOSPITAL_CODE ) as strSalCatName from ACTPAY_USERROLE_PBR t where T.GNUM_HOSPITAL_CODE= "+dto.getHOSPITAL_CODE()+" and T.GNUM_ISVALID=1 order by strSalCatName ";
				 					catFirstMenyQuery="select distinct a.NUM_SAL_CAT_ID,A.STR_CATEGORY_NAME from actpay_sal_cat_mst a where  A.GNUM_ISVALID=1 and a.NUM_SAL_CAT_ID not in("+dto.getSalCatsecondMenuData()+" ) and a.GNUM_HOSPITAL_CODE= ?  order by A.STR_CATEGORY_NAME ";
				 					catSecondMenuQuery="select distinct a.NUM_SAL_CAT_ID,A.STR_CATEGORY_NAME from actpay_sal_cat_mst a where  A.GNUM_ISVALID=1 and a.NUM_SAL_CAT_ID  in("+dto.getSalCatsecondMenuData()+" ) and a.GNUM_HOSPITAL_CODE= ?   ";
				 					catList=CommonDataBaseManip.getComboListWithoutSeletValue(catFirstMenyQuery,keyList);
				 					req.getSession().setAttribute("catFirstMenuList",catList);
				 					// catFirstMenyQuery = null;
				 					//catList.clear();
				 					catSecondList=CommonDataBaseManip.getComboListWithoutSeletValue(catSecondMenuQuery,keyList);
				 					req.getSession().setAttribute("catSecondMenuList",catSecondList);
				 					//catList.clear();
				 				}
				 			System.out.println("category queryy... "+catFirstMenyQuery);
				 			System.out.println("catSecondMenuQuery queryy... "+catSecondMenuQuery);
				 			System.out.println("cateory name is well done"+intCategory);
				 	}
				 	else
				 	{
				 			dto.setSalCatfirstMenu("");
				 			dto.setSalCatsecondMenu("");
				 			req.getSession().setAttribute("catFirstMenuList",catList);	
				 			req.getSession().setAttribute("catSecondMenuList",catList);	
				 	}
					System.out.println("salary category"+intCategory);
					System.out.println("salary sub category"+dto.getIntSubCategory());
					
					
					if(!dto.getIntSubCategory().equalsIgnoreCase("-1") && (!dto.getIntCategory().equalsIgnoreCase("-1")))
					{
					
						//String subCatQuery="SELECT c.num_sal_sub_cat_id, b.str_sub_category_name FROM actpay_sal_cat_mst a, actpay_sal_sub_cat_mst b, actpay_userrole_pbr c WHERE a.num_sal_cat_id = b.str_category_code AND a.num_sal_cat_id = c.num_sal_cat_id  AND b.num_sal_sub_cat_id = c.num_sal_sub_cat_id AND a.gnum_hospital_code = b.gnum_hospital_code  AND b.gnum_hospital_code = c.gnum_hospital_code and A.GNUM_ISVALID=1 and B.GNUM_ISVALID=1 and C.GNUM_ISVALID=1 and A.NUM_SAL_CAT_ID="+dto.getIntCategory()+" AND c.gnum_hospital_code ="+ dto.getHOSPITAL_CODE()+"   order by b.str_sub_category_name";
						String subCatQuery="SELECT b.num_sal_sub_cat_id, b.str_sub_category_name FROM actpay_sal_cat_mst a, actpay_sal_sub_cat_mst b WHERE a.num_sal_cat_id = b.str_category_code    AND a.gnum_hospital_code = b.gnum_hospital_code   and A.GNUM_ISVALID=1 and B.GNUM_ISVALID=1  and A.NUM_SAL_CAT_ID in ("+dto.getSalCatsecondMenuData()+" ) AND a.gnum_hospital_code =?   order by b.str_sub_category_name";
						System.out.println("sub category queryy... "+subCatQuery);
						
						//subCatList=CommonDataBaseManip.getComboList(subCatQuery);
						subCatList=CommonDataBaseManip.getComboListWithoutSeletValue(subCatQuery,keyList);
						req.getSession().setAttribute("salaryGenerationSubCategoryList",subCatList);			
						//subCatQuery = null;
						//subCatList.clear();
					}
					else
					{
						if(!dto.getIntCategory().equalsIgnoreCase("-1"))
							{
								//subCatList=CommonDataBaseManip.getComboList();
						
								subCat.add(new HisCombo("-1","Select Value"));
								subCat.add(new HisCombo("1","Salary Sub-Category"));
								//req.getSession().setAttribute("salaryGenSubCategory",subCat);	
								req.getSession().setAttribute("salaryGenerationSubCategoryList",subCatList);
								//subCatQuery = null;
								subCatList.clear();
							}
						else
							{
								subCat.add(new HisCombo("-1","Select Value"));
								//req.getSession().setAttribute("salaryGenSubCategory",subCat);	
								req.getSession().setAttribute("salaryGenerationSubCategoryList",subCatList);
								//subCatQuery = null;
								subCatList.clear();
					 			
							}
					}
		}
			catch(PaybillException e)
			{
				throw new PaybillException("accounts.paybill.reports.delegates.Pay_Bank_Statement_Rpt_Delegates.initializeEssentitals():"+e.getMessage());
				
			}
			catch(Exception e)
			{
				throw new PaybillException("accounts.paybill.reports.delegates.Pay_Bank_Statement_Rpt_Delegates.initializeEssentitals():"+e.getMessage());
				
			}
		}
		
		
		private ArrayList EmpList = null;

		public ArrayList GetEmpList(String pbrdt,String subCategory,String h_code) {

			try {
				
				String empStatus=StaffClinic_Global_Data_Config.PIST_SER_TER_MST_IN_SER;
				String empStatus1=StaffClinic_Global_Data_Config.PIST_SER_TER_MST_IN_SER_PRO;
				String empStatus2=StaffClinic_Global_Data_Config.PIST_SER_TER_MST_IN_SER_PRO_CL;
				EmpList = new ArrayList();
				ArrayList dataList = new ArrayList();
				dataList.add(pbrdt);
				dataList.add(pbrdt);
				//dataList.add(category);
				dataList.add(subCategory);
				dataList.add(h_code);
				dataList.add(pbrdt);
				dataList.add(pbrdt);
				//dataList.add(category);
				dataList.add(subCategory);
				dataList.add(h_code);
				dataList.add(pbrdt);
				
				//dataList.add(category);
				dataList.add(subCategory);
				dataList.add(h_code);
				
				System.out.println("pbr date  "+pbrdt);
				
				System.out.println("sub category  "+subCategory);
				
				// String getEmpList_Query="select t.str_employee_number from
				// ACTPAY_PROF_DTL t where t.str_estb_attri_code=? and
				// t.str_service_grp_code=? and t.num_nat_job_id=? and
				// t.str_class_attri_code=? and t.gnum_sl_no=(select
				// max(a.gnum_sl_no) from ACTPAY_PROF_DTL a where
				// a.str_employee_number=t.str_employee_number) and
				// t.gnum_hospital_code=? and (t.str_stop_salary !='S' or
				// t.str_stop_salary is null) ";
				/*String getEmpList_Query = "SELECT DISTINCT (a.str_employee_number) FROM actpay_prof_dtl a,pist_designation_mst gd, pist_emp_personnel_dtl p "
						+ "WHERE a.dt_date_of_join <= LAST_DAY (TO_DATE (?, 'dd/mm/yyyy')) "
						+ "AND (a.str_final_status = '"+empStatus+"' or a.str_final_status = '"+empStatus1+"' or a.str_final_status = '"+empStatus2+"' or a.str_final_status = '-1')  AND NVL (a.str_stop_salary, ' ') != 'S' "
						+ "AND a.str_employee_number=p.str_emp_no AND p.gnum_isvalid = 1 AND a.gnum_sl_no = "
						+ "(SELECT MAX (gnum_sl_no) FROM actpay_prof_dtl	WHERE str_employee_number = a.str_employee_number "
						+ "AND gnum_isvalid = 1 AND dt_basic_eff_date <=LAST_DAY (TO_DATE (?, 'dd/mm/yyyy'))) "
						+ "AND a.gnum_isvalid = 1 AND gd.num_desig_id=a.num_desig_id  AND gd.gnum_isvalid =1 "
						+ " and A.NUM_SAL_CAT_ID=? AND A.NUM_SAL_SUB_CAT_ID=? AND A.GNUM_HOSPITAL_CODE=? "
						+ "UNION "
						+ "SELECT DISTINCT (a.str_employee_number) FROM actpay_prof_dtl a , "
						+ "pist_designation_mst gd ,  pist_emp_personnel_dtl p "
						+ "WHERE a.str_employee_number=p.str_emp_no "
						+ "AND p.gnum_isvalid=1 and p.str_validate='Y' and a.dt_date_of_join <=LAST_DAY (TO_DATE (?, 'dd/mm/yyyy')) "
						+ "AND (a.str_final_status = '"+empStatus+"' or a.str_final_status = '"+empStatus1+"' or a.str_final_status = '"+empStatus2+"' or a.str_final_status = '-1') "
						+ "AND NVL (a.str_stop_salary, ' ') != 'S' AND a.str_employee_number=p.str_emp_no "
						+ "AND p.gnum_isvalid = 1 and p.str_validate='Y' AND a.gnum_sl_no =(SELECT MAX (gnum_sl_no) "
						+ "FROM actpay_prof_dtl WHERE str_employee_number = a.str_employee_number "
						+ "AND gnum_isvalid = 1 AND a.dt_basic_eff_date <=LAST_DAY (TO_DATE (?, 'dd/mm/yyyy'))) "
						+ "AND a.gnum_isvalid = 1 AND a.num_desig_id = gd.num_desig_id "
						+ "AND gd.gnum_isvalid =1 and A.NUM_SAL_CAT_ID=? AND A.NUM_SAL_SUB_CAT_ID=? AND A.GNUM_HOSPITAL_CODE=?"
						+ "UNION "
						+ "SELECT DISTINCT (a.str_employee_number) FROM actpay_prof_dtl a , "
						+ "pist_designation_mst gd ,  pist_emp_personnel_dtl p "
						+ "WHERE a.str_employee_number=p.str_emp_no "
						+ "AND p.gnum_isvalid=1 and p.str_validate='Y' and a.dt_date_of_join <=LAST_DAY (TO_DATE (?, 'dd/mm/yyyy')) "
						+ "AND (a.str_final_status = '"+empStatus+"' or a.str_final_status = '"+empStatus1+"' or a.str_final_status = '"+empStatus2+"' or a.str_final_status = '-1') "
						+ "AND NVL (a.str_stop_salary, ' ') != 'S' AND a.str_employee_number=p.str_emp_no "
						+ "AND p.gnum_isvalid = 1 and p.str_validate='Y' AND a.gnum_sl_no =(SELECT MAX (gnum_sl_no) "
						+ "FROM actpay_prof_dtl WHERE str_employee_number = a.str_employee_number "
						+ "AND gnum_isvalid = 1 ) "
						+ "AND a.gnum_isvalid = 1 AND a.num_desig_id = gd.num_desig_id "
						+ "AND gd.gnum_isvalid =1 and A.NUM_SAL_CAT_ID=? AND A.NUM_SAL_SUB_CAT_ID=? AND A.GNUM_HOSPITAL_CODE=?";
				*/
				String getEmpList_Query = "SELECT DISTINCT (a.str_employee_number) FROM actpay_prof_dtl a,pist_designation_mst gd, pist_emp_personnel_dtl p "
					+ "WHERE a.dt_date_of_join <= LAST_DAY (TO_DATE (?, 'dd/mm/yyyy')) "
					+ "AND (a.str_final_status = '"+empStatus+"' or a.str_final_status = '"+empStatus1+"' or a.str_final_status = '"+empStatus2+"' or a.str_final_status = '-1')  AND NVL (a.str_stop_salary, ' ') != 'S' "
					+ "AND a.str_employee_number=p.str_emp_no AND p.gnum_isvalid = 1 AND a.gnum_sl_no = "
					+ "(SELECT MAX (gnum_sl_no) FROM actpay_prof_dtl	WHERE str_employee_number = a.str_employee_number "
					+ "AND gnum_isvalid = 1 AND dt_basic_eff_date <=LAST_DAY (TO_DATE (?, 'dd/mm/yyyy'))) "
					+ "AND a.gnum_isvalid = 1 AND gd.num_desig_id=a.num_desig_id  AND gd.gnum_isvalid =1 "
					+ " and a.NUM_SAL_SUB_CAT_ID=?  AND A.GNUM_HOSPITAL_CODE=? "
					+ "UNION "
					+ "SELECT DISTINCT (a.str_employee_number) FROM actpay_prof_dtl a , "
					+ "pist_designation_mst gd ,  pist_emp_personnel_dtl p "
					+ "WHERE a.str_employee_number=p.str_emp_no "
					+ "AND p.gnum_isvalid=1 and p.str_validate='Y' and a.dt_date_of_join <=LAST_DAY (TO_DATE (?, 'dd/mm/yyyy')) "
					+ "AND (a.str_final_status = '"+empStatus+"' or a.str_final_status = '"+empStatus1+"' or a.str_final_status = '"+empStatus2+"' or a.str_final_status = '-1') "
					+ "AND NVL (a.str_stop_salary, ' ') != 'S' AND a.str_employee_number=p.str_emp_no "
					+ "AND p.gnum_isvalid = 1 and p.str_validate='Y' AND a.gnum_sl_no =(SELECT MAX (gnum_sl_no) "
					+ "FROM actpay_prof_dtl WHERE str_employee_number = a.str_employee_number "
					+ "AND gnum_isvalid = 1 AND a.dt_basic_eff_date <=LAST_DAY (TO_DATE (?, 'dd/mm/yyyy'))) "
					+ "AND a.gnum_isvalid = 1 AND a.num_desig_id = gd.num_desig_id "
					+ "AND gd.gnum_isvalid =1 and a.NUM_SAL_SUB_CAT_ID=? AND A.GNUM_HOSPITAL_CODE=?"
					+ "UNION "
					+ "SELECT DISTINCT (a.str_employee_number) FROM actpay_prof_dtl a , "
					+ "pist_designation_mst gd ,  pist_emp_personnel_dtl p "
					+ "WHERE a.str_employee_number=p.str_emp_no "
					+ "AND p.gnum_isvalid=1 and p.str_validate='Y' and a.dt_date_of_join <=LAST_DAY (TO_DATE (?, 'dd/mm/yyyy')) "
					+ "AND (a.str_final_status = '"+empStatus+"' or a.str_final_status = '"+empStatus1+"' or a.str_final_status = '"+empStatus2+"' or a.str_final_status = '-1') "
					+ "AND NVL (a.str_stop_salary, ' ') != 'S' AND a.str_employee_number=p.str_emp_no "
					+ "AND p.gnum_isvalid = 1 and p.str_validate='Y' AND a.gnum_sl_no =(SELECT MAX (gnum_sl_no) "
					+ "FROM actpay_prof_dtl WHERE str_employee_number = a.str_employee_number "
					+ "AND gnum_isvalid = 1 ) "
					+ "AND a.gnum_isvalid = 1 AND a.num_desig_id = gd.num_desig_id "
					+ "AND gd.gnum_isvalid =1 and a.NUM_SAL_SUB_CAT_ID=? AND A.GNUM_HOSPITAL_CODE=?";
			
				
				
				System.out.println("employee quesy is===="+getEmpList_Query);

				EmpList = (ArrayList) CommonDataBaseManip.getDetail(getEmpList_Query, dataList);
				System.out.println("Size of emp"+EmpList.size());
				dataList.clear();
			}
			catch (Exception e) 
			{
				System.out.println("Exception e" + e);
			}

			return EmpList;
		}

		
		
		public  static synchronized boolean getReport(HttpServletRequest request, HttpServletResponse response,Pay_Salary_Rpt_DTO dto) throws PaybillException,DuplicateRecordFoundException,CreateException,IllegalArgumentException
		{
			boolean result=false;
		   if(dto==null)
				throw new IllegalArgumentException("accounts.paybill.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():Invalid Arguments");
			
			
				List AL_List=new ArrayList();
				List salSubCatList = new ArrayList();	
				List earnHeadList = new ArrayList();	
				List dedHeadList = new ArrayList();	
				//List earnTotalAmountList = new ArrayList();	
				//List dedTotalAmountList = new ArrayList();	
				List Data_List=new ArrayList();
				List employee_number = new ArrayList();
				List employee_name = new ArrayList();
				List dept_name = new ArrayList();
				List attendance = new ArrayList();
				List work_days = new ArrayList();
				List designation = new ArrayList();
				List payAttribute= new ArrayList();
				String str_employee_no="";
				String str_employee_name="";
				String str_employee_desg="";
				String str_employee_dept="";
				String str_employee_oldno="";
				String str_employee_panno="";
				String str_employee_bankno="";
				String month=dto.getStrMonth();
				String year=dto.getStrYear();
				
				
				double [ ] earnTotalAmount = new double[4];// maximum 4 is allowed
				double [ ] dedTotalAmount = new double[4];// maximum 4 is allowed
				double [ ] grandTotalAmount = new double[8];//4+4
				String salaryGenerationCategory=dto.getIntCategory();
				String salaryGenerationSubCategory=dto.getIntSubCategory();
				String salCategory1="";
				String salSubCategory1="";
				List categoryList=new ArrayList();
				java.util.List hos_name  = new java.util.ArrayList();
			    java.util.List hos_code  = new java.util.ArrayList();
			    NumberFormat formatter = new DecimalFormat("#0.00");
				
				
				int no_records=0;
				int total_record=0;
				String date="20/"+month+"/"+year;
				String report_date = "";
				String salary_month = "";
				
				String hospitalQuery ="SELECT T.GSTR_HOSPITAL_NAME,T.GSTR_HOSPITAL_ADD1||', '||T.GSTR_HOSPITAL_ADD2||', '||T.GSTR_CITY ,' Ph.No. '||T.GSTR_PHONE FROM GBLT_HOSPITAL_MST T WHERE T.GNUM_ISVALID=1 AND T.GNUM_HOSPITAL_CODE=? ";
				hos_code.add(dto.getHOSPITAL_CODE());
			     try {
					hos_name=CommonDataBaseManip.getDetail(hospitalQuery,hos_code);
				} catch (GlobalException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DataNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			     String header=hos_name.get(0).toString();
			     String Hospital_Add=hos_name.get(1).toString();
				
				
				
				String  dateQuery="select to_char(sysdate,'dd fmMonth yyyy '), "+
				 "to_char(to_date('"+date+"','dd/mm/yyyy'),'fmMonth yyyy') from dual";
			try
			{

				AL_List = CommonDataBaseManip.getDetail(dateQuery);
				report_date =(String) AL_List.get(0);
				salary_month=(String) AL_List.get(1);

			}
			catch(Exception e)
			{System.out.println("error in date query===="+e);}

			AL_List.clear();
			
			System.out.println("earnMenu"+dto.getEarnHeadData());
			
			if(!(dto.getStrEarHead().equalsIgnoreCase("")))
			{
				
				String keyEarnList = dto.getEarnHeadData();
				
				System.out.println(" "+keyEarnList);
				StringTokenizer idEarnTokens= new StringTokenizer(keyEarnList,",");
				
					
				while(idEarnTokens.hasMoreElements())
				{	
					earnHeadList.add(idEarnTokens.nextToken());
				}
			}
			
        System.out.println("dedMenu"+dto.getDedHeadData());
			
			if(!(dto.getStrDedHead().equalsIgnoreCase("")))
			{
				
				String keyDedList = dto.getDedHeadData();
				
				System.out.println(" "+keyDedList);
				StringTokenizer idDedTokens= new StringTokenizer(keyDedList,",");
				
					
				while(idDedTokens.hasMoreElements())
				{	
					dedHeadList.add(idDedTokens.nextToken());
				}
			}
				
				System.out.println("salary sub category second menu"+dto.getSalSubCatsecondMenuData());
				
				
				
				List alList=new ArrayList();
				String keyList = dto.getSalSubCatsecondMenuData();
				
				System.out.println(" "+keyList);
				StringTokenizer idTokens= new StringTokenizer(keyList,",");
				
						
				while(idTokens.hasMoreElements())
				{	
					salSubCatList.add(idTokens.nextToken());
				}
				
				
			
			try
			{
				
				 int font_size=7;
				 
				 ServiceLocator  test=new ServiceLocator();
		            String path=ServiceLocator.GLOBAL_PAYBILL_IMAGE_PATH;
		            //String path=request.getContextPath()+"/hisgolbal/images/pgi-logo.gif";
		            System.out.println("path is :"+path);
		            
		            //System.out.println("path is Through Service Locator:"+request.getContextPath());
		            
		           
		            Image pgilogo=Image.getInstance(path);
		    	    
	      		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      		//Document document=new Document(new Rectangle(16*72,20*72),12,12,12,45);
	      		Document document=new Document(PageSize.A4);
	      			      			      	
	      		//PdfWriter writer = PdfWriter.getInstance(document, baos);
	      		if(dto.getStrReportFormat().equalsIgnoreCase("PDF"))
	      		{
	      			PdfWriter writer = PdfWriter.getInstance(document, baos);
	      		}
      		else
	      		{
	      			HtmlWriter writer1 = HtmlWriter.getInstance(document, baos);
	      		}
	      		
	      		document.addAuthor("C-DAC");
	    		document.addSubject("salary report");
	    		 HeaderFooter footer = new HeaderFooter(
	                        new Phrase("Page No:  ",FontFactory.getFont("Arial", font_size,Font.BOLD)), true);
	            footer.setBorder(Rectangle.NO_BORDER);
	            footer.setAlignment(Element.ALIGN_CENTER);
	            document.setFooter(footer);
	        	document.open();
		   
	        	
	        	int cols=7+earnHeadList.size()+dedHeadList.size();;
	        	int subcols=2;
	    		 
	        	double pageTotal = 0.0; // to sum and store all earning heads of one page
	        	double grandTotal = 0.0;
	     		double totalAmount=0.0;
	     		String AmountWords="";
	     		
	       
	     		 
	     		 Table pbrTab = new Table(cols);// for name,dept,total etc
	     		 pbrTab.setAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.setPadding(0);
	     		 pbrTab.setSpacing(1);
	     		 pbrTab.setBorder(0);
	     		 pbrTab.setWidth(100);
	     		 pbrTab.setCellsFitPage(true);	 
	     		
	     		
	     		 
	     		/*Font fontH1 = new Font(Font.TIMES_ROMAN,24,Font.BOLD);
	     		 Font fontH2 = new Font(Font.TIMES_ROMAN,22,Font.BOLD);
	     		 Font fontH3 = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
	     		// Font fontB = new Font(Font.TIMES_ROMAN,16,Font.NORMAL);
	     		Font fontBH = new Font(Font.TIMES_ROMAN,20,Font.NORMAL);
	     		Font fontBBH = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
	     		 Font fontBB = new Font(Font.TIMES_ROMAN,14,Font.BOLD);	*/
	     		 
	     		 
	     		Font fontH1 = new Font(Font.TIMES_ROMAN,12,Font.BOLD);
	     		 Font fontH2 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		 Font fontH3 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		
	     		Font fontBH = new Font(Font.TIMES_ROMAN,10,Font.NORMAL);
	     		Font fontBBH = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		 Font fontBB = new Font(Font.TIMES_ROMAN,8,Font.BOLD);
	     		 
	     		 
	     		 
	     		 Cell pcell;
	     		 
	     		pcell = new Cell("");
	    		 //pcell.add(new Phrase(reportHeader+"\n"+salary_month,fontH2));
	    		 pcell.add(pgilogo);
	    		 pcell.setColspan(2);
	    		 pcell.setBorder(0);
	    		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    		 pbrTab.addCell(pcell);
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase(header+"\n"+Hospital_Add+"\n",fontH1));
	     		pcell.add(new Phrase("Salary Report\nMonth: "+salary_month,fontH2));
	     		 pcell.setColspan(cols-4);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	    		 pcell.add(new Phrase("\n\n\n"+report_date,fontBB));
	    		 pcell.setColspan(2);
	    		 pcell.setBorder(0);
	    		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    		 pbrTab.addCell(pcell);
	    		 
	    		 
	     		 
	     		 /*pcell = new Cell("");
	     		 pcell.add(new Phrase(report_date,fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);*/
	     		 
	     		/*pcell = new Cell("");
	    		 pcell.add(new Phrase("",fontH2));
	    		// pcell.add(pgilogo);
	    		 pcell.setColspan(4);
	    		 pcell.setBorder(0);
	    		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Salary Report\nMonth: "+salary_month,fontH2));
	     		 pcell.setColspan(cols-8);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		// pcell.add(new Phrase(reportHeader+"\n"+salary_month,fontH2));
	     		// pcell.add(pgilogo);
	     		 pcell.setColspan(4);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 */
	     		 
	    		 pcell = new Cell("");
	     		 pcell.add(new Phrase("\n",fontBB));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.TOP);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     ///// table header starts now	
	    		 
	    		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Sl No",fontH3));
	     		 pcell.setColspan(1);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	    		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Salary Sub Category",fontH3));
	     		 pcell.setColspan(2);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("Total Employee",fontH3));
	     		 pcell.setColspan(2);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Total Amount(Rs)",fontH3));
	     		 pcell.setColspan(2);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		 
	     		 
	     		 for( int i=0;i<earnHeadList.size();i++)
	     		 {
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase(earnHeadList.get(i).toString(),fontH3));
	     		 pcell.setColspan(1);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 }
	     		for( int i=0;i<dedHeadList.size();i++)
	     		 {
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase(dedHeadList.get(i).toString(),fontH3));
	     		 pcell.setColspan(1);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 }
	     		
	     		/*pcell = new Cell("");
	     		 pcell.add(new Phrase("",fontH3));
	     		 pcell.setColspan(2);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);*/
	     		 
	     		pcell = new Cell("\n");
	     		 pcell.add(new Phrase("",fontBB));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.BOTTOM);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		 pbrTab.endHeaders();
	     		 
	     		 int row=0;
	     		 int slno=0;
	     		 /////////////
	     		 
	     		if(salSubCatList.size()>0)
				{
	     			for(int i=0;i<salSubCatList.size();i++)
	     			{
					System.out.println("salSubCatList[i]"+salSubCatList.get(i));
	     			
					totalAmount=0;
					
					for(int k=0;k<earnHeadList.size();k++)
						earnTotalAmount[k]=0;
					
					for(int k=0;k<dedHeadList.size();k++)
						dedTotalAmount[k]=0;
				
				List alList1=new ArrayList();
				///query for getting salary Sub category====
				
				String SubcatQuery="select initcap(lower(c.STR_SUB_CATEGORY_NAME)) from ACTPAY_SAL_SUB_CAT_MST c where  c. NUM_SAL_SUB_CAT_ID='"+salSubCatList.get(i)+"' and c.GNUM_ISVALID=1";
				try
				{
					alList1=CommonDataBaseManip.getDetail(SubcatQuery);
				}
				catch (Exception e) 
				{
					System.out.println("error in query");
				}
				
				if(alList1.size() !=0)
				{
					salSubCategory1=alList1.get(0).toString();
				}
				else
				{
					salSubCategory1="";
				}
				
				
				
				// commented on 12 Sep12 as taking emp from get emmlist from GetEmpList function

				/*String empQuery1="select empc.str_employee_number,empp.str_first_name||' '||empp.str_middle_name||' '||empp.str_last_name,"
							+"  P.STR_DESIG_ST_NAME,dp.gstr_dept_name,empc.str_pay_attri_code "
							+"  from actpay_prof_dtl empc,pist_emp_personnel_dtl empp,PIST_DESIGNATION_MST p,"
							+"  GBLT_DEPARTMENT_MST dp "
							+"   where p.num_desig_id=empc.num_desig_id and dp.gnum_dept_code=empc.num_dept_id "
							+"	 and empc.str_employee_number=empp.str_emp_no and empp.str_validate= 'Y' and   " 
						    +"    dp.gnum_hospital_code='"+dto.getHOSPITAL_CODE()+"' and empc.gnum_sl_no=(select max(gnum_sl_no) from actpay_prof_dtl"
						    +"   where str_employee_number=empc.str_employee_number) AND (   (    TO_DATE (SYSDATE) >= dp.gdt_effective_frm AND TO_DATE (SYSDATE) <= dp.gdt_effective_to ) OR (    dp.gdt_effective_to IS NULL AND NOT (dp.gdt_effective_frm > TO_DATE (SYSDATE))) ) "
						    +"    and empc.NUM_SAL_CAT_ID='"+salaryGenerationCategory+"'"
						     +"   and empc.NUM_SAL_SUB_CAT_ID='"+salaryGenerationSubCategory+"'AND empc.gnum_isvalid = 1 AND empp.gnum_isvalid = 1  AND p.gnum_isvalid = 1 AND dp.gnum_isvalid = 1"					     
						     +" and empc.gnum_hospital_code="+dto.getHOSPITAL_CODE()+" and empp.gnum_hospital_code="+dto.getHOSPITAL_CODE()+" and p.gnum_hospital_code="+dto.getHOSPITAL_CODE()+" order by empc.str_employee_number";
				
				String empQuery="SELECT   empc.str_employee_number  FROM actpay_prof_dtl empc WHERE empc.gnum_sl_no = (SELECT MAX (t.gnum_sl_no) FROM actpay_prof_dtl t WHERE t.str_employee_number = empc.str_employee_number  AND t.gnum_isvalid = 1) " +
						"   AND empc.num_sal_sub_cat_id = '"+salSubCatList.get(i)+"'  AND empc.gnum_isvalid = 1  AND empc.gnum_hospital_code = "+dto.getHOSPITAL_CODE()+" ORDER BY empc.str_employee_number";
				System.out.println("employee query is==="+empQuery);
				System.out.println("employee query is==="+empQuery1);
				
				try
				{
					AL_List=CommonDataBaseManip.getDetail(empQuery);
				}
				catch (Exception e) 
				{
					System.out.println("problem in employee query"+empQuery);
				}
				*/
				Pay_Salary_Rpt_Delegates obj = new Pay_Salary_Rpt_Delegates();
				
				AL_List = obj.GetEmpList(date,salSubCatList.get(i).toString(),dto.getHOSPITAL_CODE());
				
				
				
				no_records= AL_List.size(); // variable a is now number of emp selected. will be used for iteration
				total_record+=no_records;
				employee_number.clear();
				 if(no_records!=0)
					  for(int j=0;j<AL_List.size();j=j+1)
					  {
						  employee_number.add((String)AL_List.get(j));
						 
				      }
				else
				{
				System.out.println("No Employee Found");
				}
				AL_List.clear();
				
				System.out.println("Employee size: "+employee_number.size());
				
				++row;
     			++slno;
     			pcell = new Cell("");
     			 pcell.add(new Phrase(""+slno,fontH3));
     			 pcell.setColspan(1);
     			 pcell.setBorder(0);
     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
     			 pbrTab.addCell(pcell);
     			 
     			 pcell = new Cell("");
     			 pcell.add(new Phrase(""+salSubCategory1,fontH3));
     			 pcell.setColspan(2);
     			 pcell.setBorder(0);
     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
     			 pbrTab.addCell(pcell);
     			 
     			pcell = new Cell("");
    			 pcell.add(new Phrase(""+no_records,fontH3));
    			 pcell.setColspan(2);
    			 pcell.setBorder(0);
    			 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    			 pbrTab.addCell(pcell);
				
				
				
	     		 
	     			////////// 
	     		 for(int cnt=0;cnt<employee_number.size();cnt++)
	     		 {
	     			 
	     				
	     			 
	     			 String netPayQuery="select (SELECT    nvl(sum ( round(p.num_parameter_amount )),0)  FROM actpay_pay_bill_register_dtl p   WHERE p.str_employee_number = '"+employee_number.get(cnt)+"'  AND LAST_DAY (p.dt_pbr_generation_date) =  LAST_DAY (TO_DATE ('"+date+"', 'dd/mm/yyyy')) " +
	     			 		" AND p.str_parameter_type = 'E'  AND p.gnum_isvalid = 1) -  (SELECT    nvl(sum (round( p.num_parameter_amount )),0)  FROM actpay_pay_bill_register_dtl p  WHERE p.str_employee_number = '"+employee_number.get(cnt)+"' " +
	     			 		"AND LAST_DAY (p.dt_pbr_generation_date) = LAST_DAY (TO_DATE ('"+date+"', 'dd/mm/yyyy'))  AND p.str_parameter_type = 'D' AND p.gnum_isvalid = 1) as amt from dual ";
	     			System.out.println("netPayQuery========: "+netPayQuery);						
	     								
	     			 java.util.List earnList = new java.util.ArrayList();
	     			 //int total_earning=0;
	     			 try
	     			 {
	     				 earnList = CommonDataBaseManip.getDetail(netPayQuery);
	     			 }
	     			 catch(Exception e)
	     			 {System.out.println("Error in getting earninigs: "+e);}
	     			 
	     			if(earnList.size()>0)
	     			 {
	     			 totalAmount=totalAmount+Double.parseDouble(earnList.get(0).toString());
	     			 }
	     			 else
	     				{
	     				totalAmount+=0;
	     				}
	     			earnList.clear();
	     			
	     			for(int earn=0;earn<earnHeadList.size();earn++)
	     			{
	     				String earnPayQuery="SELECT    nvl(sum ( round(p.num_parameter_amount )),0)  FROM actpay_pay_bill_register_dtl p   WHERE p.str_employee_number = '"+employee_number.get(cnt)+"'  AND LAST_DAY (p.dt_pbr_generation_date) =  LAST_DAY (TO_DATE ('"+date+"', 'dd/mm/yyyy')) " +
     			 		" AND p.str_parameter_type = 'E'  and lower(p.STR_PARAMETER_CODE)=lower('"+earnHeadList.get(earn).toString()+"') AND p.gnum_isvalid = 1";
	     				System.out.println("earnPayQuery========: "+earnPayQuery);					
     								
     			
     			 //int total_earning=0;
     			 try
     			 {
     				earnList = CommonDataBaseManip.getDetail(earnPayQuery);
     			 }
     			 catch(Exception e)
     			 {System.out.println("Error in getting earninigs: "+e);}
     			 
     			if(earnList.size()>0)
     			 {
     				
     				earnTotalAmount[earn]+=Double.parseDouble(earnList.get(0).toString());
     				grandTotalAmount[earn]+=Double.parseDouble(earnList.get(0).toString());
     			 
     			 }
     			 else
     				{
     				earnTotalAmount[earn]+=0;
     				grandTotalAmount[earn]+=0;
     				}
     			earnList.clear();
	     				
	     			}
	     			
	     			for(int ded=0;ded<dedHeadList.size();ded++)
	     			{
	     				String dedPayQuery="SELECT    nvl(sum ( round(p.num_parameter_amount )),0)  FROM actpay_pay_bill_register_dtl p   WHERE p.str_employee_number = '"+employee_number.get(cnt)+"'  AND LAST_DAY (p.dt_pbr_generation_date) =  LAST_DAY (TO_DATE ('"+date+"', 'dd/mm/yyyy')) " +
     			 		" AND p.str_parameter_type = 'D'  and lower(p.STR_PARAMETER_CODE)=lower('"+dedHeadList.get(ded).toString()+"') AND p.gnum_isvalid = 1";
	     				System.out.println("dedPayQuery========: "+dedPayQuery);						
     								
     			 
     			 try
     			 {
     				 earnList = CommonDataBaseManip.getDetail(dedPayQuery);
     			 }
     			 catch(Exception e)
     			 {System.out.println("Error in getting earninigs: "+e);}
     			 
     			if(earnList.size()>0)
     			 {
     				dedTotalAmount[ded]+=Double.parseDouble(earnList.get(0).toString());
     				grandTotalAmount[earnHeadList.size()+ded]+=Double.parseDouble(earnList.get(0).toString());
     				
     			 }
     			 else
     				{
     				dedTotalAmount[ded]+=0;
     				grandTotalAmount[earnHeadList.size()+ded]+=0;
     				}
     			   earnList.clear();
	     			}
	     			
	     			
	     			earnList.clear();	
	     			 
	     		 }
	     			
	     			 
	     				 pcell = new Cell("");
	     				 pcell.add(new Phrase(formatter.format(totalAmount),fontH3));
	     				
	     				 grandTotal += totalAmount;
	     				 pageTotal += totalAmount;
	     				 pcell.setColspan(2);
	     				 pcell.setBorder(0);
	     				 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     				 pbrTab.addCell(pcell);
	     			 
	     			 
	     			 
	     				 for( int ii=0;ii<earnHeadList.size();ii++)
	    	     		 {
	    	     		 pcell = new Cell("");
	    	     		 pcell.add(new Phrase(""+earnTotalAmount[ii],fontH3));
	    	     		 pcell.setColspan(1);
	    	     		 pcell.setBorder(0);
	    	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    	     		 pbrTab.addCell(pcell);
	    	     		 }
	    	     		for( int ii=0;ii<dedHeadList.size();ii++)
	    	     		 {
	    	     		 pcell = new Cell("");
	    	     		 pcell.add(new Phrase(""+dedTotalAmount[ii],fontH3));
	    	     		 pcell.setColspan(1);
	    	     		 pcell.setBorder(0);
	    	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    	     		 pbrTab.addCell(pcell);
	    	     		 }
	     					 
	     			
	     			 
	     			 /*pcell = new Cell("");
	     			 pcell.add(new Phrase("",fontH3));
	     			 pcell.setColspan(2);
	     			 pcell.setBorder(0);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     			 pbrTab.addCell(pcell);*/
	     			 
	     		
	     			 
	     			 
	     			 
	     		  
	     		 
	     	   }
	     			
	     			 pcell = new Cell("");
		     		 pcell.add(new Phrase("\n",fontBH));
		     		 pcell.setBorder(0);
		     		 pcell.setColspan(cols);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     		 pbrTab.addCell(pcell);
		     		 	
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Grand Total",fontH1));
	     		 pcell.setBorder(Rectangle.TOP);
	     		 pcell.setColspan(3);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		pcell = new Cell("");
    			 pcell.add(new Phrase(""+total_record,fontBBH));
    			pcell.setColspan(2);
    			 pcell.setBorder(Rectangle.TOP);
    			 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    			 pbrTab.addCell(pcell);
	     		 
	     		 
	     			 pcell = new Cell("");
	     			 pcell.add(new Phrase(formatter.format(grandTotal),fontH1));
	     			pcell.setColspan(2);
	     			 pcell.setBorder(Rectangle.TOP);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     			 pbrTab.addCell(pcell);
	     			 
	     			for( int g=0;g<earnHeadList.size();g++)
   	     		 {
   	     		 pcell = new Cell("");
   	     		 pcell.add(new Phrase(formatter.format(grandTotalAmount[g]),fontH3));
   	     		 pcell.setColspan(1);
   	     		 pcell.setBorder(Rectangle.TOP);
   	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
   	     		 pbrTab.addCell(pcell);
   	     		 }
   	     		for( int g=0;g<dedHeadList.size();g++)
   	     		 {
   	     		 pcell = new Cell("");
   	     		 pcell.add(new Phrase(formatter.format(grandTotalAmount[earnHeadList.size()+g]),fontH3));
   	     		 pcell.setColspan(1);
   	     		 pcell.setBorder(Rectangle.TOP);
   	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
   	     		 pbrTab.addCell(pcell);
   	     		 }
	     			 
	     			/*pcell = new Cell("");
	    			 pcell.add(new Phrase("",fontBBH));
	    			pcell.setColspan(2);
	    			 pcell.setBorder(Rectangle.TOP);
	    			 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    			 pbrTab.addCell(pcell);*/
	     			 
	     			 pcell = new Cell("");
		     		 pcell.add(new Phrase("\n",fontBH));
		     		 pcell.setBorder(Rectangle.BOTTOM);
		     		 pcell.setColspan(cols);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     		 pbrTab.addCell(pcell);
		     		 
		     		 System.out.println("earn:"+dto.getStrEarHead());
		     		 
		     		 
		     		
	     			
			}
	     	 else
	     		{
	     			pcell = new Cell("No Records Found");
	     			 pcell.add(new Phrase("",fontBBH));
	     			pcell.setColspan(cols);
	     			 pcell.setBorder(Rectangle.BOTTOM);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     			 pbrTab.addCell(pcell);
	     		}
	     		
	     		String wordQuery="select DIGITWORD("+grandTotal+")||' ONLY' from dual";
	     		System.out.println("netPayQuery========: "+wordQuery);						
					
    			 java.util.List wordList = new java.util.ArrayList();
    			 //int total_earning=0;
    			 try
    			 {
    				 wordList = CommonDataBaseManip.getDetail(wordQuery);
    			 }
    			 catch(Exception e)
    			 {System.out.println("Error in getting wordQuery: "+e);}
    			 
    			 System.out.println("words"+wordList.get(0).toString());
    			 
    			if(wordList.size()>0)
    			 {
    				if(wordList.get(0).toString().equalsIgnoreCase("ONLY"))
    					AmountWords="( RUPEES ZERO ONLY )";
    				else
    			 AmountWords="( RUPEES "+(wordList.get(0).toString()) +")";
    			 }
    			 else
    				{
    				 AmountWords="( RUPEES ZERO ONLY )";
    				}
    			wordList.clear();
    			
    			
    			pcell = new Cell("");
	     		 pcell.add(new Phrase(AmountWords,fontBBH));
	     		 pcell.setBorder(0);
	     		 pcell.setColspan(cols);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("\n",fontBBH));
	     		 pcell.setBorder(0);
	     		 pcell.setColspan(cols);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("\n",fontBBH));
	     		 pcell.setBorder(Rectangle.TOP);
	     		 pcell.setColspan(cols);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase(StringUtils.rightPad("-Sd/-",34," ")+"\nDRAWING & DISBURSING OFFICER \n PGIMER,CHANDIGARH      ",fontBBH));
	     		 pcell.setBorder(0);
	     		 pcell.setColspan(cols);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);
	
	     		 document.add(pbrTab);
	     		 document.close();	
	     		 response.setHeader("Expires", "0");
				 response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				 response.setHeader("Pragma", "public");
				 if(dto.getStrReportFormat().equalsIgnoreCase("PDF"))
					{
						response.setContentType("application/pdf");
					}
					else if(dto.getStrReportFormat().equalsIgnoreCase("HTML"))
					{
						response.setContentType("text/html");
							
						response.setHeader("Content-Disposition", "inline; filename=\"BankStatementReport.text\"");
						
					}
					else
					{
						response.setContentType("application/xls");
						response.setHeader("Content-Disposition", "inline; filename=\"BankStatementReport.xls\"");
						
					}
		  		 //response.setContentType("application/pdf");
		  		 response.setContentLength(baos.size());
		  		 ServletOutputStream out = response.getOutputStream();
		  		 baos.writeTo(out);
		  		 out.flush();
				
		  		System.out.println("salary sub category second menu"+dto.getSalSubCatsecondMenuData());
			
			}
		
		
		
		
		catch(PaybillException e)
		{
			throw new PaybillException("accounts.paybill.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
			
		}
		catch(Exception e)
		{
			throw new PaybillException("accounts.gpf.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
			
		}
		
		return result;
	}
		
		public  static synchronized boolean getGrossSalaryReportText(HttpServletRequest request, HttpServletResponse response,Pay_Salary_Rpt_DTO dto) throws PaybillException,DuplicateRecordFoundException,CreateException,IllegalArgumentException
		{
			boolean result=false;
			   if(dto==null)
					throw new IllegalArgumentException("accounts.paybill.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():Invalid Arguments");
				
				
					List AL_List=new ArrayList();
					
					List Data_List=new ArrayList();
					List employee_number = new ArrayList();
					List employee_name = new ArrayList();
					List dept_name = new ArrayList();
					List attendance = new ArrayList();
					List work_days = new ArrayList();
					List designation = new ArrayList();
					List payAttribute= new ArrayList();
					String str_employee_no="";
					String str_employee_name="";
					String str_employee_desg="";
					String str_employee_dept="";
					String str_employee_oldno="";
					String str_employee_panno="";
					String str_employee_bankno="";
					String month=dto.getStrMonth();
					String year=dto.getStrYear();
					
					String salaryGenerationCategory=dto.getIntCategory();
					String salaryGenerationSubCategory=dto.getIntSubCategory();
					String salCategory1="";
					String salSubCategory1="";
					List categoryList=new ArrayList();
					java.util.List hos_name  = new java.util.ArrayList();
				    java.util.List hos_code  = new java.util.ArrayList();
				    NumberFormat formatter = new DecimalFormat("#0.00");
					
					
					int no_records=0;
					int total_record=0;
					String date="20/"+month+"/"+year;
					String report_date = "";
					String salary_month = "";
					
					String hospitalQuery ="SELECT T.GSTR_HOSPITAL_NAME,T.GSTR_HOSPITAL_ADD1||', '||T.GSTR_HOSPITAL_ADD2||', '||T.GSTR_CITY ,' Ph.No. '||T.GSTR_PHONE FROM GBLT_HOSPITAL_MST T WHERE T.GNUM_ISVALID=1 AND T.GNUM_HOSPITAL_CODE=? ";
					hos_code.add(dto.getHOSPITAL_CODE());
				     try {
						hos_name=CommonDataBaseManip.getDetail(hospitalQuery,hos_code);
					} catch (GlobalException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (DataNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				     String header=hos_name.get(0).toString();
				     String Hospital_Add=hos_name.get(1).toString();
					
					
					
					String  dateQuery="select to_char(sysdate,'dd fmMonth yyyy '), "+
					 "to_char(to_date('"+date+"','dd/mm/yyyy'),'fmMonth yyyy') from dual";
				try
				{

					AL_List = CommonDataBaseManip.getDetail(dateQuery);
					report_date =(String) AL_List.get(0);
					salary_month=(String) AL_List.get(1);

				}
				catch(Exception e)
				{System.out.println("error in date query===="+e);}

				AL_List.clear();
					
					System.out.println("salary sub category second menu"+dto.getSalSubCatsecondMenuData());
					
					/*List alList=new ArrayList();
					String keyList = dto.getSalSubCatsecondMenuData();
					
					System.out.println(" "+keyList);
					StringTokenizer idTokens= new StringTokenizer(keyList,",");
					
					List salSubCatList = new ArrayList();			
					while(idTokens.hasMoreElements())
					{	
						salSubCatList.add(idTokens.nextToken());
					}*/
					
					
				
				try
				{
					
					 int font_size=7;
					 
					 ServiceLocator  test=new ServiceLocator();
			            String path=ServiceLocator.GLOBAL_PAYBILL_IMAGE_PATH;
			            //String path=request.getContextPath()+"/hisgolbal/images/pgi-logo.gif";
			            System.out.println("path is :"+path);
			            
			            //System.out.println("path is Through Service Locator:"+request.getContextPath());
			            
			           
			            Image pgilogo=Image.getInstance(path);
			    	    
		      		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		      		//Document document=new Document(new Rectangle(16*72,20*72),12,12,12,45);
		      		Document document=new Document(PageSize.A4);
		      			      			      	
		      		//PdfWriter writer = PdfWriter.getInstance(document, baos);
		      		if(dto.getStrReportFormat().equalsIgnoreCase("PDF"))
		      		{
		      			PdfWriter writer = PdfWriter.getInstance(document, baos);
		      		}
	      		else
		      		{
		      			HtmlWriter writer1 = HtmlWriter.getInstance(document, baos);
		      		}
		      		
		      		document.addAuthor("C-DAC");
		    		document.addSubject("salary report");
		    		 HeaderFooter footer = new HeaderFooter(
		                        new Phrase("Page No:  ",FontFactory.getFont("Arial", font_size,Font.BOLD)), true);
		            footer.setBorder(Rectangle.NO_BORDER);
		            footer.setAlignment(Element.ALIGN_CENTER);
		            document.setFooter(footer);
		        	document.open();
			   
		        	
		        	int cols=14;
		        	int subcols=2;
		    		 
		        	double pageTotal = 0.0; // to sum and store all earning heads of one page
		        	double grandTotal = 0.0;
		     		double totalAmount=0.0;
		     		String AmountWords="";
		     		
		       
		     		 
		     		 Table pbrTab = new Table(cols);// for name,dept,total etc
		     		 pbrTab.setAlignment(Element.ALIGN_CENTER);
		     		 pbrTab.setPadding(0);
		     		 pbrTab.setSpacing(1);
		     		 pbrTab.setBorder(0);
		     		 pbrTab.setWidth(100);
		     		 pbrTab.setCellsFitPage(true);	 
		     		
		     		
		     		 
		     		/*Font fontH1 = new Font(Font.TIMES_ROMAN,24,Font.BOLD);
		     		 Font fontH2 = new Font(Font.TIMES_ROMAN,22,Font.BOLD);
		     		 Font fontH3 = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
		     		// Font fontB = new Font(Font.TIMES_ROMAN,16,Font.NORMAL);
		     		Font fontBH = new Font(Font.TIMES_ROMAN,20,Font.NORMAL);
		     		Font fontBBH = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
		     		 Font fontBB = new Font(Font.TIMES_ROMAN,14,Font.BOLD);	*/
		     		 
		     		 
		     		Font fontH1 = new Font(Font.TIMES_ROMAN,12,Font.BOLD);
		     		 Font fontH2 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
		     		 Font fontH3 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
		     		
		     		Font fontBH = new Font(Font.TIMES_ROMAN,10,Font.NORMAL);
		     		Font fontBBH = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
		     		 Font fontBB = new Font(Font.TIMES_ROMAN,8,Font.BOLD);
		     		 
		     		 
		     		 
		     		 Cell pcell;
		     		 // commented as per format
		     		 
		     		/*pcell = new Cell("");
		    		
		    		 pcell.add(pgilogo);
		    		 pcell.setColspan(2);
		    		 pcell.setBorder(0);
		    		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		    		 pbrTab.addCell(pcell);
		     		 
		     		 pcell = new Cell("");
		     		 pcell.add(new Phrase(header+"\n"+Hospital_Add+"\n",fontH1));
		     		pcell.add(new Phrase("Salary Report\nMonth: "+salary_month,fontH2));
		     		 pcell.setColspan(cols-4);
		     		 pcell.setBorder(0);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		     		 pbrTab.addCell(pcell);
		     		 
		     		pcell = new Cell("");
		    		 pcell.add(new Phrase("\n\n\n"+report_date,fontBB));
		    		 pcell.setColspan(2);
		    		 pcell.setBorder(0);
		    		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    		 pbrTab.addCell(pcell);*/
		    		 
		     		 
		     		 pcell = new Cell("");
		     		 pcell.add(new Phrase("Employee"+month+year,fontH3));
		     		 pcell.setColspan(cols);
		     		 pcell.setBorder(0);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		     		 pbrTab.addCell(pcell);
		     		 
		    		 
		    		 pbrTab.endHeaders();
		     		
		     		 
		    		 
		     		 
		     		 
		     		
			
		     		 
		     		 pcell = new Cell("");
		     		 pcell.add(new Phrase("Annexure-IV",fontH3));
		     		 pcell.setColspan(cols);
		     		 pcell.setBorder(0);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		     		 pbrTab.addCell(pcell);
		    		 
		     		 pcell = new Cell("");
		     		 pcell.add(new Phrase("Sec 4.(1).b.(x) The monthly remuneration received by each of its officers and employees, including the system of compensation provided in its regulations:",fontH3));
		     		 pcell.setColspan(cols);
		     		 pcell.setBorder(0);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     		 pbrTab.addCell(pcell);
		     		 
		     		 
		     		 pcell = new Cell("");
		     		 pcell.add(new Phrase("Gross Salary of Officers and Employees for the month of "+month+" , "+year,fontH3));
		     		 pcell.setColspan(cols);
		     		 pcell.setBorder(0);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		     		 pbrTab.addCell(pcell);
		     		 
		     		pcell = new Cell("");
		     		 pcell.add(new Phrase("",fontBB));
		     		 pcell.setColspan(cols);
		     		 pcell.setBorder(Rectangle.TOP);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     		 pbrTab.addCell(pcell);
		    		 
		    		 
		     		 pcell = new Cell("");
		     		 pcell.add(new Phrase("E.Code(Old)",fontH3));
		     		 pcell.setColspan(2);
		     		 pcell.setBorder(0);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     		 pbrTab.addCell(pcell);
		     		 
		     		pcell = new Cell("");
		     		 pcell.add(new Phrase("E.Code(New)",fontH3));
		     		 pcell.setColspan(3);
		     		 pcell.setBorder(0);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     		 pbrTab.addCell(pcell);
		     		 
		     		pcell = new Cell("");
		     		 pcell.add(new Phrase("Employee Name",fontH3));
		     		 pcell.setColspan(3);
		     		 pcell.setBorder(0);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     		 pbrTab.addCell(pcell);
		     		 
		     		
		     		 
		     		
		     		 pcell = new Cell("");
		     		 pcell.add(new Phrase("Gross Salary   ",fontH3));
		     		 pcell.setColspan(3);
		     		 pcell.setBorder(0);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		     		 pbrTab.addCell(pcell);
		     		 
		     		 pcell = new Cell("");
		     		 pcell.add(new Phrase("  Designation",fontH3));
		     		 pcell.setColspan(3);
		     		 pcell.setBorder(0);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     		 pbrTab.addCell(pcell);
		     		 
		     		pcell = new Cell("");
		     		 pcell.add(new Phrase("",fontBB));
		     		 pcell.setColspan(cols);
		     		 pcell.setBorder(Rectangle.BOTTOM);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     		 pbrTab.addCell(pcell);
		     		 
		     		
		     		 
		     		 int row=0;
		     		 int slno=0;
		     		 /////////////
		     		 
		     		
					
					String empDetails="select  pf.str_employee_number, NVL ((SELECT b.STR_OLD_EMPLOYEE_NO  FROM PIST_EMP_CUR_DETAIL_DTL b WHERE b.gnum_hospital_code = pf.gnum_hospital_code AND b.gnum_isvalid = 1 AND b.str_emp_no = pf.str_employee_number  ), '-'  ) AS oldempno ,upper(pd.str_first_name||' '||pd.str_middle_name||' '||pd.str_last_name) , " +
				 		"(select r.str_desig_name from PIST_DESIGNATION_MST r where r.gnum_isvalid=1 and r.gnum_hospital_code=pf.gnum_hospital_code and pf.num_desig_id = r.num_desig_id ) as desg,nvl ((select sum(r.NUM_PARAMETER_AMOUNT) from ACTPAY_PAY_BILL_REGISTER_DTL r where r.STR_PARAMETER_TYPE='E' and R.STR_EMPLOYEE_NUMBER=PF.STR_EMPLOYEE_NUMBER and r.DT_PBR_GENERATION_DATE=to_date('"+date+"','dd/mm/yyyy')) ,0) as Gross " +
				 				"from actpay_prof_dtl pf ,pist_emp_personnel_dtl pd where pf.str_employee_number=pd.str_emp_no  and pd.str_validate='Y'   and pf.gnum_hospital_code =pd.gnum_hospital_code  and pf.gnum_isvalid=1 and pf.gnum_hospital_code='"+dto.getHOSPITAL_CODE()+"' and pf.gnum_sl_no=(select max(t.gnum_sl_no) from actpay_prof_dtl t where t.str_employee_number=pf.str_employee_number and t.gnum_isvalid=1 and t.gnum_hospital_code=pf.gnum_hospital_code) " +
				 						"and pf.NUM_SAL_SUB_CAT_ID in ("+dto.getSalSubCatsecondMenuData()+") order by desg,oldempno ";
				System.out.println("empDetails "+empDetails);
					
				
					
					try
					{
						AL_List=CommonDataBaseManip.getDetail(empDetails);
					}
					catch (Exception e) 
					{
						System.out.println("error in query");
					}
					
					if(AL_List.size() !=0)
					{
						for(int cnt=0;cnt<AL_List.size();cnt=cnt+5)
						{
							
							
			     			 
			     			 pcell = new Cell("");
			     			 pcell.add(new Phrase(AL_List.get(cnt+1).toString(),fontBH));
			     			 pcell.setColspan(2);
			     			 pcell.setBorder(0);
			     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			     			 pbrTab.addCell(pcell);
			     			 
			     			pcell = new Cell("");
			     			 pcell.add(new Phrase(AL_List.get(cnt).toString(),fontBH));
			     			 pcell.setColspan(3);
			     			 pcell.setBorder(0);
			     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			     			 pbrTab.addCell(pcell);
			     			 
			     			pcell = new Cell("");
			    			 pcell.add(new Phrase(AL_List.get(cnt+2).toString(),fontBH));
			    			 pcell.setColspan(3);
			    			 pcell.setBorder(0);
			    			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
			    			 pbrTab.addCell(pcell);
			    			 
			    			 pcell = new Cell("");
		     				 pcell.add(new Phrase(AL_List.get(cnt+4).toString()+"  ",fontBH));
		     				
		     				 grandTotal += Double.parseDouble(AL_List.get(cnt+4).toString());
		     				 pcell.setColspan(3);
		     				 pcell.setBorder(0);
		     				 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		     				 pbrTab.addCell(pcell);
		     			 
		     			 
		     			 pcell = new Cell("");
		     			 pcell.add(new Phrase("  "+AL_List.get(cnt+3).toString(),fontBH));
		     			 pcell.setColspan(3);
		     			 pcell.setBorder(0);
		     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     			 pbrTab.addCell(pcell);
						}
					}
					else
					{
						pcell = new Cell("");
		     			 pcell.add(new Phrase("No Record Found",fontH3));
		     			 pcell.setColspan(cols);
		     			 pcell.setBorder(0);
		     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		     			 pbrTab.addCell(pcell);
					}
					
		     	
		     		
		     		 
		     		/*pcell = new Cell("");
		     		 pcell.add(new Phrase(StringUtils.rightPad("-Sd/-",34," ")+"\nDRAWING & DISBURSING OFFICER \n PGIMER,CHANDIGARH      ",fontBBH));
		     		 pcell.setBorder(0);
		     		 pcell.setColspan(cols);
		     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		     		 pbrTab.addCell(pcell);*/
		
		     		 document.add(pbrTab);
		     		 document.close();	
		     		 response.setHeader("Expires", "0");
					 response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
					 response.setHeader("Pragma", "public");
					 if(dto.getStrReportFormat().equalsIgnoreCase("PDF"))
						{
							response.setContentType("application/pdf");
						}
						else if(dto.getStrReportFormat().equalsIgnoreCase("HTML"))
						{
							response.setContentType("text/html");
								
							response.setHeader("Content-Disposition", "inline; filename=\"BankStatementReport.text\"");
							
						}
						else
						{
							response.setContentType("application/xls");
							response.setHeader("Content-Disposition", "inline; filename=\"BankStatementReport.xls\"");
							
						}
			  		 //response.setContentType("application/pdf");
			  		 response.setContentLength(baos.size());
			  		 ServletOutputStream out = response.getOutputStream();
			  		 baos.writeTo(out);
			  		 out.flush();
					
			  		System.out.println("salary sub category second menu"+dto.getSalSubCatsecondMenuData());
				
				}
			
			
			
			
			catch(PaybillException e)
			{
				throw new PaybillException("accounts.paybill.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
				
			}
			catch(Exception e)
			{
				throw new PaybillException("accounts.gpf.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
				
			}
			
			return result;
		}
		public  static synchronized boolean getGrossSalaryReportBackup(HttpServletRequest request, HttpServletResponse response,Pay_Salary_Rpt_DTO dto) throws PaybillException,DuplicateRecordFoundException,CreateException,IllegalArgumentException
		{
			boolean result=false;
		   if(dto==null)
				throw new IllegalArgumentException("accounts.paybill.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():Invalid Arguments");
			
			
				List AL_List=new ArrayList();
				
				List Data_List=new ArrayList();
				List employee_number = new ArrayList();
				List employee_name = new ArrayList();
				List dept_name = new ArrayList();
				List attendance = new ArrayList();
				List work_days = new ArrayList();
				List designation = new ArrayList();
				List payAttribute= new ArrayList();
				String str_employee_no="";
				String str_employee_name="";
				String str_employee_desg="";
				String str_employee_dept="";
				String str_employee_oldno="";
				String str_employee_panno="";
				String str_employee_bankno="";
				String month=dto.getStrMonth();
				String year=dto.getStrYear();
				
				String salaryGenerationCategory=dto.getIntCategory();
				String salaryGenerationSubCategory=dto.getIntSubCategory();
				String salCategory1="";
				String salSubCategory1="";
				List categoryList=new ArrayList();
				java.util.List hos_name  = new java.util.ArrayList();
			    java.util.List hos_code  = new java.util.ArrayList();
			    NumberFormat formatter = new DecimalFormat("#0.00");
				
				
				int no_records=0;
				int total_record=0;
				String date="20/"+month+"/"+year;
				String report_date = "";
				String salary_month = "";
				
				String hospitalQuery ="SELECT T.GSTR_HOSPITAL_NAME,T.GSTR_HOSPITAL_ADD1||', '||T.GSTR_HOSPITAL_ADD2||', '||T.GSTR_CITY ,' Ph.No. '||T.GSTR_PHONE FROM GBLT_HOSPITAL_MST T WHERE T.GNUM_ISVALID=1 AND T.GNUM_HOSPITAL_CODE=? ";
				hos_code.add(dto.getHOSPITAL_CODE());
			     try {
					hos_name=CommonDataBaseManip.getDetail(hospitalQuery,hos_code);
				} catch (GlobalException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DataNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			     String header=hos_name.get(0).toString();
			     String Hospital_Add=hos_name.get(1).toString();
				
				
				
				String  dateQuery="select to_char(sysdate,'dd fmMonth yyyy '), "+
				 "to_char(to_date('"+date+"','dd/mm/yyyy'),'fmMonth yyyy') from dual";
			try
			{

				AL_List = CommonDataBaseManip.getDetail(dateQuery);
				report_date =(String) AL_List.get(0);
				salary_month=(String) AL_List.get(1);

			}
			catch(Exception e)
			{System.out.println("error in date query===="+e);}

			AL_List.clear();
				
				System.out.println("salary sub category second menu"+dto.getSalSubCatsecondMenuData());
				
				/*List alList=new ArrayList();
				String keyList = dto.getSalSubCatsecondMenuData();
				
				System.out.println(" "+keyList);
				StringTokenizer idTokens= new StringTokenizer(keyList,",");
				
				List salSubCatList = new ArrayList();			
				while(idTokens.hasMoreElements())
				{	
					salSubCatList.add(idTokens.nextToken());
				}*/
				
				
			
			try
			{
				
				 int font_size=7;
				 
				 ServiceLocator  test=new ServiceLocator();
		            String path=ServiceLocator.GLOBAL_PAYBILL_IMAGE_PATH;
		            //String path=request.getContextPath()+"/hisgolbal/images/pgi-logo.gif";
		            System.out.println("path is :"+path);
		            
		            //System.out.println("path is Through Service Locator:"+request.getContextPath());
		            
		           
		            Image pgilogo=Image.getInstance(path);
		    	    
	      		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      		//Document document=new Document(new Rectangle(16*72,20*72),12,12,12,45);
	      		Document document=new Document(PageSize.A4);
	      			      			      	
	      		//PdfWriter writer = PdfWriter.getInstance(document, baos);
	      		if(dto.getStrReportFormat().equalsIgnoreCase("PDF"))
	      		{
	      			PdfWriter writer = PdfWriter.getInstance(document, baos);
	      		}
      		else
	      		{
	      			HtmlWriter writer1 = HtmlWriter.getInstance(document, baos);
	      		}
	      		
	      		document.addAuthor("C-DAC");
	    		document.addSubject("salary report");
	    		 HeaderFooter footer = new HeaderFooter(
	                        new Phrase("Page No:  ",FontFactory.getFont("Arial", font_size,Font.BOLD)), true);
	            footer.setBorder(Rectangle.NO_BORDER);
	            footer.setAlignment(Element.ALIGN_CENTER);
	            document.setFooter(footer);
	        	document.open();
		   
	        	
	        	int cols=14;
	        	int subcols=2;
	    		 
	        	double pageTotal = 0.0; // to sum and store all earning heads of one page
	        	double grandTotal = 0.0;
	     		double totalAmount=0.0;
	     		String AmountWords="";
	     		
	       
	     		 
	     		 Table pbrTab = new Table(cols);// for name,dept,total etc
	     		 pbrTab.setAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.setPadding(0);
	     		 pbrTab.setSpacing(1);
	     		 pbrTab.setBorder(0);
	     		 pbrTab.setWidth(100);
	     		 pbrTab.setCellsFitPage(true);	 
	     		
	     		
	     		 
	     		/*Font fontH1 = new Font(Font.TIMES_ROMAN,24,Font.BOLD);
	     		 Font fontH2 = new Font(Font.TIMES_ROMAN,22,Font.BOLD);
	     		 Font fontH3 = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
	     		// Font fontB = new Font(Font.TIMES_ROMAN,16,Font.NORMAL);
	     		Font fontBH = new Font(Font.TIMES_ROMAN,20,Font.NORMAL);
	     		Font fontBBH = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
	     		 Font fontBB = new Font(Font.TIMES_ROMAN,14,Font.BOLD);	*/
	     		 
	     		 
	     		Font fontH1 = new Font(Font.TIMES_ROMAN,12,Font.BOLD);
	     		 Font fontH2 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		 Font fontH3 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		
	     		Font fontBH = new Font(Font.TIMES_ROMAN,10,Font.NORMAL);
	     		Font fontBBH = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		 Font fontBB = new Font(Font.TIMES_ROMAN,8,Font.BOLD);
	     		 
	     		 
	     		 
	     		 Cell pcell;
	     		 // commented as per format
	     		 
	     		/*pcell = new Cell("");
	    		
	    		 pcell.add(pgilogo);
	    		 pcell.setColspan(2);
	    		 pcell.setBorder(0);
	    		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    		 pbrTab.addCell(pcell);
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase(header+"\n"+Hospital_Add+"\n",fontH1));
	     		pcell.add(new Phrase("Salary Report\nMonth: "+salary_month,fontH2));
	     		 pcell.setColspan(cols-4);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	    		 pcell.add(new Phrase("\n\n\n"+report_date,fontBB));
	    		 pcell.setColspan(2);
	    		 pcell.setBorder(0);
	    		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    		 pbrTab.addCell(pcell);*/
	    		 
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Employee"+month+year,fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	    		 
	    		 pbrTab.endHeaders();
	     		
	     		 
	    		 
	     		 
	     		 
	     		
		
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Annexure-IV",fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	    		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Sec 4.(1).b.(x) The monthly remuneration received by each of its officers and employees, including the system of compensation provided in its regulations:",fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Gross Salary of Officers and Employees for the month of "+month+" , "+year,fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("",fontBB));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.TOP);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	    		 
	    		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("E.Code(Old)",fontH3));
	     		 pcell.setColspan(2);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("E.Code(New)",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("Employee Name",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Gross Salary   ",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("  Designation",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("",fontBB));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.BOTTOM);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		 int row=0;
	     		 int slno=0;
	     		 /////////////
	     		 
	     		
				// empDetails with gross value 0 (without joining with pay bill register)
				/*String empDetails="select  pf.str_employee_number, NVL ((SELECT b.STR_OLD_EMPLOYEE_NO  FROM PIST_EMP_CUR_DETAIL_DTL b WHERE b.gnum_hospital_code = pf.gnum_hospital_code AND b.gnum_isvalid = 1 AND b.str_emp_no = pf.str_employee_number  ), '-'  ) AS oldempno ,upper(pd.str_first_name||' '||pd.str_middle_name||' '||pd.str_last_name) , " +
			 		"(select r.str_desig_name from PIST_DESIGNATION_MST r where r.gnum_isvalid=1 and r.gnum_hospital_code=pf.gnum_hospital_code and pf.num_desig_id = r.num_desig_id ) as desg,nvl ((select sum(r.NUM_PARAMETER_AMOUNT) from ACTPAY_PAY_BILL_REGISTER_DTL r where r.STR_PARAMETER_TYPE='E' and R.STR_EMPLOYEE_NUMBER=PF.STR_EMPLOYEE_NUMBER and r.DT_PBR_GENERATION_DATE=to_date('"+date+"','dd/mm/yyyy')) ,0) as Gross " +
			 				"from actpay_prof_dtl pf ,pist_emp_personnel_dtl pd where pf.str_employee_number=pd.str_emp_no  and pd.str_validate='Y'   and pf.gnum_hospital_code =pd.gnum_hospital_code  and pf.gnum_isvalid=1 and pf.gnum_hospital_code='"+dto.getHOSPITAL_CODE()+"' and pf.gnum_sl_no=(select max(t.gnum_sl_no) from actpay_prof_dtl t where t.str_employee_number=pf.str_employee_number and t.gnum_isvalid=1 and t.gnum_hospital_code=pf.gnum_hospital_code) " +
			 						"and pf.NUM_SAL_SUB_CAT_ID in ("+dto.getSalSubCatsecondMenuData()+") order by desg,oldempno ";
			*/
				String empDetails="select  pf.str_employee_number, NVL ((SELECT b.STR_OLD_EMPLOYEE_NO  FROM PIST_EMP_CUR_DETAIL_DTL b WHERE b.gnum_hospital_code = pf.gnum_hospital_code AND b.gnum_isvalid = 1 AND b.str_emp_no = pf.str_employee_number  ), '-'  ) AS oldempno ,upper(pd.str_first_name||' '||pd.str_middle_name||' '||pd.str_last_name) , " +
		 		"(select r.str_desig_name from PIST_DESIGNATION_MST r where r.gnum_isvalid=1 and r.gnum_hospital_code=pf.gnum_hospital_code and pf.num_desig_id = r.num_desig_id ) as desg,nvl ((select sum(r.NUM_PARAMETER_AMOUNT) from ACTPAY_PAY_BILL_REGISTER_DTL r where r.STR_PARAMETER_TYPE='E' and R.STR_EMPLOYEE_NUMBER=PF.STR_EMPLOYEE_NUMBER and r.DT_PBR_GENERATION_DATE=to_date('"+date+"','dd/mm/yyyy')) ,0) as Gross " +
		 				"from actpay_prof_dtl pf ,pist_emp_personnel_dtl pd,actpay_pay_bill_register_dtl pr where pf.str_employee_number=pd.str_emp_no  and PR.STR_EMPLOYEE_NUMBER=pf.str_employee_number and PR.GNUM_HOSPITAL_CODE=pf.gnum_hospital_code and PR.STR_PARAMETER_CODE='SALARYDAYS' and pr.gnum_isvalid=1  and PR.DT_PBR_GENERATION_DATE=TO_DATE ('"+date+"', 'dd/mm/yyyy') and pd.str_validate='Y'   " +
		 				"and pf.gnum_hospital_code =pd.gnum_hospital_code  and pf.gnum_isvalid=1 and pf.gnum_hospital_code='"+dto.getHOSPITAL_CODE()+"' and pf.gnum_sl_no=(select max(t.gnum_sl_no) from actpay_prof_dtl t where t.str_employee_number=pf.str_employee_number and t.gnum_isvalid=1 and t.gnum_hospital_code=pf.gnum_hospital_code) " +
		 						"and pf.NUM_SAL_SUB_CAT_ID in ("+dto.getSalSubCatsecondMenuData()+") order by desg,oldempno ";
	
				
				System.out.println("empDetails "+empDetails);
				
			
				
				try
				{
					AL_List=CommonDataBaseManip.getDetail(empDetails);
				}
				catch (Exception e) 
				{
					System.out.println("error in query");
				}
				
				if(AL_List.size() !=0)
				{
					for(int cnt=0;cnt<AL_List.size();cnt=cnt+5)
					{
						
						
		     			 
		     			 pcell = new Cell("");
		     			 pcell.add(new Phrase(AL_List.get(cnt+1).toString(),fontBH));
		     			 pcell.setColspan(2);
		     			 pcell.setBorder(0);
		     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     			 pbrTab.addCell(pcell);
		     			 
		     			pcell = new Cell("");
		     			 pcell.add(new Phrase(AL_List.get(cnt).toString(),fontBH));
		     			 pcell.setColspan(3);
		     			 pcell.setBorder(0);
		     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     			 pbrTab.addCell(pcell);
		     			 
		     			pcell = new Cell("");
		    			 pcell.add(new Phrase(AL_List.get(cnt+2).toString(),fontBH));
		    			 pcell.setColspan(3);
		    			 pcell.setBorder(0);
		    			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		    			 pbrTab.addCell(pcell);
		    			 
		    			 pcell = new Cell("");
	     				 pcell.add(new Phrase(AL_List.get(cnt+4).toString()+"  ",fontBH));
	     				
	     				 grandTotal += Double.parseDouble(AL_List.get(cnt+4).toString());
	     				 pcell.setColspan(3);
	     				 pcell.setBorder(0);
	     				 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     				 pbrTab.addCell(pcell);
	     			 
	     			 
	     			 pcell = new Cell("");
	     			 pcell.add(new Phrase("  "+AL_List.get(cnt+3).toString(),fontBH));
	     			 pcell.setColspan(3);
	     			 pcell.setBorder(0);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     			 pbrTab.addCell(pcell);
					}
				}
				else
				{
					pcell = new Cell("");
	     			 pcell.add(new Phrase("No Record Found",fontH3));
	     			 pcell.setColspan(cols);
	     			 pcell.setBorder(0);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     			 pbrTab.addCell(pcell);
				}
				
	     	
	     		
	     		/*String wordQuery="select DIGITWORD("+grandTotal+")||' ONLY' from dual";
	     		System.out.println("netPayQuery========: "+wordQuery);						
					
    			 java.util.List wordList = new java.util.ArrayList();
    			 //int total_earning=0;
    			 try
    			 {
    				 wordList = CommonDataBaseManip.getDetail(wordQuery);
    			 }
    			 catch(Exception e)
    			 {System.out.println("Error in getting wordQuery: "+e);}
    			 
    			if(wordList.size()>0)
    			 {
    			 AmountWords="( RUPEES "+(wordList.get(0).toString()) +")";
    			 }
    			 else
    				{
    				 AmountWords="( RUPEES ZERO ONLY )";
    				}
    			wordList.clear();*/
    			
    			
    			
	     		 
	     		 
	     		/*pcell = new Cell("");
	     		 pcell.add(new Phrase(StringUtils.rightPad("-Sd/-",34," ")+"\nDRAWING & DISBURSING OFFICER \n PGIMER,CHANDIGARH      ",fontBBH));
	     		 pcell.setBorder(0);
	     		 pcell.setColspan(cols);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);*/
	
	     		 document.add(pbrTab);
	     		 document.close();	
	     		 response.setHeader("Expires", "0");
				 response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				 response.setHeader("Pragma", "public");
				 if(dto.getStrReportFormat().equalsIgnoreCase("PDF"))
					{
						response.setContentType("application/pdf");
					}
					else if(dto.getStrReportFormat().equalsIgnoreCase("HTML"))
					{
						response.setContentType("text/html");
							
						response.setHeader("Content-Disposition", "inline; filename=\"BankStatementReport.text\"");
						
					}
					else
					{
						response.setContentType("application/xls");
						response.setHeader("Content-Disposition", "inline; filename=\"BankStatementReport.xls\"");
						
					}
		  		 //response.setContentType("application/pdf");
		  		 response.setContentLength(baos.size());
		  		 ServletOutputStream out = response.getOutputStream();
		  		 baos.writeTo(out);
		  		 out.flush();
				
		  		System.out.println("salary sub category second menu"+dto.getSalSubCatsecondMenuData());
			
			}
		
		
		
		
		catch(PaybillException e)
		{
			throw new PaybillException("accounts.paybill.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
			
		}
		catch(Exception e)
		{
			throw new PaybillException("accounts.gpf.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
			
		}
		
		return result;
	}			
		public  static synchronized boolean getGrossSalarySaveReport(HttpServletRequest request, HttpServletResponse response,Pay_Salary_Rpt_DTO dto) throws PaybillException,DuplicateRecordFoundException,CreateException,IllegalArgumentException
		{
			boolean result=false;
		   if(dto==null)
				throw new IllegalArgumentException("accounts.paybill.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():Invalid Arguments");
			
			
				List AL_List=new ArrayList();
				
				List Data_List=new ArrayList();
				List employee_number = new ArrayList();
				List employee_name = new ArrayList();
				List dept_name = new ArrayList();
				List attendance = new ArrayList();
				List work_days = new ArrayList();
				List designation = new ArrayList();
				List payAttribute= new ArrayList();
				String str_employee_no="";
				String str_employee_name="";
				String str_employee_desg="";
				String str_employee_dept="";
				String str_employee_oldno="";
				String str_employee_panno="";
				String str_employee_bankno="";
				String month=dto.getStrMonth();
				String year=dto.getStrYear();
				
				String salaryGenerationCategory=dto.getIntCategory();
				String salaryGenerationSubCategory=dto.getIntSubCategory();
				String salCategory1="";
				String salSubCategory1="";
				List categoryList=new ArrayList();
				java.util.List hos_name  = new java.util.ArrayList();
			    java.util.List hos_code  = new java.util.ArrayList();
			    NumberFormat formatter = new DecimalFormat("#0.00");
				
				
				int no_records=0;
				int total_record=0;
				String date="20/"+month+"/"+year;
				String report_date = "";
				String salary_month = "";
				
				String hospitalQuery ="SELECT T.GSTR_HOSPITAL_NAME,T.GSTR_HOSPITAL_ADD1||', '||T.GSTR_HOSPITAL_ADD2||', '||T.GSTR_CITY ,' Ph.No. '||T.GSTR_PHONE FROM GBLT_HOSPITAL_MST T WHERE T.GNUM_ISVALID=1 AND T.GNUM_HOSPITAL_CODE=? ";
				hos_code.add(dto.getHOSPITAL_CODE());
			     try {
					hos_name=CommonDataBaseManip.getDetail(hospitalQuery,hos_code);
				} catch (GlobalException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DataNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			     String header=hos_name.get(0).toString();
			     String Hospital_Add=hos_name.get(1).toString();
				
				
				
				String  dateQuery="select to_char(sysdate,'dd fmMonth yyyy '), "+
				 "to_char(to_date('"+date+"','dd/mm/yyyy'),'fmMonth yyyy') from dual";
			try
			{

				AL_List = CommonDataBaseManip.getDetail(dateQuery);
				report_date =(String) AL_List.get(0);
				salary_month=(String) AL_List.get(1);

			}
			catch(Exception e)
			{System.out.println("error in date query===="+e);}

			AL_List.clear();
				
				System.out.println("salary sub category second menu"+dto.getSalSubCatsecondMenuData());
				
				/*List alList=new ArrayList();
				String keyList = dto.getSalSubCatsecondMenuData();
				
				System.out.println(" "+keyList);
				StringTokenizer idTokens= new StringTokenizer(keyList,",");
				
				List salSubCatList = new ArrayList();			
				while(idTokens.hasMoreElements())
				{	
					salSubCatList.add(idTokens.nextToken());
				}*/
				
				
			
			try
			{
				
				 int font_size=7;
				 
				 ServiceLocator  test=new ServiceLocator();
		            String path=ServiceLocator.GLOBAL_PAYBILL_IMAGE_PATH;
		            String saveFilePath=ServiceLocator.GLOBAL_PAYBILL_TEXTFILE_PATH;
		            String strFilename="GrossSalaryAnexureiv"+month+year;
		            //String path=request.getContextPath()+"/hisgolbal/images/pgi-logo.gif";
		            System.out.println("path is :"+path);
		            
		            //System.out.println("path is Through Service Locator:"+request.getContextPath());
		            
		           
		            Image pgilogo=Image.getInstance(path);
		            String fileName=saveFilePath+strFilename+".pdf";
	      		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      		FileOutputStream baos = new FileOutputStream(fileName);
	      		//Document document=new Document(new Rectangle(16*72,20*72),12,12,12,45);
	      		Document document=new Document(PageSize.A4);
	      			      			      	
	      		//PdfWriter writer = PdfWriter.getInstance(document, baos);
	      		if(dto.getStrReportFormat().equalsIgnoreCase("PDF"))
	      		{
	      			PdfWriter writer = PdfWriter.getInstance(document, baos);
	      		}
      		else
	      		{
	      			HtmlWriter writer1 = HtmlWriter.getInstance(document, baos);
	      		}
	      		
	      		document.addAuthor("C-DAC");
	    		document.addSubject("salary report");
	    		 HeaderFooter footer = new HeaderFooter(
	                        new Phrase("Page No:  ",FontFactory.getFont("Arial", font_size,Font.BOLD)), true);
	            footer.setBorder(Rectangle.NO_BORDER);
	            footer.setAlignment(Element.ALIGN_CENTER);
	            document.setFooter(footer);
	        	document.open();
		   
	        	
	        	int cols=14;
	        	int subcols=2;
	    		 
	        	double pageTotal = 0.0; // to sum and store all earning heads of one page
	        	double grandTotal = 0.0;
	     		double totalAmount=0.0;
	     		String AmountWords="";
	     		
	       
	     		 
	     		 Table pbrTab = new Table(cols);// for name,dept,total etc
	     		 pbrTab.setAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.setPadding(0);
	     		 pbrTab.setSpacing(0);
	     		
	     		 pbrTab.setBorder(0);
	     		 pbrTab.setWidth(100);
	     		 pbrTab.setCellsFitPage(true);	 
	     		
	     		
	     		 
	     		/*Font fontH1 = new Font(Font.TIMES_ROMAN,24,Font.BOLD);
	     		 Font fontH2 = new Font(Font.TIMES_ROMAN,22,Font.BOLD);
	     		 Font fontH3 = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
	     		// Font fontB = new Font(Font.TIMES_ROMAN,16,Font.NORMAL);
	     		Font fontBH = new Font(Font.TIMES_ROMAN,20,Font.NORMAL);
	     		Font fontBBH = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
	     		 Font fontBB = new Font(Font.TIMES_ROMAN,14,Font.BOLD);	*/
	     		 
	     		 
	     		Font fontH1 = new Font(Font.TIMES_ROMAN,12,Font.BOLD);
	     		 Font fontH2 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		 Font fontH3 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		
	     		Font fontBH = new Font(Font.TIMES_ROMAN,8,Font.NORMAL);
	     		Font fontBBH = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		 Font fontBB = new Font(Font.TIMES_ROMAN,8,Font.BOLD);
	     		Font fontB = new Font(Font.TIMES_ROMAN,1,Font.NORMAL);
	     		 
	     		 
	     		 
	     		 Cell pcell;
	     		 // commented as per format
	     		 
	     		/*pcell = new Cell("");
	    		
	    		 pcell.add(pgilogo);
	    		 pcell.setColspan(2);
	    		 pcell.setBorder(0);
	    		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    		 pbrTab.addCell(pcell);
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase(header+"\n"+Hospital_Add+"\n",fontH1));
	     		pcell.add(new Phrase("Salary Report\nMonth: "+salary_month,fontH2));
	     		 pcell.setColspan(cols-4);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	    		 pcell.add(new Phrase("\n\n\n"+report_date,fontBB));
	    		 pcell.setColspan(2);
	    		 pcell.setBorder(0);
	    		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    		 pbrTab.addCell(pcell);*/
	    		 
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Employee"+month+year,fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(0);
	     		
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	    		 
	    		 pbrTab.endHeaders();
	     		
	     		 
	    		 
	     		 
	     		 
	     		
		
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Annexure-IV",fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.TOP|Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	    		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Sec 4.(1).b.(x) The monthly remuneration received by each of its officers and employees, including the system of compensation provided in its regulations:",fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Gross Salary of Officers and Employees for the month of "+month+" , "+year+" \n \n",fontH3));
	     		 pcell.setColspan(cols);
	     		pcell.setBorder(Rectangle.LEFT|Rectangle.RIGHT|Rectangle.BOTTOM);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		/*pcell = new Cell("");
	     		 pcell.add(new Phrase("\n",fontB));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.BOTTOM|Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);*/
	    		 
	    		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("E.Code(Old)",fontH3));
	     		 pcell.setColspan(2);
	     		 pcell.setBorder(Rectangle.LEFT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("E.Code(New)",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("Employee Name",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Gross Salary   ",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("  Designation",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("\n",fontB));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.BOTTOM|Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		 int row=0;
	     		 int slno=0;
	     		 /////////////
	     		 
	     		
				// empDetails with gross value 0 (without joining with pay bill register)
				/*String empDetails="select  pf.str_employee_number, NVL ((SELECT b.STR_OLD_EMPLOYEE_NO  FROM PIST_EMP_CUR_DETAIL_DTL b WHERE b.gnum_hospital_code = pf.gnum_hospital_code AND b.gnum_isvalid = 1 AND b.str_emp_no = pf.str_employee_number  ), '-'  ) AS oldempno ,upper(pd.str_first_name||' '||pd.str_middle_name||' '||pd.str_last_name) , " +
			 		"(select r.str_desig_name from PIST_DESIGNATION_MST r where r.gnum_isvalid=1 and r.gnum_hospital_code=pf.gnum_hospital_code and pf.num_desig_id = r.num_desig_id ) as desg,nvl ((select sum(r.NUM_PARAMETER_AMOUNT) from ACTPAY_PAY_BILL_REGISTER_DTL r where r.STR_PARAMETER_TYPE='E' and R.STR_EMPLOYEE_NUMBER=PF.STR_EMPLOYEE_NUMBER and r.DT_PBR_GENERATION_DATE=to_date('"+date+"','dd/mm/yyyy')) ,0) as Gross " +
			 				"from actpay_prof_dtl pf ,pist_emp_personnel_dtl pd where pf.str_employee_number=pd.str_emp_no  and pd.str_validate='Y'   and pf.gnum_hospital_code =pd.gnum_hospital_code  and pf.gnum_isvalid=1 and pf.gnum_hospital_code='"+dto.getHOSPITAL_CODE()+"' and pf.gnum_sl_no=(select max(t.gnum_sl_no) from actpay_prof_dtl t where t.str_employee_number=pf.str_employee_number and t.gnum_isvalid=1 and t.gnum_hospital_code=pf.gnum_hospital_code) " +
			 						"and pf.NUM_SAL_SUB_CAT_ID in ("+dto.getSalSubCatsecondMenuData()+") order by desg,oldempno ";
			*/
				String empDetails="select  pf.str_employee_number, NVL ((SELECT b.STR_OLD_EMPLOYEE_NO  FROM PIST_EMP_CUR_DETAIL_DTL b WHERE b.gnum_hospital_code = pf.gnum_hospital_code AND b.gnum_isvalid = 1 AND b.str_emp_no = pf.str_employee_number  ), '-'  ) AS oldempno ,upper(pd.str_first_name||' '||pd.str_middle_name||' '||pd.str_last_name) , " +
		 		"(select r.str_desig_name from PIST_DESIGNATION_MST r where r.gnum_isvalid=1 and r.gnum_hospital_code=pf.gnum_hospital_code and pf.num_desig_id = r.num_desig_id ) as desg,nvl ((select sum(r.NUM_PARAMETER_AMOUNT) from ACTPAY_PAY_BILL_REGISTER_DTL r where r.STR_PARAMETER_TYPE='E' and R.STR_EMPLOYEE_NUMBER=PF.STR_EMPLOYEE_NUMBER and r.DT_PBR_GENERATION_DATE=to_date('"+date+"','dd/mm/yyyy')) ,0) as Gross " +
		 				"from actpay_prof_dtl pf ,pist_emp_personnel_dtl pd,actpay_pay_bill_register_dtl pr where pf.str_employee_number=pd.str_emp_no  and PR.STR_EMPLOYEE_NUMBER=pf.str_employee_number and PR.GNUM_HOSPITAL_CODE=pf.gnum_hospital_code and PR.STR_PARAMETER_CODE='SALARYDAYS' and pr.gnum_isvalid=1  and PR.DT_PBR_GENERATION_DATE=TO_DATE ('"+date+"', 'dd/mm/yyyy') and pd.str_validate='Y'   " +
		 				"and pf.gnum_hospital_code =pd.gnum_hospital_code  and pf.gnum_isvalid=1 and pf.gnum_hospital_code='"+dto.getHOSPITAL_CODE()+"' and pf.gnum_sl_no=(select max(t.gnum_sl_no) from actpay_prof_dtl t where t.str_employee_number=pf.str_employee_number and t.gnum_isvalid=1 and t.gnum_hospital_code=pf.gnum_hospital_code) " +
		 						"and pf.NUM_SAL_SUB_CAT_ID in ("+dto.getSalSubCatsecondMenuData()+") order by desg,oldempno ";
	
				
				System.out.println("empDetails "+empDetails);
				
			
				
				try
				{
					AL_List=CommonDataBaseManip.getDetail(empDetails);
				}
				catch (Exception e) 
				{
					System.out.println("error in query");
				}
				
				if(AL_List.size() !=0)
				{
					for(int cnt=0;cnt<AL_List.size();cnt=cnt+5)
					{
						
						
		     			 
		     			 pcell = new Cell("");
		     			 pcell.add(new Phrase(AL_List.get(cnt+1).toString(),fontBH));
		     			 pcell.setColspan(2);
		     			 pcell.setBorder(Rectangle.LEFT);
		     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		     			 pbrTab.addCell(pcell);
		     			 
		     			pcell = new Cell("");
		     			 pcell.add(new Phrase(AL_List.get(cnt).toString(),fontBH));
		     			 pcell.setColspan(3);
		     			 pcell.setBorder(0);
		     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     			 pbrTab.addCell(pcell);
		     			 
		     			pcell = new Cell("");
		    			 pcell.add(new Phrase(AL_List.get(cnt+2).toString(),fontBH));
		    			 pcell.setColspan(3);
		    			 pcell.setBorder(0);
		    			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		    			 pbrTab.addCell(pcell);
		    			 
		    			 pcell = new Cell("");
	     				 pcell.add(new Phrase(AL_List.get(cnt+4).toString()+"  ",fontBH));
	     				
	     				 grandTotal += Double.parseDouble(AL_List.get(cnt+4).toString());
	     				 pcell.setColspan(3);
	     				 pcell.setBorder(0);
	     				 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     				 pbrTab.addCell(pcell);
	     			 
	     			 
	     			 pcell = new Cell("");
	     			 pcell.add(new Phrase("  "+AL_List.get(cnt+3).toString(),fontBH));
	     			 pcell.setColspan(3);
	     			 pcell.setBorder(Rectangle.RIGHT);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     			 pbrTab.addCell(pcell);
					}
					pcell = new Cell("");
	     			 pcell.add(new Phrase("\n",fontB));
	     			 pcell.setColspan(cols);
	     			 pcell.setBorder(Rectangle.BOTTOM|Rectangle.RIGHT|Rectangle.LEFT);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     			 pbrTab.addCell(pcell);
				}
				else
				{
					pcell = new Cell("");
	     			 pcell.add(new Phrase("No Record Found",fontH3));
	     			 pcell.setColspan(cols);
	     			 pcell.setBorder(1);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     			 pbrTab.addCell(pcell);
				}
				
	     	
	     		
	     		/*String wordQuery="select DIGITWORD("+grandTotal+")||' ONLY' from dual";
	     		System.out.println("netPayQuery========: "+wordQuery);						
					
    			 java.util.List wordList = new java.util.ArrayList();
    			 //int total_earning=0;
    			 try
    			 {
    				 wordList = CommonDataBaseManip.getDetail(wordQuery);
    			 }
    			 catch(Exception e)
    			 {System.out.println("Error in getting wordQuery: "+e);}
    			 
    			if(wordList.size()>0)
    			 {
    			 AmountWords="( RUPEES "+(wordList.get(0).toString()) +")";
    			 }
    			 else
    				{
    				 AmountWords="( RUPEES ZERO ONLY )";
    				}
    			wordList.clear();*/
    			
    			
    			
	     		 
	     		 
	     		/*pcell = new Cell("");
	     		 pcell.add(new Phrase(StringUtils.rightPad("-Sd/-",34," ")+"\nDRAWING & DISBURSING OFFICER \n PGIMER,CHANDIGARH      ",fontBBH));
	     		 pcell.setBorder(0);
	     		 pcell.setColspan(cols);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);*/
	
	     		 document.add(pbrTab);
	     		 document.close();	
	     		 response.setHeader("Expires", "0");
				 response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				 response.setHeader("Pragma", "public");
				 if(dto.getStrReportFormat().equalsIgnoreCase("PDF"))
					{
						response.setContentType("application/pdf");
					}
					else if(dto.getStrReportFormat().equalsIgnoreCase("HTML"))
					{
						response.setContentType("text/html");
							
						response.setHeader("Content-Disposition", "inline; filename=\"BankStatementReport.text\"");
						
					}
					else
					{
						response.setContentType("application/xls");
						response.setHeader("Content-Disposition", "inline; filename=\"BankStatementReport.xls\"");
						
					}
		  		 response.setContentType("application/pdf");
		  		 //response.setContentLength(baos.size());
		  		 ServletOutputStream out = response.getOutputStream();
		  		 
		  		// baos.writeTo(out);
		  		 out.flush();
				
		  		System.out.println("salary sub category second menu"+dto.getSalSubCatsecondMenuData());
			
			}
		
		
		
		
		catch(PaybillException e)
		{
			throw new PaybillException("accounts.paybill.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
			
		}
		catch(Exception e)
		{
			throw new PaybillException("accounts.gpf.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
			
		}
		
		return result;
	}	
		public  static synchronized boolean getGrossSalarySchedullerSaveReport() throws PaybillException,DuplicateRecordFoundException,CreateException,IllegalArgumentException
		{
			boolean result=false;
		   
			
				List AL_List=new ArrayList();
				
				List Data_List=new ArrayList();
				List employee_number = new ArrayList();
				List employee_name = new ArrayList();
				List dept_name = new ArrayList();
				List attendance = new ArrayList();
				List work_days = new ArrayList();
				List designation = new ArrayList();
				List payAttribute= new ArrayList();
				String str_employee_no="";
				String str_employee_name="";
				String str_employee_desg="";
				String str_employee_dept="";
				String str_employee_oldno="";
				String str_employee_panno="";
				String str_employee_bankno="";
				String month="FEB";
				String year="2013";
				
				
				String salCategory1="";
				String salSubCategory1="";
				List categoryList=new ArrayList();
				java.util.List hos_name  = new java.util.ArrayList();
			    java.util.List hos_code  = new java.util.ArrayList();
			    NumberFormat formatter = new DecimalFormat("#0.00");
				
				
				int no_records=0;
				int total_record=0;
				String date="20/"+month+"/"+year;
				String report_date = "";
				String salary_month = "";
				
				String hospitalQuery ="SELECT T.GSTR_HOSPITAL_NAME,T.GSTR_HOSPITAL_ADD1||', '||T.GSTR_HOSPITAL_ADD2||', '||T.GSTR_CITY ,' Ph.No. '||T.GSTR_PHONE FROM GBLT_HOSPITAL_MST T WHERE T.GNUM_ISVALID=1 AND T.GNUM_HOSPITAL_CODE=? ";
				hos_code.add("101");
			     try {
					hos_name=CommonDataBaseManip.getDetail(hospitalQuery,hos_code);
				} catch (GlobalException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DataNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			     String header=hos_name.get(0).toString();
			     String Hospital_Add=hos_name.get(1).toString();
				
				
				
				String  dateQuery="select to_char(sysdate,'dd fmMonth yyyy '), "+
				 "to_char(to_date('"+date+"','dd/mm/yyyy'),'fmMonth yyyy') from dual";
			try
			{

				AL_List = CommonDataBaseManip.getDetail(dateQuery);
				report_date =(String) AL_List.get(0);
				salary_month=(String) AL_List.get(1);

			}
			catch(Exception e)
			{System.out.println("error in date query===="+e);}

			AL_List.clear();
				
				
				
				/*List alList=new ArrayList();
				String keyList = dto.getSalSubCatsecondMenuData();
				
				System.out.println(" "+keyList);
				StringTokenizer idTokens= new StringTokenizer(keyList,",");
				
				List salSubCatList = new ArrayList();			
				while(idTokens.hasMoreElements())
				{	
					salSubCatList.add(idTokens.nextToken());
				}*/
				
				
			
			try
			{
				
				 int font_size=7;
				 
				 ServiceLocator  test=new ServiceLocator();
		            String path=ServiceLocator.GLOBAL_PAYBILL_IMAGE_PATH;
		            String saveFilePath=ServiceLocator.GLOBAL_PAYBILL_TEXTFILE_PATH;
		            String strFilename="GrossSalaryAnexureiv"+month+year;
		            //String path=request.getContextPath()+"/hisgolbal/images/pgi-logo.gif";
		            System.out.println("path is :"+path);
		            
		            //System.out.println("path is Through Service Locator:"+request.getContextPath());
		            
		           
		            Image pgilogo=Image.getInstance(path);
		            String fileName=saveFilePath+strFilename+".pdf";
	      		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      		FileOutputStream baos = new FileOutputStream(fileName);
	      		//Document document=new Document(new Rectangle(16*72,20*72),12,12,12,45);
	      		Document document=new Document(PageSize.A4);
	      			      			      	
	      		//PdfWriter writer = PdfWriter.getInstance(document, baos);
	      		
	      			PdfWriter writer = PdfWriter.getInstance(document, baos);
	      		
	      		
	      		document.addAuthor("C-DAC");
	    		document.addSubject("salary report");
	    		 HeaderFooter footer = new HeaderFooter(
	                        new Phrase("Page No:  ",FontFactory.getFont("Arial", font_size,Font.BOLD)), true);
	            footer.setBorder(Rectangle.NO_BORDER);
	            footer.setAlignment(Element.ALIGN_CENTER);
	            document.setFooter(footer);
	        	document.open();
		   
	        	
	        	int cols=14;
	        	int subcols=2;
	    		 
	        	double pageTotal = 0.0; // to sum and store all earning heads of one page
	        	double grandTotal = 0.0;
	     		double totalAmount=0.0;
	     		String AmountWords="";
	     		
	       
	     		 
	     		 Table pbrTab = new Table(cols);// for name,dept,total etc
	     		 pbrTab.setAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.setPadding(0);
	     		 pbrTab.setSpacing(0);
	     		
	     		 pbrTab.setBorder(0);
	     		 pbrTab.setWidth(100);
	     		 pbrTab.setCellsFitPage(true);	 
	     		
	     		
	     		 
	     		/*Font fontH1 = new Font(Font.TIMES_ROMAN,24,Font.BOLD);
	     		 Font fontH2 = new Font(Font.TIMES_ROMAN,22,Font.BOLD);
	     		 Font fontH3 = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
	     		// Font fontB = new Font(Font.TIMES_ROMAN,16,Font.NORMAL);
	     		Font fontBH = new Font(Font.TIMES_ROMAN,20,Font.NORMAL);
	     		Font fontBBH = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
	     		 Font fontBB = new Font(Font.TIMES_ROMAN,14,Font.BOLD);	*/
	     		 
	     		 
	     		Font fontH1 = new Font(Font.TIMES_ROMAN,12,Font.BOLD);
	     		 Font fontH2 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		 Font fontH3 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		
	     		Font fontBH = new Font(Font.TIMES_ROMAN,8,Font.NORMAL);
	     		Font fontBBH = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		 Font fontBB = new Font(Font.TIMES_ROMAN,8,Font.BOLD);
	     		Font fontB = new Font(Font.TIMES_ROMAN,1,Font.NORMAL);
	     		 
	     		 
	     		 
	     		 Cell pcell;
	     		 // commented as per format
	     		 
	     		/*pcell = new Cell("");
	    		
	    		 pcell.add(pgilogo);
	    		 pcell.setColspan(2);
	    		 pcell.setBorder(0);
	    		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    		 pbrTab.addCell(pcell);
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase(header+"\n"+Hospital_Add+"\n",fontH1));
	     		pcell.add(new Phrase("Salary Report\nMonth: "+salary_month,fontH2));
	     		 pcell.setColspan(cols-4);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	    		 pcell.add(new Phrase("\n\n\n"+report_date,fontBB));
	    		 pcell.setColspan(2);
	    		 pcell.setBorder(0);
	    		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    		 pbrTab.addCell(pcell);*/
	    		 
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Employee"+month+year,fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(0);
	     		
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	    		 
	    		 pbrTab.endHeaders();
	     		
	     		 
	    		 
	     		 
	     		 
	     		
		
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Annexure-IV",fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.TOP|Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	    		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Sec 4.(1).b.(x) The monthly remuneration received by each of its officers and employees, including the system of compensation provided in its regulations:",fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Gross Salary of Officers and Employees for the month of "+month+" , "+year+" \n \n",fontH3));
	     		 pcell.setColspan(cols);
	     		pcell.setBorder(Rectangle.LEFT|Rectangle.RIGHT|Rectangle.BOTTOM);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		/*pcell = new Cell("");
	     		 pcell.add(new Phrase("\n",fontB));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.BOTTOM|Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);*/
	    		 
	    		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("E.Code(Old)",fontH3));
	     		 pcell.setColspan(2);
	     		 pcell.setBorder(Rectangle.LEFT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("E.Code(New)",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("Employee Name",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Gross Salary   ",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("  Designation",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("\n",fontB));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.BOTTOM|Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		 int row=0;
	     		 int slno=0;
	     		 /////////////
	     		 
	     		
				// empDetails with gross value 0 (without joining with pay bill register)
				/*String empDetails="select  pf.str_employee_number, NVL ((SELECT b.STR_OLD_EMPLOYEE_NO  FROM PIST_EMP_CUR_DETAIL_DTL b WHERE b.gnum_hospital_code = pf.gnum_hospital_code AND b.gnum_isvalid = 1 AND b.str_emp_no = pf.str_employee_number  ), '-'  ) AS oldempno ,upper(pd.str_first_name||' '||pd.str_middle_name||' '||pd.str_last_name) , " +
			 		"(select r.str_desig_name from PIST_DESIGNATION_MST r where r.gnum_isvalid=1 and r.gnum_hospital_code=pf.gnum_hospital_code and pf.num_desig_id = r.num_desig_id ) as desg,nvl ((select sum(r.NUM_PARAMETER_AMOUNT) from ACTPAY_PAY_BILL_REGISTER_DTL r where r.STR_PARAMETER_TYPE='E' and R.STR_EMPLOYEE_NUMBER=PF.STR_EMPLOYEE_NUMBER and r.DT_PBR_GENERATION_DATE=to_date('"+date+"','dd/mm/yyyy')) ,0) as Gross " +
			 				"from actpay_prof_dtl pf ,pist_emp_personnel_dtl pd where pf.str_employee_number=pd.str_emp_no  and pd.str_validate='Y'   and pf.gnum_hospital_code =pd.gnum_hospital_code  and pf.gnum_isvalid=1 and pf.gnum_hospital_code='"+dto.getHOSPITAL_CODE()+"' and pf.gnum_sl_no=(select max(t.gnum_sl_no) from actpay_prof_dtl t where t.str_employee_number=pf.str_employee_number and t.gnum_isvalid=1 and t.gnum_hospital_code=pf.gnum_hospital_code) " +
			 						"and pf.NUM_SAL_SUB_CAT_ID in ("+dto.getSalSubCatsecondMenuData()+") order by desg,oldempno ";
			*/
				String empDetails="select  pf.str_employee_number, NVL ((SELECT b.STR_OLD_EMPLOYEE_NO  FROM PIST_EMP_CUR_DETAIL_DTL b WHERE b.gnum_hospital_code = pf.gnum_hospital_code AND b.gnum_isvalid = 1 AND b.str_emp_no = pf.str_employee_number  ), '-'  ) AS oldempno ,upper(pd.str_first_name||' '||pd.str_middle_name||' '||pd.str_last_name) , " +
		 		"(select r.str_desig_name from PIST_DESIGNATION_MST r where r.gnum_isvalid=1 and r.gnum_hospital_code=pf.gnum_hospital_code and pf.num_desig_id = r.num_desig_id ) as desg,nvl ((select sum(r.NUM_PARAMETER_AMOUNT) from ACTPAY_PAY_BILL_REGISTER_DTL r where r.STR_PARAMETER_TYPE='E' and R.STR_EMPLOYEE_NUMBER=PF.STR_EMPLOYEE_NUMBER and r.DT_PBR_GENERATION_DATE=to_date('"+date+"','dd/mm/yyyy')) ,0) as Gross " +
		 				"from actpay_prof_dtl pf ,pist_emp_personnel_dtl pd,actpay_pay_bill_register_dtl pr where pf.str_employee_number=pd.str_emp_no  and PR.STR_EMPLOYEE_NUMBER=pf.str_employee_number and PR.GNUM_HOSPITAL_CODE=pf.gnum_hospital_code and PR.STR_PARAMETER_CODE='SALARYDAYS' and pr.gnum_isvalid=1  and PR.DT_PBR_GENERATION_DATE=TO_DATE ('"+date+"', 'dd/mm/yyyy') and pd.str_validate='Y'   " +
		 				"and pf.gnum_hospital_code =pd.gnum_hospital_code  and pf.gnum_isvalid=1 and pf.gnum_hospital_code='101' and pf.gnum_sl_no=(select max(t.gnum_sl_no) from actpay_prof_dtl t where t.str_employee_number=pf.str_employee_number and t.gnum_isvalid=1 and t.gnum_hospital_code=pf.gnum_hospital_code) " +
		 						"  order by desg,oldempno ";
	
				
				System.out.println("empDetails "+empDetails);
				
			
				
				try
				{
					AL_List=CommonDataBaseManip.getDetail(empDetails);
				}
				catch (Exception e) 
				{
					System.out.println("error in query");
				}
				
				if(AL_List.size() !=0)
				{
					for(int cnt=0;cnt<AL_List.size();cnt=cnt+5)
					{
						
						
		     			 
		     			 pcell = new Cell("");
		     			 pcell.add(new Phrase(AL_List.get(cnt+1).toString(),fontBH));
		     			 pcell.setColspan(2);
		     			 pcell.setBorder(Rectangle.LEFT);
		     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		     			 pbrTab.addCell(pcell);
		     			 
		     			pcell = new Cell("");
		     			 pcell.add(new Phrase(AL_List.get(cnt).toString(),fontBH));
		     			 pcell.setColspan(3);
		     			 pcell.setBorder(0);
		     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     			 pbrTab.addCell(pcell);
		     			 
		     			pcell = new Cell("");
		    			 pcell.add(new Phrase(AL_List.get(cnt+2).toString(),fontBH));
		    			 pcell.setColspan(3);
		    			 pcell.setBorder(0);
		    			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		    			 pbrTab.addCell(pcell);
		    			 
		    			 pcell = new Cell("");
	     				 pcell.add(new Phrase(AL_List.get(cnt+4).toString()+"  ",fontBH));
	     				
	     				 grandTotal += Double.parseDouble(AL_List.get(cnt+4).toString());
	     				 pcell.setColspan(3);
	     				 pcell.setBorder(0);
	     				 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     				 pbrTab.addCell(pcell);
	     			 
	     			 
	     			 pcell = new Cell("");
	     			 pcell.add(new Phrase("  "+AL_List.get(cnt+3).toString(),fontBH));
	     			 pcell.setColspan(3);
	     			 pcell.setBorder(Rectangle.RIGHT);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     			 pbrTab.addCell(pcell);
					}
					pcell = new Cell("");
	     			 pcell.add(new Phrase("\n",fontB));
	     			 pcell.setColspan(cols);
	     			 pcell.setBorder(Rectangle.BOTTOM|Rectangle.RIGHT|Rectangle.LEFT);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     			 pbrTab.addCell(pcell);
				}
				else
				{
					pcell = new Cell("");
	     			 pcell.add(new Phrase("No Record Found",fontH3));
	     			 pcell.setColspan(cols);
	     			 pcell.setBorder(1);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     			 pbrTab.addCell(pcell);
				}
				
	     	
	     		
	     		/*String wordQuery="select DIGITWORD("+grandTotal+")||' ONLY' from dual";
	     		System.out.println("netPayQuery========: "+wordQuery);						
					
    			 java.util.List wordList = new java.util.ArrayList();
    			 //int total_earning=0;
    			 try
    			 {
    				 wordList = CommonDataBaseManip.getDetail(wordQuery);
    			 }
    			 catch(Exception e)
    			 {System.out.println("Error in getting wordQuery: "+e);}
    			 
    			if(wordList.size()>0)
    			 {
    			 AmountWords="( RUPEES "+(wordList.get(0).toString()) +")";
    			 }
    			 else
    				{
    				 AmountWords="( RUPEES ZERO ONLY )";
    				}
    			wordList.clear();*/
    			
    			
    			
	     		 
	     		 
	     		/*pcell = new Cell("");
	     		 pcell.add(new Phrase(StringUtils.rightPad("-Sd/-",34," ")+"\nDRAWING & DISBURSING OFFICER \n PGIMER,CHANDIGARH      ",fontBBH));
	     		 pcell.setBorder(0);
	     		 pcell.setColspan(cols);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);*/
	
	     		 document.add(pbrTab);
	     		 document.close();	
	     		/* response.setHeader("Expires", "0");
				 response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				 response.setHeader("Pragma", "public");
				 
						response.setContentType("application/pdf");
					
		  		 response.setContentType("application/pdf");*/
		  		
		  		
				
		  		
			
			}
		
		
		
		
		catch(PaybillException e)
		{
			throw new PaybillException("accounts.paybill.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
			
		}
		catch(Exception e)
		{
			throw new PaybillException("accounts.gpf.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
			
		}
		
		return result;
	}	
		
		public  static synchronized boolean getGrossSalaryReport(HttpServletRequest request, HttpServletResponse response,Pay_Salary_Rpt_DTO dto) throws PaybillException,DuplicateRecordFoundException,CreateException,IllegalArgumentException
		{
			boolean result=false;
		   if(dto==null)
				throw new IllegalArgumentException("accounts.paybill.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():Invalid Arguments");
			
			
				List AL_List=new ArrayList();
				
				List Data_List=new ArrayList();
				List employee_number = new ArrayList();
				List employee_name = new ArrayList();
				List dept_name = new ArrayList();
				List attendance = new ArrayList();
				List work_days = new ArrayList();
				List designation = new ArrayList();
				List payAttribute= new ArrayList();
				String str_employee_no="";
				String str_employee_name="";
				String str_employee_desg="";
				String str_employee_dept="";
				String str_employee_oldno="";
				String str_employee_panno="";
				String str_employee_bankno="";
				String month=dto.getStrMonth();
				String year=dto.getStrYear();
				
				String salaryGenerationCategory=dto.getIntCategory();
				String salaryGenerationSubCategory=dto.getIntSubCategory();
				String salCategory1="";
				String salSubCategory1="";
				List categoryList=new ArrayList();
				java.util.List hos_name  = new java.util.ArrayList();
			    java.util.List hos_code  = new java.util.ArrayList();
			    NumberFormat formatter = new DecimalFormat("#0.00");
				
				
				int no_records=0;
				int total_record=0;
				String date="20/"+month+"/"+year;
				String report_date = "";
				String salary_month = "";
				
				String hospitalQuery ="SELECT T.GSTR_HOSPITAL_NAME,T.GSTR_HOSPITAL_ADD1||', '||T.GSTR_HOSPITAL_ADD2||', '||T.GSTR_CITY ,' Ph.No. '||T.GSTR_PHONE FROM GBLT_HOSPITAL_MST T WHERE T.GNUM_ISVALID=1 AND T.GNUM_HOSPITAL_CODE=? ";
				hos_code.add(dto.getHOSPITAL_CODE());
			     try {
					hos_name=CommonDataBaseManip.getDetail(hospitalQuery,hos_code);
				} catch (GlobalException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DataNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			     String header=hos_name.get(0).toString();
			     String Hospital_Add=hos_name.get(1).toString();
				
				
				
				String  dateQuery="select to_char(sysdate,'dd fmMonth yyyy '), "+
				 "to_char(to_date('"+date+"','dd/mm/yyyy'),'fmMonth yyyy') from dual";
			try
			{

				AL_List = CommonDataBaseManip.getDetail(dateQuery);
				report_date =(String) AL_List.get(0);
				salary_month=(String) AL_List.get(1);

			}
			catch(Exception e)
			{System.out.println("error in date query===="+e);}

			AL_List.clear();
				
				System.out.println("salary sub category second menu"+dto.getSalSubCatsecondMenuData());
				
				/*List alList=new ArrayList();
				String keyList = dto.getSalSubCatsecondMenuData();
				
				System.out.println(" "+keyList);
				StringTokenizer idTokens= new StringTokenizer(keyList,",");
				
				List salSubCatList = new ArrayList();			
				while(idTokens.hasMoreElements())
				{	
					salSubCatList.add(idTokens.nextToken());
				}*/
				
				
			
			try
			{
				
				 int font_size=7;
				 
				 ServiceLocator  test=new ServiceLocator();
		            String path=ServiceLocator.GLOBAL_PAYBILL_IMAGE_PATH;
		            //String path=request.getContextPath()+"/hisgolbal/images/pgi-logo.gif";
		            System.out.println("path is :"+path);
		            
		            //System.out.println("path is Through Service Locator:"+request.getContextPath());
		            
		           
		            Image pgilogo=Image.getInstance(path);
		    	    
	      		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      		//FileOutputStream baos = new FileOutputStream("E://First.pdf");
	      		//Document document=new Document(new Rectangle(16*72,20*72),12,12,12,45);
	      		Document document=new Document(PageSize.A4);
	      			      			      	
	      		//PdfWriter writer = PdfWriter.getInstance(document, baos);
	      		if(dto.getStrReportFormat().equalsIgnoreCase("PDF"))
	      		{
	      			PdfWriter writer = PdfWriter.getInstance(document, baos);
	      		}
      		else
	      		{
	      			HtmlWriter writer1 = HtmlWriter.getInstance(document, baos);
	      		}
	      		
	      		document.addAuthor("C-DAC");
	    		document.addSubject("salary report");
	    		 HeaderFooter footer = new HeaderFooter(
	                        new Phrase("Page No:  ",FontFactory.getFont("Arial", font_size,Font.BOLD)), true);
	            footer.setBorder(Rectangle.NO_BORDER);
	            footer.setAlignment(Element.ALIGN_CENTER);
	            document.setFooter(footer);
	        	document.open();
		   
	        	
	        	int cols=14;
	        	int subcols=2;
	    		 
	        	double pageTotal = 0.0; // to sum and store all earning heads of one page
	        	double grandTotal = 0.0;
	     		double totalAmount=0.0;
	     		String AmountWords="";
	     		
	       
	     		 
	     		 Table pbrTab = new Table(cols);// for name,dept,total etc
	     		 pbrTab.setAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.setPadding(0);
	     		 pbrTab.setSpacing(0);
	     		
	     		 pbrTab.setBorder(0);
	     		 pbrTab.setWidth(100);
	     		 pbrTab.setCellsFitPage(true);	 
	     		
	     		
	     		 
	     		/*Font fontH1 = new Font(Font.TIMES_ROMAN,24,Font.BOLD);
	     		 Font fontH2 = new Font(Font.TIMES_ROMAN,22,Font.BOLD);
	     		 Font fontH3 = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
	     		// Font fontB = new Font(Font.TIMES_ROMAN,16,Font.NORMAL);
	     		Font fontBH = new Font(Font.TIMES_ROMAN,20,Font.NORMAL);
	     		Font fontBBH = new Font(Font.TIMES_ROMAN,20,Font.BOLD);
	     		 Font fontBB = new Font(Font.TIMES_ROMAN,14,Font.BOLD);	*/
	     		 
	     		 
	     		Font fontH1 = new Font(Font.TIMES_ROMAN,12,Font.BOLD);
	     		 Font fontH2 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		 Font fontH3 = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		
	     		Font fontBH = new Font(Font.TIMES_ROMAN,8,Font.NORMAL);
	     		Font fontBBH = new Font(Font.TIMES_ROMAN,10,Font.BOLD);
	     		 Font fontBB = new Font(Font.TIMES_ROMAN,8,Font.BOLD);
	     		Font fontB = new Font(Font.TIMES_ROMAN,1,Font.NORMAL);
	     		 
	     		 
	     		 
	     		 Cell pcell;
	     		 // commented as per format
	     		 
	     		/*pcell = new Cell("");
	    		
	    		 pcell.add(pgilogo);
	    		 pcell.setColspan(2);
	    		 pcell.setBorder(0);
	    		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	    		 pbrTab.addCell(pcell);
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase(header+"\n"+Hospital_Add+"\n",fontH1));
	     		pcell.add(new Phrase("Salary Report\nMonth: "+salary_month,fontH2));
	     		 pcell.setColspan(cols-4);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	    		 pcell.add(new Phrase("\n\n\n"+report_date,fontBB));
	    		 pcell.setColspan(2);
	    		 pcell.setBorder(0);
	    		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    		 pbrTab.addCell(pcell);*/
	    		 
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Employee"+month+year,fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(0);
	     		
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	    		 
	    		 pbrTab.endHeaders();
	     		
	     		 
	    		 
	     		 
	     		 
	     		
		
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Annexure-IV",fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.TOP|Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	    		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Sec 4.(1).b.(x) The monthly remuneration received by each of its officers and employees, including the system of compensation provided in its regulations:",fontH3));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Gross Salary of Officers and Employees for the month of "+month+" , "+year+" \n \n",fontH3));
	     		 pcell.setColspan(cols);
	     		pcell.setBorder(Rectangle.LEFT|Rectangle.RIGHT|Rectangle.BOTTOM);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		/*pcell = new Cell("");
	     		 pcell.add(new Phrase("\n",fontB));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.BOTTOM|Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);*/
	    		 
	    		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("E.Code(Old)",fontH3));
	     		 pcell.setColspan(2);
	     		 pcell.setBorder(Rectangle.LEFT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("E.Code(New)",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("Employee Name",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("Gross Salary(Rs)   ",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(0);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		 pcell = new Cell("");
	     		 pcell.add(new Phrase("  Designation",fontH3));
	     		 pcell.setColspan(3);
	     		 pcell.setBorder(Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		pcell = new Cell("");
	     		 pcell.add(new Phrase("\n",fontB));
	     		 pcell.setColspan(cols);
	     		 pcell.setBorder(Rectangle.BOTTOM|Rectangle.LEFT|Rectangle.RIGHT);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     		 pbrTab.addCell(pcell);
	     		 
	     		
	     		 
	     		 int row=0;
	     		 int slno=0;
	     		 /////////////
	     		 
	     		
				// empDetails with gross value 0 (without joining with pay bill register)
				/*String empDetails="select  pf.str_employee_number, NVL ((SELECT b.STR_OLD_EMPLOYEE_NO  FROM PIST_EMP_CUR_DETAIL_DTL b WHERE b.gnum_hospital_code = pf.gnum_hospital_code AND b.gnum_isvalid = 1 AND b.str_emp_no = pf.str_employee_number  ), '-'  ) AS oldempno ,upper(pd.str_first_name||' '||pd.str_middle_name||' '||pd.str_last_name) , " +
			 		"(select r.str_desig_name from PIST_DESIGNATION_MST r where r.gnum_isvalid=1 and r.gnum_hospital_code=pf.gnum_hospital_code and pf.num_desig_id = r.num_desig_id ) as desg,nvl ((select sum(r.NUM_PARAMETER_AMOUNT) from ACTPAY_PAY_BILL_REGISTER_DTL r where r.STR_PARAMETER_TYPE='E' and R.STR_EMPLOYEE_NUMBER=PF.STR_EMPLOYEE_NUMBER and r.DT_PBR_GENERATION_DATE=to_date('"+date+"','dd/mm/yyyy')) ,0) as Gross " +
			 				"from actpay_prof_dtl pf ,pist_emp_personnel_dtl pd where pf.str_employee_number=pd.str_emp_no  and pd.str_validate='Y'   and pf.gnum_hospital_code =pd.gnum_hospital_code  and pf.gnum_isvalid=1 and pf.gnum_hospital_code='"+dto.getHOSPITAL_CODE()+"' and pf.gnum_sl_no=(select max(t.gnum_sl_no) from actpay_prof_dtl t where t.str_employee_number=pf.str_employee_number and t.gnum_isvalid=1 and t.gnum_hospital_code=pf.gnum_hospital_code) " +
			 						"and pf.NUM_SAL_SUB_CAT_ID in ("+dto.getSalSubCatsecondMenuData()+") order by desg,oldempno ";
			*/
				String empDetails="select  pf.str_employee_number, NVL ((SELECT b.STR_OLD_EMPLOYEE_NO  FROM PIST_EMP_CUR_DETAIL_DTL b WHERE b.gnum_hospital_code = pf.gnum_hospital_code AND b.gnum_isvalid = 1 AND b.str_emp_no = pf.str_employee_number  ), '-'  ) AS oldempno ,upper(pd.str_first_name||' '||pd.str_middle_name||' '||pd.str_last_name) , " +
		 		"(select r.str_desig_name from PIST_DESIGNATION_MST r where r.gnum_isvalid=1 and r.gnum_hospital_code=pf.gnum_hospital_code and pf.num_desig_id = r.num_desig_id ) as desg,nvl ((select sum(r.NUM_PARAMETER_AMOUNT) from ACTPAY_PAY_BILL_REGISTER_DTL r where r.STR_PARAMETER_TYPE='E' and R.STR_EMPLOYEE_NUMBER=PF.STR_EMPLOYEE_NUMBER and r.DT_PBR_GENERATION_DATE=to_date('"+date+"','dd/mm/yyyy')) ,0) as Gross " +
		 				"from actpay_prof_dtl pf ,pist_emp_personnel_dtl pd,actpay_pay_bill_register_dtl pr where pf.str_employee_number=pd.str_emp_no  and PR.STR_EMPLOYEE_NUMBER=pf.str_employee_number and PR.GNUM_HOSPITAL_CODE=pf.gnum_hospital_code and PR.STR_PARAMETER_CODE='SALARYDAYS' and pr.gnum_isvalid=1  and PR.DT_PBR_GENERATION_DATE=TO_DATE ('"+date+"', 'dd/mm/yyyy') and pd.str_validate='Y'   " +
		 				"and pf.gnum_hospital_code =pd.gnum_hospital_code  and pf.gnum_isvalid=1 and pf.gnum_hospital_code='"+dto.getHOSPITAL_CODE()+"' and pf.gnum_sl_no=(select max(t.gnum_sl_no) from actpay_prof_dtl t where t.str_employee_number=pf.str_employee_number and t.gnum_isvalid=1 and t.gnum_hospital_code=pf.gnum_hospital_code) " +
		 						"and pf.NUM_SAL_SUB_CAT_ID in ("+dto.getSalSubCatsecondMenuData()+") order by desg,oldempno ";
	
				
				System.out.println("empDetails "+empDetails);
				
			
				
				try
				{
					AL_List=CommonDataBaseManip.getDetail(empDetails);
				}
				catch (Exception e) 
				{
					System.out.println("error in query");
				}
				
				if(AL_List.size() !=0)
				{
					for(int cnt=0;cnt<AL_List.size();cnt=cnt+5)
					{
						
						
		     			 
		     			 pcell = new Cell("");
		     			 pcell.add(new Phrase(AL_List.get(cnt+1).toString(),fontBH));
		     			 pcell.setColspan(2);
		     			 pcell.setBorder(Rectangle.LEFT);
		     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		     			 pbrTab.addCell(pcell);
		     			 
		     			pcell = new Cell("");
		     			 pcell.add(new Phrase(AL_List.get(cnt).toString(),fontBH));
		     			 pcell.setColspan(3);
		     			 pcell.setBorder(0);
		     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		     			 pbrTab.addCell(pcell);
		     			 
		     			pcell = new Cell("");
		    			 pcell.add(new Phrase(AL_List.get(cnt+2).toString(),fontBH));
		    			 pcell.setColspan(3);
		    			 pcell.setBorder(0);
		    			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
		    			 pbrTab.addCell(pcell);
		    			 
		    			 pcell = new Cell("");
	     				 pcell.add(new Phrase(AL_List.get(cnt+4).toString()+"  ",fontBH));
	     				
	     				 grandTotal += Double.parseDouble(AL_List.get(cnt+4).toString());
	     				 pcell.setColspan(3);
	     				 pcell.setBorder(0);
	     				 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     				 pbrTab.addCell(pcell);
	     			 
	     			 
	     			 pcell = new Cell("");
	     			 pcell.add(new Phrase("  "+AL_List.get(cnt+3).toString(),fontBH));
	     			 pcell.setColspan(3);
	     			 pcell.setBorder(Rectangle.RIGHT);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
	     			 pbrTab.addCell(pcell);
					}
					pcell = new Cell("");
	     			 pcell.add(new Phrase("\n",fontB));
	     			 pcell.setColspan(cols);
	     			 pcell.setBorder(Rectangle.BOTTOM|Rectangle.RIGHT|Rectangle.LEFT);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     			 pbrTab.addCell(pcell);
				}
				else
				{
					pcell = new Cell("");
	     			 pcell.add(new Phrase("No Record Found",fontH3));
	     			 pcell.setColspan(cols);
	     			 pcell.setBorder(1);
	     			 pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
	     			 pbrTab.addCell(pcell);
				}
				
	     	
	     		
	     		/*String wordQuery="select DIGITWORD("+grandTotal+")||' ONLY' from dual";
	     		System.out.println("netPayQuery========: "+wordQuery);						
					
    			 java.util.List wordList = new java.util.ArrayList();
    			 //int total_earning=0;
    			 try
    			 {
    				 wordList = CommonDataBaseManip.getDetail(wordQuery);
    			 }
    			 catch(Exception e)
    			 {System.out.println("Error in getting wordQuery: "+e);}
    			 
    			if(wordList.size()>0)
    			 {
    			 AmountWords="( RUPEES "+(wordList.get(0).toString()) +")";
    			 }
    			 else
    				{
    				 AmountWords="( RUPEES ZERO ONLY )";
    				}
    			wordList.clear();*/
    			
    			
    			
	     		 
	     		 
	     		/*pcell = new Cell("");
	     		 pcell.add(new Phrase(StringUtils.rightPad("-Sd/-",34," ")+"\nDRAWING & DISBURSING OFFICER \n PGIMER,CHANDIGARH      ",fontBBH));
	     		 pcell.setBorder(0);
	     		 pcell.setColspan(cols);
	     		 pcell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	     		 pbrTab.addCell(pcell);*/
	
	     		 document.add(pbrTab);
	     		 document.close();	
	     		 response.setHeader("Expires", "0");
				 response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
				 response.setHeader("Pragma", "public");
				 if(dto.getStrReportFormat().equalsIgnoreCase("PDF"))
					{
						response.setContentType("application/pdf");
					}
					else if(dto.getStrReportFormat().equalsIgnoreCase("HTML"))
					{
						response.setContentType("text/html");
							
						response.setHeader("Content-Disposition", "inline; filename=\"BankStatementReport.text\"");
						
					}
					else
					{
						response.setContentType("application/xls");
						response.setHeader("Content-Disposition", "inline; filename=\"BankStatementReport.xls\"");
						
					}
		  		// response.setContentType("application/pdf");
		  		 response.setContentLength(baos.size());
		  		 ServletOutputStream out = response.getOutputStream();
		  		 
		  		 baos.writeTo(out);
		  		 out.flush();
				
		  		System.out.println("salary sub category second menu"+dto.getSalSubCatsecondMenuData());
		  		
		  		InputStream in = new FileInputStream(new File("C:\\opt\\websphere\\myaccountfiles\\paybill\\SalaryTextFile\\GrossSalaryAnexureivFEB2013.pdf"));
		  		OutputStream outs = new FileOutputStream(new File("\\\\10.226.18.79\\Salary\\GrossSalaryAnexureivFEB2013.pdf"));

		  		// Transfer bytes from in to out
		  		byte[] buf = new byte[1024];
		  		int len;
		  		while ((len = in.read(buf)) > 0) {
		  			outs.write(buf, 0, len);
		  		}
		  		in.close();
		  		outs.close();
			
			}
		
		
		
		
		catch(PaybillException e)
		{
			throw new PaybillException("accounts.paybill.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
			
		}
		catch(Exception e)
		{
			throw new PaybillException("accounts.gpf.reports.delegates.Pay_Salary_Rpt_Delegates.getReport():"+e.getMessage());
			
		}
		
		return result;
	}			
	

}

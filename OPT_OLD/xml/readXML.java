package hisglobal.transactionmgmnt.xmlTest;

/*
Developed By          : Partha P Chattaraj
Creation Dated        : 17-06-2006
Modification Dated    : 07-05-2008
Version               : HIMS 2.0

*/

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import hisglobal.transactionmgmnt.xmlTest.transaction.BlockType;
import hisglobal.transactionmgmnt.xmlTest.transaction.OprType;
import hisglobal.transactionmgmnt.xmlTest.transaction.Transaction;


public class readXML
{
	private int totBlock;
	private int totQuery;
	private String transMgt = "";
	private int[] blocktotQuery;
	private String[] blockRowsIns;
	private String[] blockMandatory;
	private List[] blockQuery = null;
	private List[] blockQueryColSize = null;
	///////getter//////////////
	public int getTotBlock(){return this.totBlock;}
	public int getTotQuery(){return this.totQuery;}
	public String getTransMgt(){return this.transMgt;}
	public int[] getBlocktotQuery(){return this.blocktotQuery;}
	public String[] getBlockRowsIns(){return this.blockRowsIns;}
	public String[] getBlockMandatory(){return this.blockMandatory;}
	public List[] getBlockQuery(){return this.blockQuery;}
	public List[] getBlockQueryColSize(){return this.blockQueryColSize;}

	public void getValueOfXml(String xmlPath,String mode)
	{
		int i,j,k;
		List list1 = null;
		List list2 = null;
		try
		{
			JAXBContext jc=JAXBContext.newInstance("hisglobal.transactionmgmnt.xmlTest.transaction");//path of folder where jaxb.properties file is stored
			Unmarshaller unmarshaller=jc.createUnmarshaller();
			unmarshaller.setValidating(true);
			Transaction collection=(Transaction)unmarshaller.unmarshal(new
			File(xmlPath));
			List oprLst = collection.getOPR();
			for(i=0;i<oprLst.size();i++)
			{
				OprType oprObj = (OprType)oprLst.get(i);
				if(oprObj.getID().equals(mode))
				{
					this.totBlock = Integer.parseInt(oprObj.getBLOCKSIZE());
					this.totQuery = Integer.parseInt(oprObj.getQRYTOTSIZE());
					this.transMgt = oprObj.getTRNMGMT();
					this.blocktotQuery = new int[this.totBlock];
					this.blockRowsIns = new String[this.totBlock];
					this.blockMandatory = new String[this.totBlock];
					this.blockQuery = new List[this.totBlock];
					this.blockQueryColSize = new List[this.totBlock];
					List blockLst = oprObj.getBLOCK();
					for(j=0;j<blockLst.size();j++)
					{
						BlockType blockObj = (BlockType)blockLst.get(j);
						blocktotQuery[j] = Integer.parseInt(blockObj.getQRYSIZE());
						blockRowsIns[j] = blockObj.getROWSINS();
						blockMandatory[j] = blockObj.getMANDATORY();
						List qryLst = blockObj.getQRY();
						list1 = new ArrayList();
						list2 = new ArrayList();
						for(k=0;k<qryLst.size();k++)
						{
							BlockType.QRYType qryObj = (BlockType.QRYType)qryLst.get(k);
							list1.add(qryObj.getValue());
							list2.add(qryObj.getCOLSIZE());
						}
						blockQuery[j] = list1;
						blockQueryColSize[j] = list2;
					}
					break;
				}///end of if
			}/// end of for
		}///end of try
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Errro is: "+e);
		}
	}
}
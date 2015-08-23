package lal.foreverlove.ui;
import static lal.foreverlove.ui.Constant.*;

//���ܱ�ʰȡѡ����Ƭ��������Ϣ�洢����������
public class CandidateDis implements Comparable<CandidateDis>
{
   float currAngleSpan;//�˷���Ƭ��270�Ƚǵļн�
   float currAngle;//�˷���Ƭ�ĽǶ�
   int index;//�˷���Ƭ������

   public CandidateDis(float currAngleSpan,float currAngle,int index)
   {
	   this.currAngleSpan=currAngleSpan;
	   this.currAngle=currAngle;
	   this.index=index;
   }

   public int compareTo(CandidateDis another) 
   {
	    //�Ƚ�������Ƭ˭��270�ȵļн�С
		if(this.currAngleSpan<another.currAngleSpan)
	    {
	    	return -1;
	    }
	    if(this.currAngleSpan==another.currAngleSpan)
	    {
	    	return 0;
	    }
	    if(this.currAngleSpan>another.currAngleSpan)
	    {
	    	return 1;
	    }
		return 0;
   }
   
   
   public boolean isInXRange(float x,float y)//xyΪNEAR���ϵĴ�������
   {
	   //������Ƭ�ڲ�㣨�������ģ���X����
	   double xn=Board.length*Math.cos(Math.toRadians(currAngle));
	   //������Ƭ���㣨Զ�����ģ���X����
	   double xw=(Board.length+Board.width)*Math.cos(Math.toRadians(currAngle));
	   //������Ƭ�ڲ�㣨�������ģ���Z����
	   double zn=-Board.length*Math.sin(Math.toRadians(currAngle))+CENTER_Z;
	   //������Ƭ���㣨Զ�����ģ���Z����
	   double zw=-(Board.length+Board.width)*Math.sin(Math.toRadians(currAngle))+CENTER_Z;
	   
	   //���ݵȱ�������ԭ��ֱ�������������NEAR���ϵ�X����ͶӰ
	   double proj_xn=-NEAR*xn/zn;
	   double proj_xw=-NEAR*xw/zw;  
	   
	   //�ֱ��������ͶӰX�����д�ĺ�С�ģ������ڽ��з�Χ�߼��ж�
	   double xmax=Math.max(proj_xn, proj_xw);
	   double xmin=Math.min(proj_xn, proj_xw);
	   
	   //���ش���YͶӰ��Χ
	   double k=x/NEAR;
	   double p=xn/(zn-CENTER_Z);
	   double zq=CENTER_Z*p/(p+k);
	   double xq=-k*zq;
	   double oa=Math.sqrt(x*x+NEAR*NEAR);
	   double ob=Math.sqrt(xq*xq+zq*zq);
	   double yq=oa*Board.height/ob;
	   
	   if(x<xmax&&x>xmin)
	   {//���ڷ�Χ�ڷ���true
		   if(y>-yq&&y<yq)
		   {
			   return true;
		   }
		   else
		   {
			   return false;
		   }
	   }
	   else
	   {//�����ڷ�Χ�ڷ���false
		   return false;
	   }
   }
}

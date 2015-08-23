package lal.foreverlove.ui;
 
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
//��ʾ��Ƭ���������
public class Board {
	private FloatBuffer   mVertexBuffer;//�����������ݻ���
    private FloatBuffer   mTextureBuffer;//�����������ݻ���
    int vCount=0;//������
    
    public static final float length=3f;//0��λ��ͼƬ������ԭ��ĺ������
    public static final float width=20f;//ͼƬ�Ŀ�
    public static final float height=15f*0.5f;//ͼƬ�İ�߶�

    
    public Board()
    {    	
    	//�����������ݵĳ�ʼ��================begin============================
        vCount=6;
        float vertices[]=new float[]
        {
        	length,height,0,
        	length,-height,0,
        	length+width,height,0,
        	
        	length+width,height,0,
        	length,-height,0,
        	length+width,-height,0
        };
		
        //���������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��Ϊint�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //�����������ݵĳ�ʼ��================end============================
        
        //������ɫ���ݵĳ�ʼ��================begin============================
        float textures[]=new float[]//������ɫֵ���飬ÿ������4��ɫ��ֵRGBA
        {
        		0,0,
        		0,1,
        		1,0,
        		
        		1,0,
        		0,1,
        		1,1
        };        
        //���������������ݻ���
        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mTextureBuffer = cbb.asFloatBuffer();//ת��Ϊint�ͻ���
        mTextureBuffer.put(textures);//�򻺳����з��붥����ɫ����
        mTextureBuffer.position(0);//���û�������ʼλ��
        //�ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
        //ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
        //������ɫ���ݵĳ�ʼ��================end============================
    }

    public void drawSelf(GL10 gl,int texId)
    {            
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);//���ö�����������
		//Ϊ����ָ��������������
        gl.glVertexPointer
        (
        		3,				//ÿ���������������Ϊ3  xyz 
        		GL10.GL_FLOAT,	//��������ֵ������Ϊ GL_FLOAT
        		0, 				//����������������֮��ļ��
        		mVertexBuffer	//������������
        );
		
        //��������
        gl.glEnable(GL10.GL_TEXTURE_2D);   
        //����ʹ������ST���껺��
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        //Ϊ����ָ������ST���껺��
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        //�󶨵�ǰ����
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);
		
        //����ͼ��
        gl.glDrawArrays
        (
        		GL10.GL_TRIANGLES, 		//�������η�ʽ���
        		0, 			 			//��ʼ����
        		vCount					//��������
        );  
        
        //�ر�����
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}

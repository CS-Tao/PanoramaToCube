package panoramatocube;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Convert {

	public class XYZ
	{
		public double x;
		public double y;
		public double z;
	}
	
	public class RGB
	{
		public int R;
		public int G;
		public int B;
	}
	
	public XYZ outImgToXYZ(int i, int j,int face, double edge)
	{
		XYZ xyz = new XYZ();
		double a = 2.0*((double)i)/edge;
		double b = 2.0*((double)j)/edge;
		if(face == 0)
		{//back
			xyz.x = -1.0;
			xyz.y = 1.0 - a;
			xyz.z = 3.0 - b;
		}
		else if(face == 1)
		{//left
			xyz.x = a - 3.0;
			xyz.y = -1.0;
			xyz.z = 3.0 - b;
		}
		else if(face == 2)
		{//front
			xyz.x = 1.0;
			xyz.y = a - 5.0;
			xyz.z = 3.0 - b;
		}
		else if(face == 3)
		{//right
			xyz.x = 7.0 - a;
			xyz.y = 1.0;
			xyz.z = 3.0 - b;
		}
		else if(face == 4)
		{//top
			xyz.x = b - 1.0;
			xyz.y = a - 5.0;
			xyz.z = 1.0;
		}
		else if(face == 5)
		{//bottom
			xyz.x = 5.0 - b;
			xyz.y = a - 5.0;
			xyz.z = -1.0;
		}
		return xyz;
	}
	
	public void convertBack(BufferedImage ImgIn, BufferedImage ImgOut)
	{
		int inWidth = ImgIn.getWidth(null);
		int inHeight = ImgIn.getHeight(null);
		int outWidth = ImgOut.getWidth(null);
		
		int edge = inWidth / 4;//立方体边长
		
		for(int i=0;i<outWidth;i++)
		{
			int face = (int)(i/edge);//0 - back, 1 - left 2 - front, 3 - right
			int loopmin;
			int loopmax;
			if(face == 2)
			{
				loopmin = 0;
				loopmax = (int) (edge * 3);
			}
			else 
			{
				loopmin = (int) edge;
				loopmax = (int) (edge * 2);
			}
			for(int j=loopmin;j<loopmax;j++)
			{
				int face2 = face;
				if(j < edge)
					face2 = 4;//top
				else if(j >= 2*edge)
					face2 = 5;//bottom
				else
					face2 = face;
				
				//以下为算法步骤，无注释（含双线性插值）
				XYZ xyz = outImgToXYZ(i, j, face2, edge);
				double theta = Math.atan2(xyz.y,xyz.x);
				
				double r = Math.hypot(xyz.x, xyz.y);
				double phi = Math.atan2(xyz.z, r);
				
				double uf = (2.0*edge*(theta + Math.PI)/Math.PI);
				double vf = (2.0*edge*(Math.PI/2 - phi)/Math.PI);
				
				int ui = (int) Math.floor(uf);
				int vi = (int) Math.floor(vf);
				
				int u2 = ui + 1;
				int v2 = vi + 1;
				
				double mu = uf - ui;
				double nu = vf - vi;
				
				int A = ImgIn.getRGB((int)(ui % inWidth), (int)(vi < 0 ? 0 : vi > (inHeight-1) ? (inHeight-1) : vi));
				int B = ImgIn.getRGB((int)(u2 % inWidth), (int)(vi < 0 ? 0 : vi > (inHeight-1) ? (inHeight-1) : vi));
				int C = ImgIn.getRGB((int)(ui % inWidth), (int)(v2 < 0 ? 0 : v2 > (inHeight-1) ? (inHeight-1) : v2));
				int D = ImgIn.getRGB((int)(u2 % inWidth), (int)(v2 < 0 ? 0 : v2 > (inHeight-1) ? (inHeight-1) : v2));
				
				RGB Argb = new RGB();
				Argb.R = (A & 0xff0000) >> 16;
				Argb.G = (A & 0xff00) >> 8;
				Argb.B = (A & 0xff);
				
				RGB Brgb = new RGB();
				Brgb.R = (B & 0xff0000) >> 16;
				Brgb.G = (B & 0xff00) >> 8;
				Brgb.B = (B & 0xff);
				
				RGB Crgb = new RGB();
				Crgb.R = (C & 0xff0000) >> 16;
				Crgb.G = (C & 0xff00) >> 8;
				Crgb.B = (C & 0xff);
				
				RGB Drgb = new RGB();
				Drgb.R = (D & 0xff0000) >> 16;
				Drgb.G = (D & 0xff00) >> 8;
				Drgb.B = (D & 0xff);
				
				RGB outrgb = new RGB();
				outrgb.R = (int) Math.round(Argb.R*(1-mu)*(1-nu) + Brgb.R*(mu)*(1-nu) + Crgb.R*(1-mu)*nu + Drgb.R*mu*nu);
				outrgb.G = (int) Math.round(Argb.G*(1-mu)*(1-nu) + Brgb.G*(mu)*(1-nu) + Crgb.G*(1-mu)*nu + Drgb.G*mu*nu);
				outrgb.B = (int) Math.round(Argb.B*(1-mu)*(1-nu) + Brgb.B*(mu)*(1-nu) + Crgb.B*(1-mu)*nu + Drgb.B*mu*nu);
				
				Color color = new Color(outrgb.R, outrgb.G, outrgb.B);
				
				ImgOut.setRGB(i, j, color.getRGB());
			}
		}
	}
}

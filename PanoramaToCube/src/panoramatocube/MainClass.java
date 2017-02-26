package panoramatocube;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MainClass {

	public static void PrintUsage()
	{
		System.out.println("#Usage for PanoramaToCube:\n"
				+ "#PanoramaToCube [Input Image] [Output Image]\n"
				+ "#PS:Output Image must be PNG.");
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		if(args.length == 0)
		{
			PrintUsage();
			return;
		}
		
		String inputPath = args[0];
		String outputPath = args[1];
		
		try
		{
			System.out.println("Panorama2Cube is runing...");
			
			BufferedImage ImgIn = ImageIO.read(
					new File(inputPath));//读取全景图
			
			int inWidth = ImgIn.getWidth(null);//全景图宽
			
			BufferedImage ImgOut2 = new BufferedImage(inWidth, inWidth * 3/4, BufferedImage.TYPE_INT_ARGB);
			
			(new Convert()).convertBack(ImgIn, ImgOut2);
			
			System.out.println("Convert successfully!\nOutputing image...");
			
			ImageIO.write(ImgOut2, "png", 
					new File(outputPath));//输出图片
			
			System.out.println("Bingo!\n");
			
			return;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

}

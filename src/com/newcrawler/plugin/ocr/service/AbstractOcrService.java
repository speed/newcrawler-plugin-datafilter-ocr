package com.newcrawler.plugin.ocr.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.newcrawler.plugin.common.cache.MemCache;
import com.newcrawler.plugin.common.db.BaseDao;
import com.newcrawler.plugin.common.utils.Distance;
import com.newcrawler.plugin.ocr.bo.OCRCacheBo;
import com.newcrawler.plugin.ocr.tdzyw.Constants;

/**
 * 灰度公式是 gray = 0.212671 * R + 0.715160 * G + 0.072169 * B
 * gray大于128，为白色，小于，为黑色
 * @author www.02ta.com
 *
 */
public abstract class AbstractOcrService {
	private static Log logger = LogFactory.getLog(AbstractOcrService.class);
	
	protected abstract Map<String, String> getTempMap();
	private static final int MAX_LEN=20;//数字长度
	
	public String query(Map<String, String> properties, String md5){
		String value=(String)MemCache.getInstance().get(md5);
		if(value!=null){
			return value;
		}
		String sql="select * from plugin_ocr_cache where md5=?";
		List<Object> parameters=new ArrayList<Object>();
		parameters.add(md5);
		
		List<?> list=null;
		try {
			list = BaseDao.query(properties, OCRCacheBo.class, sql, parameters);
		} catch (Exception e) {
			logger.error(e);
		}
		if(list!=null && !list.isEmpty()){
			value=((OCRCacheBo)list.get(0)).getValue();
			MemCache.getInstance().put(md5, value);
			return value;
		}
		return null;
	}
	
	public boolean save(Map<String, String> properties, String md5, String value){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String create_time=String.valueOf(sdf.format(new Date()));
		
		List<Object> parameters=new ArrayList<Object>();
		parameters.add(md5);
		parameters.add(value);
		parameters.add(create_time);
		
		String sql="INSERT INTO `plugin_ocr_cache` (`md5`, `value`, `create_time`) VALUES (?, ?, ?);";
		BaseDao.saveOrUpdate(properties, sql, parameters);
		MemCache.getInstance().put(md5, value);
		return true;
	}
	
	/**
	 * 识别数字
	 * @param list
	 * @return
	 */
	public String recognition(List<String> list, int rate){
		String result="";
		Map<String,String> map=getTempMap();
		for(String str:list){
			str=str.replaceAll("\r\n", "");
			if(map.containsKey(str)){
				String v=map.get(str);
				result+=v;
				//System.out.println(v);
			}else{
				int maxRate=0;
				String v="";
				for(String temp:map.keySet()){
					int t=Distance.similarRate(temp, str);
					if(t>maxRate){
						maxRate=t;
						v=map.get(temp);
						//System.out.println(v+">"+maxRate);
					}
				}
				if(maxRate>rate){
					//System.out.println(v);
					result+=v;
				}
			}
		}
		return result;
	}
	
	/**
	 * 打印矩阵
	 * @param list
	 */
	public void print(List<String> list){
		for(String str:list){
			System.out.println(str.replaceAll("0", " "));
			System.out.println(str.replaceAll("\r\n", ""));
			System.out.println("");
		}
	}
	/**
	 * 读取每个数字的矩阵
	 * @param imageInByte
	 * @return
	 * @throws IOException
	 */
	public List<String> readTemp(byte[] imageInByte) throws IOException{
		List<String> list=new ArrayList<String>();
		InputStream in = new ByteArrayInputStream(imageInByte);
		BufferedImage image = ImageIO.read(in);
		
		int iw = image.getWidth();
		int ih = image.getHeight();
		int pixels[] =convertToBlackWhite(image, iw, ih) ;
        int matrix[][]=new int[ih][iw];
        int x=0;
        int y=0;
		for (int i=0,len=pixels.length;i<len;i++) {
			matrix[y][x]=pixels[i];
			if((i+1)%iw==0){
				y++;
				x=0;
			}else{
				x++;
			}
		}
		boolean isSplit=true;
		boolean isEmpty=true;
		int n=0;
		int t=0;
		int maxWidth=0;
		int maxHeight=0;
		int nmatrix[][][]=new int[MAX_LEN][ih][iw];
		for (int i=0;i<iw;i++) {
			isSplit=true;
			for (int j=0;j<ih;j++) {
				int v=matrix[j][i];
				nmatrix[n][j][t]=v;
				
				if (matrix[j][i] != -1){
					isSplit=false;
				}
			}
			t++;
			if(isSplit){
				if(!isEmpty){
					StringBuffer sb=new StringBuffer();
					int height=read(sb, nmatrix[n], ih, iw, maxWidth);
					if(height>maxHeight){
						maxHeight=height;
					}
					list.add(sb.toString());
					n++;
					isEmpty=true;
				}
				t=0;
			}else{
				maxWidth=t;
				isEmpty=false;
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @param inStream
	 * @return
	 * @throws IOException
	 */
	public byte[] readByByte(InputStream inStream) throws IOException { 
        ByteArrayOutputStream outstream = new ByteArrayOutputStream(); 
        byte[] buffer = new byte[1024];
        int len = -1; 
        while ((len = inStream.read(buffer)) != -1) { 
            outstream.write(buffer, 0, len); 
        }
        outstream.close(); 
        return outstream.toByteArray();
    } 
	
	/**
	 * 转换到黑白单色位图
	 * @param srcImg
	 * @param width
	 * @param height
	 * @return
	 */
	private int[] convertToBlackWhite(final BufferedImage srcImg, final int width, final int height){
        int[] pixels = new int[width * height];
        srcImg.getRGB(0, 0, width, height, pixels, 0, width);
        int newPixels[] = new int[width * height];
        for(int i = 0; i < width * height; i++) {
            int r = (pixels[i] >> 16) & 0xff;
            int g = (pixels[i] >> 8) & 0xff;
            int b = (pixels[i]) & 0xff;
            // Gray=R×0.299+G×0.587+B×0.114
            int gray = (int)(Constants.GRAY_R  * r + Constants.GRAY_G * g + Constants.GRAY_B * b);
            if(gray>128){
            	newPixels[i]=-1;//0x00ffffff;白色
            }else{
            	//newPixels[i]= (gray<<16)+ (gray<<8)+ gray;
            	newPixels[i]= 0xff000000;//黑色
            }
        }
        return newPixels;
    }
	
	private int read(StringBuffer sb, int [][] matrix, int ih, int iw, int maxWidth){
		int height=0;
		for (int i=0;i<ih;i++) {
			boolean isEmpty=true;
			String temp="";
			for (int j=0;j<maxWidth;j++) {
				if (matrix[i][j] == -1 || matrix[i][j] == 0){
					temp+="0";
				} else{
					isEmpty=false;
					temp+="1";
				}
			}
			if(isEmpty){
				continue;
			}
			height++;
			sb.append(temp);
			sb.append("\r\n");
		}
		return height;
	}
	
}

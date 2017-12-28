/**
 * 
 */
package com.properties.main;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.properties.component.CqdaiEncryptPanel;
import com.properties.component.DataQueryPanel;
import com.properties.component.PropertyManagerPanel;

/**
 * @author bomb
 *
 */
public class Main extends JFrame{


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Main(){
		
		this.setTitle("实用工具");
		InputStream input = Main.class.getResourceAsStream("/favicon.png");
		if(input != null){
			try {
				Image image = ImageIO.read(input);
				this.setIconImage(image);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
		PropertyManagerPanel propertyManagerPanel = new PropertyManagerPanel();
		
		DataQueryPanel dataQueryPanel = new DataQueryPanel();
		
		CqdaiEncryptPanel cqdaiEncryptPanel = new CqdaiEncryptPanel();
		
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(propertyManagerPanel);
		tabbedPane.add(dataQueryPanel);
		tabbedPane.add(cqdaiEncryptPanel);
		this.add(tabbedPane);
		this.setSize(680, 400);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}
	
	/**
	 * main method
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new Main();
	}
	
	



	
}

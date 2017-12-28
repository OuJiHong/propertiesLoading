package com.properties.component;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.commons.codec.binary.Hex;

/**
 * cqdai常规数据加解密界面
 * @author OJH
 *
 */
public class CqdaiEncryptPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 对应密钥
	 */
	private static byte[] byte_key1 = { 17, 34, 79, 88, -38, -100, -80, -104 };

	private static byte[] byte_key2 = { -53, -35, 85, 102, -3, 50, 121, 36 };
	
	
	/**
	 * 按照“加密-解密-加密”的顺序组合
	 */
	private static byte[] complexKey = new byte[byte_key1.length + byte_key2.length + byte_key1.length];
	
	static{
		
		System.arraycopy(byte_key1, 0, complexKey, 0, byte_key1.length);
		System.arraycopy(byte_key2, 0, complexKey, byte_key1.length, byte_key2.length);
		System.arraycopy(byte_key1, 0, complexKey, byte_key1.length + byte_key2.length, byte_key1.length);
	}
	
	
	private Border sameBorder = new EmptyBorder(0, 20, 0, 0);
	
	public CqdaiEncryptPanel() {
		
		this.setName("橙旗贷加解密");
		this.setLayout(new BorderLayout(8,4));
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weighty = 1;
		
		
		JPanel normalPanel = new JPanel(gridBagLayout);
		final JLabel normalLabel = new JLabel("待加密的数据：");
		normalLabel.setBorder(sameBorder);
		final JTextArea normalArea = new JTextArea();
		
		constraints.weightx = 0;
		gridBagLayout.setConstraints(normalLabel, constraints);
		normalPanel.add(normalLabel);
		
		constraints.weightx = 1;
		JScrollPane normalAreaPane = new JScrollPane(normalArea);
		gridBagLayout.setConstraints(normalAreaPane, constraints);
		normalPanel.add(normalAreaPane);
		
		
	
		
		JPanel encryptedPanel = new JPanel(gridBagLayout);
		final JLabel encryptedLabel = new JLabel("待解密的数据：");
		encryptedLabel.setBorder(sameBorder);
		final JTextArea encryptedArea = new JTextArea();
		
		JScrollPane encryptAreaPane = new JScrollPane(encryptedArea);
		constraints.weightx = 0;
		gridBagLayout.setConstraints(encryptedLabel, constraints);
		constraints.weightx = 1;
		
		gridBagLayout.setConstraints(encryptAreaPane, constraints);
		
		encryptedPanel.add(encryptedLabel);
		encryptedPanel.add(encryptAreaPane);
		
		
		//wrap
		JPanel wrapPanel = new JPanel(new GridLayout(0,1));
		wrapPanel.add(normalPanel);
		wrapPanel.add(encryptedPanel);
		
		
		
		//button
		JPanel buttonWrap = new JPanel(new GridLayout(0,2));
		final JButton encryptBtn = new JButton("加密");
		final JButton decryptBtn = new JButton("解密");
		encryptBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String normalText = normalArea.getText();
				if(normalText == null || normalText.length() == 0){
					JOptionPane.showMessageDialog(CqdaiEncryptPanel.this, "请输入待加密的数据", "错误提示", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					encryptBtn.setEnabled(false);
					String cipherData = encryptData(normalText);
					encryptedArea.setText(cipherData);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(CqdaiEncryptPanel.this, e1.getMessage(), "错误提示", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}finally{
					encryptBtn.setEnabled(true);
				}
				
			}
		});
		
		decryptBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String cipherData = encryptedArea.getText();
				if(cipherData == null || cipherData.length() == 0){
					JOptionPane.showMessageDialog(CqdaiEncryptPanel.this, "请输入待解密的数据", "错误提示", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					decryptBtn.setEnabled(false);
					String normalText = decryptData(cipherData);
					normalArea.setText(normalText);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(CqdaiEncryptPanel.this, e1.getMessage(), "错误提示", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}finally{
					decryptBtn.setEnabled(true);
				}
			}
		});
		
		
		buttonWrap.add(encryptBtn);
		buttonWrap.add(decryptBtn);
		
		
		this.add(wrapPanel, BorderLayout.CENTER);
		this.add(buttonWrap, BorderLayout.SOUTH);
		
		
		
	}
	
	
	/**
	 * 数据填充至8的倍数
	 * @param normalText
	 * @return
	 */
	private byte[] fillMultiple(String normalText){
		byte[] originalData = normalText.getBytes();
		int remainder = originalData.length % 8;
		if(remainder != 0){
			 byte[] tmpData = new byte[originalData.length + (8 - remainder)];
			 Arrays.fill(tmpData, (byte)0);
			 System.arraycopy(originalData, 0, tmpData, 0, originalData.length);
			 originalData = tmpData;
		}
		
		return originalData;
	}
	
	
	/**
	 * 加密数据
	 * @param normalText
	 * @return
	 */
	private String encryptData(String normalText) throws Exception{
		
		SecretKeySpec secretKeySpec = new SecretKeySpec(complexKey, "DESede");

		Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");//原有代码没有应用填充，需要手动填充空格
//		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		byte[] encryptData = cipher.doFinal(fillMultiple(normalText));
		
		return Hex.encodeHexString(encryptData);
		  
	}
	
	
	/**
	 * 解密数据
	 * @param encryptData
	 * @return
	 */
	private String decryptData(String hexStr) throws Exception{
		SecretKeySpec secretKeySpec = new SecretKeySpec(complexKey, "DESede");

		Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");//原有代码没有应用填充，需要手动填充空格
//		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		
		byte[] originalCipherData = Hex.decodeHex(hexStr.toCharArray());
		byte[] decryptData = cipher.doFinal(originalCipherData);
		
		return new String(decryptData, "UTF-8");
		
	}
	
	
	
}

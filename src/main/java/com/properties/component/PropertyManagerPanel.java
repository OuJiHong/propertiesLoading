package com.properties.component;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.properties.bean.ProjectConfig;
import com.properties.util.RedissonUtil;
import com.properties.util.RedissonUtil.ENV;

/**
 * 配置修改管理面板
 * @author OJH
 *
 */
public class PropertyManagerPanel extends JPanel{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 项目类型
	 * @author OJH
	 *
	 */
	public enum ProjectType{
		shopProject("shop项目",ProjectConfig.createShopProjectConfig()),
		commonProject("common项目",ProjectConfig.createCommonProjectConfig()),
		;
		
		/**
		 *名称 
		 */
		private String name;
		
		private ProjectConfig config;

		
		private ProjectType(String name, ProjectConfig config) {
			this.name = name;
			this.config = config;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ProjectConfig getConfig() {
			return config;
		}

		public void setConfig(ProjectConfig config) {
			this.config = config;
		}

		
		
		
	}
	
	
	/**
	 * 选中的环境
	 */
	private ENV selectedEnv = null;
	
	private JRadioButton selectedProjectRadio = null;
	
	private Border sameBorder = new EmptyBorder(0, 20, 0, 0);
	
	/**
	 * constructor 
	 */
	public PropertyManagerPanel() {
		
		System.out.println("encoding:" + System.getProperty("file.encoding"));
		this.setName("配置文件导出");
		
		JTextField redisMapKeyField = new JTextField();
		JTextField  pathField = new JTextField();
		JTextArea outArea = new JTextArea();
		
		//top
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(0, 1, 8, 4));
		topPanel.add(createEnvPanel());
		topPanel.add(createProjectPanel(redisMapKeyField,pathField));
		
		
		JPanel redisMapKeyWrap = new JPanel(new GridLayout(0,2));
		JLabel redisMapKeyLabel = new JLabel("redis对应的key:");
		redisMapKeyLabel.setBorder(sameBorder);
		
		redisMapKeyWrap.add(redisMapKeyLabel);
		redisMapKeyWrap.add(redisMapKeyField);
		
		JPanel pathWrap = new JPanel(new GridLayout(0,3));
		JLabel pathLabel = new JLabel("导入/导出的文件路径:");
		pathLabel.setBorder(sameBorder);
		
		pathWrap.add(pathLabel);
		pathWrap.add(pathField);
		pathWrap.add(createSelectFile(pathField));
		
		topPanel.add(redisMapKeyWrap);
		topPanel.add(pathWrap);
		
		//center
		JPanel centerPanel = new JPanel();
		
		centerPanel.setLayout(new GridLayout(0,1));
		centerPanel.add(new JScrollPane(outArea));
		
		//bottom
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(0,2));
		bottomPanel.add(createExportBtn(redisMapKeyField,pathField,outArea));
		bottomPanel.add(createImportBtn(redisMapKeyField,pathField,outArea));
		
		
		this.setLayout(new BorderLayout());
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);
		
	}
	
	
	/**
	 * 创建选择按钮
	 * @return
	 */
	private JButton createSelectFile(final JTextField receiveField){
		JButton selectFile = new JButton("导出/导入位置");
		selectFile.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				File dir = new File(System.getProperty("user.home"));
				fileChooser.setCurrentDirectory(dir);
				fileChooser.setDialogTitle("选择文件");
				fileChooser.setDialogType(JFileChooser.FILES_ONLY);
				int state = fileChooser.showOpenDialog(PropertyManagerPanel.this); 
				if(state == JFileChooser.APPROVE_OPTION){
					File selectedFile = fileChooser.getSelectedFile();
					receiveField.setText(selectedFile.getAbsolutePath());
				}
				
			}
			
		});
		
		return selectFile;
	}
	
	
	/**
	 * 创建导出按钮
	 * @return
	 */
	private JButton createExportBtn(final JTextField redisMapKeyField, final JTextField pathField, final JTextArea outArea){
		final JButton exportBtn = new JButton("导出到本地");
		
		exportBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				final String redisMapKey = redisMapKeyField.getText();
				final String outPath = pathField.getText();
				
				if(redisMapKey == null || redisMapKey.equals("")){
					JOptionPane.showMessageDialog(PropertyManagerPanel.this, "请选择一个操作项目","提示信息", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(outPath == null || outPath.equals("")){
					JOptionPane.showMessageDialog(PropertyManagerPanel.this, "请选择一个输出文件","提示信息", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
			
				
				int state = JOptionPane.showConfirmDialog(PropertyManagerPanel.this, "确认导出到本地？", "确认提示", JOptionPane.OK_CANCEL_OPTION);
				if(state == JOptionPane.OK_OPTION){
					exportBtn.setEnabled(false);
					Thread thread = new Thread(new Runnable(){

						public void run() {
							
							try {
								String exportStr = RedissonUtil.readMapData(selectedEnv, redisMapKey, new File(outPath));
								outArea.setText(exportStr);
								JOptionPane.showMessageDialog(PropertyManagerPanel.this, "导出成功", "成功提示", JOptionPane.INFORMATION_MESSAGE);
							} catch (Exception e1) {
								e1.printStackTrace();
								outArea.setText(e1.getMessage());
								JOptionPane.showMessageDialog(PropertyManagerPanel.this, "导出数据失败 !!!", "错误提示", JOptionPane.ERROR_MESSAGE);
							}finally{
								exportBtn.setEnabled(true);
							}
						}
						
					});
					
					thread.start();
					
				}
				
				
			}
			
			
		});

		return exportBtn;
	}
	
	
	/**
	 * 创建导入按钮
	 * 
	 * @return
	 */
	private JButton createImportBtn(final JTextField redisMapKeyField, final JTextField pathField, final JTextArea outArea){
		final JButton importBtn = new JButton("导入到服务器");
		
		importBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				final String redisMapKey = redisMapKeyField.getText();
				final String outPath = pathField.getText();
				
				
				if(redisMapKey == null || redisMapKey.equals("")){
					JOptionPane.showMessageDialog(PropertyManagerPanel.this, "请选择一个操作项目","提示信息", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(outPath == null || outPath.equals("")){
					JOptionPane.showMessageDialog(PropertyManagerPanel.this, "请选择一个输出文件","提示信息", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
			
				int state = JOptionPane.showConfirmDialog(PropertyManagerPanel.this, "确认导入到服务器？", "确认提示", JOptionPane.OK_CANCEL_OPTION);
				if(state == JOptionPane.OK_OPTION){
					importBtn.setEnabled(false);
					
					Thread thread = new Thread(new Runnable(){

						public void run() {
							try {
								RedissonUtil.writeMapData(selectedEnv, redisMapKey, new File(outPath));
								outArea.setText("导入完成！！！");
								JOptionPane.showMessageDialog(PropertyManagerPanel.this, "导入成功", "成功提示", JOptionPane.INFORMATION_MESSAGE);
							} catch (Exception e1) {
								e1.printStackTrace();
								outArea.setText(e1.getMessage());
								JOptionPane.showMessageDialog(PropertyManagerPanel.this, "导入数据失败 !!!", "错误提示", JOptionPane.ERROR_MESSAGE);
							}finally{
								importBtn.setEnabled(true);
							}
						}
						
					});
					
					thread.start();
					
				}
				
			}
		});

		return importBtn;
		
	}
	
	
	/**
	 * 创建环境选择栏目
	 * @return
	 */
	private JPanel createEnvPanel(){
		
		JPanel envPanel = new JPanel(new GridLayout(0, 2));
		
		JLabel titleLabel = new JLabel("选择环境:");
		titleLabel.setBorder(sameBorder);
		
		final ButtonGroup envRadioGroup = new ButtonGroup();
		
		JPanel radioButtonWrap = new JPanel(new GridLayout(0,2));
		
		for(final ENV env : ENV.values()){
			JRadioButton radioButton = new JRadioButton(env.getName());
			radioButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedEnv = env;//设置环境
					System.out.println("当前选中环境：" + env);
					
					if(selectedProjectRadio != null){
						selectedProjectRadio.doClick();
					}
					
				}
				
			});
			
			envRadioGroup.add(radioButton);
			radioButtonWrap.add(radioButton);
		}
		
		envPanel.add(titleLabel);
		envPanel.add(radioButtonWrap);
		
		//选择第一个
		envRadioGroup.getElements().nextElement().doClick();
		
		return envPanel;
	}
	
	
	/**
	 * 创建项目选择按钮
	 * @return
	 */
	private JPanel createProjectPanel(final JTextField redisMapKeyField, final JTextField pathField){

		JPanel projectPanel = new JPanel(new GridLayout(0,2));
		JLabel titleLabel = new JLabel("项目选择:");
		titleLabel.setBorder(sameBorder);
		
		JPanel radioButtonWrap = new JPanel(new GridLayout(0,2));
		
		final ButtonGroup projectRadioGroup = new ButtonGroup();
		
		for(final ProjectType projectType: ProjectType.values()){
			final JRadioButton  projectButton = new JRadioButton(projectType.getName());
			
			projectButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					ProjectConfig config = projectType.getConfig();
					redisMapKeyField.setText(config.getRedisKey());
					String outFile = config.getOutFile(selectedEnv);
					
					pathField.setText(outFile);
					
					selectedProjectRadio = projectButton;
				}
			});
			
			projectRadioGroup.add(projectButton);
			radioButtonWrap.add(projectButton);
		}
		
		projectRadioGroup.getElements().nextElement().doClick();
		
		projectPanel.add(titleLabel);
		projectPanel.add(radioButtonWrap);
		
		return projectPanel;
		
	}
	
	
	
}

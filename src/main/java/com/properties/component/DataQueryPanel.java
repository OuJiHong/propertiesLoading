package com.properties.component;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import com.properties.other.MysqlDBUtils;

/**
 * 数据查询面板
 * @author OJH
 *
 */
public class DataQueryPanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public DataQueryPanel() {
		
		JLabel queryEditLabel = new JLabel("查询语句：");
		final JButton queryButton = new JButton("查询");
		final JTextArea queryEditArea = new JTextArea();
		JScrollPane editAreaScrollPane = new JScrollPane(queryEditArea);
		
		
		JPanel queryToolPanel = new JPanel(new GridLayout(0,2));
		queryToolPanel.add(queryEditLabel);
		queryToolPanel.add(queryButton);
		
		JPanel queryPanel = new JPanel(new BorderLayout());
		queryPanel.add(queryToolPanel, BorderLayout.NORTH);
		queryPanel.add(editAreaScrollPane, BorderLayout.CENTER);
		
		
		final JPanel resultPanel = new JPanel(new GridLayout(0,1));
		JLabel resultLabel = new JLabel("点击“查询”显示结果");
		resultLabel.setHorizontalAlignment(JLabel.CENTER);
		resultPanel.add(resultLabel);
		
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(queryPanel);
		splitPane.setBottomComponent(resultPanel);
		splitPane.setDividerLocation(80);
		
		
		
		//按钮处理
		queryButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String queryText = queryEditArea.getText();
				if(queryText == null || queryText.length() == 0){
					JOptionPane.showMessageDialog(DataQueryPanel.this, "请输入需要查询的sql语句", "提示信息", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				queryButton.setEnabled(false);
				Connection connection = null;
				Vector<Vector<Object>> rowData = new Vector<Vector<Object>>();
				Vector<String> columnNames = new Vector<String>();
				
				int maxRowCount = 30;
				
				try{
					connection = MysqlDBUtils.getConn();
					PreparedStatement preparedStatement = connection.prepareStatement(queryText);
					ResultSet resultSet = preparedStatement.executeQuery();
					ResultSetMetaData metaData = resultSet.getMetaData();
					for(int i = 1; i <= metaData.getColumnCount(); i++ ){
						columnNames.add(metaData.getColumnName(i));
					}
					
					int rowCount = 0;
					while(resultSet.next()){
						Vector<Object> row = new Vector<Object>();
						row.add(resultSet.getRow());
						for(String name : columnNames){
							Object val = resultSet.getObject(name);
							row.add(val);
						}
						rowData.add(row);
						rowCount++;
						if(rowCount >= maxRowCount){
							break;
						}
					}
					
					//补充行号
					columnNames.add(0, "(行号)");
					resultSet.close();
					preparedStatement.close();
					
					System.out.println("查询获取结果：" + rowCount);
					//设置表格
					JTable table = new JTable(rowData, columnNames);
					table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					resultPanel.removeAll();
					resultPanel.add(new JScrollPane(table));
				}catch(Exception e1){
					e1.printStackTrace();
					//显示错误
					JLabel label = new JLabel(e1.getMessage());
					resultPanel.removeAll();
					resultPanel.add(new JScrollPane(label));
				}finally{
					if(connection != null){
						try {
							connection.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
							String errMsg = "close connection failed:" + e1.getMessage();
							JOptionPane.showMessageDialog(DataQueryPanel.this, errMsg, "提示信息", JOptionPane.ERROR_MESSAGE);
						}
					}
					queryButton.setEnabled(true);
				}
				
				
				resultPanel.updateUI();
				
			}
			
		});
		
		
		this.setName("sql查询");
		this.setLayout(new BorderLayout());
		this.add(splitPane);
		
	}
	
	
	
}

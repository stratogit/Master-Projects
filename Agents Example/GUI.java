

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class GUI extends JFrame {

	private JPanel contentPane;
	private JTextField agentsToAttackTxtField;
	private JTextField ipTxtField;
	private JTextField portTxtField;
	private JTextArea logTxtArea;
	private JTextField fibTxtField;
	private AgentArchitect agentArchitect;
	private JButton btnAttack;
	private JLabel lblTickerTime;
	private JTextField tickerTimeTxtField;
	private JButton btnStop;
	/**
	 * Create the frame.
	 */
	public GUI(AgentArchitect agentArchitect2) {
		this.agentArchitect = agentArchitect2;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 479, 396);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnAttack = new JButton("Attack");
		btnAttack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				logTxtArea.setText("");
				sendLog("Creating agents...");

				int numAttackers = Integer.parseInt(agentsToAttackTxtField.getText());
				agentArchitect.attack(numAttackers, ipTxtField.getText(), portTxtField.getText(), tickerTimeTxtField.getText(),fibTxtField.getText());
				attackOccurring();		
			}
		});
		btnAttack.setBounds(15, 330, 117, 25);
		contentPane.add(btnAttack);
		
		agentsToAttackTxtField = new JTextField();
		agentsToAttackTxtField.setText("10");
		agentsToAttackTxtField.setBounds(15, 22, 114, 19);
		contentPane.add(agentsToAttackTxtField);
		agentsToAttackTxtField.setColumns(10);
		
		ipTxtField = new JTextField();
		ipTxtField.setText("192.168.201.2");
		ipTxtField.setBounds(15, 71, 298, 19);
		contentPane.add(ipTxtField);
		ipTxtField.setColumns(10);
		
		portTxtField = new JTextField();
		portTxtField.setText("40001");
		portTxtField.setBounds(346, 71, 104, 19);
		contentPane.add(portTxtField);
		portTxtField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Num of attacking agents");
		lblNewLabel.setBounds(12, 0, 182, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblIp = new JLabel("IP");
		lblIp.setBounds(15, 53, 70, 15);
		contentPane.add(lblIp);
		
		JLabel lblPort = new JLabel("PORT");
		lblPort.setBounds(336, 53, 70, 15);
		contentPane.add(lblPort);
		
		JLabel lblLog = new JLabel("Log");
		lblLog.setBounds(15, 102, 70, 15);
		contentPane.add(lblLog);
		
		logTxtArea = new JTextArea();
		logTxtArea.setEditable(false);
		logTxtArea.setBounds(15, 119, 438, 199);
		logTxtArea.setLineWrap(true);
		contentPane.add(logTxtArea);
		
		JLabel  lblFibonnaciNumberTo = new JLabel("Fibonnaci number");
		lblFibonnaciNumberTo.setBounds(199, 0, 136, 15);
		contentPane.add(lblFibonnaciNumberTo);
		
		fibTxtField = new JTextField();
		fibTxtField.setText("10");
		fibTxtField.setBounds(199, 22, 114, 19);
		contentPane.add(fibTxtField);
		fibTxtField.setColumns(10);
		
		lblTickerTime = new JLabel("Ticker Time(ms)");
		lblTickerTime.setBounds(336, 0, 117, 15);
		contentPane.add(lblTickerTime);
		
		tickerTimeTxtField = new JTextField();
		tickerTimeTxtField.setText("100");
		tickerTimeTxtField.setBounds(346, 22, 104, 19);
		contentPane.add(tickerTimeTxtField);
		tickerTimeTxtField.setColumns(10);
		
		btnStop = new JButton("Stop");
		btnStop.setBounds(15, 330, 117, 25);
		contentPane.add(btnStop);
		btnStop.setVisible(false);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				agentArchitect.stopAttack();
			}
		});
	}
	public void sendLog(String log)
	{
		logTxtArea.setText(logTxtArea.getText() + log + '\n');
	}
	public void attackOccurring()
	{
		btnAttack.setVisible(false);
		btnStop.setVisible(true);
	}
	public void attackFinished(long attackDuration)
	{
		sendLog("Attack finished");
		sendLog("Report...");
		sendLog("IP Attacked: " + ipTxtField.getText());
		sendLog("Port: " +  portTxtField.getText());
		sendLog("Agents used: " + agentsToAttackTxtField.getText());
		sendLog("Fibonnacci to calculate:" + fibTxtField.getText());
		sendLog("Duration of Attack (ms):" + attackDuration);
		btnAttack.setVisible(true);
		btnStop.setVisible(false);
	}
}

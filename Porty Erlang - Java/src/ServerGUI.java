import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.List;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.example.tutorial.AddressBookProtos.Person;
import com.example.tutorial.AddressBookProtos.Person.PhoneNumber;
import com.example.tutorial.AddressBookProtos.AddressBook;


public class ServerGUI extends JFrame implements ActionListener{
	private JTextField idField, nameField, emailField, phoneField1, phoneField2, phoneField3;
	private JComboBox phoneBox1, phoneBox2, phoneBox3;
	private JTextArea displayArea;
	private JButton addButton, showButton;

	class WindowHandler extends WindowAdapter{
		public void windowClosing(WindowEvent e){
			try{
				System.out.write(encodeLength(5));
				System.out.write("close".getBytes());
				System.out.flush();
			}catch(IOException ex){
				ex.printStackTrace();
			}
			ServerGUI.this.setVisible(false);
			ServerGUI.this.dispose();
			System.exit(0);
		}
	}
	public ServerGUI(){
		super("Addressbook");
		setLayout(new GridLayout(2,0));
		this.addWindowListener(new WindowHandler());
		JTextField textField;
		JPanel subPanel = new JPanel();
		JPanel subSubPanel;
		subPanel.setLayout(new GridLayout(8,0));
		add(subPanel);
		subSubPanel = new JPanel();
		subSubPanel.setLayout(new GridLayout(0,2));
		textField = new JTextField("Id");
		textField.setEditable(false);
		subSubPanel.add(textField);
		idField = new JTextField(20);
		subSubPanel.add(idField);
		subPanel.add(subSubPanel);
		
		subSubPanel = new JPanel();
		subSubPanel.setLayout(new GridLayout(0,2));
		textField = new JTextField("Name");
		textField.setEditable(false);
		subSubPanel.add(textField);
		nameField = new JTextField(40);
		subSubPanel.add(nameField);
		subPanel.add(subSubPanel);
		
		subSubPanel = new JPanel();
		subSubPanel.setLayout(new GridLayout(0,2));
		textField = new JTextField("Email");
		textField.setEditable(false);
		subSubPanel.add(textField);
		emailField = new JTextField(40);
		subSubPanel.add(emailField);
		subPanel.add(subSubPanel);
		
		Object[] phoneTypes = new Object[]{Person.PhoneType.HOME, Person.PhoneType.WORK,
				Person.PhoneType.MOBILE};
		
		subSubPanel = new JPanel();
		subSubPanel.setLayout(new GridLayout(0,3));
		textField = new JTextField("Phone 1");
		textField.setEditable(false);
		subSubPanel.add(textField);
		phoneField1 = new JTextField(20);
		subSubPanel.add(phoneField1);
		phoneBox1 = new JComboBox(new Object[]{Person.PhoneType.HOME, Person.PhoneType.WORK,
				Person.PhoneType.MOBILE});
		subSubPanel.add(phoneBox1);
		subPanel.add(subSubPanel);
		
		subSubPanel = new JPanel();
		subSubPanel.setLayout(new GridLayout(0,3));
		textField = new JTextField("Phone 2");
		textField.setEditable(false);
		subSubPanel.add(textField);
		phoneField2 = new JTextField(20);
		subSubPanel.add(phoneField2);
		phoneBox2 = new JComboBox(phoneTypes);
		subSubPanel.add(phoneBox2);
		subPanel.add(subSubPanel);
		
		subSubPanel = new JPanel();
		subSubPanel.setLayout(new GridLayout(0,3));
		textField = new JTextField("Phone 3");
		textField.setEditable(false);
		subSubPanel.add(textField);
		phoneField3 = new JTextField(20);
		subSubPanel.add(phoneField3);
		phoneBox3 = new JComboBox(phoneTypes);
		subSubPanel.add(phoneBox3);
		subPanel.add(subSubPanel);
		
		
		addButton = new JButton("Add");
		subPanel.add(addButton);
		addButton.addActionListener(this);
		showButton = new JButton("Show book");
		subPanel.add(showButton);
		showButton.addActionListener(this);
		displayArea = new JTextArea();
		displayArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(displayArea);
		add(scrollPane);
		pack();
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == addButton){
			Person person = serialize();
			try{
				System.out.write(encodeLength(3));
				System.out.write("add".getBytes());
				System.out.flush();
				byte[] personBytes = person.toByteArray();
				System.out.write(encodeLength(personBytes.length));
				System.out.write(personBytes);
				System.out.flush();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		} else if(e.getSource() == showButton){
			try{
				System.out.write(encodeLength(4));
				System.out.write("show".getBytes());
				System.out.flush();
				PushbackInputStream is = new PushbackInputStream(System.in);
				int b1, b2, b3, b4;
				while((b1 = is.read()) == -1){ b1 = b1; }
				while((b2 = is.read()) == -1){ b2 = b2; }
				while((b3 = is.read()) == -1){ b3 = b3; }
				while((b4 = is.read()) == -1){ b4 = b4; }
				int l = decodeLength(b1, b2, b3, b4);
				byte[] message = new byte[l];
				int b;
				for(int i=0; i<l; ++i){
					while((b = is.read()) == -1){ b = b; }
					message[i] = (byte)b;
				}
				AddressBook book = AddressBook.parseFrom(message);
				String text = "";
				for(Person person : book.getPersonList()){
					text += formatPerson(person) + "\n";
				}
				displayArea.setText(text);
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	public Person serialize(){
		Person.Builder person = Person.newBuilder();
	    person.setId(Integer.parseInt(idField.getText()));
	    person.setName(nameField.getText());
	    if(!emailField.getText().equals(""))
	    	person.setEmail(emailField.getText());
	    if(!phoneField1.getText().equals("")){
	    	Person.PhoneNumber.Builder phoneNumber1 =
	    		Person.PhoneNumber.newBuilder().setNumber(phoneField1.getText());
	    	phoneNumber1.setType(Person.PhoneType.HOME);
	    	phoneNumber1.setType((Person.PhoneType)(phoneBox1.getSelectedItem()));
	    	person.addPhone(phoneNumber1);
	    }
	    if(!phoneField2.getText().equals("")){
	    	Person.PhoneNumber.Builder phoneNumber2 =
	    	    Person.PhoneNumber.newBuilder().setNumber(phoneField2.getText());
	    	phoneNumber2.setType((Person.PhoneType)phoneBox2.getSelectedItem());
	    	person.addPhone(phoneNumber2);
	    }
	    if(!phoneField3.getText().equals("")){
	    	Person.PhoneNumber.Builder phoneNumber3 =
	    	        Person.PhoneNumber.newBuilder().setNumber(phoneField3.getText());
	    	phoneNumber3.setType((Person.PhoneType)phoneBox3.getSelectedItem());
	    	person.addPhone(phoneNumber3);
	    }
	    return person.build();
	}
	public static String formatPerson(Person person){
		String formatted = "ID: " + person.getId() + "\nName: " + person.getName() +
				"\nEmail: " + person.getEmail() + "\nPhones: ";
		for(PhoneNumber phone : person.getPhoneList()){
			formatted += " " + phone.getNumber() + "(" + phone.getType() + ")";
		}
		formatted += "\n";
		return formatted;
	}
	public static byte[] encodeLength(int x){
		return new byte[] {
			(byte)(x >>> 24),
			(byte)(x >>> 16),
	        (byte)(x >>> 8),
	        (byte)x
		};
	}
	public static int decodeLength(int b1, int b2, int b3, int b4){
		return ((int)(b1) << 24) + ((int)(b2) << 16) + ((int)(b3) << 8) + (int)(b4);
	}
	public static void main(String[] args){
		ServerGUI test = new ServerGUI();
	}
}

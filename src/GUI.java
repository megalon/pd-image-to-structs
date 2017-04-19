import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GUI extends JPanel implements ActionListener{

	Converter converter;
	
	JPanel buttonPanel, sizePanel;
	JButton openButton, saveButton, convertButton;
	JLabel sizeLabel;
	JTextField sizeField;
	JTextArea console;
	JFileChooser fileChooser;
	
	String pathToImage, pathToPDFile;
	int pixelSize;
	
	public GUI(){
		fileChooser = new JFileChooser();
		
		console = new JTextArea(10, 40);
		console.setEditable(false);
		JScrollPane consoleScrollPane = new JScrollPane(console); 

		openButton = new JButton("Path to image file");
		openButton.addActionListener(this);
		
		saveButton = new JButton("Path to pd file");
		saveButton.addActionListener(this);
		
		convertButton = new JButton("Convert!");
		convertButton.addActionListener(this);
		
		
		
		// Alignement
		Dimension buttonSize = new Dimension(200, 30);
		openButton.setPreferredSize(buttonSize);
		//openButton.setAlignmentY(CENTER_ALIGNMENT);
		saveButton.setPreferredSize(buttonSize);
		//saveButton.setAlignmentY(CENTER_ALIGNMENT);
		convertButton.setPreferredSize(buttonSize);
		//convertButton.setAlignmentY(CENTER_ALIGNMENT);
		
		sizePanel = new JPanel();
		
		sizeField = new JTextField("1", 3);
		sizeLabel = new JLabel("Pixel size:");

		sizePanel.add(sizeLabel);
		sizePanel.add(sizeField);
		

		Dimension minSize = new Dimension(0, 10);
		Dimension prefSize = new Dimension(0, 10);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setMinimumSize(new Dimension(50, 200));
		buttonPanel.setPreferredSize(new Dimension(200, 150));
		buttonPanel.add(openButton);
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));
		buttonPanel.add(saveButton);
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));
		buttonPanel.add(sizePanel);
		buttonPanel.add(new Box.Filler(minSize, prefSize, maxSize));
		buttonPanel.add(convertButton);
		
		
		this.add(buttonPanel, new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(consoleScrollPane, new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		pathToImage = null;
		pathToPDFile = null;
		pixelSize = 1;
		
		converter = new Converter();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// Open button
        if (e.getSource() == openButton) {
            int returnVal = fileChooser.showOpenDialog(GUI.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                
                console.append("Image: " + file.getName() + "." + "\n");
                
                pathToImage = file.getAbsolutePath();
            } else {
            	console.append("Please select an image to open." + "\n");
            }
            
            console.setCaretPosition(console.getDocument().getLength());
        }
        
		// Save button
        if (e.getSource() == saveButton) {
            int returnVal = fileChooser.showOpenDialog(GUI.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                console.append("PD file: " + file.getName() + "." + "\n");
                
                pathToPDFile = file.getAbsolutePath();
            } else {
            	console.append("Please select where to save the PD file." + "\n");
            }
            
            console.setCaretPosition(console.getDocument().getLength());
        }
        
		// Convert button
        if (e.getSource() == convertButton) {

        	String text = sizeField.getText();
        	
        	try{
        		pixelSize = Integer.parseInt(text);
        		
        		if(pixelSize < 1){
        			throw new NumberFormatException();
        		}
            	
            	BufferedImage img = converter.openImageFile(pathToImage);
            	
            	if(img != null){
                    console.append("Converting:" + pathToImage + "..." + "\n");
            		converter.writePDFile(img, pathToPDFile, pixelSize);
                    console.append("Finished converting. Output to pd file " + pathToPDFile + "\n\n");
            	}else{
                    console.append("ERROR: Could not read image file: " + pathToImage + "." + "\n");
            	}
        	}catch (NumberFormatException e1){
                console.append("ERROR: Pixel size must be an integer >= 1!\n");
                pixelSize = 1;
                sizeField.setText("1");
        	}
            
            console.setCaretPosition(console.getDocument().getLength());
        }
	}

	public void showGUI(){
        JFrame frame = new JFrame("Image to Struct converter!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new GUI());

        frame.pack();
        frame.setVisible(true);
	}
	
}

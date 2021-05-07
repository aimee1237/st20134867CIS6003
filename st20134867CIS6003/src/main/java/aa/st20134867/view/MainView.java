package aa.st20134867.view;

import aa.st20134867.algorithms.Knn;
import aa.st20134867.controller.MainControl;
import aa.st20134867.file.MultiReader;
import aa.st20134867.image.ImageModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainView {

    private static int MAX_IMAGES = 10000;
    // Declaring main view components
    private JFrame mainWindow;
    private JButton openFileBtn;
    private JTextField fileNameTxt;
    private JLabel fileNameLbl;
    private JLabel kValueLbl;
    private JTextField kValueTxt;
    private JButton grayScaleBtn;
    private JButton rgbButton;
    private JButton edgeButton;
    private JButton classificationBtn;
    private JPanel displayPanel;
    private JLabel imageLabel;
    private List<JLabel> imageLabelList;
    private JFileChooser fileChooser;


    MainControl controller;
    List<ImageModel> imageModelList;

    // Default constructor
    public MainView() {
        imageModelList = new ArrayList<>();
        controller = new MainControl(null);

        mainWindow = new JFrame("Aimee Alexander - ST20134867");
        mainWindow.setLayout(new BorderLayout());

        JPanel fileSelectPanel = new JPanel();

        fileNameLbl = new JLabel("File name: ");
        fileNameTxt = new JTextField(50);
        fileNameTxt.setEnabled(false);
        openFileBtn = new JButton("Open File");

        kValueTxt = new JTextField(2);
        kValueLbl = new JLabel("Insert k value: ");

        // Add components to the JPanel
        fileSelectPanel.add(fileNameLbl);
        fileSelectPanel.add(fileNameTxt);
        fileSelectPanel.add(openFileBtn);

        // Add JPanel to the JFrame's north position
        mainWindow.add(fileSelectPanel, BorderLayout.PAGE_START);

        // Creating main operation button panel
        JPanel opButtonPanel = new JPanel();

        rgbButton = new JButton("RGB Image");
        grayScaleBtn = new JButton("Grayscale Image");
        edgeButton = new JButton("Edge Image");
        classificationBtn = new JButton("Start classification");

        enableControls(false);

        opButtonPanel.add(rgbButton);
        opButtonPanel.add(grayScaleBtn);
        opButtonPanel.add(edgeButton);
        opButtonPanel.add(kValueLbl);
        opButtonPanel.add(kValueTxt);
        opButtonPanel.add(classificationBtn);

        // A JPanel with box layout
        JPanel northLayout = new JPanel();
        northLayout.setLayout(new BoxLayout(northLayout, BoxLayout.Y_AXIS));

        // add both flow layouts to northLayout
        northLayout.add(fileSelectPanel);
        northLayout.add(opButtonPanel);

        // add northLayout to mainWindow's north
        mainWindow.add(northLayout, BorderLayout.NORTH);

        // instantiate a new JPanel to hold image labels
        displayPanel = new JPanel();
        displayPanel.setPreferredSize(new Dimension(500, 500));
        displayPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

        // adding multiple JLabel to allow for display of multiple images from a file
        imageLabelList = new ArrayList<>();
        for (int i = 0; i < MAX_IMAGES; i++) {
            imageLabel = new JLabel();
            imageLabelList.add(imageLabel);
            displayPanel.add(imageLabel);
        }

        // add image panel to the centre of the main window
        mainWindow.add(displayPanel, BorderLayout.CENTER);

        // Make the program quit, as the window is closed
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the width and height of mainWindow
        // mainWindow.setSize(new Dimension(200, 100));;
        mainWindow.pack();
        // Display the window.
        mainWindow.setVisible(true);


        //Creating event handler method for the buttons to be able to open file directory
        openFileBtn.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {

                File selectedFile = showFileChooserDialog();
                //File selectedFile = new File("data_batch_1.bin");
                if (selectedFile != null) {
                    clearImages();
                    enableControls(true);
                    fileNameTxt.setText(selectedFile.getAbsolutePath());
                    try {
                        if (selectedFile.getAbsolutePath().endsWith("bin")) {
                            MultiReader multiReader = new MultiReader(selectedFile);
                            multiReader.read();
                            imageModelList = multiReader.getData();
                        } else {
                            imageModelList.clear();
                            imageModelList.add(new ImageModel(selectedFile));
                        }
                    } catch (IOException e1) {

                        enableControls(false);
                        System.out.println("IO Exception in "
                                + "imageLoad(); " + e.toString());

                        JOptionPane.showMessageDialog(null,
                                "Error in loading the selected image!",
                                "Image Error",
                                JOptionPane.ERROR_MESSAGE);
                        fileNameTxt.setText("");
                    }
                } else{
                    //enableControls(false);
                    //fileNameTxt.setText("No file selected");
                }

            }

            private File showFileChooserDialog() {

                fileChooser = new JFileChooser();
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp", "png", "bin"));
                fileChooser.setAcceptAllFileFilterUsed(false);

                fileChooser.setCurrentDirectory(new
                        File(System.getProperty("user.home")));
                int status = fileChooser.showOpenDialog(mainWindow);

                File selected_file = null;
                if (status == JFileChooser.APPROVE_OPTION) {
                    selected_file = fileChooser.getSelectedFile();
                }
                return selected_file;

            }
        });

        //Creating action listener to recognise action events when buttons are clicked
        rgbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < imageModelList.size() && i < MAX_IMAGES; i++) {
                    ImageModel imgModel = imageModelList.get(i);

                    BufferedImage img = imgModel.getRGBImage();
                    if (img != null) {
                        displayImage(i, img);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "No image file is choosen",
                                "Image error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        grayScaleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < imageModelList.size() && i < MAX_IMAGES; i++) {
                    ImageModel imgModel = imageModelList.get(i);
                    BufferedImage img = imgModel.getGrayscaleImage();
                    if (img != null)
                        displayImage(i, img);
                    else {
                        JOptionPane.showMessageDialog(null,
                                "Error in loading the grayscale image",
                                "Image error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        edgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < imageModelList.size() && i < MAX_IMAGES; i++) {
                    ImageModel imgModel = imageModelList.get(i);
                    BufferedImage img = imgModel.getEdgeImage();
                    if (img != null)
                        displayImage(i, img);
                    else {
                        JOptionPane.showMessageDialog(null,
                                "Error in loading the edge image",
                                "Image error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        classificationBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Integer k = getInteger(kValueTxt.getText());
                if (k !=null && k>0 && k <=10 ) {
                    JOptionPane.showMessageDialog(null,
                            "Ready to start.  kValue " + kValueTxt.getText(),
                            "Start Classification",
                            JOptionPane.INFORMATION_MESSAGE);

                    Knn knn = new Knn(k, imageModelList,imageModelList.get(0));
                    knn.preprocessdata();
                    knn.classify();

                } else {
                    JOptionPane.showMessageDialog(null,
                            "kValue must be an integer between 1 and 10",
                            "Start Classification",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                kValueTxt.requestFocusInWindow();
            }
        });

        kValueTxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("The entered number is: " + kValueTxt.getText());
            }
        });
    }

    private void clearImages() {
        for (JLabel image: imageLabelList){
            image.setIcon(null);
        }
    }

    private void displayImage(int position, BufferedImage img) {
        imageLabelList.get(position).setIcon(new ImageIcon(img));
    }

    private void displayImage(BufferedImage img) {
        displayImage(0, img);
    }

    private Integer getInteger(String strNum) {
        Integer i;
        if (strNum == null) {
            return null;
        }
        try {
            i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return null;
        }
        return i;
    }

    private void enableControls(boolean flag){
        rgbButton.setEnabled(flag);
        edgeButton.setEnabled(flag);
        grayScaleBtn.setEnabled(flag);
        kValueTxt.setEnabled(flag);
        classificationBtn.setEnabled(flag);
    }

}
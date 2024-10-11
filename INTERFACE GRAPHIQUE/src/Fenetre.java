import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class Fenetre extends JFrame {
    public Fenetre(){
        super("Cryptage Triple DES");
        this.setSize(1200,600);
        this.setLocationRelativeTo(null); 
		this.setLayout(new BoxLayout(this.getContentPane() ,BoxLayout.Y_AXIS ));

        JButton selection_fichier = new JButton("Choississez un fichier"); 
        this.add(selection_fichier);
        selection_fichier.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser file_chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers texte (.txt)", "txt");
                file_chooser.setFileFilter(filter);

                int resultat = file_chooser.showOpenDialog(selection_fichier);                
                if (resultat == JFileChooser.APPROVE_OPTION){
                    File fichier_selectionner = file_chooser.getSelectedFile();
                    try {
                        System.out.println(Files.readString(fichier_selectionner.toPath()));
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
            
        });
        
        
        this.setVisible(true);
    }

    public static void main(String[] args) {
        Fenetre f = new Fenetre();
    }
}

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Fenetre extends JFrame {
    public Fenetre(){
        super("Cryptage Triple DES");
        this.setSize(1200,600);
		this.setLayout(new GridLayout(1,3));
        JButton selectionFichier = new JButton("Choississez un fichier"); 
        this.add(selectionFichier);
        selectionFichier.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
    
            }
            
        };
        
        
        this.setVisible(true);
    }

    public static void main(String[] args) {
        Fenetre f = new Fenetre();
    }
}

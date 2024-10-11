import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;


public class Fenetre extends JFrame {
    TripleDes triple_des = new TripleDes();
    Des des = new Des();
    String texte;

    public Fenetre(){
        super("Cryptage Triple DES");
        this.setSize(1200,600);
        this.setLocationRelativeTo(null); 
		this.setLayout(new BoxLayout(this.getContentPane() ,BoxLayout.Y_AXIS ));
        
        JButton selection_fichier = new JButton("Choississez un fichier"); 
        this.add(selection_fichier);
   
        Fenetre fenetre = this;
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
                        texte = Files.readString(fichier_selectionner.toPath());
                        fenetre.addButtonCryptage(texte);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                
            }
            
        });
        
        this.setVisible(true);
    }

    /**
     * Ajouter le bouton pour crypter ou décrypter 
     * @param texte
     */
    private void addButtonCryptage(String texte){
        JButton bouton_cryptage = des.estBinaire(texte) ? new JButton("décrypter") : new JButton("crypter");
        this.add(bouton_cryptage);
        bouton_cryptage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (bouton_cryptage.getText().equals("crypter")) {
                    System.out.println(texte);
                    String texte_crypter = triple_des.crypte(texte);
                    System.out.println(texte_crypter.length());
                    showDialog("Message crypter",texte_crypter);
                    saveFile(texte_crypter);
                }
                else{
                    System.out.println(texte.equals(triple_des.crypte("s")));
                    /*String texte_decrypter = triple_des.decrypte(texte);
                    showDialog("Message décrypter",texte_decrypter);
                    saveFile(texte_decrypter);*/
                }
            }
        });
        this.setVisible(true);

    }

    /**
     * Afficher une boite de dialogue
     * @param titre
     * @param contenu
     */
    private void showDialog(String titre, String contenu){
        JFrame frame = new JFrame(); 
        JDialog dialogue = new JDialog(frame , titre, true);  
        JTextArea textarea = new JTextArea(contenu);
        dialogue.add(textarea);
        dialogue.pack(); 
        dialogue.setLocationRelativeTo(null);
        dialogue.setVisible(true);
    }

    private void saveFile(String texte){
        try {
            FileWriter file_writer = des.estBinaire(texte) ? new FileWriter("fichier_crypté.txt") : new FileWriter("fichier_décrypté.txt");
            for (int i = 0; i < texte.length(); i++) {
                file_writer.write(texte.charAt(i));
            }
            file_writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args) {
        Fenetre f = new Fenetre();
       System.out.println(f.triple_des.decrypte(f.triple_des.crypte("s")));
    }
}

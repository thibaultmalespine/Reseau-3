import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Fenetre extends JFrame {
    TripleDes triple_des = new TripleDes(Arrays.asList(MasterKey.createMasterKey(), MasterKey.createMasterKey(), MasterKey.createMasterKey()));

    public Fenetre(){
        super("Cryptage Triple DES");
        this.setSize(600,300);
        this.setLocationRelativeTo(null); 

        // Utilisation de GridBagLayout pour centrer les boutons
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = -1; // Position horizontale
        gbc.gridy = 0; // Position verticale
        gbc.insets = new Insets(10, 10, 10, 10); // Espacement entre les boutons

        // Création des boutons "Crypter" et "Décrypter"
        JButton bouton_crypté = new JButton("Crypter");
        JButton bouton_decrypté = new JButton("Décrypter");
        
        // Ajout du bouton Crypter au layout
        this.add(bouton_crypté, gbc);
        
        // Déplacer gbc.gridy pour positionner le deuxième bouton plus bas
        gbc.gridx = 1;
        this.add(bouton_decrypté, gbc);

        bouton_crypté.addActionListener(actionListenerBoutonCrypté(this, bouton_crypté));
        
        this.setVisible(true);
    }

    /**
     * Ajouter le bouton pour crypter ou décrypter 
     * @param texte
     */
    private void addButtonCryptage(String texte){
        JButton bouton_cryptage = ChaineBinaire.estBinaire(texte) ? new JButton("décrypter") : new JButton("crypter");
        this.add(bouton_cryptage);
        bouton_cryptage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (bouton_cryptage.getText().equals("crypter")) {
                    String texte_crypter = triple_des.crypte(texte);
                    showDialog("Message crypter",texte_crypter);
                    saveFile(texte_crypter);
                }
                else{
                    String texte_decrypter = triple_des.decrypte(texte);
                    showDialog("Message décrypter",texte_decrypter);
                    saveFile(texte_decrypter);
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
            FileWriter file_writer = ChaineBinaire.estBinaire(texte) ? new FileWriter("fichier_crypté.txt") : new FileWriter("fichier_décrypté.txt");
            for (int i = 0; i < texte.length(); i++) {
                file_writer.write(texte.charAt(i));
            }
            file_writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ActionListener actionListenerBoutonCrypté(Fenetre fenetre, JButton bouton_crypté){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser file_chooser = new JFileChooser();
                file_chooser.setCurrentDirectory(new File("./"));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers texte (.txt)", "txt");
                file_chooser.setFileFilter(filter);

                int resultat = file_chooser.showOpenDialog(bouton_crypté);  
                            
                if (resultat == JFileChooser.APPROVE_OPTION){
                    try {
                        String texte="";
                        for (String ligne : Files.readAllLines(Paths.get(file_chooser.getSelectedFile().getAbsolutePath()))) {
                            texte += ligne;
                        }
                        String texte_crypter = triple_des.crypte(texte);
                        showDialog("Message crypter",texte_crypter);
                        
                    } catch (IOException io_exception) {
                        System.out.println(io_exception);
                        // TODO: handle exception
                    }
                   
                }
                
            }
        };
        
    }

    public static void main(String[] args) {
        Fenetre f = new Fenetre();
    }
}

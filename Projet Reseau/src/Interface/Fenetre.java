package Interface;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import Algo.TripleDes;
import Data.MasterKey;

/**
 * Interface Graphique
 */
public class Fenetre extends JFrame {
    TripleDes triple_des = new TripleDes(Arrays.asList(MasterKey.createMasterKey(), MasterKey.createMasterKey(), MasterKey.createMasterKey()));

    public Fenetre(){
        super("Cryptage Triple DES");
        this.setSize(600,300);
        this.setLocationRelativeTo(null); 

        // Utilisation de GridBagLayout pour centrer les boutons
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = -1;
        gbc.gridy = 0; 
        gbc.insets = new Insets(10, 10, 10, 10); // Espacement entre les boutons

        JButton bouton_crypté = new JButton("Crypter");
        JButton bouton_decrypté = new JButton("Décrypter");
        
        this.add(bouton_crypté, gbc);
        
        gbc.gridx = 1;
        this.add(bouton_decrypté, gbc);

        bouton_crypté.addActionListener(actionListenerBoutonCrypté());
        bouton_decrypté.addActionListener(actionListenerBoutonDecrypté());
        
        this.setVisible(true);
    }

    /**
     * Afficher une boite de dialogue
     * @param titre
     * @param contenu
     */
    private void showDialog(String titre, String contenu){
        this.setVisible(false);
        
        JFrame frame = new JFrame(); 
        JDialog dialogue = new JDialog(frame , titre, true); 

        JTextArea textarea = new JTextArea(contenu);
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(false);
        textarea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textarea);  
        scrollPane.setPreferredSize(new Dimension(800,560));
        dialogue.add(scrollPane);   

        dialogue.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);  
            }
        });
        
        dialogue.pack(); 
        dialogue.setLocationRelativeTo(null);
        dialogue.setVisible(true);
    
        
    }

    /**
     * Enregistrer le texte sous forme de fichier .txt dans le répertoire courant
     * @param titre Le titre du fichier .txt
     * @param texte Le corps du fichier .txt
     */
    private void saveFile(String titre, String texte){
        try {
            FileWriter file_writer = new FileWriter(titre);
            for (int i = 0; i < texte.length(); i++) {
                file_writer.write(texte.charAt(i));
            }
            file_writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Controleur du bouton crypté
     * @return
     */
    private ActionListener actionListenerBoutonCrypté(){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser file_chooser = new JFileChooser();
                file_chooser.setCurrentDirectory(new File("./"));
                file_chooser.setDialogTitle("Choississez le fichier à crypté");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers texte (.txt)", "txt");
                file_chooser.setFileFilter(filter);

                int resultat = file_chooser.showOpenDialog(null);  
                            
                if (resultat == JFileChooser.APPROVE_OPTION){
                    try {
                        String texte="";
                        for (String ligne : Files.readAllLines(Paths.get(file_chooser.getSelectedFile().getAbsolutePath()))) {
                            texte += ligne;
                        }
                        String texte_crypter = triple_des.crypte(texte);
                        saveFile("clé_fichier_crypté.txt", getMasterKeys());
                        saveFile("fichier_crypté.txt",texte_crypter);
                        showDialog("Message crypter",texte_crypter);

                        
                    } catch (IOException io_exception) {
                        System.out.println(io_exception);
                        // TODO: handle exception
                    }
                }
            }
        };
    }

    /**
     * Récupère les master keys utilisé pour le cryptage triple DES
     * @return
     */
    private String getMasterKeys(){
        
        String master_keys = triple_des.masterKey1+"\n";
        master_keys += triple_des.masterKey2+"\n";
        master_keys += triple_des.masterKey3;
        
        return master_keys;
    }

    /**
     * Controleur du bouton decrypté
     * @return
     */
    private ActionListener actionListenerBoutonDecrypté(){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser file_chooser = new JFileChooser();
                file_chooser.setCurrentDirectory(new File("./"));
                file_chooser.setDialogTitle("Choississez le fichier contenant les clés de décryptage");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers texte (.txt)", "txt");
                file_chooser.setFileFilter(filter);

                int resultat = file_chooser.showOpenDialog(null);  
                            
                if (resultat == JFileChooser.APPROVE_OPTION){
                    triple_des = new TripleDes(getMasterKeysFrom( file_chooser.getSelectedFile().getAbsolutePath()));
                    file_chooser.setDialogTitle("Choississez le fichier à décrypté");
                    resultat = file_chooser.showOpenDialog(null);  

                    if (resultat == JFileChooser.APPROVE_OPTION){
                        try {
                            String texte="";
                            for (String ligne : Files.readAllLines(Paths.get(file_chooser.getSelectedFile().getAbsolutePath()))) {
                                texte += ligne;
                            }
                            String texte_decrypter = triple_des.decrypte(texte);
                            saveFile("fichier_decrypté.txt",texte_decrypter);
                            showDialog("Message decrypter",texte_decrypter);
    
                            
                        } catch (IOException io_exception) {
                            System.out.println(io_exception);
                            // TODO: handle exception
                        }
                    }
                   
                }
                
            }
        };
    }

    /**
     * Récupère les master keys stockées dans un fichier .txt 
     * @param chemin_vers_fichier_txt 
     * @return
     */
    private ArrayList<String> getMasterKeysFrom(String chemin_vers_fichier_txt){
        ArrayList<String> master_keys = new ArrayList<>();

        Path path = Paths.get(chemin_vers_fichier_txt);

        try (Stream<String> lignes = Files.lines(path)) {
            lignes.forEach(ligne -> {
                master_keys.add(ligne);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return master_keys;
    }

}

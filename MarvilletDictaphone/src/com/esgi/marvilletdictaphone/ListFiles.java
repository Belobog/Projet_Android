package com.esgi.marvilletdictaphone;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ListFiles extends Activity {
	
	private ListView enregistrements;
	private File repertoire_racine;
	private String[] noms;// = {"","","","",""};
	private MediaPlayer   mPlayer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_files);
		
		enregistrements = (ListView)findViewById(R.id.enregistrements);
	
		repertoire_racine = new File(Environment.getExternalStorageDirectory().toString()+"/marvillet");
		File [] fichiers = repertoire_racine.listFiles();
		noms = new String [fichiers.length];
		for(int i=0; i<fichiers.length;i++)noms[i] = fichiers[i].getName().toString().substring(0,fichiers[i].getName().toString().indexOf("."));
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, noms);
	    enregistrements.setAdapter(adapter);
	    
	    
	    enregistrements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	  @Override
	    	  public void onItemClick(AdapterView<?> adapterView,View view,int position,long id) {
	    		//Log.w("Click", id+" "+noms[position]);  
	    		
	    		
	    		mPlayer = new MediaPlayer();
		        try {
		            mPlayer.setDataSource(Environment.getExternalStorageDirectory().toString()+"/marvillet/"+noms[position]+".3gp");
		            mPlayer.prepare();
		            mPlayer.start();
		        } catch (IOException e) {
		            //Log.e(LOG_TAG, "prepare() failed");
		        }
	    		
	  	    	
	    	  }
	    	});
	    
	    
	    enregistrements.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,View view,int position,long id) {
				Log.w("Click", id+" "+noms[position]);  
				modifier(noms[position]);
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_files, menu);
		return true;
	}
	
	private void modifier(final String fichier) {
		// TODO Auto-generated method stub
		//On instancie notre layout en tant que View
 
		final MediaRecorder enregistrement = new MediaRecorder();
		
		final StringBuilder estCommence = new StringBuilder("N");
		
		
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.modifier_enregistrement, null);
        
        final File file = new File(Environment.getExternalStorageDirectory().toString()+"/marvillet/"+fichier+".3gp");
        
        TextView date_modif = (TextView) alertDialogView.findViewById(R.id.date_modif);
        String date_format = new SimpleDateFormat("dd/MM/yyyy").format(file.lastModified());
        date_modif.setText("Date de derniere modification : "+date_format);
        
        EditText nom_fichier = (EditText) alertDialogView.findViewById(R.id.nom_fichier);
        nom_fichier.setText(fichier);
        
        
        final Chronometer temps = (Chronometer)alertDialogView.findViewById(R.id.temps);
        
        final Button arret = (Button) alertDialogView.findViewById(R.id.arret);
        arret.setOnTouchListener(new OnTouchListener() {
			
			
			public boolean onTouch(View v, MotionEvent event) {
				arret.setBackgroundResource(R.drawable.boutton_arret_press);
				return false;
			}
		});
        
        arret.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(estCommence.charAt(0)=='O'){
					estCommence.deleteCharAt(0);
					estCommence.append('N');
					arret.setBackgroundResource(R.drawable.boutton_arret);
					enregistrement.stop();
					enregistrement.release();
					temps.stop();
				}
				else{
					arret.setBackgroundResource(R.drawable.boutton_arret);
				}
				
				
				
				
			}
		});
        
        
        final Button marche = (Button) alertDialogView.findViewById(R.id.marche);
        marche.setOnTouchListener(new OnTouchListener() {
			
			
			public boolean onTouch(View v, MotionEvent event) {
				marche.setBackgroundResource(R.drawable.boutton_marche_press);
				return false;
			}
		});
        
        marche.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(estCommence.charAt(0)=='N'){
					estCommence.deleteCharAt(0);
					estCommence.append('O');
					marche.setBackgroundResource(R.drawable.boutton_marche);
					temps.setVisibility(1);
					temps.setBase(SystemClock.elapsedRealtime());
					temps.start();
					String fichier_de_sortie = Environment.getExternalStorageDirectory().toString()+"/marvillet/"+fichier+".3gp";
					enregistrement.setAudioSource(MediaRecorder.AudioSource.MIC);
					enregistrement.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					enregistrement.setOutputFile(fichier_de_sortie);
					enregistrement.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

					try {
						enregistrement.prepare();
					} catch (IOException e) {

					}

					enregistrement.start();
				}
				else{
					marche.setBackgroundResource(R.drawable.boutton_marche);
				}
				
				
			}
		});
        
 
        //Création de l'AlertDialog
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
 
        //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
        adb.setView(alertDialogView);
 
        //On donne un titre à l'AlertDialog
        adb.setTitle("Nom du fichier");
 
        //On modifie l'icône de l'AlertDialog pour le fun ;)
        adb.setIcon(android.R.drawable.ic_dialog_alert);
 
        //On affecte un bouton "OK" à notre AlertDialog et on lui affecte un évènement
        adb.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
 
            	//Lorsque l'on cliquera sur le bouton "OK", on récupère l'EditText correspondant à notre vue personnalisée (cad à alertDialogView)
            	EditText valsaisie = (EditText)alertDialogView.findViewById(R.id.nom_fichier);
            	String nom_fichier_nouveau = valsaisie.getText().toString();
            	if(fichier.equalsIgnoreCase(nom_fichier_nouveau)==false){
            		nom_fichier_nouveau = nom_fichier_nouveau.toUpperCase();
					StringBuilder provisoire = new StringBuilder("");

					for(int i=0;i<nom_fichier_nouveau.length();i++){
						if( new String(""+nom_fichier_nouveau.charAt(i)).matches("[A-Z0-9_]")   )provisoire.append(nom_fichier_nouveau.charAt(i));
					}
					nom_fichier_nouveau = provisoire.toString();


					
					File destination = new File(Environment.getExternalStorageDirectory().toString()+"/marvillet/"+nom_fichier_nouveau+".3gp");
					file.renameTo(destination);
					finish();
					startActivity(getIntent());
					
            	}
          } });
        adb.setNegativeButton("Supprimer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	file.delete();
            	finish();
            	startActivity(getIntent());
          } });
      
        adb.show();
    }
	
	
	
	
	

}

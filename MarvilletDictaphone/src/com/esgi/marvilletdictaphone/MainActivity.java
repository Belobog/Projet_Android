package com.esgi.marvilletdictaphone;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;

public class MainActivity extends Activity implements View.OnTouchListener, View.OnClickListener{


	private Button arret;
	private Button marche;
	private Button liste;
	private Chronometer temps;

	private MediaRecorder enregistrement;
	private  String fichier_de_sortie;
	private boolean en_marche = false;

	/*
	private final static int ID_NORMAL_DIALOG = 0;
	private final static int ID_ENERVEE_DIALOG = 1;
	private final static int ENERVEMENT = 4;
	private int compteur = 0;
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		File repertoire_racine = new File(Environment.getExternalStorageDirectory().toString()+"/marvillet");
		if(!repertoire_racine.exists())repertoire_racine.mkdirs();


		arret = (Button)findViewById(R.id.arret);
		marche =(Button)findViewById(R.id.marche);
		liste = (Button)findViewById(R.id.liste);

		temps = (Chronometer)findViewById(R.id.temps);


		arret.setOnTouchListener(this);
		marche.setOnTouchListener(this);
		liste.setOnTouchListener(this);

		arret.setOnClickListener(this);
		marche.setOnClickListener(this);
		liste.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {

		if(arg0.getId() == R.id.arret){
			arret.setBackgroundResource(R.drawable.boutton_arret_press);



		}
		else if(arg0.getId() == R.id.marche){

			marche.setBackgroundResource(R.drawable.boutton_marche_press);





		}
		else if(arg0.getId() == R.id.liste){
			liste.setBackgroundResource(R.drawable.boutton_liste_press);

		}
		return false;
	}

	@Override
	public void onClick(View arg0) {

		if(arg0.getId() == R.id.arret){
			if(en_marche==true){
				stopRecording();
				fileName();

			}
			arret.setBackgroundResource(R.drawable.boutton_arret);	
		}



		else if(arg0.getId() == R.id.marche){

			if(en_marche == false){
				startRecording();
			}
			marche.setBackgroundResource(R.drawable.boutton_marche);

		}



		else if(arg0.getId() == R.id.liste){

			if(en_marche==true){
				stopRecording();
				//fileName();
			}

			liste.setBackgroundResource(R.drawable.boutton_liste);
			Intent intent = new Intent(this,ListFiles.class);
			startActivity(intent);

		}	
	}


	private void stopRecording() {
		en_marche = false;
		enregistrement.stop();
		enregistrement.release();
		enregistrement = null;
		temps.stop();

	}

	private void startRecording() {

		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		String heure = f.format(d);
		en_marche = true;
		temps.setVisibility(1);
		temps.setBase(SystemClock.elapsedRealtime());
		temps.start();
		fichier_de_sortie = Environment.getExternalStorageDirectory().toString()+"/marvillet/REG_"+heure+".3gp";
		enregistrement = new MediaRecorder();
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





	private void fileName() {
		// TODO Auto-generated method stub
		//On instancie notre layout en tant que View


		LayoutInflater factory = LayoutInflater.from(this);
		final View alertDialogView = factory.inflate(R.layout.dialogajoutxml, null);


		final String nom_fichier_format = fichier_de_sortie.substring(fichier_de_sortie.lastIndexOf("/")+1,fichier_de_sortie.indexOf("."));
		EditText valsaisie = (EditText)alertDialogView.findViewById(R.id.EditTextajout);
		valsaisie.setText(nom_fichier_format);


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
				EditText valsaisie = (EditText)alertDialogView.findViewById(R.id.EditTextajout);
				String nom_fichier_nouveau = valsaisie.getText().toString();
				if(nom_fichier_format.equalsIgnoreCase(nom_fichier_nouveau)==false){
					nom_fichier_nouveau = nom_fichier_nouveau.toUpperCase();
					StringBuilder provisoire = new StringBuilder("");

					for(int i=0;i<nom_fichier_nouveau.length();i++){
						if( new String(""+nom_fichier_nouveau.charAt(i)).matches("[A-Z0-9_]")   )provisoire.append(nom_fichier_nouveau.charAt(i));
					}
					nom_fichier_nouveau = provisoire.toString();


					File source = new File(fichier_de_sortie);
					File destination = new File(Environment.getExternalStorageDirectory().toString()+"/marvillet/"+nom_fichier_nouveau+".3gp");
					source.renameTo(destination);

				}

			} });

		adb.show();
	}

}
